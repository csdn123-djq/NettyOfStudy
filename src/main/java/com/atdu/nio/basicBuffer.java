package com.atdu.nio;

import java.nio.IntBuffer;

public class basicBuffer {
    public static void main(String[] args) {
        IntBuffer  intBuffer= IntBuffer.allocate(5);
        for (int i = 0; i < intBuffer.capacity(); i++){
            intBuffer.put(i * 2);
        }
        intBuffer.flip();//BUffer的读写切换(!!!!会导致Buffer里面的标志发生变化）
        while(intBuffer.hasRemaining()){
            System.out.println(intBuffer.get());
        }
    }
}
