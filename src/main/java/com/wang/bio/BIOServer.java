package com.wang.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOServer {

    public static void main(String[] args) throws IOException {
        //1.创建一个线程池
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        //2.如果有客户端连接就创建一个线程与之通讯（单独写一个方法）
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("服务器启动了");

        //监听,等待客户端的连接
        while (true){
            //阻塞等待
            System.out.println("等待连接...");
            final Socket socket = serverSocket.accept();
            System.out.println("连接到一个客户端");
            newCachedThreadPool.execute(new Runnable() {
                public void run() {
                    handle(socket);
                }
            });
        }
    }

    //线程处理连接
    public static void handle(Socket socket){
        System.out.println("线程ID:"+Thread.currentThread().getId());
        byte[] bytes = new byte[1024];
        //通过socket获取输入流
        try {
            InputStream inputStream = socket.getInputStream();
            //循环读取客户端的数据
            while (true){
                System.out.println("线程ID:"+Thread.currentThread().getId());
                System.out.println("read...");
                int read = inputStream.read(bytes);
                if(read!=-1){
                    System.out.println(new String(bytes,0,read));
                }else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                System.out.println("关闭客户端的连接");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
