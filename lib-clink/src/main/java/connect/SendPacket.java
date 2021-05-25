package connect;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: 发送包的定义
 * @Author: GZK0329
 * @Date: 2021/5/14
 **/

public abstract class SendPacket<T extends InputStream> extends Packet<T> {

    //是否取消了
    private boolean isCanceled;

    public boolean isCanceled() {
        return isCanceled;
    }

}

