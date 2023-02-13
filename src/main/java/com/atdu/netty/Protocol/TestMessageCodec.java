package com.atdu.netty.Protocol;

import com.atdu.netty.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TestMessageCodec {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LoggingHandler(LogLevel.INFO),
                //处理粘包，半包的问题
                new LengthFieldBasedFrameDecoder(1024,12,4,0,0),
                new MessageCodec());
        //测试编码
        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123", "张三");
        channel.writeOutbound(message);
        //使用writeOutbound（）方法将消息写到Channel中，并通过ChannelPipeline沿着出站的方向传递。
        // 随后，你可以使用readOutbound（）方法来读取已被处理过的消息，已确定结果是否和预期一样。
        // 类似地，对于入站数据，你需要使用writeInbound（）和readInbound（）方法。
       //测试解码
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null,message,buffer);
        ByteBuf s1 = buffer.slice(0, 100);

        ByteBuf s2 = buffer.slice(100, buffer.readableBytes() - 100);
        s1.retain();//防止释放后内存块的丢失，需要保持s1物理内存
        channel.writeInbound(s1);
        channel.writeInbound(s2);
    }
}
