package com.atdu.Selector;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
public class Server {
    public static void main(String[] args) throws Exception{
        int count=0;
        ServerSocketChannel ssc = ServerSocketChannel.open();
        Selector selector = Selector.open();
        ssc.configureBlocking(false);
        SelectionKey sscKey = ssc.register(selector, 0, null);
        System.out.println(sscKey.toString());
        //key只关注accept()事件，将此键的兴趣集设置为给定值
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));
        while (true){
              //select在事件未处理时，是不会阻塞
               selector.select();
            //处理事件，SelectorKeys内部包含了所有发生的事件，所以需要迭代器遍历同时处理事件
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while(iter.hasNext()){
                //处理事件
                SelectionKey key = iter.next();
                iter.remove();
                System.out.println("key ="+key.toString());
                //区分事件类型
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    SelectionKey sckey = sc.register(selector, 0, null);
                    sckey.interestOps(SelectionKey.OP_READ);
//                     sckey.interestOps(sckey.interestOps()&(SelectionKey.OP_READ));
                    System.out.println("sc ="+ sc);
                }else if (key.isReadable()) {
                    SocketChannel channel1 = (SocketChannel)key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    try {
                        int read = channel1.read(buffer);
                     if(read==-1) {
                    //    channel1.close();
                        key.cancel();
                     }else{
                         buffer.flip();
                         String s = StandardCharsets.UTF_8.decode(buffer).toString();
                         System.out.println("buffer ="+s);
                     }
                    }catch (Exception e){
                        e.printStackTrace();
                        key.cancel();
                    }
                }
                count+=1;
                System.out.println("count ="+count);
//                System.out.println();
            }
        }
    }
}
