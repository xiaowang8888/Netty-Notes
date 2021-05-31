package com.wang.nio;

import java.nio.IntBuffer;

public class BufferTest {
    public static void main(String[] args) {
        IntBuffer intBuffer = IntBuffer.allocate(5);
        intBuffer.put(1);
        intBuffer.put(2);
        intBuffer.put(3);
        intBuffer.put(4);
        intBuffer.put(5);
        //将buffer转换,读写切换
        intBuffer.flip();
        while (intBuffer.hasRemaining()){
            System.out.println(intBuffer.get());
        }
    }
}
