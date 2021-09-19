package connect;

import java.io.Closeable;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * @Description: 数据包的定义
 * @Author: GZK0329
 * @Date: 2021/1/14
 **/

public abstract class Packet <Stream extends Closeable>  implements Closeable {
    //Bytes 类型 1
    public static final byte TYPE_MEMORY_BYTES = 1;
    //String 类型 2
    public static final byte TYPE_MEMORY_STRING = 2;
    //File 类型 3
    public static final byte TYPE_STREAM_FILE = 3;
    //长链接流 类型 4
    public static final byte TYPE_STREAM_DIRECT = 4;

    protected long length;
    private Stream stream;

    /*
    * 创建流
    * */
    protected abstract Stream createStream();

    protected void closeStream(Stream stream) throws IOException {
            stream.close();
    }

    public final Stream open() {
        if (stream == null) {
            stream = createStream();
        }
        return stream;
    }

    public abstract byte type();

    public final void close() throws IOException {
        if (stream != null) {
            closeStream(stream);
            stream = null;
        }
    }

    public long length() {
        return length;
    }

    /*
    *  头部的额外信息,用于携带额外的校验信息
    * */
    public byte[] headerInfo(){
        return null;
    }
}

