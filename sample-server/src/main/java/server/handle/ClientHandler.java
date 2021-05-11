package server.handle;

import utils.CloseUtils;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/21
 **/

public class ClientHandler {

    private SocketChannel socketChannel;

    private boolean flag = true;
    private final ClientReaderHandler readerHandler;
    private final ClientWriteHandler writeHandler;
    private final ClientHandlerCallBack clientHandlerCallBack;
    private final String clientInfo;
    private ByteBuffer byteBuffer;

    public String getClientInfo() {
        return clientInfo;
    }

    public ClientHandler(SocketChannel socketChannel, ClientHandlerCallBack clientHandlerCallBack) throws IOException {

        this.socketChannel = socketChannel;

        Selector readerSelector = Selector.open();
        socketChannel.register(readerSelector, SelectionKey.OP_READ);

        Selector writeSelector = Selector.open();
        socketChannel.register(writeSelector, SelectionKey.OP_WRITE);

        this.readerHandler = new ClientReaderHandler(readerSelector);
        this.writeHandler = new ClientWriteHandler(writeSelector);
        this.clientHandlerCallBack = clientHandlerCallBack;
        this.clientInfo = socketChannel.getLocalAddress().toString();
        this.byteBuffer = ByteBuffer.allocate(256);
    }

    public void send(String str) {
        //发送
        writeHandler.send(str);
    }

    public void readToPrint() {
        //启动读线程
        readerHandler.start();
    }

    public void exit()  {
        readerHandler.exit();
        writeHandler.exit();
        CloseUtils.close(socketChannel);

        try {
            System.out.println("客户端已经退出" + socketChannel.getLocalAddress().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exitBySelf() {
        exit();
        clientHandlerCallBack.onSelfClosed(this);
    }

    public interface ClientHandlerCallBack {
        void onSelfClosed(ClientHandler handler);

        void onNewMessageArrived(ClientHandler handler, String msg);
    }


    private class ClientReaderHandler extends Thread {
        private boolean done = false;//结束标志

        private Selector readSelector;

        public ClientReaderHandler(Selector readSelector) {
            this.readSelector = readSelector;
        }

        @Override
        public void run() {
            super.run();
            try {
                do {
                    if (readSelector.select() == 0) {
                        if (done) {
                            return;
                        }
                        continue;
                    }
                    Iterator<SelectionKey> readIterator = readSelector.selectedKeys().iterator();
                    while (readIterator.hasNext()) {
                        SelectionKey key = readIterator.next();
                        readIterator.remove();

                        if (key.isReadable()) {
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            byteBuffer.clear();
                            int read = socketChannel.read(byteBuffer);
                            if (read > 0) {
                                String str = new String(byteBuffer.array(), 0, read + 1);
                                //打印数据
                                //System.out.println(str);
                                clientHandlerCallBack.onNewMessageArrived(ClientHandler.this, str);
                            } else {
                                System.out.println("当前客户端已经无法读取到数据");
                                ClientHandler.this.exitBySelf();
                                break;
                            }
                        }
                    }
                } while (!done);

            } catch (Exception e) {
                if (!done) {
                    System.out.println("连接异常 断开!");
                    ClientHandler.this.exitBySelf();
                }
            } finally {
                CloseUtils.close(readSelector);
            }

        }

        void exit() {
            done = true;
            CloseUtils.close(readSelector);
        }

    }

    private class ClientWriteHandler {
        private boolean done = false;
        private final ExecutorService executorService;
        private final Selector writeSelector;

        public ClientWriteHandler(Selector writeSelector) {
            this.executorService = Executors.newSingleThreadExecutor();
            this.writeSelector = writeSelector;
        }

        //发送str
        void send(String str) {
            executorService.execute(new WriteRunnable(str));
        }

        public void exit() {
            done = true;
            CloseUtils.close(writeSelector);
        }

        class WriteRunnable implements Runnable {

            private final String msg;

            public WriteRunnable(String msg) {
                this.msg = msg + '\n';
            }

            @Override
            public void run() {
                if (ClientWriteHandler.this.done) {
                    return;
                }

                byteBuffer.clear();
                byteBuffer.put(msg.getBytes());
                // 反转操作, 重点
                byteBuffer.flip();

                while (!done && byteBuffer.hasRemaining()) {
                    try {
                        int len = socketChannel.write(byteBuffer);
                        // len = 0 合法
                        if (len < 0) {
                            System.out.println("客户端已无法发送数据！");
                            ClientHandler.this.exitBySelf();
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}


