package com.atdu.netty.Advanced.C3.handler;

import com.atdu.netty.Advanced.C3.session.GroupSessionFactory;
import com.atdu.netty.message.ChatResponseMessage;
import com.atdu.netty.message.GroupChatRequestMessage;
import com.atdu.netty.message.GroupChatResponseMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j
@ChannelHandler.Sharable
public class GroupChatMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        List<Channel> channels = GroupSessionFactory.getGroupSession().getMembersChannel(msg.getGroupName());
        for (Channel channel:channels) {
            channel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(), msg.getContent()));
            //站在接受方的角度，得到发出方发出的信息内容
        }
    }
}
