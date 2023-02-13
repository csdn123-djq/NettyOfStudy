package com.atdu.netty.RPC.Handler;

import com.atdu.netty.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    //响应中是Nio与main主线程之间的通信问题 --解决：利用SequenceId找到promise容器
    public static  final Map<Integer, Promise<Object>>PROMISES=new ConcurrentHashMap<>();//线程安全起见

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage message) throws Exception {
        Promise<Object> promise = PROMISES.get(message.getSequenceId());
         if (promise!=null){  //首先原则形判断promise容器存在
             //判断是否有问题存在
             Exception exceptionValue = message.getExceptionValue();
             Object returnValue = message.getReturnValue();
             if (exceptionValue!=null){
                 promise.setFailure(exceptionValue);
             }else {
                 promise.setSuccess(returnValue);
             }



         }
        log.info(" msg :{}",message);
    }
}
