package com.wang.nio.serverandclient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioClient {
    public static void main(String[] args) throws IOException {
        //创建一个SocketChannel
        SocketChannel socketChannel = SocketChannel.open();

        //设置成非阻塞
        socketChannel.configureBlocking(false);

        //提供服务器的IP 端口
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);
        //连接服务器
        if(!socketChannel.connect(inetSocketAddress)){
            while (!socketChannel.finishConnect()){
                System.out.println("因为连接需要时间,客户端不会阻塞,可以做其他工作");
            }
        }
        //如果连接成功
        String str="hello,My name is wangmengcheng!!";
        //包含一个字节数组,直接返回数组大小的Buffer
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());
        //将缓冲区的数据写入通道
        socketChannel.write(buffer);
        System.in.read();
    }
}
