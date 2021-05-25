package connect;

import java.io.Closeable;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * @Description: 数据包的定义
 * @Author: GZK0329
 * @Date: 2021/5/14
 **/

public abstract class Packet <T extends Closeable>  implements Closeable {
    protected byte type;
    protected long length;
    private T stream;
    protected abstract T createStream();

    protected void closeStream(T stream) throws IOException {
            stream.close();
    }

    public final T open() {
        if (stream == null) {
            stream = createStream();
        }
        return stream;
    }

    public final void close() throws IOException {
        if (stream != null) {
            closeStream(stream);
            stream = null;
        }
    }

    public byte type() {
        return type;
    }

    public long length() {
        return length;
    }
}
