package com.atdu.netty.Protocol;

import com.atdu.netty.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/*
   自定义协议要素
* 魔数，用来在第一时间判定是否是无效数据包
* 版本号，可以支持协议的升级
* 序列化算法，消息正文到底采用哪种序列化反序列化方式，可以由此扩展，例如：json、protobuf、hessian、jdk
* 指令类型，是登录、注册、单聊、群聊... 跟业务相关
* 请求序号，为了双工通信，提供异步能力
* 正文长度
* 消息正文
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {
    @Override //编码：出栈前，将Message转变成ByteBuf
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
       out.writeBytes(new byte[]{1,2,3,4});//魔数
        out.writeByte(1);//版本号
        out.writeByte(0);//序列化算法 jdk：0 ，json：1
        out.writeByte(msg.getMessageType());//字节的指令类型
        out.writeInt(msg.getSequenceId());//四个字节
        out.writeByte(0xff);//无意义，填充对齐
        //获取内容的字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);//将指定的对象写入 ObjectOutputStream
        // 过程：oos会把msg以间接的方式写入bos中，最得到bytes[]数组
        byte[] bytes = bos.toByteArray();
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }

    @Override//解码：入栈前，将ByteBuf转变成Message
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
      in.readByte();//填充
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes,0,length);
            //先用bis把bytes[]数组包装一下，然后ois从中读出来成为一个对象
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
          Message message=(Message)ois.readObject();
        log.info("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
        log.info("{}",message);
        out.add(message);
    }
}
