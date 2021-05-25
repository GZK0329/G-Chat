package connect;

/**
 * @Description: 发送调度 缓存需要发送的数据 按顺序发送
 * @Author: GZK0329
 * @Date: 2021/5/14
 **/

public interface SendDispatcher {

    /*
    * 发送
    * */
    void send(SendPacket packet) throws Exception;

    /*
    * 取消发送
    * */
    void cancel(SendPacket packet);

    void close();
}
