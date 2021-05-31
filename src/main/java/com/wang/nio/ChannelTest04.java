package com.wang.nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class ChannelTest04 {
    public static void main(String[] args) throws IOException {
        //创建相关的流
        FileInputStream fileInputStream = new FileInputStream("e:\\file01.txt");
        FileOutputStream fileOutputStream = new FileOutputStream("e:\\file03.txt");

        //获取channel
        FileChannel source = fileInputStream.getChannel();
        FileChannel dest = fileOutputStream.getChannel();

        //使用transferFrom完成拷贝
        dest.transferFrom(source,0,source.size());

        //关闭相关流
        source.close();;
        dest.close();
        fileInputStream.close();
        fileOutputStream.close();
    }
}
