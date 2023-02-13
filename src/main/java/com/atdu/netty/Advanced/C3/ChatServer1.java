package com.atdu.netty.Advanced.C3;

import com.atdu.netty.Advanced.C3.Protocol.ProtocolFrameDecoder;
import com.atdu.netty.Advanced.C3.handler.*;
import com.atdu.netty.Protocol.MessageCodec;
import com.atdu.netty.Protocol.MessageCodecSharable;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DuplexChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer1 {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.INFO);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        LoginRequestMessageHandler LOGIN_HANDLER = new LoginRequestMessageHandler();
        ChatRequestMessageHandler CHAT_HANDLER = new ChatRequestMessageHandler();
        GroupCreateRequestMessageHandler GROUP_CREATE_HANDLER = new GroupCreateRequestMessageHandler();
        GroupChatMessageHandler GROUP_CHAT_HANDLER = new GroupChatMessageHandler();
        QuitHandler QUIT_HANDLER = new QuitHandler();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss,work);
            serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                 ch.pipeline().addLast(new ProtocolFrameDecoder());
                 //假死检测，避免长时间服务端无响应,终端长时间读与写的空闲时间
                ch.pipeline().addLast(new IdleStateHandler(5,0,0 ));
                ch.pipeline().addLast(new ChannelDuplexHandler(){
                    @Override//处理特殊事件
                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                        IdleStateEvent event=(IdleStateEvent) evt;
                        if(event.state()== IdleState.READER_IDLE){
                            log.info("已经 5s 没有读到数据");
                        }
                        super.userEventTriggered(ctx, evt);
                    }
                });//同时处理读与写事件
                ch.pipeline().addLast(LOGGING_HANDLER);
                 ch.pipeline().addLast(MESSAGE_CODEC);
                 ch.pipeline().addLast(LOGIN_HANDLER);
                 ch.pipeline().addLast(CHAT_HANDLER);
                 ch.pipeline().addLast(GROUP_CREATE_HANDLER);
                 ch.pipeline().addLast(GROUP_CHAT_HANDLER);
                 ch.pipeline().addLast(QUIT_HANDLER);
                }
            });
            Channel channel = serverBootstrap.bind(8080).sync().channel();
            channel.closeFuture().sync();
        }catch (Exception e){
             log.error("Error: {}",e);
        }finally {
            work.shutdownGracefully();
            boss.shutdownGracefully();
        }

    }

}
