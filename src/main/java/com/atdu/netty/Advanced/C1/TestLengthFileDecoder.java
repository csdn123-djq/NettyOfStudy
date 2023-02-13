package com.atdu.netty.Advanced.C1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TestLengthFileDecoder {
    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
new LengthFieldBasedFrameDecoder(1024,0,4,1,4),
                new LoggingHandler(LogLevel.INFO)
        );
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        send(buffer,  "Hello, world");
        /*
        00 00 00 0c：长度为4，"Hello, world"=12（C）
         */
        send(buffer,  "hi!");
        /*
        00 00 00 03:长度为4，"hi!"（2+1=3）
         */
        channel.writeInbound(buffer);
    }

    private static void send(ByteBuf buffer, String context) {
        byte[] bytes = context.getBytes();
        int length = bytes.length;
        buffer.writeInt(length);
        buffer.writeByte(1);//添加额外内容后需要更改LengthAdjustment参数，跳过额外加入的字节的"长度"
        buffer.writeBytes(bytes);
    }
}
