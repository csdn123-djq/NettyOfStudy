package com.atdu.Socket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class ServerOfNot {//非阻塞服务器
    public static void main(String[] args) throws Exception{
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8080));
        ArrayList<SocketChannel> channels= new ArrayList<>();
        while (true){  //设置wile(true)目的为不断的获取连接，不仅仅只是一次连接就结束
//  接受与此通道的套接字建立的连接。
// 如果此通道处于非阻塞模式，则如果没有挂起的连接，此方法将立即返回 null 。
// 否则，它将无限期阻塞，直到新连接可用或发生 I/O 错误
            SocketChannel sc   = ssc.accept();//阻塞方法，线程停止运行
            if (sc != null) {
                channels.add(sc);
                System.out.println("--2");
            }
            //，会有多个连接，所以对全是SocketChannel的ist进行遍历
            for (SocketChannel channel: channels) {
               // System.out.println("--3");
                int read = channel.read(byteBuffer);//将字节序列从此"通道"读取到"给定缓冲区"中。
               if(read>0){
                   byteBuffer.flip();
                   System.out.println(new String(byteBuffer.array()));
                   System.out.println(channel.getRemoteAddress());
                   byteBuffer.clear();//阻塞方法，线程停止运行
                   System.out.println("--4");
               }
            }
        }
    }
}
