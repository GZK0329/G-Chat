package UDPSearch;

import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.Buffer;

/**
 * @Description: socket配置
 * @Author: GZK0329
 * @Date: 2021/4/19
 **/

public  class InitSocket {
    public static void initUDPSocket(DatagramSocket ds) throws SocketException {
        //是否开启广播 param:boolean
        ds.setBroadcast(true);

        //设置收发缓冲区大小
        ds.setReceiveBufferSize(128);
        ds.setSendBufferSize(128);

        //设置端口复用方法setReuseAddress(boolean)
        //使得服务关掉重启时立马可使用该端口，而不是提示端口占用
        //还有一个getReuserAddress(boolean)方法
        //当接受方通过Socket的close()方法关闭Socket时，如果网络上还有发送到这个Socket的数据，
        //那么底层的Socket不会立刻释放本地端口，而是会等待一段时间，确保收到了网络上发送过来的延迟数据，然再释放该端口。
        //Socket接受到延迟数据后，不会对这些数据做任何处理。Socket接受延迟数据的目的是，确保这些数据不会被其他碰巧绑定到同样端口的新进程接收到。
        //客户程序一般采用随机端口，因此会出现两个客户端程序绑定到同样端口的可能性不大。许多服务器都使用固定的端口。
        //当服务器进程关闭后，有可能它的端口还会被占用一段时间，如果此时立刻在同一主机上重启服务器程序，由于端口已经被占用，使得服务器无法绑定到该端口，启动失败。
        //为了确保一个进程被关闭后，及时它还没有释放该端口，同一个主机上的其他进程还可以立刻重用该端口，可以调用Socket的setResuseAddress(true)方法;
        //值得注意的是:socket.setResuseAddress(true)方法必须在Socket还没有绑定到一个本地端口之前调用，否则执行socket.setResuseAddress(true)方法无效
        ds.setReuseAddress(true);

        //设置超时时间
        ds.setSoTimeout(20000);
    }

    public static void initTCPSocket(Socket socket) throws SocketException {

        socket.setReuseAddress(true);

        socket.setReceiveBufferSize(128);
        socket.setSendBufferSize(128);

        socket.setSoTimeout(20000);

        socket.setKeepAlive(true);

        //优先权重
        socket.setPerformancePreferences(1,1,1);
        socket.setTcpNoDelay(true);

    }

}
