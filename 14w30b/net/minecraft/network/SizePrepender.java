package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class SizePrepender extends MessageToByteEncoder {
   protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) {
      int var4 = byteBuf.readableBytes();
      int var5 = PacketByteBuf.getVarIntSizeBytes(var4);
      if (var5 > 3) {
         throw new IllegalArgumentException("unable to fit " + var4 + " into " + 3);
      } else {
         PacketByteBuf var6 = new PacketByteBuf(byteBuf2);
         var6.ensureWritable(var5 + var4);
         var6.writeVarInt(var4);
         var6.writeBytes(byteBuf, byteBuf.readerIndex(), var4);
      }
   }
}
