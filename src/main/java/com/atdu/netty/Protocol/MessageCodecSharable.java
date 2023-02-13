package com.atdu.netty.Protocol;

import com.atdu.netty.Advanced.C2.Config;
import com.atdu.netty.Advanced.C2.Serializer;
import com.atdu.netty.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ChannelHandler.Sharable
/* 什么时候可以用@Sharable
        * 当 handler 不保存状态时，就可以安全地在多线程下被共享
        * 但要注意对于编解码器类，
        * 不能继承 ByteToMessageCodec 或 CombinedChannelDuplexHandler 父类，他们的构造方法对 @Sharable 有限制<会抛出异常
        * 如果能确保编解码器不会保存状态，可以继承 MessageToMessageCodec 父类
*/
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        out.writeBytes(new byte[]{1,2,3,4});//魔数
        out.writeByte(1);//版本号
        out.writeByte(Config.getSerializerAlgorithm().ordinal());//序列化算法 jdk：0 ，json：1
        out.writeByte(msg.getMessageType());//字节的指令类型
        out.writeInt(msg.getSequenceId());//四个字节
        out.writeByte(0xff);//无意义，填充对齐
        //获取内容的字节数组
       /* ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);//将指定的对象写入 ObjectOutputStream
        // 过程：oos会把msg以间接的方式写入bos中，最得到bytes[]数组
        byte[] bytes = bos.toByteArray();*/
        byte[] bytes = Config.getSerializerAlgorithm().serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
        outList.add(out);
    }
    @Override //此时的ByteBuf包含的是完整的消息
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerAlgorithm = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();//填充
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes,0,length);
        //先用bis把bytes[]数组包装一下，然后ois从中读出来成为一个对象
//        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
//        Message message=(Message)ois.readObject();
        //找到反序列化算法
        Serializer.Algorithm algorithm=Serializer.Algorithm.values()[serializerAlgorithm];
        //确定具体消息类型
        Class<? > messageClass = Message.getMessageClass(messageType);//找到具体的class
       Object deserializer = algorithm.deserialize(messageClass, bytes);

        /*log.info("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
        log.info("{}",message);*/
        out.add(deserializer);
    }
}
