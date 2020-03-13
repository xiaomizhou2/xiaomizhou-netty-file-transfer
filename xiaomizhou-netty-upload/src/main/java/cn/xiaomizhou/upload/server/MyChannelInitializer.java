package cn.xiaomizhou.upload.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @Description TODO
 * @Author xiaomizhou
 * @Date 2020/3/13 10:10
 **/
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) {
        //使用默认的编码解码传输
        channel.pipeline().addLast(new ObjectEncoder());
        channel.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null)));
        //增加传输数据的实现方法
        channel.pipeline().addLast(new MyServerHandle());
    }
}
