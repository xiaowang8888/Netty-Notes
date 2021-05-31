package com.wang.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    /**
     * 读取客户端数据
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        //判断msg是不是HttpRequest请求
        if(msg instanceof HttpRequest){
            System.out.println("msg类型:"+msg.getClass());
            System.out.println("客户端地址:"+ctx.channel().remoteAddress());

            HttpRequest request = (HttpRequest) msg;

            //获取uri,进行路径过滤
            // URI uri = new URI(request.uri());
            // if("/favicon.ico".equals(uri.getPath())){
            //     System.out.println("请求了favicon.ico,不作响应");
            // }

            //回复信息给浏览器
            ByteBuf content = Unpooled.copiedBuffer("Hello,客户端!!", CharsetUtil.UTF_8);
            //构造一个HttpResponse
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());

            //将构建好的response返回
            ctx.writeAndFlush(response);
        }
    }
}
