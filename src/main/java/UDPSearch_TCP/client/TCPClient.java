package UDPSearch_TCP.client;



import UDPSearch_TCP.ServerInfo;

import java.io.*;
import java.net.*;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/20
 **/

public class TCPClient {

    //建立TCP连接
    public static void linkWith(ServerInfo info) throws Exception {
        Socket socket = new Socket();

        socket.setSoTimeout(3000);
        System.out.println(Inet4Address.getByName(info.getAddress()));
        System.out.println(info.getPort());
        //连接套接字
        socket.connect(new InetSocketAddress(Inet4Address.getByName(info.getAddress()), info.getPort()), 3000);

        System.out.println("已经进入TCP连接");
        System.out.println("客户端信息:" + socket.getLocalAddress() + socket.getLocalPort());
        System.out.println("服务端信息:" + socket.getInetAddress() + socket.getPort());

        try {
            todo(socket);
        } catch (Exception e) {
            System.out.println("异常关闭!");
        } finally {
            socket.close();
            System.out.println("退出客户端了");
        }
    }

    public static void todo(Socket socket) {

        try {
            // 构建键盘输入流
            InputStream in = System.in;
            BufferedReader input = new BufferedReader(new InputStreamReader(in));

            //获取套接字输出流
            OutputStream outputStream = socket.getOutputStream();
            PrintStream socketOutPut = new PrintStream(outputStream);

            //获取套接字输入流
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            boolean flag = true;

            do {
                //键盘读一行
                String str = input.readLine();
                //发送到服务器
                socketOutPut.println(str);

                //从服务器读一行
                String strFromServer = bufferedReader.readLine();
                if ("bye".equalsIgnoreCase(strFromServer)) {
                    flag = false;
                    System.out.println("链接结束了 拜拜");
                } else {
                    System.out.println(strFromServer);
                }
            } while (flag);

            socketOutPut.close();
            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
