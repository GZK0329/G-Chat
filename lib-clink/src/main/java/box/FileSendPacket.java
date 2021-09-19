package box;

import connect.SendPacket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/1/18
 **/

public class FileSendPacket extends SendPacket<FileInputStream> {
    private final File file;

    public FileSendPacket(File file) {
        this.file = file;
        this.length = file.length();
    }

    public byte type() {
        return TYPE_STREAM_FILE;
    }

    @Override
    protected FileInputStream createStream() {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

}
