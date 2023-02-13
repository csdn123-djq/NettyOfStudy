package com.atdu.Selector;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class ServerOfTest {
    public static void split(ByteBuffer source){
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            if(source.get(i)=='\n'){
                int len=i+1-source.position();//记录
                ByteBuffer target=ByteBuffer.allocate(len);
                for(int j=0;j<len;j+=1){
                    target.put(source.get());
                }
                System.out.println("target="+new String(target.array()));
            }
        }
        source.compact();
    }
    public static void main(String[] args) throws Exception{
        ServerSocketChannel ssc = ServerSocketChannel.open();
        Selector selector = Selector.open();
        ssc.configureBlocking(false);
        SelectionKey sscKey = ssc.register(selector, 0, null);
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
                    ByteBuffer buffer= ByteBuffer.allocate(16);
                    SelectionKey sckey = sc.register(selector, 0, buffer);
                    sckey.interestOps(SelectionKey.OP_READ);
//                     sckey.interestOps(sckey.interestOps()&(SelectionKey.OP_READ));
                    System.out.println("sc ="+ sc);
                }else if (key.isReadable()) {
                    try{
                        SocketChannel channel1 = (SocketChannel)key.channel();
                        ByteBuffer buffer = (ByteBuffer)key.attachment();
                        int read = channel1.read(buffer);
                        if(read==-1){
                            key.cancel();
                        }else{
                            split(buffer);
                            if(buffer.position()==buffer.limit()){
                                ByteBuffer newBuffer=ByteBuffer.allocate(buffer.capacity()*2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                key.attach(newBuffer);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        key.cancel();
                    }
                }
            }
        }
    }
}
