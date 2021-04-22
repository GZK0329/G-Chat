package UDPSearch_TCP.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/20
 **/

public class TCPServer {

    private final int port;
    private ClientListener mListener;


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
                    e.printStackTrace();
                }

                //异步处理收到的客户端
                ClientHandler clientHandler = new ClientHandler(client);

                clientHandler.start();
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

    private class ClientHandler extends Thread {
        private boolean flag = true;
        private Socket client;

        ClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("新客户端连接: IP:" + client.getInetAddress() + " Port:" + client.getPort());


            try {
                //先获取输出流
                OutputStream outputStream = client.getOutputStream();
                //包装成打印流
                PrintStream socketOutPut = new PrintStream(outputStream);

                //获取输入流
                InputStream inputStream = client.getInputStream();
                //包装城InputStreamReader
                InputStreamReader inputReader = new InputStreamReader(inputStream);
                //包装城BufferReader
                BufferedReader bufferedReader = new BufferedReader(inputReader);

                do{
                    //获取到内容
                    String str = bufferedReader.readLine();
                    if ("bye".equalsIgnoreCase(str)) {
                        flag = false;
                        System.out.println("bye");
                    }else{
                        //打印数据
                        System.out.println(str);
                        //回送数据长度
                        socketOutPut.println("回送长度:"+ str.length());
                    }
                }while(flag);
                
                socketOutPut.close();
                bufferedReader.close();

            } catch (Exception e) {

                System.out.println("连接异常 断开!");
            }finally {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("客户端处理结束,退出, 客户端地址:"+ client.getInetAddress() + "port" + client.getPort());
        }

    }

}
