package connect;

import utils.CloseUtils;

import java.io.IOException;

/**
 * @Description: 配置并且提供IOProvider
 * @Date: 2021/1/25
 **/

public class IOContext {
    //单例
    private static IOContext INSTANCE;

    private final IOProvider ioProvider;

    public IOContext(IOProvider ioProvider) {
        this.ioProvider = ioProvider;
    }

    public IOProvider getIoProvider() {
        return ioProvider;
    }

    public static IOContext getINSTANCE() {
        return INSTANCE;
    }

    public static StartBoot setUp(){
        return new StartBoot();
    }

    public static class StartBoot{
        private  IOProvider ioProvider;

        public StartBoot ioProvider(IOProvider ioProvider){
            this.ioProvider = ioProvider;
            return this;
        }

        public IOContext start(){
            INSTANCE = new IOContext(ioProvider);
            return INSTANCE;
        }
    }
    public static void close() throws IOException {
        if(INSTANCE!= null){
            INSTANCE.callClose();
        }

    }
    private  void callClose() throws IOException {
        ioProvider.close();
    }

}
