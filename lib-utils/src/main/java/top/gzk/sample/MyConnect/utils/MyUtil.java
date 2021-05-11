package top.gzk.sample.MyConnect.utils;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/4/19
 **/

public class MyUtil {

    public static byte[] intToByteArray(int temp){
        byte[] ans = new byte[]{(byte)((temp >> 24) & 0xff),
                (byte)((temp >> 16) & 0xff),
                (byte)((temp >> 8) & 0xff),
                (byte)(temp&0xff)
        };
        return ans;
    }

    public static Integer byteArrayToInt(byte[] ans){
        return  ans[3] & 0xFF |
                (ans[2] & 0xFF) << 8 |
                (ans[1] & 0xFF) << 16  |
                (ans[0] & 0xFF) << 24;

    }
}
