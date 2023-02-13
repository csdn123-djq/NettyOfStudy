package com.atdu.netty.RPC;

import com.atdu.netty.Protocol.MessageCodecSharable;
import com.atdu.netty.RPC.Decoder.ProcotolFrameDecoder;
import com.atdu.netty.RPC.Handler.RpcResponseMessageHandler;
import com.atdu.netty.message.RpcRequestMessage;


import java.lang.reflect.Proxy;

@Slf4j
public class RpcClientManager {
    public static void main(String[] args) { //用户 -->代理模式 -->符合RPC框架发送
        /*getChannel().writeAndFlush(new RpcRequestMessage(1,//序列标识
                "com.atdu.netty.RPC.HelloService" ,//class路径
                "sayHello",//方法名
                String.class//返回类型的class
                , new Class[]{String.class},//可能的形参组合成的数组
                new Object[]{"张三"}));*/
        HelloService service = getProxyService(HelloService.class);
        System.out.println(service.sayHello("lisi"));
        System.out.println(service.sayHello("wangwu"));
    }
    //创建代理类
    public  static <T>  T getProxyService(Class<T> serviceClass){

        ClassLoader loader=serviceClass.getClassLoader();
        Class<?>[] interfaces=new Class[]{serviceClass};
        Object o = Proxy.newProxyInstance(loader, interfaces, ((proxy, method, args) -> {//1.将方法转换为消息对象
            // 2. 把消息传送出去
            //proxy:正在代理哪个类的代理
            //method：正在实现的方法
            //args:此时此刻传入的实际参数
            int id = SequenceIdGenerator.nextId();
            RpcRequestMessage response = new RpcRequestMessage(
                      id ,
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
            getChannel().writeAndFlush(response);//发送信息
            //准备空容器promise接受结果
            DefaultPromise<Object>  promise=new DefaultPromise<>(getChannel().eventLoop());//指定promise接受结果的线程
            RpcResponseMessageHandler.PROMISES.put(id,promise);
            promise.await();//等待promise结果
            if (promise.isSuccess()){
               return  promise.getNow();
            }else {
                throw  new RuntimeException(promise.cause());//拿到异常信息，并打印出来
            }
        }));
        return (T)o;
    }
    public  static  Channel channel=null;
    //因为Channel只有一个，不可能来一个writeAndFlush（）创建一个channel，故用单例模式
   public  static  final  Object LOCK=new Object();
    public  static  Channel getChannel(){
        if(channel!=null){
            return channel;
        }
        synchronized(LOCK){
            if(channel!=null){
                return channel;
            }
            InitChannel();
            return channel;
        }
    }

    private static void InitChannel() {
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
            channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            log.error("Error: {}",e);
        }
    }

}

