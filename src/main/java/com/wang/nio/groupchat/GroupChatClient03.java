package com.wang.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class GroupChatClient03 {
    //定义相关的属性
    private static final String HOST = "127.0.0.1"; //服务器的IP地址
    private static final int PORT = 6667; //服务器端口
    private Selector selector;
    private SocketChannel socketChannel;
    private String username;

    //构造器,初始化操作
    public GroupChatClient03() throws IOException {
        selector=Selector.open();
        //连接服务器
        socketChannel=SocketChannel.open(new InetSocketAddress(HOST,PORT));
        //设置非阻塞
        socketChannel.configureBlocking(false);
        //注册到selector
        socketChannel.register(selector, SelectionKey.OP_READ);
        //得到username
        username=socketChannel.getLocalAddress().toString().substring(1);
        System.out.println(username+"is ok...");
    }

    //向服务器发送信息
    public void sendInfo(String info){
        info = username + "说:"+ info;

        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取从服务器回复的消息
    public void readInfo(){
        try {

            int readChannels = selector.select();
            if(readChannels>0){
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if(key.isReadable()){
                        //得到相关通道
                        SocketChannel channel = (SocketChannel) key.channel();
                        //创建缓冲区
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        //读取
                        channel.read(buffer);
                        //把缓冲区的数据转换的字符串
                        String msg = new String(buffer.array());
                        System.out.println(msg.trim());
                    }
                    iterator.remove();
                }
            }else {
                //System.out.println("没有可用的通道...");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        //启动客户端
        GroupChatClient03 groupChatClient03 = new GroupChatClient03();

        //启动一个线程用于读取服务器的消息
        new Thread(()->{
            while (true){
                groupChatClient03.readInfo();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //主线程用于发送数据给服务端
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            String s = scanner.nextLine();
            groupChatClient03.sendInfo(s);
        }
    }
}
