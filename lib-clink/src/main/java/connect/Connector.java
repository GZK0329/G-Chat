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

    void setUp(SocketChannel socketChannel) throws IOException {
        this.channel = socketChannel;
        IOContext ioContext = IOContext.getINSTANCE();
        SocketChannelAdapter socketChannelAdapter =
                new SocketChannelAdapter(channel, ioContext.getIoProvider(), this);
        sender = socketChannelAdapter;
        receiver = socketChannelAdapter;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void onChannelClosed(SocketChannel channel) {
        System.out.println("管道关闭了");
    }
}
