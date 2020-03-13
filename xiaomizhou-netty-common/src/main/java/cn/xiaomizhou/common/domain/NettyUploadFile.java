package cn.xiaomizhou.common.domain;

import lombok.Data;

import java.io.File;
import java.io.Serializable;

/**
 * @Description 上传实体类
 * @Author xiaomizhou
 * @Date 2020/3/13 10:30
 **/
@Data
public class NettyUploadFile implements Serializable {

    //文件名
    private String fileName;
    //文件大小
    private Long fileLength;
    //文件路径
    private String filePath;
    //文件对象
    private File file;
    //开始上传位置
    private long starPos;
    //文件byte
    private byte[] bytes;
    //结束上传位置
    private int endPos;

}
