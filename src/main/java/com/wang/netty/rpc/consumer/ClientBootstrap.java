package com.wang.netty.rpc.consumer;

import com.wang.netty.rpc.netty.NettyClient;
import com.wang.netty.rpc.publicinterface.HelloService;

public class ClientBootstrap {
    //这里定义协议头
    public static final String providerName = "HelloService#hello#";

    public static void main(String[] args) throws InterruptedException {
        //创建一个消费者
        NettyClient customer = new NettyClient();
        //创建代理对象
        HelloService service = (HelloService) customer.getBean(HelloService.class, providerName);

        //通过代理对象调用服务提供者的方法
        String res = service.hello("你好 Dubbo");
        System.out.println("调用的结果，res = " + res);
        Thread.sleep(2000);
    }
}
