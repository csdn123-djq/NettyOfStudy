package com.atdu.netty.BasicC1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class HelloClient {
    public static void main(String[] args) throws InterruptedException {
        new Bootstrap()//启动服务器，负责组装Netty组件
                .group(new NioEventLoopGroup())//分为BossEventLoop，WorkEventLoop（Selector，Thread），Group组
                .channel(NioSocketChannel.class)//选择服务器的的NioSocketChannels实现，
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost",8080))
                .sync()
                .channel()
                .writeAndFlush("Hello World");
    }
}
