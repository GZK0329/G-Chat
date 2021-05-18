package impl.async;

import box.StringReceivePacket;
import connect.*;
import utils.CloseUtils;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/5/18
 **/

public class AsyncReceiveDispatcher implements ReceiveDispatcher, Closeable {
    private final Receiver receiver;
    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    private final ReceiveDispatcher.receivePacketCallBack callBack;
    private IOArgs ioArgs = new IOArgs();

    private ReceivePacket packetTemp;

    private byte[] buffer;
    private int total;
    private int position;

    public AsyncReceiveDispatcher(Receiver receiver, ReceiveDispatcher.receivePacketCallBack callBack) {
        this.receiver = receiver;
        receiver.setReceiveListener(ioArgsEventListener);
        this.callBack = callBack;
    }

    @Override
    public void start() {
       registerReceive();
    }

    @Override
    public void stop() {

    }

    public void close() {
        if(isClosed.compareAndSet(false, true)){
            ReceivePacket packet = this.packetTemp;
            if(packet != null){
                packetTemp = null;
                CloseUtils.close(packet);
            }
        }
    }

    public void closeAndNotify() {
        CloseUtils.close(this);
    }

    public void registerReceive() {
        receiver.receiveAsync(ioArgs);
    }


    public IOArgs.IOArgsEventListener ioArgsEventListener = new IOArgs.IOArgsEventListener() {
        @Override
        public void onStarted(IOArgs args) {
            int receiveSize;
            if (packetTemp == null) {
                receiveSize = 4;
            } else {
                receiveSize = Math.min(total - position, args.capacity());
            }
            //设置本次接收数据的大小
            args.limit(receiveSize);
        }

        @Override
        public void onCompleted(IOArgs args) {
            assemblePacket(args);
            //接收下一条数据
            registerReceive();
        }
    };

    /*
     * 解析数据到packet
     * */
    private void assemblePacket(IOArgs args) {
        if (packetTemp == null) {
            int length = args.readLength();
            packetTemp = new StringReceivePacket(length);
            buffer = new byte[length];
            total = length;
            position = 0;
        }
        int count = args.writeTo(buffer, 0);
        if (count > 0) {
            packetTemp.save(buffer, count);
            position += count;

            if (position == total) {
                completePacket();
                packetTemp = null;
            }
        }
    }

    private void completePacket() {
        ReceivePacket packet = this.packetTemp;
        CloseUtils.close(packet);
        //回调外层 数据已经接收
        callBack.onReceivePacketCompleted(packet);
    }
}
