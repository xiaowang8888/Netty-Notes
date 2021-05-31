package com.wang.nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ChannelTest03 {
    public static void main(String[] args) throws IOException {
        File file = new File("e:\\file01.txt");

        //输入流
        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel channel01 = fileInputStream.getChannel();
        //创建输出流
        FileOutputStream fileOutputStream = new FileOutputStream("e:\\file02.txt");
        FileChannel channel02 = fileOutputStream.getChannel();
        //创建缓冲区
        ByteBuffer buffer = ByteBuffer.allocate((int) file.length());
        //循环读写
        while (true){
            //将buffer清空，重要操作，不能忘
            buffer.clear();
            int read = channel01.read(buffer);
            System.out.println("read:"+read);
            if(read==-1){
                break;
            }
            //将缓冲区的读写转换
            buffer.flip();
            channel02.write(buffer);
        }

        fileInputStream.close();
        fileOutputStream.close();
    }
}
