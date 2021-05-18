package box;

import connect.ReceivePacket;

/**
 * @Description: 字符串接收包的定义
 * @Author: GZK0329
 * @Date: 2021/5/14
 **/

public class StringReceivePacket extends ReceivePacket {
    private byte[] buffer;
    private int position;

    public StringReceivePacket(int len) {
        this.buffer = new byte[len];
        length = len;
    }

    @Override
    public void save(byte[] bytes, int count) {
        System.arraycopy(bytes, 0, buffer, position, count);
        position += count;
    }

    public String string() {
        return new String(buffer);
    }
}
