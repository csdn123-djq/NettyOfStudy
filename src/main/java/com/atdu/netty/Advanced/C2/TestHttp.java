package com.atdu.netty.Advanced.C2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

@Slf4j
public class TestHttp {

    public static void main(String[] args)  {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss,work);
            serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                  ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                  ch.pipeline().addLast(new HttpServerCodec());   //解码               编码
                    //继承之CombinedChannelDuplexHandler<HttpResponseDecoder,HttpRequestEncoder>
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                        @Override           //可以简单的只关注HttpRequest（请求体，请求行）的消息 ，HttpContext:请求内容
                        protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
                             log.info(msg.uri());//获取URI地址
                            //返回响应
DefaultFullHttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
                            byte[] bytes = "<h1>Hello,world</h1>".getBytes();
                            response.headers().setInt(CONTENT_LENGTH, bytes.length);
                            response.content().writeBytes(bytes);//响应中写入内容,其本质上也是ByteBuf
                            ctx.writeAndFlush(response);
                            //一直转圈：因为浏览器默认会接受更多的响应,故需要设置一定的内容的长度

                        }
                    });
                }
            });
            Channel channel = serverBootstrap.bind(8080).sync().channel();
            channel.closeFuture().sync();
        }catch (Exception e){
          log.error("Errro :{}",e);
        }finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();

        }


    }
}
