package connect;

/**
 * @Description: 发送包的定义
 * @Author: GZK0329
 * @Date: 2021/5/14
 **/

public abstract class SendPacket extends Packet {
    //是否取消了
    private boolean isCanceled;

    //抽象方法获取数据
    public abstract byte[] bytes();

    public boolean isCanceled() {
        return isCanceled;
    }
}
