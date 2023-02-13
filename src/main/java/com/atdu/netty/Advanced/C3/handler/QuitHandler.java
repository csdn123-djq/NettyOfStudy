package com.atdu.netty.Advanced.C3.handler;

import com.atdu.netty.Advanced.C3.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class QuitHandler extends ChannelInboundHandlerAdapter {
    @Override//连接正常断开时触发Inactive
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.info("{} 已经断开",ctx.channel());
    }

    @Override//连接由于某种异常断开
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.info("{} 已经断开 ,异常是{}",ctx.channel(),cause.getMessage());
    }
}
