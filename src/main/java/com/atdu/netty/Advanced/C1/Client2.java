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
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;


public class Client2 {
    public  static  final Logger log=LoggerFactory.getLogger(Client2.class);

    public static void main(String[] args) {
      /* fillBytes('1',2);
        fillBytes('2',5);
        fillBytes('3',10);*/
       send();
        System.out.println("Finished...");
    }
     private  static  byte[] fillBytes(char c,int len){
         byte[] bytes = new byte[10];
         Arrays.fill(bytes, (byte) '_');
         for (int i = 0; i < len; i++) {
             bytes[i] = (byte) c;
         }
         System.out.println(new String(bytes));
         return bytes;
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
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                  ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                      @Override
                      public void channelActive(ChannelHandlerContext ctx) throws Exception {
                   //     log.info("sending...");
                              ByteBuf buffer = ctx.alloc().buffer();
//             buffer.writeBytes(new byte[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15});
                          char ch='0';
                          Random random = new Random();
                          for (int i = 0; i < 10; i++) {
                              byte[] bytes = fillBytes(ch, random.nextInt(10) + 1);
                              buffer.writeBytes(bytes);
                              ch+=1;

                          }
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
