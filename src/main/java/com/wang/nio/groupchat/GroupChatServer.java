package com.wang.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class GroupChatServer {

    //监听通道
    private ServerSocketChannel lsitenChannel;
    //选择器
    private Selector selector;
    //端口
    private static final int PORT=6667;

    //初始化工作
    public GroupChatServer(){
        try {
            lsitenChannel=ServerSocketChannel.open();
            selector=Selector.open();
            //绑定端口
            lsitenChannel.socket().bind(new InetSocketAddress(PORT));
            //设置非阻塞模式
            lsitenChannel.configureBlocking(false);
            //将listenChannel注册到selector
            lsitenChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //监听
    public void listen(){
        try {
            while (true){

                int count = selector.select();
                //有事件处理
                if(count>0){
                    //直接得到SelectionKey集合的迭代器
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();

                        //监听到Accept事件
                        if(key.isAcceptable()){
                            SocketChannel sc = lsitenChannel.accept();
                            //设置非阻塞并注册到selector
                            sc.configureBlocking(false);
                            sc.register(selector,SelectionKey.OP_READ);
                            //提示客户端上线
                            System.out.println(sc.getRemoteAddress()+"已上线...");
                        }

                        if(key.isReadable()){
                            //处理读,专门写方法
                            readData(key);
                        }

                        //清除当前的key,避免重复处理
                        iterator.remove();
                    }
                }else {
                    System.out.println("等待连接...");
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //读取客户端的消息
    private void readData(SelectionKey key) {
        //定义一个SocketChannel
        SocketChannel channel = null;
        try {
            //取到关联的Channel
            channel= (SocketChannel) key.channel();
            //创建缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            //将缓冲区的数据读取到通道
            int read = channel.read(buffer);
            //根据read判断是否读取到数据
            if(read>0){
                //把缓冲区的数据转化成字符串
                String msg = new String(buffer.array());
                //输出此消息
                System.out.println("from 客户端:"+msg);

                //向其他客户端转发此消息
                sendInfoToOtherClients(msg,channel);
            }

        }catch (IOException e){
            try {
                System.out.println(channel.getRemoteAddress()+"离线了...");
                //取消注册
                key.cancel();
                //关闭通道
                channel.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void sendInfoToOtherClients(String msg, SocketChannel self) throws IOException {

        System.out.println("服务器转发消息中...");
        for(SelectionKey key:selector.keys()){
            Channel targetChannel = key.channel();

            if(targetChannel instanceof SocketChannel && targetChannel!=self){
                SocketChannel dest = (SocketChannel) targetChannel;
                //创建缓冲区
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                //将缓冲区的数据写入通道
                dest.write(buffer);
            }
        }

    }

    public static void main(String[] args) {
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }
}
