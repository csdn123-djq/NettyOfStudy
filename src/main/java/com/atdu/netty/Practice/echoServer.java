package com.atdu.netty.Practice;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.Charset;
import java.util.Scanner;

@Slf4j
//实现客户端与服务端的双向通信
public class echoServer {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .group(group)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                ByteBuf response = ctx.alloc().buffer(20);
                                response.writeBytes("hello; I am Server".getBytes());
                                ctx.writeAndFlush(response);
                            }
                        });
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = msg instanceof ByteBuf ? (ByteBuf) msg : null;
                                System.out.println(byteBuf.toString(Charset.defaultCharset()));
                                log.info(byteBuf.toString(Charset.defaultCharset()));
                                super.channelRead(ctx, msg);
                            }
                        });
                    }
                }).bind(8080);
        Channel channel = channelFuture.sync().channel();
        channel.closeFuture().addListener(future -> {
            group.shutdownGracefully();
        });
        new Thread(()->{
            Scanner scanner = new Scanner(System.in);
            while(true){
                String s = scanner.nextLine();
                if("q".equals(s)){
                    channel.close(
                    );
                    break;
                }
                log.info(s);
                channel.writeAndFlush(s);
            }
        }).start();
    }
}
