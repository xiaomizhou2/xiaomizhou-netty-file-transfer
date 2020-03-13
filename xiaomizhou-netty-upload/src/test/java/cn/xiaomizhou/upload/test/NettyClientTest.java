package cn.xiaomizhou.upload.test;

import cn.xiaomizhou.common.domain.NettyUploadFile;
import cn.xiaomizhou.upload.client.NettyClient;

import java.io.File;

/**
 * @Description TODO
 * @Author xiaomizhou
 * @Date 2020/3/13 15:40
 **/
public class NettyClientTest {

    public static NettyUploadFile init() {
        NettyUploadFile nettyUploadFile = new NettyUploadFile();
        File file = new File("D:\\test\\test.zip");
        nettyUploadFile.setFileName(file.getName());
        nettyUploadFile.setFileLength(file.length());
        //nettyUploadFile.setFilePath(path);
        nettyUploadFile.setStarPos(0L);
        nettyUploadFile.setFile(file);
        return nettyUploadFile;
    }


    public static void main(String[] args) {
        NettyUploadFile nettyUploadFile = init();

        new NettyClient().connect("127.0.0.1", 9999, nettyUploadFile);
    }
}
