package com.atdu.netty.RPC;

import com.atdu.netty.Protocol.MessageCodecSharable;
import com.atdu.netty.RPC.Decoder.ProcotolFrameDecoder;
import com.atdu.netty.RPC.Handler.RpcResponseMessageHandler;
import com.atdu.netty.message.RpcRequestMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.INFO);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        // rpc 响应消息处理器，待实现
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        try {

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast((ChannelHandler) RPC_HANDLER);
                }
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            ChannelFuture future = channel.writeAndFlush(new RpcRequestMessage(1,//序列标识
                    "com.atdu.netty.RPC.HelloService",//class路径
                    "sayHello",//方法名
                    String.class//返回类型的class
                    , new Class[]{String.class},//可能的形参组合成的数组
                    new Object[]{"张三"}));
            future.addListener(promise->{
                if (!promise.isSuccess()) {  //判断是否成功
                    //属于比较隐蔽的异常
                    Throwable cause = promise.cause();
                    log.error("cause : {}",cause);
                }
            });
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Error: {}",e);
        } finally {
            group.shutdownGracefully();
        }
    }

}

