package com.atdu.netty.BasicC1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.nio.channels.Channel;

public class ChannelFutureClient {
    public static void main(String[] args) throws InterruptedException {
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<io.netty.channel.Channel>() {
                    @Override
                    protected void initChannel(io.netty.channel.Channel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })//异步非阻塞方法，不能获取到channel对象
                .connect(new InetSocketAddress("localhost", 8080));
           //2.1:利用sync的方法，等待连接建立完成
           Channel channel = (Channel) channelFuture.sync().channel();//sync():是同步等待连接建立完成
        System.out.println(Thread.currentThread().getName()+" :"+channel);
      /*
      2.2:采用回调
       System.out.println(Thread.currentThread().getName()+" :"+channelFuture.channel());
        channelFuture.addListener((ChannelFutureListener) Future->{
           Channel channel = Future.channel();
           System.out.println(Thread.currentThread().getName()+" :"+channel);
       });
       */

    }
}
