package impl.async;

import connect.*;
import utils.CloseUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description: 异步发送转换器
 * @Author: GZK0329
 * @Date: 2021/1/14
 **/

public class AsyncSendDispatcher implements SendDispatcher, Closeable, IOArgs.IOArgsEventProcessor {
    private final Sender sender;
    private final Queue<SendPacket> queue = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean isSending = new AtomicBoolean();
    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    private ReadableByteChannel packetChannel;
    private IOArgs ioArgs = new IOArgs();


    private long total;
    private long position;

    private SendPacket<?> packetTemp;


    public AsyncSendDispatcher(Sender sender) {
        this.sender = sender;
        sender.setSendListener(this);
    }

    @Override
    public IOArgs provideIOArgs() {
        IOArgs args = ioArgs;
        if(packetChannel == null){
            //首包
            packetChannel = Channels.newChannel(packetTemp.open());
            args.limit(4);
            args.writeLength((int) packetTemp.length());
        }else {
            //非首包
            args.limit((int) Math.min(args.capacity(), total - position));
            try {
                //从channel中读取数据到args
                int count = args.readFrom(packetChannel);
                position += count;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return args;
    }

    @Override
    public void onConsumeFailed(IOArgs ioArgs, Exception e) {
            e.printStackTrace();
    }

    @Override
    public void onConsumeCompleted(IOArgs ioArgs) {
        try {
            sendCurrentPacket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void send(SendPacket packet) throws Exception {
        queue.offer(packet);
        if (isSending.compareAndSet(false, true)) {
            sendNextPacket();
        }
    }


    private SendPacket takePacket() {
        SendPacket packet = queue.poll();
        if (packet != null && packet.isCanceled()) {
            return takePacket();
        }
        return packet;
    }

    private void sendNextPacket() throws Exception {

        SendPacket temp = packetTemp;
        CloseUtils.close(temp);

        SendPacket packet = packetTemp = takePacket();

        if (packet == null) {
            isSending.set(false);
            return;
        }
        total = packet.length();
        position = 0;
        sendCurrentPacket();
    }

    private void sendCurrentPacket() throws Exception {
        if (position >= total) {
            completePacket(total == position);
            sendNextPacket();
            return;
        }
        try {
            sender.postSendAsync();
        } catch (IOException e) {
            closeAndNotify();
        }
    }

    private void completePacket(boolean isSucceed) {
        SendPacket packet = this.packetTemp;
        if (packet == null) {
            return;
        }
        CloseUtils.close(packet);
        CloseUtils.close(packetChannel);
        packetChannel = null;
        packetTemp = null;
        total = 0;
        position = 0;
    }


    private void closeAndNotify() {
        CloseUtils.close(this);
    }

    @Override
    public void cancel(SendPacket packet) {

    }

    @Override
    public void close() {
        if (isClosed.compareAndSet(false, true)) {
            isSending.set(false);
            //异常导致完成
            completePacket(false);
        }
    }


}
