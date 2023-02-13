package com.atdu.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class TestFileChannelTransferTo {
    public static void main(String[] args) throws  Exception{
        FileChannel fileInputStream = new FileInputStream("/Users/dujiaqi/Desktop/Plane-management-system-master/Netty/src/main/java/com/atdu/words.txt").getChannel();
        FileChannel fileOutputStream = new FileOutputStream("/Users/dujiaqi/Desktop/Plane-management-system-master/Netty/src/main/java/com/atdu/words3.txt").getChannel();
        // position – 文件中开始传输的位置;必须是非负数
        //count – 要传输的最大字节数;必须是非负数
        //目标 – 目标通道
        fileInputStream.transferTo(0,fileInputStream.size(),fileOutputStream);
          //to : 将字节从此通道的文件传输到给定的可写字节通道
    }
}
