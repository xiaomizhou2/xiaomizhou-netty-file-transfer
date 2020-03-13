package cn.xiaomizhou.upload.client;

import cn.xiaomizhou.common.domain.NettyUploadFile;
import cn.xiaomizhou.upload.server.MyServerHandle;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Author xiaomizhou
 * @Date 2020/3/13 14:32
 **/
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private NettyUploadFile nettyUploadFile;

    public MyChannelInitializer(NettyUploadFile nettyUploadFile) {
        this.nettyUploadFile = nettyUploadFile;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        //使用默认的编码解码传输
        channel.pipeline().addLast(new ObjectEncoder());
        channel.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
        //读写超时时间
        channel.pipeline().addLast(new ReadTimeoutHandler(20000, TimeUnit.MILLISECONDS));
        channel.pipeline().addLast(new WriteTimeoutHandler(20000, TimeUnit.MILLISECONDS));
        //增加传输数据的实现方法
        channel.pipeline().addLast(new MyClientHandler(nettyUploadFile));

    }
}
