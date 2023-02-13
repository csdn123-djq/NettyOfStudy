package com.atdu.netty.Protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class ChatServer {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.INFO);//减少资源的浪费，因为线程安全导致可以共享
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss,work);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024,12,4,0,0));
                ch.pipeline().addLast(loggingHandler);
                ch.pipeline().addLast(messageCodecSharable);
            }
        });
        Channel channel = serverBootstrap.bind(8080).sync().channel();
        channel.closeFuture().sync();
        boss.shutdownGracefully();
        work.shutdownGracefully();
    }
}
