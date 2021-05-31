package com.wang.netty.websocket;

import com.wang.netty.heartbeat.MyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;


public class MyServer {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            //因为基于Http协议,需要添加http的编解码器
                            pipeline.addLast(new HttpServerCodec());
                            //以块方式写,添加写入块的处理器
                            pipeline.addLast(new ChunkedWriteHandler());

                            /**
                             * 说明:
                             * 1.http数据在传输过程中是分段的,HttpObjectAggregator,就可以将多个段聚合
                             * 2.这就是为什么浏览器发送大量数据时,会产生多次的http请求
                             */
                            pipeline.addLast(new HttpObjectAggregator(8192));

                            /**
                             * 1.对应websocket,它的数据是以帧(frame)形式传递
                             * 2.可以看到WebSocketFrame下面有六个子类
                             * 3.浏览器请求时 ws://localhost:7000/hello 表示请求的uri
                             * 4.WebSocketServerProtocolHandler核心功能是将http协议升级为ws协议,保持长连接
                             */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));

                            //自定义handler
                            pipeline.addLast(new MyTextWebSocketFrameHandler());

                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind(7000).sync();
            channelFuture.channel().closeFuture().sync();


        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
