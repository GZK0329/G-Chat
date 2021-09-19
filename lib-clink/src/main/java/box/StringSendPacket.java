package box;

import connect.SendPacket;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @Description: string转换为packet
 * @Author: GZK0329
 * @Date: 2021/1/14
 **/

public class StringSendPacket extends BytesSendPacket {
    private byte[] buffer;

    public StringSendPacket(String msg) {
        super(msg.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public byte type() {
        return TYPE_MEMORY_STRING;
    }
}
