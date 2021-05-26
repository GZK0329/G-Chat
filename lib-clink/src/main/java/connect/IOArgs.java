package connect;

import com.sun.corba.se.spi.ior.Writeable;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.DuplicateFormatFlagsException;

/**
 * @Description: 输入输出的数据的封装
 * @Author: GZK0329
 * @Date: 2021/4/25
 **/

public class IOArgs {
    private int limit = 256;
    ByteBuffer buffer = ByteBuffer.allocate(256);

    /*
    * 从buffer中读取数据
    * */
    public int readFrom(ReadableByteChannel channel) throws Exception {
        startWriting();
        int byteProduced = 0;
        while(buffer.hasRemaining()){
            int len = channel.read(buffer);
            if(len < 0){
                throw new EOFException();
            }
            byteProduced += len;
        }
        finishWriting();
        return byteProduced;
    }

    /*
     * 向buffer中写入数据
     * */
    public int writeTo(WritableByteChannel channel) throws Exception {

        int byteProduced = 0;
        while(buffer.hasRemaining()){
            int len = channel.write(buffer);
            if(len < 0){
                throw new EOFException();
            }
            byteProduced += len;
        }

        return byteProduced;
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
        startWriting();
        buffer.putInt(total);
        finishWriting();
    }
    public int readLength() {
        return buffer.getInt();
    }

    public int capacity() {
        return buffer.capacity();
    }

    /*
    * IOArgs提供者 处理者;数据的生产者 消费者
    * */
    public interface IOArgsEventProcessor {
        /*
        * 提供一份IOArgs
        * */
        IOArgs provideIOArgs();

        /*
        * 消费失败的通知并抛出异常
        * */
        void onConsumeFailed(IOArgs ioArgs, Exception e);

        /*
        * 消费完成的通知
        * */
        void onConsumeCompleted(IOArgs ioArgs) ;

    }

}
