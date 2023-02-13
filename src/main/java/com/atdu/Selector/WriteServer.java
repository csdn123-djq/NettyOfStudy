package com.atdu.Selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

public class WriteServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8080));
        Selector selector = Selector.open();
        ssc.register(selector,SelectionKey.OP_ACCEPT);
        while(true){
            selector.select();
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while(iter.hasNext()){
                SelectionKey key = iter.next();
                iter.remove();
                if(key.isAcceptable()){
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    SelectionKey sckey = sc.register(selector, SelectionKey.OP_READ);
                    StringBuilder sb=new StringBuilder();
                    for (int i = 0; i < 300000; i++) {
                        sb.append('a');
                    }
                    //将字符串编码为此字符集中的字节的便捷方法
                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());
                    int write = sc.write(buffer);
                    System.out.println("实际写入Write="+write);
                    if (buffer.hasRemaining()){
                       sckey.interestOps(sckey.interestOps()+SelectionKey.OP_WRITE);
                       sckey.attach(buffer);//取出上次未写完数据，挂在sckey中
                    }
                }else if(key.isWritable()){
                    ByteBuffer attachment =(ByteBuffer) key.attachment();
                   SocketChannel sc= (SocketChannel) key.channel();
                    int write = sc.write(attachment);
                    System.out.println("实际写入了"+write+" write");
                    if(!attachment.hasRemaining()){//所有已经写完
                        key.interestOps(key.interestOps()-SelectionKey.OP_WRITE);
                        key.attach(null);//减少对内存的占用
                    }
                }
            }
        }

    }
}
