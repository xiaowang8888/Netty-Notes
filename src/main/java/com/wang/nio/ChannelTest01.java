package com.wang.nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ChannelTest01 {
    public static void main(String[] args) throws IOException {

        String str="Hello,Netty!!!";
        //创建一个输出流->channel
        FileOutputStream fileOutputStream = new FileOutputStream("e:\\file01.txt");
        //通过fileOutputStream获取对应的FileChannel
        //这个channel的真实类型是FileChannelImpl
        FileChannel channel = fileOutputStream.getChannel();
        //创建一个缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //将字符串读入缓冲区
        buffer.put(str.getBytes());
        //切换缓冲区的读写
        buffer.flip();
        //将缓冲区的数据写入通道
        channel.write(buffer);
        fileOutputStream.close();
    }
}
