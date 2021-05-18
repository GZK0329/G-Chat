package connect;

import java.io.IOException;

//接收者
public interface Receiver {

    //设置接收监听
    void setReceiveListener(IOArgs.IOArgsEventListener ioArgsEventListener);

    //异步接收
    boolean receiveAsync(IOArgs args);

    void close() throws IOException;
}
