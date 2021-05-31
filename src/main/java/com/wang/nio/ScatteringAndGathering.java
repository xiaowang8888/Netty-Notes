package com.wang.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Scattering:将数据写入buffer时,可以采用buffer数组依次写入
 * Gathering:从buffer读取数据时,可以从buffer数组中依次读入
 */
public class ScatteringAndGathering {
    public static void main(String[] args) throws IOException {
        //使用ServerSocketChannel和SocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);

        //绑定端口到socket,并启动
        serverSocketChannel.socket().bind(inetSocketAddress);

        //创建buffer数组
        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        byteBuffers[0]=ByteBuffer.allocate(5);
        byteBuffers[1]=ByteBuffer.allocate(3);

        //等待客户端的连接(telnet)
        SocketChannel socketChannel = serverSocketChannel.accept();
        //假设从客户端接收八个字节
        int messageLength=8;
        //循环读取
        while (true){
            int byteRead=0;

            while (byteRead<messageLength){
                long l = socketChannel.read(byteBuffers);
                byteRead+=l;
                System.out.println("byteRead:"+byteRead);

                //使用流打印,看看当前buffer的position和limit
                Arrays.asList(byteBuffers).stream().map(buffer->"position="+buffer.position()+",limit="+buffer.limit())
                        .forEach(System.out::println);
            }

            //将所有的buffer进行flip
            Arrays.asList(byteBuffers).forEach(buffer->buffer.flip());

            //将数据读取显示到客户端
            long byteWrite=0;
            while (byteWrite<messageLength){
                long l = socketChannel.write(byteBuffers);
                byteWrite+=l;
            }

            //将所有buffer进行清理
            Arrays.asList(byteBuffers).forEach(buffer->{
                buffer.clear();
            });
            System.out.println("byteRead="+byteRead+"byteWrite="+byteWrite+",messageLength="+messageLength);
        }
    }
}
