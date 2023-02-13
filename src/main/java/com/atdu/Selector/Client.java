package com.atdu.Selector;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Client {
    public static void main(String[] args) throws  Exception{
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost",8080));
       socketChannel.write(Charset.defaultCharset().encode("01234\n56789abcdef3333\n"));
       socketChannel.write(Charset.defaultCharset().encode("0123456789abcdef3333\n"));
        //socketChannel.write(Charset.defaultCharset().encode("中国"));
        System.out.println("waiting...");
   //     System.in.read();
    //    socketChannel.close();
    }
}
