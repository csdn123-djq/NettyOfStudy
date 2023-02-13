package com.atdu.Selector;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
public class MutilThreadServer {
    /*
    要求：采用多线程：一个选择器Selector专门负责与客户端Accept，另外一个专门负责和客户端Read事件
     */
    public static void main(String[] args) {
        try {
            Boss boss = new Boss();
            boss.register();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static class  Boss implements  Runnable{
        private Selector boss;
        private  Work[] works;
        private  volatile boolean start=false;
       AtomicInteger index= new AtomicInteger();
        public  void  register() throws IOException {
            if(!start){
                System.out.println("1");
                ServerSocketChannel ssc = ServerSocketChannel.open();
                ssc.bind(new InetSocketAddress(8080));
                ssc.configureBlocking(false);
                 boss = Selector.open();//细节之一：不能重新身明类型，保证全局变量得到实例化
                works=initWork();
                SelectionKey ssckey = ssc.register(boss, 0, null);
                ssckey.interestOps(SelectionKey.OP_ACCEPT);
                new Thread(this,"boss").start();
                System.out.println("boss start....");
                start=true;
            }
        }
       public  Work[] initWork(){
            Work[] worksLoop=new Work[2];
           for (int i = 0; i < worksLoop.length; i++) {
               worksLoop[i]=new Work(i);
           }
           return worksLoop;
       }
        @Override
        public void run()  {
        //    System.out.println("---1");
           while(true){
               try {
                   boss.select();
                   Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
                   while(iter.hasNext()){
                       SelectionKey key = iter.next();
                       iter.remove();
                       if(key.isAcceptable()){
                           ServerSocketChannel c = (ServerSocketChannel) key.channel();
                           SocketChannel sc = c.accept();
                           sc.configureBlocking(false);
                           System.out.println("localhost ="+sc.getRemoteAddress());
                          works[index.getAndIncrement()%works.length].register(sc);
                       }
                   }
               } catch (IOException e) {
                   e.printStackTrace();
               }

           }
        }
    }
    static  class  Work implements  Runnable{
        private  Selector work;
        private  volatile  boolean start=false;
        private final int index;
        /*
        新元素插入队列尾部，队列检索操作获取队列头部的元素。当许多线程将共享对公共集合的访问权限时，
        A ConcurrentLinkedQueue 是一个合适的选择
         */
        private  final ConcurrentLinkedQueue<Runnable> task=new ConcurrentLinkedQueue<>();
        public Work(int index) {
               this.index=index;
        }

        public void  register(SocketChannel sc){
            if (!start) {
                try {
                    work=Selector.open();
                    new Thread(this,"work-"+index).start();
                    start=true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //细节二：往队列中加入事件
          task.add(()->{
              try {
                  SelectionKey sckey = sc.register(work, 0, null);
                  sckey.interestOps(SelectionKey.OP_READ);
                  work.selectNow();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          });
            work.wakeup();
        }
        @Override
        public void run() {
           while(true){
               try {//细节三：获取队列中的事件
                   work.select();
                   Runnable poll = task.poll();
                   if(poll!=null){
                      poll.run();
                   }
                   Iterator<SelectionKey> iter = work.selectedKeys().iterator();
                   while(iter.hasNext()){
                       SelectionKey key = iter.next();
                       iter.remove();
                      if(key.isReadable()){
                         SocketChannel sc=(SocketChannel) key.channel();
                          sc.configureBlocking(false);
                          ByteBuffer buffer=ByteBuffer.allocate(128);
                          int read = sc.read(buffer);
                          if(read==-1){
                              key.cancel();
                              sc.close();
                          }else{
                              buffer.flip();
                              System.out.print("localhost ="+sc.getRemoteAddress());
                              String s = StandardCharsets.UTF_8.decode(buffer).toString();
                              System.out.println(" target ="+s);
                          }
                      }
                   }
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
        }
    }
}
