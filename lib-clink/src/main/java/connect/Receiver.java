package connect;

import java.io.IOException;

//接收者
public interface Receiver {
    //设置processor
    void setReceiveListener(IOArgs.IOArgsEventProcessor processor);

    //异步接收
    boolean postReceiveAsync() throws Exception;

}
