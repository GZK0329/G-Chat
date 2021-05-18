package client;

import connect.IOContext;
import constants.ServerInfo;
import impl.IOSelectorProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/22
 **/

public class ClientTest {
    public static boolean done = false;

    public static void main(String[] args) throws IOException {
        IOContext.setUp()
                .ioProvider(new IOSelectorProvider())
                .start();

        ServerInfo serverInfo = ClientSearcher.searchServer(10000);
        System.out.println("serverInfo:" + serverInfo);
        if (serverInfo == null) return;
        final List<TCPClient> tcpClientList = new ArrayList<>();
        int size = 0;//统计成功连接数量
        for (int i = 0; i < 10; i++) {
            try {
                TCPClient tcpClient = TCPClient.startWith(serverInfo);
                if (tcpClient == null) {
                    System.out.println("连接异常!");
                    continue;
                }
                tcpClientList.add(tcpClient);
                System.out.println("连接第" + (size++) + "个客户端");

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //System.out.println(tcpClientList.isEmpty());
        //键盘输入
        System.in.read();
        //异步输入
        Runnable runnable = () -> {
            while (!done) {
                for (TCPClient tcpClient : tcpClientList) {
                    tcpClient.send("Hello~~");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();


        System.in.read();

        done = true;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (TCPClient tcpClient : tcpClientList) {
            tcpClient.exit();
        }
    }
}
