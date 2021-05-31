package com.wang.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        //向管道加入处理器

        //得到管道
        ChannelPipeline pipeline = ch.pipeline();
        //加入一个netty提供的httpServerCodec(编解码器)
        pipeline.addLast("MyHttpServerCodec",new HttpServerCodec());
        //增加自定义的Handler
        pipeline.addLast("MyHttpServerHandler",new HttpServerHandler());
    }
}
