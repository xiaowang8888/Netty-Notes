package com.wang.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    //定义一个channel组,管理所有的channel
    //GlobalEventExecutor.INSTANCE 是全局事件执行器,是一个单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    //时间格式
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //此方法表示连接建立,一旦建立连接,第一个执行此方法
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //该方法会将 channelGroup 中所有 channel 遍历，并发送消息，而不需要我们自己去遍历
        channelGroup.writeAndFlush("[客户端]"+channel.remoteAddress()+sdf.format(new Date())+"加入聊天");
        //将当前的channel加入到channelGroup
        channelGroup.add(channel);
    }

    //表示 channel 处于活动状态，提示 xxx 上线
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+" "+sdf.format(new Date())+"上线了~");
    }

    //表示 channel 处于不活动状态，提示 xxx 离线
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+" "+sdf.format(new Date())+"离线了~");
    }

    //表示 channel 断开连接，将xx客户离开信息推送给当前在线客户
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //获取当前通道
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[客户端]"+channel.remoteAddress()+sdf.format(new Date())+"离开了");
    }

    //读取数据，并进行消息转发
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        //这时遍历channelGroup,根据不同情况回送不同消息
        channelGroup.forEach(item->{
            if(item!=channel){
                item.writeAndFlush("[客户]" + channel.remoteAddress() + "发送了消息：" + msg + "\n");
            }else {
                item.writeAndFlush("[自己]发送了消息：" + msg + "\n");
            }
        });
    }

    //捕获异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
