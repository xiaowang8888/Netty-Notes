package com.wang.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ChannelTest02 {
    public static void main(String[] args) throws IOException {

        //创建文件
        File file = new File("e:\\file01.txt");
        //创建输入流
        FileInputStream fileInputStream = new FileInputStream(file);
        //根据输入流获取channel
        FileChannel channel = fileInputStream.getChannel();
        //创建缓冲区
        ByteBuffer buffer = ByteBuffer.allocate((int)file.length());
        //将通道的数据写入缓冲区
        channel.read(buffer);
        //将buffer中的字节转化为字符串
        System.out.println(new String(buffer.array()));
        //关闭流
        fileInputStream.close();

    }
}
