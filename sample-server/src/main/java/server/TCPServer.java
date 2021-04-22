package server;



import server.handle.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/20
 **/

public class TCPServer  implements ClientHandler.ClientHandlerCallBack{

    private final int port;
    private ClientListener mListener;
    private List<ClientHandler> clientHandlerList = new ArrayList<>();
    private final ExecutorService forwardingThreadPoolExecutor;


    public TCPServer(int port) {
        this.port = port;
        this.forwardingThreadPoolExecutor = Executors.newSingleThreadExecutor();
    }

    public boolean start() {
        try {
            ClientListener listener = new ClientListener(port);
            mListener = listener;
            listener.start();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void stop() {
        if (mListener != null) {
            mListener.exit();
        }
        synchronized (TCPServer.this){
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
    public void onNewMessageArrived(ClientHandler handler, String msg) {
        System.out.println("收到新客户端连接，新客户端信息:"+handler.getClientInfo()+",携带信息:"+msg);
        //异步提交转发任务
        forwardingThreadPoolExecutor.execute(
                () ->{
                    synchronized (TCPServer.this){
                        for (ClientHandler clientHandler : clientHandlerList) {
                            if(clientHandler.equals(handler)){
                                continue;//如果是自己那么就跳过
                            }
                            clientHandler.send(msg);//不然就把消息转发
                        }
                    }
                }
        );
    }


    private class ClientListener extends Thread {
        private ServerSocket server;
        private boolean done;

        public ClientListener(int port) throws IOException {
            this.server = new ServerSocket(port);
            System.out.println("服务器的信息: " + "\n" + "IP:" + server.getInetAddress() + "\n" +
                    "port:" + server.getLocalPort());
        }

        @Override
        public void run() {
            super.run();
            System.out.println("服务器已经准备好了");

            do {
                Socket client = null;
                try {
                    client = server.accept();
                } catch (IOException e) {
                    continue;
                }

                //异步处理收到的客户端
                ClientHandler clientHandler = null;
                try {
                    clientHandler = new ClientHandler(client,TCPServer.this);//

                } catch (IOException e) {
                    e.printStackTrace();
                }

                clientHandler.readToPrint();
                synchronized (TCPServer.this){
                    clientHandlerList.add(clientHandler);
                }
            } while (!done);
        }

        private void exit() {
            done = true;
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



}
