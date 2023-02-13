package com.atdu.netty.Advanced.C3.handler;

import com.atdu.netty.Advanced.C3.session.SessionFactory;
import com.atdu.netty.message.ChatRequestMessage;
import com.atdu.netty.message.ChatResponseMessage;
import com.atdu.netty.message.LoginResponseMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String to = msg.getTo();
        Channel channel = SessionFactory.getSession().getChannel(to);
        //判断to的channel是否在线
        if (channel!=null){
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(),msg.getContent()));
        }else{
            ctx.writeAndFlush(new ChatResponseMessage(false,"对方用户不存在或不在线"));
            //这是在服务器的角度，给发送方的响应
        }

    }
}
