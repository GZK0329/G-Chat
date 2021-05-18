package connect;

import java.io.Closeable;
import java.io.IOException;

/**
 * @Description: 数据包的定义
 * @Author: GZK0329
 * @Date: 2021/5/14
 **/

public class Packet implements Closeable {
    protected byte type;
    protected int length;

    public byte type() {
        return type;
    }

    public int length() {
        return length;
    }

    @Override
    public void close() throws IOException {

    }
}
