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

    private IOArgs.IOArgsEventProcessor receiveProcessor;
    private IOArgs.IOArgsEventProcessor sendProcessor;

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
    public void setReceiveListener(IOArgs.IOArgsEventProcessor processor) {
        receiveProcessor = processor;
    }
    @Override
    public void setSendListener(IOArgs.IOArgsEventProcessor processor) {
        sendProcessor = processor;
    }

    @Override
    public boolean postSendAsync() {
        if (isClosed.get()) {
            System.out.println("current channel is closed!");
        }
        //IOArgs args = sendProcessor.provideIOArgs();
        return ioProvider.registerOutPut(channel, outPutCallBack);
    }

    @Override
    public boolean postReceiveAsync() {
        if (isClosed.get()) {
            System.out.println("current channel is closed!");
        }
        return ioProvider.registerInPut(channel, inPutCallBack);
    }

    private final IOProvider.HandleInPutCallBack inPutCallBack = new IOProvider.HandleInPutCallBack() {
        @Override
        protected void canProviderInPut() {
            if (isClosed.get()) {
                return;
            }
            IOArgs.IOArgsEventProcessor processor = receiveProcessor;
            IOArgs args = processor.provideIOArgs();
            try {
                if (args.readFrom(channel) > 0) {
                    processor.onConsumeCompleted(args);//读完了 回调函数 通知一下
                } else {
                    processor.onConsumeFailed(args,new Exception("没读到数据"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                CloseUtils.close(SocketChannelAdapter.this);
            }
        }
    };

    private final IOProvider.HandleOutPutCallBack outPutCallBack = new IOProvider.HandleOutPutCallBack() {

        @Override
        protected void canProviderOutPut() {
            if (isClosed.get()) {
                return;
            }
            IOArgs.IOArgsEventProcessor processor = sendProcessor;
            IOArgs args = processor.provideIOArgs();

            try {
                if (args.writeTo(channel) > 0) {
                    processor.onConsumeCompleted(args);//读完了 回调函数 通知一下
                } else {
                    processor.onConsumeFailed(args, new IOException("读不到数据！"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                CloseUtils.close(SocketChannelAdapter.this);
            }

        }

    };

    @Override
    public void close() {
        if (isClosed.compareAndSet(false, true)) {
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
