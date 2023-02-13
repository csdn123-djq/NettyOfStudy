package com.atdu.netty.BasicC1;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;
import java.util.Scanner;
@Slf4j
public class CloseFutureClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));
        Channel channel = channelFuture.sync().channel();//两种方式：同步阻塞+回调
           log.info("{}",channel);
        new Thread(()->{
            Scanner scanner = new Scanner(System.in);
          while (true){
              String line = scanner.nextLine();
              if("q".equals(line)){
                  channel.close();
                  break;
              }
              channel.writeAndFlush(line);
          }
        },"input").start();
        ChannelFuture closeFuture = channel.closeFuture();
        //同步处理关闭
       /*
        log.debug("waiting close.....");
        closeFuture.sync();
        log.debug("关闭之后的操作");*/
        //异步
//     closeFuture.addListener(new ChannelFutureListener() {
//         @Override
//         public void operationComplete(ChannelFuture channelFuture) throws Exception {
//             log.info("关闭之后的操作");
//             group.shutdownGracefully();
//         }
//     });
    }
}
