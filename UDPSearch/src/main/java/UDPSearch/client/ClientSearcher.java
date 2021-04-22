package UDPSearch.client;

import UDPSearch.ServerInfo;
import UDPSearch.constants.*;
import utils.ByteUtils;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 客户端搜索机制
 * @Author: GZK0329
 * @Date: 2021/4/19
 **/

public class ClientSearcher {

    //客户端回送的端口
    private static final int LISTEN_PORT = UDPConstants.PORT_CLIENT_RESPONSE;

    public static ServerInfo searchServer(int timeout) {
        System.out.println("UDPSearcher started!");
        CountDownLatch receiveLatch = new CountDownLatch(1);
        Listener listener = null;
        try {

            listener = listen(receiveLatch);
            sendBroadCast();
            receiveLatch.await(timeout, TimeUnit.MILLISECONDS);

        }catch (Exception e){
            e.printStackTrace();
        }
        // 完成
        System.out.println("UDPSearcher Finished.");
        if (listener == null) {
            return null;
        }
        List<ServerInfo> devices = listener.getServerAndClose();
        if (devices.size() > 0) {
            return devices.get(0);
        }
        return null;
    }

    private static Listener listen(CountDownLatch receiveLatch) throws InterruptedException {
        System.out.println("UDPSearcher start listen.");
        CountDownLatch startDownLatch = new CountDownLatch(1);
        Listener listener = new Listener(LISTEN_PORT, startDownLatch, receiveLatch);
        listener.start();
        startDownLatch.await();
        return listener;
    }
    //广播
    private static void sendBroadCast() throws IOException {
        System.out.println("broadCast started");

        //搜索 自动分配端口即可
        DatagramSocket ds = new DatagramSocket();

        //构建请求数据 请求空间
        ByteBuffer byteBuffer = ByteBuffer.allocate(128);

        byteBuffer.put(UDPConstants.HEADER);
        byteBuffer.putShort((short) 1);
        //回送端口
        byteBuffer.putInt(LISTEN_PORT);

        /*
        * 向PORT_SERVER端口发送广播，并且携带自己监听的LISTEN_PORT端口的数据报
        * */

        //构建packet
        DatagramPacket requestPacket = new DatagramPacket(byteBuffer.array(), byteBuffer.position() + 1);

        //数据报中携带广播地址
        requestPacket.setAddress(InetAddress.getByName("255.255.255.255"));
        //这个数据包发往的端口 也就是服务端的端口
        requestPacket.setPort(UDPConstants.PORT_SERVER);

        //发送
        ds.send(requestPacket);
        ds.close();

        System.out.println("broadCast is finished");
    }

    private static class Listener extends Thread{
        private final int listenPort;
        private final CountDownLatch startDownLatch;
        private final CountDownLatch receiveDownLatch;
        private final List<ServerInfo> serverInfoList = new ArrayList<>();
        private final byte[] buffer = new byte[128];
        private final int minLen = UDPConstants.HEADER.length + 2 + 4;
        private boolean done = false;
        private DatagramSocket ds = null;

        private Listener(int listenPort, CountDownLatch startDownLatch, CountDownLatch receiveDownLatch){
            super();
            this.listenPort = listenPort;
            this.startDownLatch = startDownLatch;
            this.receiveDownLatch = receiveDownLatch;
        }

        @Override
        public void run() {
            super.run();

            //递减锁存器的计数，如果计数到达零，则释放所有等待的线程。如果当前计数大于零，则将计数减少.
            startDownLatch.countDown();

            try {
                //构建UDP套接字 监听回送端口
                ds = new DatagramSocket(listenPort);
                //构建数据报接收数据
                DatagramPacket recvdp = new DatagramPacket(buffer, buffer.length);

                while(!done){
                    //接收数据报
                    ds.receive(recvdp);

                    //打印接收到的信息
                    String ip = recvdp.getAddress().getHostAddress();
                    int port = recvdp.getPort();
                    byte[] data = recvdp.getData();
                    int dataLen = recvdp.getLength();
                    boolean isValid = dataLen >= minLen
                            && ByteUtils.startWith(data, UDPConstants.HEADER);
                    System.out.println("UDPSearcher receive data from ip:" + ip +
                            "port:" + port +
                            "isValid:" + isValid);

                    if(!isValid){
                        //无效
                        continue;
                    }

                    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, UDPConstants.HEADER.length, dataLen);
                    final short cmd = byteBuffer.getShort();
                    final int serverPort = byteBuffer.getInt();

                    if(cmd != 2 || serverPort <= 0){
                        System.out.println("UDPServer receive cmd: "+ cmd + "port:" + serverPort);
                        continue;
                    }

                    String sn = new String(buffer, minLen, dataLen-minLen);
                    ServerInfo serverInfo = new ServerInfo(sn, serverPort, ip);
                    serverInfoList.add(serverInfo);

                    receiveDownLatch.countDown();
                }

            } catch (Exception ignored) {
            } finally {
                close();
            }
            System.out.println("listener finished!");
        }

        private void close(){
            if(ds != null){
                ds.close();
                ds = null;
            }
        }
         List<ServerInfo> getServerAndClose(){
            done = true;
            close();
            return serverInfoList;
         }
    }


}
