package box;

import connect.SendPacket;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @Description: string转换为packet
 * @Author: GZK0329
 * @Date: 2021/5/14
 **/

public class StringSendPacket extends SendPacket<ByteArrayInputStream> {
    private byte[] buffer;

    public StringSendPacket(String msg) {
        this.buffer = msg.getBytes(StandardCharsets.UTF_8);
        this.length = buffer.length;
    }

    @Override
    protected ByteArrayInputStream createStream() {
        return new ByteArrayInputStream(buffer);
    }
}
