package UDPSearch.server;

import UDPSearch.constants.*;

import java.io.IOException;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/19
 **/

public class Server {
    public static void main(String[] args) {

        ServerProvider.  start(TCPConstants.PORT_SERVER);

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ServerProvider.stop();
    }
}
