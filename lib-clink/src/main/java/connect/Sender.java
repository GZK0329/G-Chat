package connect;

//发送者
public interface Sender {
    //设置发送的回调
    void setSendListener(IOArgs.IOArgsEventProcessor processor);

    //异步发送
    boolean postSendAsync() throws Exception;

}
