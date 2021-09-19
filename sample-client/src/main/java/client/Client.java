package client;


import box.FileReceivePacket;
import box.FileSendPacket;
import connect.IOContext;
import constants.Foo;
import constants.ServerInfo;
import impl.IOSelectorProvider;

import java.io.*;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/1/19
 **/

public class Client {
    //public static boolean done = true;
    public static void main(String[] args) throws Exception {
        File cachePath = Foo.getCacheDir("client");
        IOContext.setUp()
                .ioProvider(new IOSelectorProvider())
                .start();

        ServerInfo info = ClientSearcher.searchServer(10000);
        System.out.println("ServerInfo:" + info);
        if (info != null) {
            TCPClient tcpClient = null;
            try {
                tcpClient = TCPClient.startWith(info, cachePath);
                if (tcpClient == null) return;

                //往服务器发
                write(tcpClient);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (tcpClient != null) {
                    tcpClient.exit();
                }
            }
        }
    }

    public static void write(TCPClient tcpClient) {
        if (tcpClient == null) {
            return;
        }
        InputStream in = System.in;//字节流
        BufferedReader input = new BufferedReader(new InputStreamReader(in));
        try {
            do {
                String str = input.readLine();
                if ("00bye00".equalsIgnoreCase(str)) {
                    break;
                }
                /*
                * 假设str以--f开头则代表这是个文件
                * */
                if(str.startsWith("--f")){
                    String[] array = str.split(" ");
                    if(array.length >= 2){
                        String filePath = array[1];
                        File file = new File(filePath);
                        if(file.exists() && file.isFile()){
                            FileSendPacket packet = new FileSendPacket(file);
                            tcpClient.send(packet);
                            continue;
                        }
                    }
                }
                tcpClient.send(str);
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
