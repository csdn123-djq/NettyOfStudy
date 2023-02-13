package com.atdu.netty.BasicC1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
public class EventLoopServer {//更本质的处理io任务
    public static void main(String[] args) {
        //细分二：单独的EventGroup
       EventLoop group = new DefaultEventLoop();//Default不处理io事件
        new ServerBootstrap()
                 // 细分1：
                 // boss线程：只负责ServerSocketChannel上的accept事件  ，work线程：只负责SocketChannel线程
                 .group( new NioEventLoopGroup(),new NioEventLoopGroup(2))
                 .channel(NioServerSocketChannel.class)
                 .childHandler(new ChannelInitializer<NioSocketChannel>() {
                     @Override
                     protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast("handler1",new ChannelInboundHandlerAdapter(){
                            //每次会变化，不固定的是Handler方法
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
//                                log.info(buf.toString(Charset.defaultCharset()));
                                System.out.println(Thread.currentThread().getName()+" :"+buf.toString(Charset.defaultCharset()));
                                ctx.fireChannelRead(msg);
                            }
                        }).addLast(group,"handler2",new ChannelInboundHandlerAdapter(){
                            //单独利用handler2来处理额外新的任务，与io操作分隔开
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
//                                log.info(buf.toString(Charset.defaultCharset()));
                                System.out.println(Thread.currentThread().getName()+" :"+buf.toString(Charset.defaultCharset()));
                            }
                        });

                     }
                 }).bind(8080);
    }
}
