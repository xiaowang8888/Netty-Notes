package com.wang.netty.rpc.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

public class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext context; //上下文
    private String result; //返回的结果
    private String para; //客户端调用方法时，传入的参数

    //与服务端创建连接后调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("通道连接成功");
        context = ctx; //因为我们在其他方法会使用到 ctx
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        result = msg.toString();
        notify(); //唤醒等待的线程
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    //被代理对象的调用，真正发送数据给服务器，发送完后就阻塞，等待被唤醒（channelRead）
    @Override
    public synchronized Object call() throws Exception {
        System.out.println("线程被调用-----");
        context.writeAndFlush(para);
        //进行wait
        wait(); //等待 channelRead 获取到服务器的结果后，进行唤醒。
        return result; //服务方返回的结果
    }

    public void setPara(String para){
        this.para = para;
    }

}
