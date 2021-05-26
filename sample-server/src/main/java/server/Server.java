package server;


import connect.IOContext;
import constants.Foo;
import constants.TCPConstants;
import impl.IOSelectorProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/19
 **/

public class Server {

    public static void main(String[] args) throws Exception {
        File cachePath = Foo.getCacheDir("server");

        IOContext.setUp()
                .ioProvider(new IOSelectorProvider())
                .start();
        //返回IOContext 且IOContext中包含以一个IOProvider是IOSelectorProvider


        TCPServer tcpServer = new TCPServer(TCPConstants.PORT_SERVER, cachePath);
        boolean isSucceed = tcpServer.start();
        if (!isSucceed) {
            System.out.println("Start TCP server failed!");
            return;
        }

        UDPProvider.start(TCPConstants.PORT_SERVER);

        //向所有客户端发送信息
        BufferedReader bufferedReader = new
                BufferedReader(new InputStreamReader(System.in));

        try {
            String str;
            do {
                str = bufferedReader.readLine();
                if (!"00bye00".equalsIgnoreCase(str)) {
                    break;
                }
                tcpServer.broadcast(str);
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        UDPProvider.stop();
        tcpServer.stop();

        IOContext.close();
    }

}
