
import constants.TCPConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

        //向所有客户端发送信息
        BufferedReader bufferedReader = new
                BufferedReader(new InputStreamReader(System.in));

        try {
            String str;
            do{
                str = bufferedReader.readLine();
                tcpServer.broadcast(str);

            }while(!"00bye00".equalsIgnoreCase(str));
        } catch (IOException e) {
            e.printStackTrace();
        }


        UDPProvider.stop();
        tcpServer.stop();
    }

}
