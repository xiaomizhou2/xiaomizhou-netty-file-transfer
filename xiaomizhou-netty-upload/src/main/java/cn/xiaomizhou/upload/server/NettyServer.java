package cn.xiaomizhou.upload.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.log4j.Logger;

/**
 * @Description Netty上传服务端
 * @Author xiaomizhou
 * @Date 2020/3/13 9:50
 **/

public class NettyServer {

    public static Logger log = Logger.getLogger(NettyServer.class);

    //配置服务端NIO线程组
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel channel;

    public ChannelFuture bind(int port) {
        ChannelFuture channelFuture = null;

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)   //非阻塞模式
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new MyChannelInitializer());
            channelFuture = b.bind(port).syncUninterruptibly();

            this.channel = channelFuture.channel();
        } catch (Exception e) {
            log.error("文件上传服务端出现异常-> ", e);
        }finally {
            //优雅的关闭netty
            //destroy();
        }
        return channelFuture;
    }

    public void destroy() {
        if (channel == null)
            return;
        channel.close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public Channel getChannel() {
        return channel;
    }
}
