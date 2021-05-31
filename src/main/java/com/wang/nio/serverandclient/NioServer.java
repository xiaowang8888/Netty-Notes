package com.wang.nio.serverandclient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer {
    public static void main(String[] args) throws IOException {
        //创建ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //绑定端口
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));

        //得到一个Selector对象
        Selector selector = Selector.open();

        //将ServerSocketChannel设置成非阻塞
        serverSocketChannel.configureBlocking(false);

        //将ServerSocketChannel注册到Selector,事件为OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //循环等待客户端连接
        while (true){

            //这里我们等待一秒,如果没有事件发生,返回
            if(selector.select(1000)==0){
                System.out.println("客户端无连接,等待一秒");
                continue;
            }

            //如果返回>0,就获取相应的SelectionKey集合
            //1.如果返回>0,表示已经获取到关注的事件
            //2.selector.selectedKeys()返回关注事件集合
            //通过selectionKeys反向获取通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            //使用迭代器
            Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();

            while (selectionKeyIterator.hasNext()){
                //获取SelectionKey
                SelectionKey key = selectionKeyIterator.next();
                //根据key对应通道的事件做出相应的处理
                if(key.isAcceptable()){ //如果是OP_ACCEPT,有新用户连接
                    //给该客户端创建一个SocketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    //输出连接的SocketChannel
                    System.out.println("socketChannel:"+socketChannel.hashCode());
                    //将SocketChannel设置成非阻塞的
                    socketChannel.configureBlocking(false);
                    //将SocketChannel注册到selector
                    socketChannel.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }
                if(key.isReadable()){ //发生OP_READ
                    //通过key获取当前通道
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    //获取该channel关联的Buffer
                    ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                    socketChannel.read(byteBuffer);
                    System.out.println("from 客户端:"+ new String(byteBuffer.array()));
                }

                //手动从集合中移除当前的SelectionKey,防止重复操作
                selectionKeyIterator.remove();
            }
        }

    }
}
