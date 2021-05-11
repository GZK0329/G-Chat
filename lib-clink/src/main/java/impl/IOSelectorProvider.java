package impl;

import connect.IOProvider;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description: 实现IOProvider 通过Selector
 * @Author: GZK0329
 * @Date: 2021/4/25
 **/

public class IOSelectorProvider implements IOProvider {
    //关闭
    private AtomicBoolean isClosed = new AtomicBoolean(false);


    @Override
    public boolean registerInPut(SocketChannel channel, HandleInPutCallBack inPutCallBack) {
        return false;
    }

    @Override
    public boolean registerOutPut(SocketChannel channel, HandleOutPutCallBack outPutCallBack) {
        return false;
    }

    @Override
    public void unRegisterInPut(SocketChannel channel) {

    }

    @Override
    public void unRegisterOutPut(SocketChannel channel) {

    }

    @Override
    public void close() throws IOException {

    }
}
