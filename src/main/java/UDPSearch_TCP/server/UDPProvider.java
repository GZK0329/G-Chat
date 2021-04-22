package UDPSearch_TCP.server;

import UDPSearch_TCP.constants.TCPConstants;
import UDPSearch_TCP.constants.UDPConstants;

import utils.ByteUtils;
import utils.MyUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/19
 **/

public class UDPProvider {
    private static  Provider PROVIDER_INSTANCE;

    static void start(int port){
        //启动前 如果实例已经存在 则停掉
        stop();
        String sn = UUID.randomUUID().toString();
        Provider provider = new Provider(sn, port);
        provider.start();
        PROVIDER_INSTANCE = provider;
    }
    static void stop(){
        if(PROVIDER_INSTANCE != null){
            //成员方法
            PROVIDER_INSTANCE.exit();
            PROVIDER_INSTANCE = null;
        }
    }

    private static class Provider extends Thread{
        //sn码 设备识别
        private final byte[] sn;

        //端口
        private final int pport;

        //循环标识
        private boolean done = false;
        //声明udp套接字
        private DatagramSocket ds = null;

        final byte[] buffer = new byte[128];

        public Provider(String sn, int port) {
            super();
            this.sn = sn.getBytes(StandardCharsets.UTF_8);
            this.pport = port;
        }

        @Override
        public void run() {
            super.run();

            try {
                //新建套接字 然后监听UDP服务器固定port
                DatagramSocket ds = new DatagramSocket(UDPConstants.PORT_SERVER);
                //新建数据报 以buffer的形式 长度为buffer.length  可以设置为小于等于buffer长度的大小
                DatagramPacket receivePack = new DatagramPacket(buffer, buffer.length);

                while(!done){
                    //接收一个数据包
                    ds.receive(receivePack);

                    //分析数据包中的信息
                    String clientIp = receivePack.getAddress().getHostAddress();//发送者 也就是客户端的ip
                    int port = receivePack.getPort();
                    int dataLen = receivePack.getLength();
                    byte[] data = receivePack.getData();
                    boolean isValid = dataLen > UDPConstants.HEADER.length + 2 + 4 &&
                            ByteUtils.startWith(data, UDPConstants.HEADER);

                    System.out.println("收到的数据包中包含 客户端IP:" + clientIp +
                            "端口:"+port+
                            "是否有效:"+ isValid);
                    if(!isValid){
                        //如果无效
                        continue;
                    }

                    int index = UDPConstants.HEADER.length;
                    short cmd = (short)((data[index++] << 8) | (data[index++] & 0xFF));
                    int responsePort = MyUtil.byteArrayToInt(
                            Arrays.copyOfRange(data, index, index + 4)//左闭右开原则
                    );//解析出了回送端口

                    //准备数据包
                    if(cmd == 1 && responsePort >= 0){
                        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                        byteBuffer.put(UDPConstants.HEADER);
                        byteBuffer.putShort((short) 2);//回送的包指令设为2
                        byteBuffer.putInt(pport);//把服务端的端口回送
                        byteBuffer.put(sn);
                        int len = byteBuffer.position();
                        System.out.println("准备数据包 发往:" + receivePack.getAddress() +"  "+ responsePort);
                        DatagramPacket responsePacket = new DatagramPacket(buffer, len, receivePack.getAddress(), responsePort);
                        //发送数据包 回客户端
                        ds.send(responsePacket);
                    }
                }


            } catch (Exception Ignored) {
            }


        }

        //关套接字
        private void close(){
            if(ds != null){
                ds.close();
                ds = null;
            }
        }

        //退出循环 并关掉套接字
         void exit(){
            done = true;
            close();
        }
    }
}
