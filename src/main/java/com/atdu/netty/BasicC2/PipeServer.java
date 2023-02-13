package com.atdu.netty.BasicC2;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;


@Slf4j
public class PipeServer {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("h1",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.info("h1");
                                ByteBuf buf=(ByteBuf) msg;
                                String name = buf.toString(Charset.defaultCharset());
                               //如果不调用，相当于整个链就断开了
                                super.channelRead(ctx,name);//或者ctx.fireChannel(msg)也是ok的
                            }
                        });
                        pipeline.addLast("h2",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object name) throws Exception {
                                log.info("h2");
                                Student student = new Student(name.toString());
                                super.channelRead(ctx, student);
                            }
                        });
                        pipeline.addLast("h3",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.info("h3");
                            //    log.info("h3, 结果是{} ,class:{}",msg,msg.getClass());
//                                super.channelRead(ctx, msg);
                                ctx.write(ctx.alloc().buffer().writeBytes("server....".getBytes()));//从当前handler向前寻找出栈handler，看是否有handler
                              // ch.writeAndFlush(ctx.alloc().buffer().writeBytes("server....".getBytes()));
                               //触发下面的出栈处理器
                            }
                        });
                        pipeline.addLast("h4",new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.info("h4");
                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast("h5",new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.info("h5");
                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast("h6",new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.info("h6");
                                super.write(ctx, msg, promise);
                            }
                        });

    }
}).bind(8080);
    }
    @Data
    @AllArgsConstructor
    static  class  Student{
        private  String name;
    }
}