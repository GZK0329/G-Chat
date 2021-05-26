package connect;

/**
 * @Description: 分片 最小组成 —— 帧
 * @Author: GZK0329
 * @Date: 2021/5/26
 **/

public class Frame {
    //头部长度6个字节
    private static final int FRAME_HEADER_LENGTH = 6;
    //单帧最大容量为 64K
    private static final int MAX_CAPACATITY = 64 * 1024 - 1;

    //packet头信息帧
    public static final byte TYPE_PACKET_HEADER = 11;
    //packet数据分片信息帧
    public static final byte TYPE_PACKET_ENTITY = 12;
    //指令--取消发送
    public static final byte TYPE_COMMAND_SEND_CANCEL = 41;
    //指令--接收拒绝
    public static final byte TYPE_COMMAND_RECEIVE_REJECT = 42;

    //帧标志信息
    public static final byte FLAG_NONE = 0;

    //头部固定的6个字节 帧大小（2 byte） + 帧类型（1 byte）+ 帧标志（1 byte） + 包标识（1 byte） + 预留空间（1byte）
    protected final byte[] header = new byte[FRAME_HEADER_LENGTH];

    public Frame(int length, byte type, byte flag, short identifier) {

    }
}
