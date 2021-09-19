package client;


import constants.ServerInfo;
import constants.UDPConstants;
import utils.ByteUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/1/19
 **/

public class ClientSearcher {

    //广播 == 搜索
    //监听

    //客户端要回送的端口
    private static final int LISTEN_PORT = UDPConstants.PORT_CLIENT_RESPONSE;

    //寻找服务器 返回服务器信息
    public static ServerInfo searchServer(int timeout)  {
        CountDownLatch receiveLatch =  new CountDownLatch(1);//栅栏数值为1
        Listener listener = null;
        try {
            listener = listen(receiveLatch);
            sendBroadCast();
            receiveLatch.await(timeout, TimeUnit.MILLISECONDS);//将线程阻塞直到receiveLatch降为0或者设定阻塞时间过去
        } catch (Exception Ignored) {
        }

        // 完成
        if (listener == null) {
            return null;
        }
        List<ServerInfo> devices = listener.getServerAndClose();
        //System.out.println(devices.size());
        if (devices.size() > 0) {
            //System.out.println(devices.size());
            return devices.get(0);
        }
        return null;

    }
    static void sendBroadCast() throws IOException {
        System.out.println("广播开始");

        //建立套接字 端口自动分配就好 不重要
        DatagramSocket ds = new DatagramSocket();

        //建立数据包
        ByteBuffer byteBuffer = ByteBuffer.allocate(128);

        byteBuffer.put(UDPConstants.HEADER);
        byteBuffer.putShort((short) 1);

        byteBuffer.putInt(LISTEN_PORT);

        int len = byteBuffer.position();//末尾位置

        DatagramPacket requestPacket = new DatagramPacket(byteBuffer.array(), len + 1);
        System.out.println("请求数据包的发送地址"+InetAddress.getByName("255.255.255.255")+" "+UDPConstants.PORT_SERVER);

        requestPacket.setAddress(InetAddress.getByName("255.255.255.255"));
        requestPacket.setPort(UDPConstants.PORT_SERVER);

        ds.send(requestPacket);
        ds.close();

        System.out.println("广播结束");
    }

    static Listener listen(CountDownLatch receiveLatch) throws InterruptedException {
        System.out.println("UDP搜索 监听开启");
        CountDownLatch startLatch = new CountDownLatch(1);
        Listener listener = new Listener(LISTEN_PORT, startLatch, receiveLatch);
        listener.start();
        startLatch.await();
        return listener;
    }


    //监听类 线程
    private static class Listener extends Thread{
        private int listenPort;
        private CountDownLatch startLatch;
        private CountDownLatch receiveLatch;
        private List<ServerInfo> serverInfoList = new ArrayList<>();
        private int minLen = UDPConstants.HEADER.length + 2 + 4;
        private byte[] buffer = new byte[128];
        private DatagramSocket ds = null;
        private boolean done = false;

        public Listener(int listenPort, CountDownLatch startLatch, CountDownLatch receiveLatch) {
            this.listenPort = listenPort;
            this.startLatch = startLatch;
            this.receiveLatch = receiveLatch;
        }

        @Override
        public void run() {
            super.run();

            startLatch.countDown();

            try {
                //建立套接字 监听回送端口
                ds = new DatagramSocket(listenPort);

                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

                while(!done){
                    ds.receive(receivePacket);

                    String ip = receivePacket.getAddress().getHostAddress();
                    int port = receivePacket.getPort();
                    int dataLen = receivePacket.getLength();
                    byte[] data = receivePacket.getData();
                    boolean isValid = dataLen > minLen &&
                            ByteUtils.startWith(data, UDPConstants.HEADER);

                    if(!isValid){
                        //无效
                        continue;
                    }
                    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, UDPConstants.HEADER.length, dataLen);
                    short cmd = byteBuffer.getShort();//获取命令
                    int serverPort = byteBuffer.getInt();//数据报内容中的回送端口 也就是服务器端口

                    if(cmd != 2 || serverPort < 0){
                        continue;
                    }

                    String sn = new String(buffer, minLen, dataLen-minLen);
                    System.out.println(sn + "端口:" + serverPort + "IP:" + ip );

                    ServerInfo serverInfo = new ServerInfo(sn, serverPort, ip);

                    //System.out.printf(serverInfo.toString());
                    serverInfoList.add(serverInfo);
                    receiveLatch.countDown();
                }
            } catch (Exception Ignored) {
            }finally {
                close();
            }
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
