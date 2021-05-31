package com.wang.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     *读取客户端发送过来的消息
     * @param ctx 上下文对象，含有 管道pipeline，通道channel，地址
     * @param msg 就是客户端发送的数据，默认Object
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //用sleep模拟一段耗时的任务
        //比如这里有一个非常耗时的任务->异步执行->提交给该channel对应的NioEventLoop中的TaskQueue中
        //解决方案一:用户程序自定义的普通任务
        //解决方案二:用户自定义定时任务
        // ctx.channel().eventLoop().execute(new Runnable() {
        //     @Override
        //     public void run() {
        //         try {
        //             Thread.sleep(10*1000);
        //         } catch (InterruptedException e) {
        //             e.printStackTrace();
        //         }
        //         ctx.writeAndFlush(Unpooled.copiedBuffer("我刚执行一段非常长的任务1",CharsetUtil.UTF_8));
        //     }
        // });

        // ctx.channel().eventLoop().schedule(new Runnable() {
        //     @Override
        //     public void run() {
        //         try {
        //             Thread.sleep(10*1000);
        //         } catch (InterruptedException e) {
        //             e.printStackTrace();
        //         }
        //         ctx.writeAndFlush(Unpooled.copiedBuffer("我刚执行一段非常长的任务2",CharsetUtil.UTF_8));
        //     }
        // },5, TimeUnit.SECONDS);

        // Thread.sleep(10*1000);
        // ctx.writeAndFlush(Unpooled.copiedBuffer("我刚执行一段非常长的任务",CharsetUtil.UTF_8));
        // System.out.println("服务器继续运行");


        System.out.println("服务器读取线程：" + Thread.currentThread().getName());
        System.out.println("server ctx = " + ctx);
        //看看Channel和Pipeline的关系
        Channel channel = ctx.channel();
        ChannelPipeline pipeline = ctx.pipeline(); //本质是个双向链表，出栈入栈

        //将msg转成一个ByteBuf，比NIO的ByteBuffer性能更高
        ByteBuf buf = (ByteBuf)msg;
        System.out.println("客户端发送的消息是：" + buf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址：" + ctx.channel().remoteAddress());
    }

    //数据读取完毕
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //它是 write + flush，将数据写入到缓存buffer，并将buffer中的数据flush进通道
        //一般讲，我们对这个发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~", CharsetUtil.UTF_8));
    }

    //处理异常，一般是关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
