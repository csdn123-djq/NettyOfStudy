package com.atdu.nio;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class FileNioChannel01 {
    public static void main(String[] args) throws  Exception{
        //使用ByteBuffer(缓冲区）和FileChannel（通道）实现字符串"Hello Netty框架"写入File01.txt文件
        //File.txt文件不存在
        String str= "Hello Netty框架02";
        FileOutputStream fileOutputStream = new FileOutputStream("/Users/dujiaqi/Desktop:\\file01.txt");
         //创建一个输出流->channel
        FileChannel filechannel = fileOutputStream.getChannel();
        //这个filechannel的真正实现类是filechannelImpl
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(str.getBytes());
        byteBuffer.flip();//读写转换
        filechannel.write(byteBuffer);
        fileOutputStream.close();
    }
}
