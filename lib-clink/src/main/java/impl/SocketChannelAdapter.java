package impl;

import connect.*;
import utils.CloseUtils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description: 处理接收发送
 * @Author: GZK0329
 * @Date: 2021/4/25
 **/

public class SocketChannelAdapter implements Receiver, Sender, Closeable {
    //标识是否已经关闭 channel
    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    private final SocketChannel channel;
    private final IOProvider ioProvider;
    private final OnChannelStatusListener listener;

    private IOArgs.IOArgsEventListener receiveListener;
    private IOArgs.IOArgsEventListener sendListener;

    public SocketChannelAdapter(SocketChannel channel,
                                IOProvider ioProvider,
                                OnChannelStatusListener listener) throws IOException {
        this.channel = channel;
        this.ioProvider = ioProvider;
        this.listener = listener;
        //通道设置为非阻塞
        channel.configureBlocking(false);
    }

    @Override
    public boolean receiveAsync(IOArgs.IOArgsEventListener listener) {
        if (isClosed.get()) {
            System.out.println("current channel is closed!");
        }
        receiveListener = listener;

        return ioProvider.registerInPut(channel, inPutCallBack);
    }

    @Override
    public boolean sendAsync(IOArgs ioArgs, IOArgs.IOArgsEventListener listener) {
        if(isClosed.get()){
            System.out.println("current channel is closed!");
        }

        sendListener = listener;
        outPutCallBack.setAttach(ioArgs);
        return ioProvider.registerOutPut(channel, outPutCallBack);

    }

    private final IOProvider.HandleInPutCallBack inPutCallBack = new IOProvider.HandleInPutCallBack() {
        @Override
        protected void canProviderInPut() {
            if(isClosed.get()){
                return;
            }
            IOArgs args = new IOArgs();
            IOArgs.IOArgsEventListener listener = SocketChannelAdapter.this.receiveListener;
            try {
                if(listener != null && args.read(channel) > 0){
                    listener.onCompleted(args);//读完了 回调函数 通知一下
                }else{
                    throw new Exception("can not read data!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                CloseUtils.close(SocketChannelAdapter.this);
            }
        }
    };

    private final IOProvider.HandleOutPutCallBack outPutCallBack = new IOProvider.HandleOutPutCallBack() {

        @Override
        protected void canProviderOutPut(Object attach){
            if(isClosed.get()){
                return;
            }
            //TODO
            sendListener.onCompleted(null);
        }

    };



    @Override
    public void close() throws IOException {
        if(isClosed.compareAndSet(false, true)){
            //解除注册 回调
            ioProvider.unRegisterInPut(channel);
            ioProvider.unRegisterOutPut(channel);

            CloseUtils.close(channel);

            //回调通知 这个管道已经关闭了
            listener.onChannelClosed(channel);

        }
    }

    //channel关闭时的回调
    public interface OnChannelStatusListener {
        void onChannelClosed(SocketChannel channel);
    }
}
