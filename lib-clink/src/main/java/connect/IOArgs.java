package connect;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @Description: 输入输出的数据的封装
 * @Author: GZK0329
 * @Date: 2021/4/25
 **/

public class IOArgs {
    ByteBuffer buffer = ByteBuffer.allocate(256);

    public int write(SocketChannel socketChannel) throws IOException {
        buffer.clear();
        return socketChannel.write(buffer);
    }

    public int read(SocketChannel socketChannel) throws IOException {
        buffer.clear();
        return socketChannel.read(buffer);
    }

    //buffer转字符串
    public String bufferToString(){
        return new String(buffer.array(), 0, buffer.position() - 1);
    }

    //IO参数 事件监听
    public interface IOArgsEventListener {

        //是否开始
        void onStarted(IOArgs args);

        //是否完成
        void onCompleted(IOArgs args);
    }

}
