package connect;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/5/14
 **/

public interface ReceiveDispatcher {
    /*
    * 开始接收
    * */
    void start();

    /*
    * 停止接受
    * */
    void stop();

    void close();
    /*接收到数据之后 通知回调*/
    interface receivePacketCallBack{
        void onReceivePacketCompleted(ReceivePacket packet);
    }

}
