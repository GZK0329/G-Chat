package utils;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/19
 **/

public class ByteUtils {

    public static boolean startWith(byte[] obj, byte[] prefix){

        for (int i = 0; i < prefix.length; i++) {
            if(obj[i] == prefix[i]) continue;
            else{
                return false;
            }
        }
        return true;
    }
}
