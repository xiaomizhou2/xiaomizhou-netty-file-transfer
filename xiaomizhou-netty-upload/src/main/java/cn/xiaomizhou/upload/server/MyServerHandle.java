package cn.xiaomizhou.upload.server;

import cn.xiaomizhou.common.domain.NettyUploadFile;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @Description 服务端传输文件的实现方法
 * @Author xiaomizhou
 * @Date 2020/3/13 10:18
 **/
public class MyServerHandle extends ChannelInboundHandlerAdapter {

    private final static Logger LOGGER = Logger.getLogger(MyServerHandle.class);

    private int byteRead;
    private volatile long start = 0;
    private RandomAccessFile randomAccessFile;
    private String rootPath = "E:\\test";


    /**
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        LOGGER.info("链接报告开始");
        LOGGER.info("链接报告信息：有一客户端链接到本服务端。channelId：" + channel.id());
        LOGGER.info("链接报告IP:" + channel.localAddress().getHostString());
        LOGGER.info("链接报告Port:" + channel.localAddress().getPort());
        LOGGER.info("链接报告完毕");
    }

    /**
     * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("客户端断开链接" + ctx.channel().localAddress().toString());
        ctx.flush();
        ctx.close();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            //实体类验证
            if (!(msg instanceof NettyUploadFile)) return;

            //开始对上传文件进行处理
            NettyUploadFile ef = (NettyUploadFile) msg;
            byte[] bytes = ef.getBytes();
            byteRead = ef.getEndPos();
            Long fileLength = ef.getFileLength();
            long starPos = ef.getStarPos();
            //构建文件存储路径
            String fileName = ef.getFileName();
            //String filePath = ef.getFilePath();
            String path = rootPath + File.separator + fileName;
            File file = new File(path);
            //判断文件是否存在
            if (file.exists()) {
                //获取存储目录文件大小
                long saveFileLength = file.length();
                if (saveFileLength == fileLength) {
                    //文件存在且已经传输完成
                    ctx.writeAndFlush(saveFileLength);
                    LOGGER.info(fileName + "文件上传完成");
                } else if (saveFileLength < fileLength) {
                    start = saveFileLength;
                    //开始位置等于文件断点，开始断点续传
                    if (starPos == start) {
                        //r: 只读模式 rw:读写模式
                        randomAccessFile = new RandomAccessFile(file, "rw");
                        //移动文件记录指针的位置,
                        randomAccessFile.seek(start);
                        //调用了seek（start）方法，是指把文件的记录指针定位到start字节的位置。也就是说程序将从start字节开始写数据
                        randomAccessFile.write(bytes);
                        start = start + byteRead;
                        if (byteRead > 0) {
                            //向客户端发送消息
                            ctx.writeAndFlush(start);
                            randomAccessFile.close();
                            if (byteRead != 1024 * 100) {
                                start = 0;
                                LOGGER.info(fileName + "文件上传临时目录完成");
                                Thread.sleep(1000);
                                channelInactive(ctx);
                            }
                        } else {
                            ctx.close();
                        }
                    } else {
                        //不等于断点位置，回写给客户端
                        ctx.writeAndFlush(saveFileLength);
                    }
                }
            } else {
                //文件不存在，从0开始上传
                //r: 只读模式 rw:读写模式
                randomAccessFile = new RandomAccessFile(file, "rw");
                //移动文件记录指针的位置,
                randomAccessFile.seek(start);
                //调用了seek（start）方法，是指把文件的记录指针定位到start字节的位置。也就是说程序将从start字节开始写数据
                randomAccessFile.write(bytes);
                start = start + byteRead;
                if (byteRead > 0) {
                    //向客户端发送消息
                    ctx.writeAndFlush(start);
                    randomAccessFile.close();
                    if (byteRead != 1024 * 100) {
                        start = 0;
                        LOGGER.info(fileName + "文件上传临时目录完成");
                        Thread.sleep(1000);
                        channelInactive(ctx);
                    }
                } else {
                    ctx.close();
                }
            }
        } finally {
            if (randomAccessFile != null) randomAccessFile.close();
        }
    }

    /**
     * 抓住异常，当发生异常的时候，可以做一些相应的处理，比如打印日志、关闭链接
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        LOGGER.info("出现异常->" + cause.getMessage());
    }
}
