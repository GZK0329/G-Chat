package connect;

import java.io.EOFException;
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
    private int limit = 256;

    /*
    * 从buffer中读取数据
    * */
    public int readFrom(byte[] bytes, int offset){
        int size = Math.min(bytes.length - offset, buffer.remaining());
        buffer.put(bytes, offset, size);
        return size;
    }

    /*
     * 向buffer中写入数据
     * */
    public int writeTo(byte[] bytes, int offset){
        int size = Math.min(bytes.length - offset, buffer.remaining());
        buffer.get(bytes, offset, size);
        return size;
    }

    /*
     * 写入数据到socketChannel
     * */
    public int writeTo(SocketChannel socketChannel) throws IOException {
        int byteProduced = 0;
        while(buffer.hasRemaining()){
            int len = socketChannel.write(buffer);
            if(len < 0){
                throw new EOFException();
            }
            byteProduced += len;
        }
        return byteProduced;
    }

    /*
    * 从socketChannel中读取数据
    * */
    public int readFrom(SocketChannel socketChannel) throws IOException {
        startWriting();
        int byteProduced = 0;
        while(buffer.hasRemaining()){
            int len = socketChannel.read(buffer);
            if(len < 0){
                throw new EOFException();
            }
            byteProduced += len;
        }
        finishWriting();
        return byteProduced;
    }

    /*
    * 开始写入数据到socketChannel
    * */
    public void startWriting(){
        buffer.clear();
        buffer.limit(limit);
    }

    /*
    * 结束写入数据到socketChannel
    * */
    public void finishWriting(){
        buffer.flip();
    }

    /*
    * 设置单次写的容纳区间 limit
    * */
    public void limit (int limit){
        this.limit = limit;
    }

    //buffer转字符串
    public String bufferToString(){
        return new String(buffer.array(), 0, buffer.position() - 1);
    }

    public void writeLength(int total) {
        buffer.putInt(total);
    }
    public int readLength() {
        return buffer.getInt();
    }

    public int capacity() {
        return buffer.capacity();
    }

    //IO参数 事件监听
    public interface IOArgsEventListener {

        //是否开始
        void onStarted(IOArgs args);

        //是否完成
        void onCompleted(IOArgs args);
    }

}
