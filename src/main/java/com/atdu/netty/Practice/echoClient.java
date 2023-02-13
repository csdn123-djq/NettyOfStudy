package com.atdu.netty.Practice;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.Charset;
import java.util.Scanner;

@Slf4j
public class echoClient {
    public static void main(String[] args)  {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Channel channel = new Bootstrap()
                    .channel(NioSocketChannel.class)
                    .group(group)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                   @Override
                 protected void initChannel(NioSocketChannel ch) throws Exception {
                       ch.pipeline().addLast(new StringEncoder());
                       ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                           @Override
                           public void channelActive(ChannelHandlerContext ctx) throws Exception {
                               ByteBuf buffer = ctx.alloc().buffer();
                               buffer.writeBytes("hello Client".getBytes());
                               ctx.writeAndFlush(buffer);
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
                 }).connect("127.0.0.1", 8080).sync().channel();
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
        } catch (Exception e) {
            log.error("Client Error {}",e);
        }

    }
}
