package com.wang.netty.rpc.provider;

import com.wang.netty.rpc.netty.NettyServer;

public class ServerBootstrap {
    public static void main(String[] args) {
        NettyServer.startServer("127.0.0.1",7000);
    }
}
