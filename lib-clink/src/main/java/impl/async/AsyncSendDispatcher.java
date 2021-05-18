package impl.async;

import connect.IOArgs;
import connect.SendDispatcher;
import connect.SendPacket;
import connect.Sender;
import utils.CloseUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description: 异步发送转换器
 * @Author: GZK0329
 * @Date: 2021/5/14
 **/

public class AsyncSendDispatcher implements SendDispatcher, Closeable {
    private final Sender sender;
    private final Queue<SendPacket> queue = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean isSending = new AtomicBoolean(false);
    private final AtomicBoolean isClosed = new AtomicBoolean(false);


    private IOArgs ioArgs = new IOArgs();
    private SendPacket sendPacket;

    private int total;
    private int position;

    private SendPacket packetTemp;


    public AsyncSendDispatcher(Sender sender) {
        this.sender = sender;
    }


    @Override
    public void send(SendPacket packet) {
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

    private void sendNextPacket() {
        SendPacket temp = packetTemp;
        if (temp != null) {
            CloseUtils.close(temp);
        }

        SendPacket packet = packetTemp = takePacket();
        if (packet == null) {
            isSending.set(false);
            return;
        }
        total = packet.length();
        position = 0;
        sendCurrentPacket();
    }

    private void sendCurrentPacket() {
        IOArgs args = ioArgs;
        args.startWriting();

        if (position >= total) {
            sendNextPacket();
            return;
        } else if (position == 0) {
            //首包，需携带长度信息
            args.writeLength(total);
        }
        byte[] bytes = packetTemp.bytes();
        int count = args.readFrom(bytes, position);
        position += count;

        args.finishWriting();
        sender.sendAsync(args, IOArgsEventListener);

    }


    private void closeAndNotify() {
        CloseUtils.close(this);
    }

    private IOArgs.IOArgsEventListener IOArgsEventListener = new IOArgs.IOArgsEventListener() {
        @Override
        public void onStarted(IOArgs args) {

        }

        @Override
        public void onCompleted(IOArgs args) {
            //继续发送当前包
            sendCurrentPacket();
        }
    };

    @Override
    public void cancel(SendPacket packet) {

    }

    @Override
    public void close() {
        if (isClosed.compareAndSet(false, true)) {
            isSending.set(false);
            SendPacket packet = packetTemp;
            if (packetTemp != null) {
                packetTemp = null;
                CloseUtils.close(packet);
            }
        }
    }
}
