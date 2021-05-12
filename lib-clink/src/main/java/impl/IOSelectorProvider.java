package impl;

import connect.IOProvider;
import utils.CloseUtils;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description: 实现IOProvider 通过Selector
 * @Author: GZK0329
 * @Date: 2021/4/25
 **/

public class IOSelectorProvider implements IOProvider {
    //关闭 原子化标志
    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    private final AtomicBoolean inRegInput = new AtomicBoolean(false);
    private final AtomicBoolean inRegOutput = new AtomicBoolean(false);

    private final Selector writeSelector;
    private final Selector readSelector;

    private final HashMap<SelectionKey, Runnable> inputCallBackMap = new HashMap<>();
    private final HashMap<SelectionKey, Runnable> outputCallBackMap = new HashMap<>();

    private final ExecutorService inputThreadPool;
    private final ExecutorService outputThreadPool;

    public IOSelectorProvider() throws IOException {
        this.writeSelector = Selector.open();
        this.readSelector = Selector.open();
        this.inputThreadPool = Executors.newFixedThreadPool(4,
                new IOProviderThreadFactory("IOProvider-Input-Thread-"));
        this.outputThreadPool = Executors.newFixedThreadPool(4,
                new IOProviderThreadFactory("IOProvider-Output-Thread-"));

        startWrite();
        startRead();
    }

    private void startRead() {
        Thread thread = new Thread("Connect IOProvider ReadSelector Thread") {
            @Override
            public void run() {
                super.run();
                while (!isClosed.get()) {
                    try {
                        if (readSelector.select() == 0) {
                            waitSelection(inRegInput);
                            continue;
                        }
                        Set<SelectionKey> selectionReadKeys = readSelector.selectedKeys();
                        for (SelectionKey selectionReadKey : selectionReadKeys) {
                            if (selectionReadKey.isValid()) {
                                handleSelectionRead(selectionReadKey, SelectionKey.OP_READ, inputCallBackMap, inputThreadPool);
                            }
                        }
                        selectionReadKeys.clear();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    private void startWrite() {
        Thread thread = new Thread("Connect IOProvider WriteSelector Thread") {
            @Override
            public void run() {
                super.run();
                while (!isClosed.get()) {
                    try {
                        if (writeSelector.select() == 0) {
                            waitSelection(inRegOutput);
                            continue;
                        }
                        Set<SelectionKey> selectionWriteKeys = writeSelector.selectedKeys();
                        for (SelectionKey selectionWriteKey : selectionWriteKeys) {
                            if (selectionWriteKey.isValid()) {
                                handleSelectionWrite(selectionWriteKey, SelectionKey.OP_WRITE, outputCallBackMap, outputThreadPool);
                            }
                        }
                        selectionWriteKeys.clear();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        };
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    private void handleSelectionRead(SelectionKey selectionReadKey, int opRead,
                                     HashMap<SelectionKey, Runnable> inputCallBackMap,
                                     ExecutorService inputThreadPool) {
        //取消对keyOps的监听
        selectionReadKey.interestOps(selectionReadKey.readyOps() & ~opRead);

        Runnable runnable = inputCallBackMap.get(selectionReadKey);
        if (runnable != null && !inputThreadPool.isShutdown()) {
            //异步调度 执行任务
            inputThreadPool.execute(runnable);
        }
    }

    private void handleSelectionWrite(SelectionKey selectionWriteKey, int opWrite,
                                      HashMap<SelectionKey, Runnable> outputCallBackMap,
                                      ExecutorService outputThreadPool) {
        //取消对keyOps的监听
        selectionWriteKey.interestOps(selectionWriteKey.readyOps() & ~opWrite);

        Runnable runnable = outputCallBackMap.get(selectionWriteKey);
        if (runnable != null && !outputThreadPool.isShutdown()) {
            //异步调度 执行任务
            outputThreadPool.execute(runnable);
        }

    }

    private void waitSelection(final AtomicBoolean locker){

        synchronized (locker){
            if(locker.get()){
                try {
                    locker.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static SelectionKey register(SocketChannel channel, Selector selector,
                          int registerOps, AtomicBoolean locker,
                          HashMap<SelectionKey, Runnable> map,
                          Runnable runnable) {

        synchronized (locker) {
            //设置锁的状态
            locker.set(true);
            try {
                //唤醒selector 解除select()状态(阻塞)
                selector.wakeup();
                SelectionKey key = null;
                if (channel.isRegistered()) {
                    //返回该channel在Selector上的注册关系所对应的SelectionKey。若无注册关系，返回null
                    key = channel.keyFor(selector);
                    if(key != null){
                        key.interestOps(key.readyOps() | registerOps);
                    }
                }
                if(key == null){
                    //没注册 就注册
                    key = channel.register(selector, registerOps);
                    map.put(key, runnable);
                }
                return key;
            } catch (Exception e){
                return null;
            }finally {
                locker.set(false);
                try{
                    locker.notify();
                }catch (Exception Ignored){

                }
            }
        }
    }

    private static void unRegister(SocketChannel channel, Selector selector,
                                           HashMap<SelectionKey, Runnable> map
                                           ) throws IOException {
        if(channel.isRegistered()){
            SelectionKey key = channel.keyFor(selector);
            if(key != null){
                key.cancel();
                map.remove(key);
                selector.wakeup();
            }
        }
    }

    @Override
    public boolean registerInPut(SocketChannel channel, HandleInPutCallBack inPutCallBack) {
        return register(channel, readSelector, SelectionKey.OP_READ, inRegInput, inputCallBackMap, inPutCallBack) != null;
    }

    @Override
    public boolean registerOutPut(SocketChannel channel, HandleOutPutCallBack outPutCallBack) {
        return register(channel, writeSelector, SelectionKey.OP_WRITE, inRegOutput, outputCallBackMap, outPutCallBack) != null;
    }

    @Override
    public void unRegisterInPut(SocketChannel channel) {
        try {
            unRegister(channel, readSelector, inputCallBackMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unRegisterOutPut(SocketChannel channel) {
        try {
            unRegister(channel, writeSelector, outputCallBackMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        if(isClosed.compareAndSet(false,true)){
            inputThreadPool.shutdownNow();
            outputThreadPool.shutdownNow();

            inputCallBackMap.clear();
            outputCallBackMap.clear();

            readSelector.wakeup();
            writeSelector.wakeup();

            CloseUtils.close(readSelector, writeSelector);
        }

    }

    public class IOProviderThreadFactory implements ThreadFactory {
        //private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        IOProviderThreadFactory(String namePrefix) {
            SecurityManager s = System.getSecurityManager();
            this.group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            this.namePrefix = namePrefix;
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

}
