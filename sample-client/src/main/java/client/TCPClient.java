package client;


import connect.Connector;
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
    public TCPClient(SocketChannel socketChannel) throws IOException {
        setUp(socketChannel);
    }

    public void exit() {
        CloseUtils.close(this);
    }

    @Override
    public void onChannelClosed(SocketChannel channel) {
        super.onChannelClosed(channel);
        System.out.println("连接已关闭，无法读取更多信息");
    }

    //建立TCP连接
    public static TCPClient startWith(ServerInfo info) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        //连接套接字
        socketChannel.connect(new InetSocketAddress(Inet4Address.getByName(info.getAddress()), info.getPort()));
        System.out.println("已经进入TCP连接");
        System.out.println("客户端信息:" + socketChannel.getLocalAddress().toString());
        System.out.println("服务端信息:" + socketChannel.getRemoteAddress().toString());
        return new TCPClient(socketChannel);
    }


}
