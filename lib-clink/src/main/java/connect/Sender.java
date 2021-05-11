package connect;

//发送者
public interface Sender {

    //异步发送
    boolean sendAsync(IOArgs ioArgs, IOArgs.IOArgsEventListener listener);
}
