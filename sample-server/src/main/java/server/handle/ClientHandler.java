package server.handle;

import connect.Connector;
import utils.CloseUtils;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/21
 **/

public class ClientHandler extends Connector {

    private final ClientHandlerCallBack clientHandlerCallBack;
    private final String clientInfo;

    public String getClientInfo() {
        return clientInfo;
    }

    public ClientHandler(SocketChannel socketChannel, ClientHandlerCallBack clientHandlerCallBack) throws IOException {
        this.clientHandlerCallBack = clientHandlerCallBack;
        this.clientInfo = socketChannel.getRemoteAddress().toString();
        System.out.println("新客户端连接:" + clientInfo);

        setUp(socketChannel);
    }

    public void exit() {
        CloseUtils.close(this);
        System.out.println("客户端已经退出" + clientInfo);
    }

    public void exitBySelf() {
        exit();
        clientHandlerCallBack.onSelfClosed(this);
    }

    @Override
    protected void onReceiveNewMessage(String str) {
        super.onReceiveNewMessage(str);
        clientHandlerCallBack.onNewMessageArrived(this, str);
    }

    @Override
    public void onChannelClosed(SocketChannel channel) {
        super.onChannelClosed(channel);
        exitBySelf();
    }

    public interface ClientHandlerCallBack {

        void onSelfClosed(ClientHandler handler);

        void onNewMessageArrived(ClientHandler handler, String msg);
    }
}


