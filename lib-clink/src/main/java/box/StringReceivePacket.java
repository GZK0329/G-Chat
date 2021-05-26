package box;

import connect.ReceivePacket;

import java.io.*;
import java.util.stream.Stream;

/**
 * @Description: 字符串接收包的定义
 * @Author: GZK0329
 * @Date: 2021/5/14
 **/

public class StringReceivePacket extends AbsByteArrayReceivePacket<String> {

    public StringReceivePacket(long len) {
        super(len);
    }

    @Override
    protected String buildEntity(ByteArrayOutputStream stream) {
        return new String(stream.toByteArray());
    }

    public byte type(){
        return TYPE_MEMORY_STRING;
    }

}
