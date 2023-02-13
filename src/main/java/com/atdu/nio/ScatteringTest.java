package com.atdu.nio;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ScatteringTest {
    public static void main(String[] args) throws  Exception{
        FileChannel fileChannel = new RandomAccessFile("/Users/dujiaqi/Desktop/Plane-management-system-master/Netty/src/main/java/com/atdu/words.txt", "r").getChannel();
        ByteBuffer  b1= ByteBuffer.allocate(3);
        ByteBuffer b2 = ByteBuffer.allocate(3);
        ByteBuffer b3 = ByteBuffer.allocate(5);
        fileChannel.read(new ByteBuffer[]{b1,b2,b3});
        b1.flip();
        b2.flip();
        b3.flip();
        System.out.println(new String(b1.array()));
        System.out.println(new String(b2.array()));
        System.out.println(new String(b3.array()));

    }
}
