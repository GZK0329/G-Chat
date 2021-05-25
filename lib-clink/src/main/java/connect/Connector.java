package connect;

import box.StringReceivePacket;
import box.StringSendPacket;
import impl.SocketChannelAdapter;
import impl.async.AsyncReceiveDispatcher;
import impl.async.AsyncSendDispatcher;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.UUID;

/**
 * @Description: 构建连接实例
 * @Author: GZK0329
 * @Date: 2021/4/25
 **/

public class Connector implements SocketChannelAdapter.OnChannelStatusListener, Closeable {

    //标识连接 独一无二
    private final static UUID uuid = UUID.randomUUID();
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
            if(packet instanceof StringReceivePacket){
                String msg = ((StringReceivePacket) packet).string();
                onReceiveNewMessage(msg);
            }
        }
    };

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

    public void send(String msg)  {
        SendPacket packet = new StringSendPacket(msg);
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

}
