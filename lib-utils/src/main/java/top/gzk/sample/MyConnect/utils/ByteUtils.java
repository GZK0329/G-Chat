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

    public static boolean startWith(byte[] source, byte[] match){
        return startWith(source,0, match);
    }

    public static boolean equals(byte[] source, byte[] match){
        if(source.length == match.length){
            return startWith(source,0, match);
        }
        return false;
    }

    public static void getBytes(byte[] source, int srcBegin, int srcEnd, byte[] destination,
                                int dstBegin) {
        //source数组从下标srcBegin开始 复制到destination数组从下标dstBegin开始 ，复制数量为srcEnd - srcBegin个
        System.arraycopy(source, srcBegin, destination, dstBegin, srcEnd - srcBegin);
    }

    //source数组的下标从srcBegin开始复制 srcEnd-srcBegin个元素 到目标数组从下标0开始 并返回目标数组
    public static byte[] subbytes(byte[] source, int srcBegin, int srcEnd) {
        byte destination[];

        destination = new byte[srcEnd - srcBegin];
        getBytes(source, srcBegin, srcEnd, destination, 0);

        return destination;
    }


    public static byte[] subbytes(byte[] source, int srcBegin) {
        return subbytes(source, srcBegin, source.length);
    }




}
