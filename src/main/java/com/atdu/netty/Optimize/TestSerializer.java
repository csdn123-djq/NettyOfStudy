package com.atdu.netty.Optimize;

import com.atdu.netty.Advanced.C2.Config;
import com.atdu.netty.Advanced.C2.Serializer;
import com.atdu.netty.Protocol.MessageCodecSharable;
import com.atdu.netty.message.LoginRequestMessage;
import com.atdu.netty.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TestSerializer {
    public static void main(String[] args) {
        MessageCodecSharable CODEC = new MessageCodecSharable();
        LoggingHandler LOGGING = new LoggingHandler(LogLevel.INFO);
        EmbeddedChannel channel = new EmbeddedChannel(LOGGING, CODEC, LOGGING);
        //记录序列化
        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
       // channel.writeOutbound(message);  //向Channel写入数据，模拟Channel发送数据
        ByteBuf byteBuf = messageToByteBuf(message);
        channel.writeInbound(byteBuf);//向Channel写入数据，模拟Channel收到数据
        //   writeOutbound(…)会将数据写到Channel并经过OutboundHandler，
   //   然后通过readOutbound(…)方法就能读取到处理后的数据。
   //   模拟收到数据也是类似的，通过writeInbound(…)和readInbound(…)方法。
   //   收到的数据和发送的数据的逻辑基本是一样的，经过ChannelPipeline后到达终点后会存储在EmbeddedChannel中。
    }
    public static ByteBuf messageToByteBuf(Message msg) {
        int algorithm = Config.getSerializerAlgorithm().ordinal();
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        out.writeBytes(new byte[]{1, 2, 3, 4});
        out.writeByte(1);
        out.writeByte(algorithm);
        out.writeByte(msg.getMessageType());
        out.writeInt(msg.getSequenceId());
        out.writeByte(0xff);
        byte[] bytes = Serializer.Algorithm.values()[algorithm].serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
        return out;
    }
}
