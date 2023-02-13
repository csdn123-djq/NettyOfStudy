package com.atdu.netty.Optimize;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestConnectionTimeOut { //改进后，只要是服务端未开启，客户端便会报异常
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .channel(NioSocketChannel.class)
                    .group(group)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,30)//设置客户端多少时间未连接服务器的警示
                    .handler(new LoggingHandler(LogLevel.INFO));
            ChannelFuture future = bootstrap.connect("localhost", 8080);
            future.sync().channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("timeout");//最好用log.error稳妥些
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }
}
