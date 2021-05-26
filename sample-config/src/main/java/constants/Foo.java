package constants;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @Description: TODO
 * @Author: GZK0329
 * @Date: 2021/5/26
 **/

public  class Foo {
    private static final String CACHE_DIR = "cache";

    public static File getCacheDir(String dir) throws Exception {
        String path = System.getProperty("user.dir") + (File.separator + CACHE_DIR + File.separator + dir);
        File file = new File(path);
        if(!file.exists()){
            if(!file.mkdirs()){
                throw new Exception("文件未创建成功!");
            }
        }
        return file;
    }

    /*
    * 创建文件
    * new File 创建文件对象
    * file.createNewFile 创建文件
    * */
    public static File createRandomTemp(File parent){
        String child = UUID.randomUUID() + ".tmp";
        File file = new File(parent, child);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
