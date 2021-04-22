package server.handle;



import utils.CloseUtils;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/21
 **/

public class ClientHandler {

    private boolean flag = true;
    private final Socket client;
    private final ClientReaderHandler readerHandler;
    private final ClientWriteHandler writeHandler;
    private final CloseNotify closeNotify;


    public ClientHandler(Socket client, CloseNotify closeNotify) throws IOException {
        this.readerHandler = new ClientReaderHandler(client.getInputStream());
        this.writeHandler = new ClientWriteHandler(client.getOutputStream());
        this.client = client;
        this.closeNotify = closeNotify;
    }

    public void send(String str) {
        //发送
        writeHandler.send(str);
    }

    public void readToPrint() {
        readerHandler.start();
    }

    public void exit() {
        readerHandler.exit();
        writeHandler.exit();
        CloseUtils.close(client);
        System.out.println("客户端已经退出" + client.getInetAddress() + " " + client.getPort());

    }

    public void exitBySelf() {
        exit();
        closeNotify.onSelfClosed(this);
    }

    public interface CloseNotify {
        void onSelfClosed(ClientHandler handler);
    }


    private class ClientReaderHandler extends Thread {
        private boolean done = false;//结束标志
        private InputStream inputStream;//输入流

        public ClientReaderHandler() {

        }

        public ClientReaderHandler(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            super.run();
            try {
                //包装城InputStreamReader
                InputStreamReader inputReader = new InputStreamReader(inputStream);
                //包装成BufferReader
                BufferedReader socketInput = new BufferedReader(inputReader);
                String str;
                do {
                    //获取到内容
                    str = socketInput.readLine();

                    if (str == null) {
                        System.out.println("无法获取数据");
                        ClientHandler.this.exitBySelf();
                    }
                    //打印数据
                    System.out.println(str);

                } while (!done);

            } catch (Exception e) {
                if (!done) {
                    System.out.println("连接异常 断开!");
                    ClientHandler.this.exitBySelf();
                }
            } finally {
                CloseUtils.close(inputStream);
            }

        }


        void exit() {
            done = true;
            CloseUtils.close(inputStream);
        }

    }

    private class ClientWriteHandler {
        private boolean done = false;
        private final PrintStream printStream;
        private final ExecutorService executorService;

        public ClientWriteHandler(OutputStream outputStream) {
            this.printStream = new PrintStream(outputStream);
            this.executorService = Executors.newSingleThreadExecutor();
        }

        //发送str
        void send(String str) {
            executorService.execute(new WriteRunnable(str));
        }

        public void exit() {
            done = true;
            CloseUtils.close(printStream);
        }

        class WriteRunnable implements Runnable {

            private final String msg;

            public WriteRunnable(String msg) {
                this.msg = msg;
            }

            @Override
            public void run() {

                if (ClientWriteHandler.this.done == true) {
                    return;
                } else {
                    ClientWriteHandler.this.printStream.println(msg);
                }
            }
        }


    }

}
