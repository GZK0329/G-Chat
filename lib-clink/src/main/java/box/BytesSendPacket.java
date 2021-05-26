package box;

import connect.SendPacket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @Description: byte发送包的定义
 * @Author: GZK0329
 * @Date: 2021/5/25
 **/

public class BytesSendPacket extends SendPacket<ByteArrayInputStream> {
    private final byte[] bytes;

    public BytesSendPacket(byte[] bytes) {
        this.bytes = bytes;
        this.length = bytes.length;
    }

    public byte type(){
        return TYPE_MEMORY_BYTES;
    }

    @Override
    protected ByteArrayInputStream createStream() {
        return new ByteArrayInputStream(bytes);
    }
}
