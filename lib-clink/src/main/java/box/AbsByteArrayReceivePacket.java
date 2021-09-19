package box;

import connect.ReceivePacket;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.OutputStream;

/**
 * @Description: 基础输出包的定义
 * @Author: GZK0329
 * @Date: 2021/1/25
 **/

public abstract class AbsByteArrayReceivePacket<Entity> extends ReceivePacket<ByteArrayOutputStream, Entity> {
    public AbsByteArrayReceivePacket(long len) {
        super(len);
    }

    @Override
    protected ByteArrayOutputStream createStream() {
        return new ByteArrayOutputStream((int) length);
    }
}
