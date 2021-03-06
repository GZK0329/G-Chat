package connect;

import java.io.Closeable;
import java.nio.channels.SocketChannel;

//IO的提供 注册输入输出 取消注册
public interface IOProvider extends Closeable {

    boolean registerInPut(SocketChannel channel, HandleInPutCallBack inPutCallBack);

    boolean registerOutPut(SocketChannel channel, HandleOutPutCallBack outPutCallBack);

    void unRegisterInPut(SocketChannel channel);

    void unRegisterOutPut(SocketChannel channel);

    abstract class HandleInPutCallBack implements Runnable{
        @Override
        public void run() {
            canProviderInPut();
        }
        protected abstract void canProviderInPut();
    }

    abstract class HandleOutPutCallBack implements Runnable{
        @Override
        public void run() {
            canProviderOutPut();
        }
        protected abstract void canProviderOutPut();
    }
}
