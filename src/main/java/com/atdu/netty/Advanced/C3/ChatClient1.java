package com.atdu.netty.Advanced.C3;

import com.atdu.netty.Advanced.C3.Protocol.ProtocolFrameDecoder;
import com.atdu.netty.Advanced.C3.handler.GroupCreateRequestMessageHandler;
import com.atdu.netty.Protocol.MessageCodec;
import com.atdu.netty.message.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient1 {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.INFO);
        MessageCodec MESSAGE_CODEC = new MessageCodec();
        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);//这里初始值为1，当值为0时，代码会继续向下进行
        //CountDownLatch：一种同步辅助工具，它允许一个或多个线程等待，直到在其他线程中执行的一组操作完成。
        AtomicBoolean LOGIN=new AtomicBoolean(false);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                  //  ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);//不关心的方面设为 0
                    ch.pipeline().addLast(new IdleStateHandler(0,3,0 ));
                    ch.pipeline().addLast(new ChannelDuplexHandler(){
                        @Override//处理特殊事件
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event=(IdleStateEvent) evt;
                            if(event.state()== IdleState.WRITER_IDLE){
                                log.info("已经 3s 没有写数据了");//证明客户端是Online的
                                ctx.writeAndFlush(new PingMessage());
                            }
                            super.userEventTriggered(ctx, evt);
                        }
                    });//同时处理读与写事件
                    ch.pipeline().addLast("Client Handler",new ChannelInboundHandlerAdapter(){

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.info("msg: {}",msg);
                            //需要告诉何时减计数的操作
                            if ((msg instanceof LoginResponseMessage)) {
                               LoginResponseMessage response=(LoginResponseMessage)msg;
                               if(response.isSuccess()){ //如果登陆成功
                                   LOGIN.set(true);
                               }
                                WAIT_FOR_LOGIN.countDown();//把计数减为0，唤醒阻塞的线程，让代码沿着阻塞代码继续进行
                            }
                        }


                        @Override //处理业务之连接建立
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                             //创建另外一个线程既负责接受用户在控制台的输入信息，又负责向服务端发送各种信息
                            new Thread(()->{
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("请输入用户名：");
                                String username = scanner.nextLine();
                                System.out.println("请输入密码：");
                                String password = scanner.nextLine();
                                //构造用户-对象
                                LoginRequestMessage message = new LoginRequestMessage(username, password);
                                ctx.writeAndFlush(message);
                                //消息传递的过程：由于是Inbound，然后writeAndFlush会触发出站操作
                                // 从当前handler依次向前经过 (编码)-->(日志）
                                System.out.println("等待后续操作....");
                                try {
                                    WAIT_FOR_LOGIN.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (!LOGIN.get()) { //登陆失败，则退出System in线程
                                    ctx.channel().close();
                                    return;
                                }
                               while (true){
                                   System.out.println("==================================");
                                   System.out.println("send [username] [content]");
                                   System.out.println("gsend [group name] [content]");
                                   System.out.println("gcreate [group name] [m1,m2,m3...]");
                                   System.out.println("gmembers [group name]");
                                   System.out.println("gjoin [group name]");
                                   System.out.println("gquit [group name]");
                                   System.out.println("quit");
                                   System.out.println("==================================");
                                   String command = scanner.nextLine();
                                   String[] s = command.split(" ");

                                   switch (s[0]){
                                       case "send":
                                           ctx.writeAndFlush(new ChatRequestMessage(username,s[1],s[2]));
                                           break;
                                       case "gsend":
                                           ctx.writeAndFlush(new GroupChatRequestMessage(username,s[1],s[2]));
                                           break;
                                       case "gcreate":
                                           HashSet set=new HashSet(Arrays.asList(  s[2].split(",")));
                                           set.add(username);
                                           ctx.writeAndFlush(new GroupCreateRequestMessage(s[1],set));
                                           break;
                                       case "gmember":
                                           ctx.writeAndFlush(new GroupMembersRequestMessage(s[1]));
                                           break;
                                       case "gjoin":
                                           ctx.writeAndFlush(new GroupJoinRequestMessage(username,s[1]));
                                           break;
                                       case "gquit":
                                           ctx.writeAndFlush(new GroupQuitRequestMessage(username,s[1]));
                                           break;
                                           case "quit":
                                               ctx.channel().close();
                                               return;
                                   }
                               }
                            },"system in").start();
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
           log.error(" Error {}",e);
        } finally {
            group.shutdownGracefully();
        }

    }
}
