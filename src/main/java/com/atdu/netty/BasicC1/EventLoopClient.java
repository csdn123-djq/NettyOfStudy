package com.atdu.netty.BasicC1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class EventLoopClient {
    public static void main(String[] args) {
        try {
            Channel channel = new Bootstrap()
                    .group(new NioEventLoopGroup())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new StringEncoder());
                        }
                    })
                    .connect(new InetSocketAddress("localhost", 8080))
                    .sync().channel();
            System.out.println(channel);
            System.out.println("1");
                 //  channel.writeAndFlush("Hello,EventLoopClient");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
