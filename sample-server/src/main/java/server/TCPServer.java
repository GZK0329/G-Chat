package server;


import server.handle.ClientHandler;
import utils.CloseUtils;

import java.io.IOException;
import java.net.InetSocketAddress;

import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/20
 **/

public class TCPServer implements ClientHandler.ClientHandlerCallBack {

    private final int port;
    private ClientListener listener = new ClientListener();
    private List<ClientHandler> clientHandlerList = new ArrayList<>();
    private final ExecutorService forwardingThreadPoolExecutor;
    private Selector selector;
    private ServerSocketChannel server;

    public TCPServer(int port) {
        this.port = port;
        this.forwardingThreadPoolExecutor = Executors.newSingleThreadExecutor();
    }

    public boolean start() {
        try {
            //创建一个选择器
            Selector selector = Selector.open();
            //创建一个服务端通道
            ServerSocketChannel server = ServerSocketChannel.open();
            //配置为非阻塞通道
            server.configureBlocking(false);
            //连接 监听本地服务器端口port
            server.socket().bind(new InetSocketAddress(port));
            //注册选择器 监听接收事件 也就是 Socket client = socket.accept();
            server.register(selector, SelectionKey.OP_ACCEPT);

            this.server = server;
            this.selector = selector;

            System.out.println("启动服务器注册"+server.getLocalAddress().toString());

            listener.start();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void stop() throws IOException {
        if (listener != null) {
            listener.exit();
        }
        synchronized (TCPServer.this) {
            for (ClientHandler clientHandler : clientHandlerList) {
                clientHandler.exit();
            }
            clientHandlerList.clear();
        }

        forwardingThreadPoolExecutor.shutdownNow();
    }

    public synchronized void broadcast(String str) {
        for (ClientHandler clientHandler : clientHandlerList) {
            clientHandler.send(str);
        }
    }

    @Override
    public synchronized void onSelfClosed(ClientHandler handler) {
        clientHandlerList.remove(handler);
    }

    @Override
    public void onNewMessageArrived(final ClientHandler handler, final String msg) {
        System.out.println("收到新客户端连接，新客户端信息:" + handler.getClientInfo() + ",携带信息:" + msg);
        //异步提交转发任务
        forwardingThreadPoolExecutor.execute(
                () -> {
                    synchronized (TCPServer.this) {
                        for (ClientHandler clientHandler : clientHandlerList) {
                            if (clientHandler.equals(handler)) {
                                continue;//如果是自己那么就跳过
                            }
                            clientHandler.send(msg);//不然就把消息转发
                        }
                    }
                }
        );
    }


    private class ClientListener extends Thread {
        private boolean done;

        @Override
        public void run() {
            super.run();
            System.out.println("服务器已经准备好了");

            do {
                try {
                    if (selector.select() == 0) {
                        if (done) {
                            return;
                        }
                        continue;
                    }
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {

                        SelectionKey key = iterator.next();
                        iterator.remove();

                        //可以接收
                        if (key.isAcceptable()) {
                            //通过ServerSocketChannel接收 获取SocketChannel
                            SocketChannel clientChannel = server.accept();
                            //配置为非阻塞
                            clientChannel.configureBlocking(false);
                            ClientHandler clientHandler = new ClientHandler(clientChannel, TCPServer.this);
                            //clientHandler.readToPrint();
                            synchronized (TCPServer.this) {
                                clientHandlerList.add(clientHandler);
                            }
                        }
                    }
                } catch (IOException e) {
                    continue;
                }
            } while (!done);
        }

        private void exit() {
            done = true;
            try {
                CloseUtils.close(server);
                CloseUtils.close(selector);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
