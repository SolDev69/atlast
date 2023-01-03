package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;

public class SplitterHandler extends ByteToMessageDecoder {
   protected void decode(ChannelHandlerContext context, ByteBuf buffer, List output) {
      buffer.markReaderIndex();
      byte[] var4 = new byte[3];

      for(int var5 = 0; var5 < var4.length; ++var5) {
         if (!buffer.isReadable()) {
            buffer.resetReaderIndex();
            return;
         }

         var4[var5] = buffer.readByte();
         if (var4[var5] >= 0) {
            PacketByteBuf var6 = new PacketByteBuf(Unpooled.wrappedBuffer(var4));

            try {
               int var7 = var6.readVarInt();
               if (buffer.readableBytes() >= var7) {
                  output.add(buffer.readBytes(var7));
                  return;
               }

               buffer.resetReaderIndex();
            } finally {
               var6.release();
            }

            return;
         }
      }

      throw new CorruptedFrameException("length wider than 21-bit");
   }
}
