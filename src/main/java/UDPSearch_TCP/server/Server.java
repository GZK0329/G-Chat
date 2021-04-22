package UDPSearch_TCP.server;

import UDPSearch_TCP.constants.TCPConstants;
import UDPSearch_TCP.constants.UDPConstants;

import java.io.IOException;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/19
 **/

public class Server {

    public static void main(String[] args) {
        TCPServer tcpServer = new TCPServer(TCPConstants.PORT_SERVER);
        boolean isSucceed = tcpServer.start();
        if (!isSucceed) {
            System.out.println("Start TCP server failed!");
            return;
        }

        UDPProvider.start(TCPConstants.PORT_SERVER);

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        UDPProvider.stop();
        tcpServer.stop();
    }

}
