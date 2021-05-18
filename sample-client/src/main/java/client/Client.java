package client;


import connect.IOContext;
import constants.ServerInfo;
import impl.IOSelectorProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/19
 **/

public class Client {
    //public static boolean done = true;
    public static void main(String[] args) throws IOException {

        IOContext.setUp()
                .ioProvider(new IOSelectorProvider())
                .start();

        ServerInfo info = ClientSearcher.searchServer(10000);
        System.out.println("ServerInfo:" + info);
        if (info != null) {
            TCPClient tcpClient = null;
            try {
                tcpClient = TCPClient.startWith(info);
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
                tcpClient.send(str);
                tcpClient.send(str);
                tcpClient.send(str);
                tcpClient.send(str);
                if ("00bye00".equalsIgnoreCase(str)) {
                    break;
                }
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
