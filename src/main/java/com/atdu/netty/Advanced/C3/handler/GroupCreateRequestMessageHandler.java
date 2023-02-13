package com.atdu.netty.Advanced.C3.handler;

import com.atdu.netty.Advanced.C3.session.*;
import com.atdu.netty.message.GroupCreateRequestMessage;
import com.atdu.netty.message.GroupCreateResponseMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
@Slf4j
@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        log.info(groupName);
        Set<String> members = msg.getMembers();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        if(group==null){
            ctx.writeAndFlush(new GroupCreateResponseMessage(true,groupName+"创建成功"));
            //发送拉群信息
            List<Channel> channels = groupSession.getMembersChannel(groupName);
            for (Channel channel:channels) {
                channel.writeAndFlush(new GroupCreateResponseMessage(true,"您已被拉入 "+groupName+" 群聊"));
            }
        }else{
            ctx.writeAndFlush(new GroupCreateResponseMessage(false,groupName+"已经存在"));
        }
    }
}
