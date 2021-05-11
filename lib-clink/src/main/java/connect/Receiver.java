package connect;

//接收者
public interface Receiver {

    //异步接收
    boolean receiveAsync(IOArgs.IOArgsEventListener listener);
}
