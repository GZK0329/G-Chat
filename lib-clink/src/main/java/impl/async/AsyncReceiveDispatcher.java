package impl.async;

import box.FileReceivePacket;
import box.StringReceivePacket;
import connect.*;
import utils.CloseUtils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description: 异步接收调度
 * @Author: GZK0329
 * @Date: 2021/1/18
 **/

public class AsyncReceiveDispatcher implements ReceiveDispatcher, Closeable, IOArgs.IOArgsEventProcessor {
    private final Receiver receiver;
    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private final AtomicBoolean isSucceed = new AtomicBoolean(false);
    private ReceivePacketCallBack callback;

    private IOArgs ioArgs = new IOArgs();
    private ReceivePacket<?, ?> packetTemp;

    private WritableByteChannel packetChannel;
    private long total;
    private long position;

    public AsyncReceiveDispatcher(Receiver receiver, ReceivePacketCallBack callback) {
        this.receiver = receiver;
        this.callback = callback;
        receiver.setReceiveListener(this);
    }

    @Override
    public void start() {
        registerReceive();
    }

    @Override
    public void stop() {

    }

    public void close() {
        if (isClosed.compareAndSet(false, true)) {
            completePacket(false);
            ReceivePacket packet = this.packetTemp;

            if (packet != null) {
                packetTemp = null;
                CloseUtils.close(packet);
            }
        }
    }

    public void closeAndNotify() {
        CloseUtils.close(this);
    }

    public void registerReceive() {
        try {
            receiver.postReceiveAsync();
        } catch (Exception e) {
            closeAndNotify();
        }
    }

    /*
     * 解析数据到packet
     * */
    private void assemblePacket(IOArgs args) {
        if (packetTemp == null) {
            int length = args.readLength();
            byte type = length > 200 ? Packet.TYPE_STREAM_FILE : Packet.TYPE_MEMORY_STRING;
            packetTemp = callback.onArrivedNewPacket(type, length);
            packetChannel = Channels.newChannel(packetTemp.open());

            total = length;
            position = 0;
        }

        try {
            int count = args.writeTo(packetChannel);
            position += count;
            if (position == total) {
                completePacket(true);
                //packetTemp = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            completePacket(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * isSucceed 是否成功关闭状态
     * 非正常关闭false 正常true
     * */
    private void completePacket(boolean isSucceed) {
        ReceivePacket packet = this.packetTemp;
        CloseUtils.close(packet);
        packetTemp = null;

        WritableByteChannel channel = this.packetChannel;
        CloseUtils.close(channel);
        packetChannel = null;

        if (packet != null) {
            callback.onReceivePacketCompleted(packet);
        }
    }

    @Override
    public IOArgs provideIOArgs() {
        IOArgs args = ioArgs;
        int receiveSize;
        if (packetTemp == null) {
            receiveSize = 4;
        } else {
            receiveSize = (int) Math.min(args.capacity(), total - position);
        }
        args.limit(receiveSize);

        return args;
    }

    @Override
    public void onConsumeFailed(IOArgs ioArgs, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onConsumeCompleted(IOArgs ioArgs) {
        assemblePacket(ioArgs);
        registerReceive();
    }
}
