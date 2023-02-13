package com.atdu.Socket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class Server {
    public static void main(String[] args) throws Exception{
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(8080));
        ArrayList<SocketChannel> channels= new ArrayList<>();
        while (true){  //设置wile(true)目的为不断的获取连接，不仅仅只是一次连接就结束
            System.out.println("--1");
            SocketChannel sc   = ssc.accept();//阻塞方法，线程停止运行
            channels.add(sc);
            System.out.println("--2");
            //，会有多个连接，所以对全是SocketChannel的ist进行遍历
            for (SocketChannel channel: channels) {
                System.out.println("--3");
                channel.read(byteBuffer);//将字节序列从此"通道"读取到"给定缓冲区"中。
                byteBuffer.flip();
                System.out.println(channel.getRemoteAddress());
                byteBuffer.clear();//阻塞方法，线程停止运行
                System.out.println("--4");
            }
        }
    }
}
