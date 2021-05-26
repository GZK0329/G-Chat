package connect;

import box.BytesReceivePacket;
import box.FileReceivePacket;
import box.StringReceivePacket;
import box.StringSendPacket;
import impl.SocketChannelAdapter;
import impl.async.AsyncReceiveDispatcher;
import impl.async.AsyncSendDispatcher;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.UUID;

/**
 * @Description: 构建连接实例
 * @Author: GZK0329
 * @Date: 2021/4/25
 **/

public abstract class Connector implements SocketChannelAdapter.OnChannelStatusListener, Closeable {

    //标识连接 独一无二
    protected final static UUID uuid = UUID.randomUUID();
    //通道
    private SocketChannel channel;
    //发送者
    private Sender sender;
    //接收者
    private Receiver receiver;
    //调度者
    private SendDispatcher sendDispatcher;
    private ReceiveDispatcher receiveDispatcher;

    private ReceiveDispatcher.ReceivePacketCallBack receiveCallBack = new ReceiveDispatcher.ReceivePacketCallBack() {
        @Override
        public void onReceivePacketCompleted(ReceivePacket packet) {
            onReceivePacket(packet);
        }

        @Override
        public ReceivePacket<?, ?> onArrivedNewPacket(byte type, int length) {
            switch (type) {
                case Packet.TYPE_MEMORY_BYTES:
                    return new BytesReceivePacket(length);
                case Packet.TYPE_MEMORY_STRING:
                    return new StringReceivePacket(length);
                case Packet.TYPE_STREAM_FILE:
                    return new FileReceivePacket(length, createNewReceiveFile());
                case Packet.TYPE_STREAM_DIRECT:
                    return new BytesReceivePacket(length);
                default:
                    throw new UnsupportedOperationException("不支持的传输文件类型:" + type);
            }
        }
    };

    protected abstract File createNewReceiveFile();

    public void setUp(SocketChannel socketChannel) throws IOException {
        this.channel = socketChannel;
        IOContext ioContext = IOContext.getINSTANCE();

        SocketChannelAdapter socketChannelAdapter =
                new SocketChannelAdapter(channel, ioContext.getIoProvider(), this);

        sender = socketChannelAdapter;
        receiver = socketChannelAdapter;


        sendDispatcher = new AsyncSendDispatcher(sender);
        receiveDispatcher = new AsyncReceiveDispatcher(receiver, receiveCallBack);

        //启动接收
        receiveDispatcher.start();
    }

    public void send(String msg) {
        SendPacket packet = new StringSendPacket(msg);
        try {
            sendDispatcher.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(SendPacket packet) {
        try {
            sendDispatcher.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        receiveDispatcher.close();
        sendDispatcher.close();

        channel.close();
    }

    @Override
    public void onChannelClosed(SocketChannel channel) {
        System.out.println("pipe is closed!");
    }

    protected void onReceiveNewMessage(String str) {
        System.out.println(uuid.toString() + ":" + str);
    }

    protected void onReceivePacket(ReceivePacket packet) {
        System.out.println(uuid.toString() + "[New-Packet]-Type:" + packet.type() + ",Length:" + packet.length);
    }

}
