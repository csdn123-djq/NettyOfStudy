package com.atdu.netty.BasicC2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

public class TestSlice {

    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a','b','c','d','e','f','g','h','i','j'});
        log(buf);
        //逻辑上的切片，用指针替代-但同样是利用的同个内存块
        ByteBuf f1 = buf.slice(0, 5);
        f1.retain();//对f1进行保留
        ByteBuf f2 = buf.slice(5, 5);
        f2.retain();
//        log(f1);
//        log(f2);
//        ByteBuf buf1 = f1.setByte(0, 'a');
//        log(buf1);
            log(f1);
        System.out.println("释放原有ByteBuf");
        f1.release();
        log(f1);
    }
    public static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index:").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(NEWLINE);
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf);
    }
}
