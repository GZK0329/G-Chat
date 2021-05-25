package box;

import connect.ReceivePacket;

import java.io.*;
import java.util.stream.Stream;

/**
 * @Description: 字符串接收包的定义
 * @Author: GZK0329
 * @Date: 2021/5/14
 **/

public class StringReceivePacket extends ReceivePacket<ByteArrayOutputStream> {
    private String string;

    public StringReceivePacket(int len) {
        length = len;
    }


    public String string() {
        return string;
    }

    @Override
    protected void closeStream(ByteArrayOutputStream stream) throws IOException {
        super.closeStream(stream);
        string = new String( stream.toByteArray());
    }

    @Override
    protected ByteArrayOutputStream createStream() {
        return new ByteArrayOutputStream((int)length);
    }
}
