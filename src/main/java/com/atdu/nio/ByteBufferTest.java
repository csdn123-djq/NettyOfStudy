package com.atdu.nio;

import java.nio.ByteBuffer;

public class ByteBufferTest {
    //粘包，半包的处理
    public static void main(String[] args) {
        ByteBuffer source= ByteBuffer.allocate(32);
        source.put("Hello,world\nI'm ZhangSan\nHo".getBytes());
        split(source);
        source.put("w are you\n".getBytes());
        split(source);

    }
    public static void split(ByteBuffer source){
           source.flip();
        for (int i = 0; i < source.limit(); i++) {
            if(source.get(i)=='\n'){
                int len=i+1-source.position();//记录
                ByteBuffer target=ByteBuffer.allocate(len);
                for(int j=0;j<len;j+=1){
                    target.put(source.get());
                }
                System.out.print(new String(target.array()));
            }
        }
        source.compact();
    }
}
