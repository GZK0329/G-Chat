package box;

import connect.ReceivePacket;
import connect.SendPacket;

import java.io.ByteArrayOutputStream;

/**
 * @Description: byte数组接收包的定义
 * @Author: GZK0329
 * @Date: 2021/5/
 **/

public class BytesReceivePacket extends AbsByteArrayReceivePacket<byte[]> {

    public BytesReceivePacket(long len) {
        super(len);
    }

    /*
    * 类型
    * */
    public byte type() {
        return TYPE_MEMORY_BYTES;
    }

    /*
    * 生成实体
    * */
    @Override
    protected byte[] buildEntity(ByteArrayOutputStream stream) {
        return stream.toByteArray();
    }
}
