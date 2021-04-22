package UDPSearch_TCP.client;

import UDPSearch_TCP.ServerInfo;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/19
 **/

public class Client {
    public static void main(String[] args) {
        ServerInfo info = ClientSearcher.searchServer(10000);
        System.out.println("Server:" + info);
        if(info != null){
            try {
                TCPClient.linkWith(info);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
