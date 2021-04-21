import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/20
 **/

public class TCPServer {

    private final int port;
    private ClientListener mListener;
    private List<ClientHandler> clientHandlerList = new ArrayList<>();


    public TCPServer(int port) {
        this.port = port;
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
        for (ClientHandler clientHandler : clientHandlerList) {
            clientHandler.exit();
        }
        clientHandlerList.clear();
    }

    public void broadcast(String str) {
        for (ClientHandler clientHandler : clientHandlerList) {
            clientHandler.send(str);
        }
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
                    clientHandler = new ClientHandler(client,
                            handler ->  clientHandlerList.remove(handler));//相当于使用这个handler作为参数，实现了这个接口中的方法
                    //等于使用clientHandlerList.remove(handler)实现了接口，使用handler作为参数执行这个方法

                } catch (IOException e) {
                    e.printStackTrace();
                }

                clientHandler.readToPrint();
                clientHandlerList.add(clientHandler);
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
