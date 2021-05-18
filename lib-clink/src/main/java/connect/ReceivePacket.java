package connect;

/**
 * @Description: 接收包的定义
 * @Author: GZK0329
 * @Date: 2021/5/14
 **/

public abstract class ReceivePacket extends Packet {

    public abstract void save(byte[] bytes, int count);

}
