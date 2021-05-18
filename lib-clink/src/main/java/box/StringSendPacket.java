package box;

import connect.SendPacket;

import java.nio.charset.StandardCharsets;

/**
 * @Description: string转换为packet
 * @Author: GZK0329
 * @Date: 2021/5/14
 **/

public class StringSendPacket extends SendPacket {
    private byte[] buffer;
    public StringSendPacket(String msg) {
        this.buffer = msg.getBytes(StandardCharsets.UTF_8);
        this.length = buffer.length;
    }

    @Override
    public byte[] bytes() {
        return buffer;
    }
}
