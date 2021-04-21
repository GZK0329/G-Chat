package top.gzk.sample.MyConnect.utils;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/19
 **/

public class ByteUtils {

    /*
     * @Author GZK0329
     * @Description 
     * @Date 18:31 2021/4/21
     * @Param
     * @return true if obj
     **/
    public static boolean startWith(byte[] source, int offset, byte[] match){

        if(source.length - offset < match.length) return false;

        for(int i = 0; i < source.length; ++i){
            if(source[offset + i] == match[i]) {continue;}
            else{
                return false;
            }
        }
        return true;
    }
}
