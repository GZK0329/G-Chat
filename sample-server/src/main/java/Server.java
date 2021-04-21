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
        UDPSearch_TCP.server.TCPServer tcpServer = new UDPSearch_TCP.server.TCPServer(TCPConstants.PORT_SERVER);
        boolean isSucceed = tcpServer.start();
        if (!isSucceed) {
            System.out.println("Start TCP server failed!");
            return;
        }

        UDPSearch_TCP.server.UDPProvider.start(TCPConstants.PORT_SERVER);

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


        UDPSearch_TCP.server.UDPProvider.stop();
        tcpServer.stop();
    }

}
