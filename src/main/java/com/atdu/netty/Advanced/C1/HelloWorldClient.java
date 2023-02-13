package com.atdu.netty.Advanced.C1;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HelloWorldClient {
    public  static  final Logger log=LoggerFactory.getLogger(HelloWorldClient.class);

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            send();
        }
        System.out.println("Finished...");
    }

    private static void send() {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(worker);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                //  log.info("connected...");
                  ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                      @Override
                      public void channelActive(ChannelHandlerContext ctx) throws Exception {
                   //     log.info("sending...");
                              ByteBuf buffer = ctx.alloc().buffer();
                              buffer.writeBytes(new byte[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15});
                              ctx.writeAndFlush(buffer);
                          ctx.channel().close();
                      }
                  });
                }
            });
            ChannelFuture channelFuture = bootstrap.connect("localhost", 8080).sync();
          channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            log.info("Client Error {}",e);
        }finally {
worker.shutdownGracefully();
        }
    }
}
