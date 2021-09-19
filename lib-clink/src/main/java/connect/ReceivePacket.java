package connect;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @Description: 接收包的定义
 * @Author: GZK0329
 * @Date: 2021/1/14
 **/

public abstract class ReceivePacket<Stream extends OutputStream, Entity> extends Packet <Stream>{
    /*
    * 定义接收包的最终实体
    * */
    private Entity entity;

    public ReceivePacket(long len) {
        this.length = len;
    }

    public Entity entity(){
        return entity;
    }

    /*
    * 根据流生成实体
    * */
    protected abstract Entity buildEntity(Stream stream);

    /*
    * 先关闭流然后将流的内容转换成实体
    * */
    @Override
    protected void closeStream(Stream stream) throws IOException {
        super.closeStream(stream);
        entity = buildEntity(stream);
    }
}
