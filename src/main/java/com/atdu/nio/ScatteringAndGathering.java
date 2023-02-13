package com.atdu.nio;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.stream.Stream;

public class ScatteringAndGathering {
    public static void main(String[] args) throws  Exception {
        ServerSocketChannel open = ServerSocketChannel.open();
        ServerSocket socket = open.socket();
        socket.setReuseAddress(true);
        socket.bind(  new InetSocketAddress(7000));
        ByteBuffer[] buffers=new ByteBuffer[2];
        buffers[0]=ByteBuffer.allocate(5);//分配空间大小为5
        buffers[1]=ByteBuffer.allocate(3);//同样分配空间大小为3
        SocketChannel accept = open.accept();
        int messageLength=8;
        while(true){
            int Read=0;
            while(Read<messageLength){
                long read = accept.read(buffers); //将字节序列从此通道读取到给定缓冲区中
                Read+=read;//记录累积读取的字节数
                System.out.println("ByteRead ="+Read);
                //用Map记录buffer中各个值的变化，利用 流打印的方式
                //从buffers数组中取出buffer对象，创建map映射， "forEach(System.out::println)"自动遍历
                Arrays.asList(buffers).stream().map(buffer -> "Position =" + buffer.position() + ", limit =" + buffer.limit()).forEach(System.out::println);

            }
            Arrays.asList(buffers).forEach(buffer ->buffer.flip());//同样原理，对数组中每个buffer对象进行读写转换
            //将数据读出到客户端，同时也是把buffer的数据写到channnel
            long WriteByte=0;
            while(WriteByte<messageLength){
                long write = accept.write(buffers);//从给定缓冲区将字节序列写入此通道。
                WriteByte+=write;
            }
            Arrays.asList(buffers).forEach(buffer ->buffer.clear());
            System.out.println("Read ="+ Read +", WriteByte ="+WriteByte +" ,messageLength ="+messageLength);
        }
    }
}
