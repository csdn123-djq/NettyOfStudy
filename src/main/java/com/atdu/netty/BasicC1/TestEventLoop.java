package com.atdu.netty.BasicC1;

import io.netty.channel.DefaultEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
@Slf4j
public class TestEventLoop {
    public static void main(String[] args) {
        DefaultEventLoopGroup group = new DefaultEventLoopGroup(2);
        /*
          System.out.println(group.next());//next()：获取集合中下一个EventLoop对象
        System.out.println(group.next());
        System.out.println(group.next());
         */
//        for (EventExecutor eventLoop:group) {
//            System.out.println(eventLoop);
//        }
     //   System.out.println(NettyRuntime.availableProcessors());
        /*
         group.next().execute(()->{//  在将来的某个时间执行给定的命令。
            // 该命令可以在新线程、池线程或调用线程中执行，由实现自行 Executor 决定。
            try {
                Thread.sleep(1000);//一秒的休眠时间
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+" --ok");
        });//执行普通任务
         */
       //执行定时任务
        group.next().scheduleAtFixedRate(()->
        {
            log.info("ok");
//            System.out.println(Thread.currentThread().getName()+" --ok");
        },0,1, TimeUnit.SECONDS);
        log.info("main");
//        System.out.println(Thread.currentThread().getName()+" --main");

    }
}
