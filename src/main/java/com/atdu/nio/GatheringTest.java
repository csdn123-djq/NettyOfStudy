package com.atdu.nio;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class GatheringTest {
    public static void main(String[] args) throws  Exception{
        ByteBuffer hello = StandardCharsets.UTF_8.encode("Hello");
        ByteBuffer world = StandardCharsets.UTF_8.encode("world");
        ByteBuffer encode = StandardCharsets.UTF_8.encode("你好");
        FileChannel rw = new RandomAccessFile("/Users/dujiaqi/Desktop/Plane-management-system-master/Netty/src/main/java/com/atdu/words2.txt", "rw").getChannel();
        rw.write(new ByteBuffer[]{hello,world,encode});

    }
}
