package com.atdu.netty.RPC.Handler;
import com.atdu.netty.Advanced.C3.service.ServicesFactory;
import com.atdu.netty.RPC.HelloService;
import com.atdu.netty.message.RpcRequestMessage;
import com.atdu.netty.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
@ChannelHandler.Sharable
@Slf4j
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

       //注意配置文件中接口的实现类
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RpcRequestMessage message = new RpcRequestMessage(1,//序列标识
                "com.atdu.netty.RPC.HelloService",//class路径
                "sayHello",//方法名
                String.class//返回类型的class
                ,new Class[]{String.class},//可能的形参组合成的数组
                new Object[]{"张三"});//实参
        Class<?> name = Class.forName(message.getInterfaceName());
        HelloService service = (HelloService) ServicesFactory.getService(name);
        //根据接口名称获取具体接口实现类
        Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
        Object invoke = method.invoke(service, message.getParameterValue());
        System.out.println(invoke);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) throws Exception {
        RpcResponseMessage response = new RpcResponseMessage();
        response.setSequenceId(message.getSequenceId());
        try {
            Class<?> name = Class.forName(message.getInterfaceName());
            HelloService service = (HelloService) ServicesFactory.getService(name);
            //根据接口名称获取具体接口实现类
            Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
            Object invoke = method.invoke(service, message.getParameterValue());
            response.setReturnValue(invoke);
        } catch (Exception e) {
            response.setExceptionValue(e);
        }
        ctx.writeAndFlush(response);//利用反射中基本知识，最后把结果返回给客户端
    }
}

