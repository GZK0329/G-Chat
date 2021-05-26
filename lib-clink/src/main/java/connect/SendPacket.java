package connect;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: 发送包的定义
 * @Author: GZK0329
 * @Date: 2021/5/14
 **/

public abstract class SendPacket<T extends InputStream> extends Packet<T> {

    /*
    * 是否发送了
    * true  未发送完成
    * false 已发送完成
    * */
    private boolean isCanceled;

    public boolean isCanceled() {
        return isCanceled;
    }

    /*
    * 用于取消发送标志
    * */
    public void cancel(){
        isCanceled = true;
    }
}

