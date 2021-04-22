package UDPSearch.server;

import UDPSearch.constants.*;
import utils.ByteUtils;
import utils.MyUtil;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/17
 **/

public class ServerProvider {

    private static  Provider PROVIDER_INSTANCE;

    static void start(int port){
        stop();
        String sn = UUID.randomUUID().toString();
        Provider provider = new Provider(sn, port);
        provider.start();
        PROVIDER_INSTANCE = provider;
    }
    static void stop(){
        if(PROVIDER_INSTANCE != null){
            PROVIDER_INSTANCE.exit();
            PROVIDER_INSTANCE = null;
        }
    }

    private static class Provider extends Thread{
        //sn码 设备识别
        private final byte[] sn;

        //端口
        private final int port;

        //循环标识
        private boolean done = false;
        //声明udp套接字
        private DatagramSocket ds = null;

        final byte[] buffer = new byte[128];

        public Provider(String sn, int port) {
            super();
            this.sn = sn.getBytes(StandardCharsets.UTF_8);
            this.port = port;
        }

        @Override
        public void run() {

            super.run();
            System.out.println("ServerProvider start!");

            try {
                //创建udp套接字 需要设置监听端口 不然默认为0
                ds = new DatagramSocket(UDPConstants.PORT_SERVER);
                //设置套接字
                //InitSocket.initUDPSocket(ds);

                //新建一个数据包用于接收数据 Param:data,data.length ==> this(this, 0, this.length)
                DatagramPacket recvDp = new DatagramPacket(buffer, buffer.length);

                while(!done){
                    //读写read write 循环
                    //接收数据
                    ds.receive(recvDp);

                    String clientIP = recvDp.getAddress().getHostAddress();
                    int clientPort = recvDp.getPort();
                    int clientDataLength = recvDp.getLength();
                    byte[] clientData = recvDp.getData();

                    boolean isValid = clientDataLength >= (UDPConstants.HEADER.length + 2 + 4)
                            && ByteUtils.startWith(clientData, UDPConstants.HEADER);
                    System.out.println("server receive data from client IP:" + clientIP + "\n" +
                            "Port:" + clientPort + "\n" +
                            "DataisValid:" + isValid);
                    if(!isValid){
                        //无效
                        continue;
                    }

                    //解析UDP数据包
                    int index = UDPConstants.HEADER.length;
                    //去掉头 接下来两个字节是命令
                    short cmd = (short) ((clientData[index++] << 8) | (clientData[index++] & 0xFF));
                    //解析源端口  UDP数据包 源端口 目标端口 ...形式
                    //调用方法的byte[] => int
                    int responsePort = MyUtil.byteArrayToInt(
                            Arrays.copyOfRange(clientData,index, index + 4)
                    );
                    System.out.println("回送端口Port为:" + responsePort);

                    //cmd为1代表输入 cmd为2代表输出
                    if(cmd == 1 && responsePort > 0){
                        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                        byteBuffer.put(UDPConstants.HEADER);
                        byteBuffer.putShort((short) 2);
                        byteBuffer.putInt(port);
                        byteBuffer.put(sn);
                        int len = byteBuffer.position();
                        DatagramPacket responsePacket = new DatagramPacket(buffer, len, recvDp.getAddress(), responsePort);
                        System.out.println("port: "+ port);
                        System.out.println("responsePort: "+ responsePort);
                        ds.send(responsePacket);
                        System.out.println("ServerProvider response to:" +  clientIP + "\tport:" + responsePort + "\tdataLen:" + len);
                    }else {
                        System.out.println("cmd :" + cmd + "responsePort:" + responsePort + "有问题!");
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Server is finished!");

        }
        private void close(){
            if(ds != null){
                ds.close();
                ds = null;
            }
        }
        void exit(){
            done = true;
            close();
        }
    }
}
