package client;


import constants.ServerInfo;
import utils.CloseUtils;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/20
 **/

public class TCPClient {
    private final Socket socket;
    private final ReaderHandler readerHandler;
    private final PrintStream printStream;


    public TCPClient(Socket socket, ReaderHandler readerHandler) throws IOException {
        this.socket = socket;
        this.readerHandler = readerHandler;
        this.printStream = new PrintStream(socket.getOutputStream());
    }

    public void exit() {
        readerHandler.exit();
        CloseUtils.close(printStream);
        CloseUtils.close(socket);
    }


    //发
    public void send(String msg){
        printStream.println(msg);
    }


    //建立TCP连接
    public static TCPClient startWith(ServerInfo info) throws Exception {
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

            //改为读写操作
            //todo(socket);
            //输入流
            ReaderHandler readerHandler = new ReaderHandler(socket.getInputStream());
            readerHandler.start();

            //输出
            //write(socket);

            return new TCPClient(socket, readerHandler);

            //readerHandler.exit();

        } catch (Exception e) {
            System.out.println("异常! 关闭套接字!");
            CloseUtils.close(socket);
        }

        return null;
    }

    //读
    private static class ReaderHandler extends Thread {
        private boolean done = false;
        private final InputStream inputStream;

        public ReaderHandler(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            super.run();
            BufferedReader socketInput = new BufferedReader(new InputStreamReader(inputStream));
        try{
            do{
                String strReader = null;
                try {
                    strReader = socketInput.readLine();
                } catch (IOException e) {
                    continue;
                }
                if(strReader == null){
                    System.out.println("连接已关闭，无法继续读取");
                    break;
                }
                System.out.println(strReader);
            }while(!done);
        }catch (Exception e){
            if(!done){
                System.out.println("出现异常，链接结束！"+ e.getMessage());
            }
        }finally {
            CloseUtils.close(inputStream);
        }
        }

        void exit() {
            done = false;
            CloseUtils.close(inputStream);
        }
    }



    /*private static void write(Socket client) throws IOException {
        //构建键盘输入流
        InputStream in = System.in;//字节流
        BufferedReader input = new BufferedReader(new InputStreamReader(in));//字节流 => 字符流 => bufferReader

        //构建socket输出流 转换成打印流
        OutputStream outputStream = client.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);

        do {
            String strFromKeyBoard = input.readLine();
            socketPrintStream.println(strFromKeyBoard);
            if("00bye00".equalsIgnoreCase(strFromKeyBoard)){
                break;
            }
        } while (true);

        input.close();
        socketPrintStream.close();
    }*/


   /* public static void todo(Socket socket) {

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
    }*/

}
