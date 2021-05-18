package utils;

import impl.async.AsyncSendDispatcher;

import java.io.Closeable;
import java.io.IOException;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/21
 **/

public class CloseUtils {
    public static void close(Closeable... closeables){
        if(closeables == null){
            return;
        }else{
            for (Closeable closeable : closeables) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
