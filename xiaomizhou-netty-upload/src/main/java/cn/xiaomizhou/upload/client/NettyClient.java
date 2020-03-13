package cn.xiaomizhou.upload.client;

import cn.xiaomizhou.common.domain.NettyUploadFile;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.Logger;

/**
 * @Description 文件上传客户端
 * @Author xiaomizhou
 * @Date 2020/3/13 11:31
 **/
public class NettyClient {

    public static Logger log = Logger.getLogger(NettyClient.class);

    //配置客户端NIO线程组
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel channel;

    public ChannelFuture connect(String host, int port, final NettyUploadFile nettyUploadFile) {
        ChannelFuture channelFuture = null;
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000); //连接超时时间
            b.handler(new MyChannelInitializer(nettyUploadFile));
            channelFuture = b.connect(host, port).syncUninterruptibly();
            this.channel = channelFuture.channel();
        }catch (Exception e) {
            log.error("文件上传客户端出现异常-> ", e);
        }finally {
            //优雅的关闭netty
            //destroy();
        }
        return channelFuture;
    }

    public void destroy() {
        if (null == channel) return;
        channel.close();
        workerGroup.shutdownGracefully();
    }
}
