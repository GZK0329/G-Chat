package client;


import connect.Connector;
import connect.Packet;
import connect.ReceivePacket;
import constants.Foo;
import constants.ServerInfo;
import utils.CloseUtils;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/20
 **/

public class TCPClient extends Connector {
    private final File cachePath;
    public TCPClient(SocketChannel socketChannel, File cachePath) throws IOException {
        setUp(socketChannel);
        this.cachePath = cachePath;
    }

    public void exit() {
        CloseUtils.close(this);
    }

    @Override
    protected File createNewReceiveFile() {
        return Foo.createRandomTemp(cachePath);
    }

    @Override
    public void onChannelClosed(SocketChannel channel) {
        super.onChannelClosed(channel);
        System.out.println("连接已关闭，无法读取更多信息");
    }

    //建立TCP连接
    public static TCPClient startWith(ServerInfo info, File cachePath) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        //连接套接字
        socketChannel.connect(new InetSocketAddress(Inet4Address.getByName(info.getAddress()), info.getPort()));
        System.out.println("已经进入TCP连接");
        System.out.println("客户端信息:" + socketChannel.getLocalAddress().toString());
        System.out.println("服务端信息:" + socketChannel.getRemoteAddress().toString());
        return new TCPClient(socketChannel, cachePath);
    }

    @Override
    protected void onReceivePacket(ReceivePacket packet) {
        super.onReceivePacket(packet);
        if(packet.type() == Packet.TYPE_MEMORY_STRING){
            String str = (String) packet.entity();
            System.out.println(uuid.toString() + ":" + str);
        }
    }
}
