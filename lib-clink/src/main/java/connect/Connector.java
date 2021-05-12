package connect;

import impl.SocketChannelAdapter;

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

    public void setUp(SocketChannel socketChannel) throws IOException {
        this.channel = socketChannel;
        IOContext ioContext = IOContext.getINSTANCE();
        SocketChannelAdapter socketChannelAdapter =
                new SocketChannelAdapter(channel, ioContext.getIoProvider(), this);
        sender = socketChannelAdapter;
        receiver = socketChannelAdapter;

        readNextMessage();
    }

    private void readNextMessage() {
        if (receiver != null) {
            try {
                receiver.receiveAsync(echoReceiveListener);
            } catch (Exception e) {
                System.out.println("接收数据异常" + e.getMessage());
            }
        }
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void onChannelClosed(SocketChannel channel) {
        System.out.println("管道关闭了");
    }

    private IOArgs.IOArgsEventListener echoReceiveListener = new IOArgs.IOArgsEventListener() {
        @Override
        public void onStarted(IOArgs args) {

        }

        @Override
        public void onCompleted(IOArgs args) {
            //打印
            onReceiveNewMessage(args.bufferToString());
            //读下一条数据
            readNextMessage();
        }
    };

    protected void onReceiveNewMessage(String str) {
        System.out.println(uuid.toString() + ":" + str);
    }

}
