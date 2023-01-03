package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.util.List;
import java.util.zip.Inflater;

public class CompressionDecoder extends ByteToMessageDecoder {
   private final Inflater inflater;
   private int threshold;

   public CompressionDecoder(int threshold) {
      this.threshold = threshold;
      this.inflater = new Inflater();
   }

   protected void decode(ChannelHandlerContext context, ByteBuf in, List out) {
      if (in.readableBytes() != 0) {
         PacketByteBuf var4 = new PacketByteBuf(in);
         int var5 = var4.readVarInt();
         if (var5 == 0) {
            out.add(var4.readBytes(var4.readableBytes()));
         } else {
            if (var5 < this.threshold) {
               throw new DecoderException("Badly compressed packet - size of " + var5 + " is below server threshold of " + this.threshold);
            }

            if (var5 > 2097152) {
               throw new DecoderException("Badly compressed packet - size of " + var5 + " is larger than protocol maximum of " + 2097152);
            }

            byte[] var6 = new byte[var4.readableBytes()];
            var4.readBytes(var6);
            this.inflater.setInput(var6);
            byte[] var7 = new byte[var5];
            this.inflater.inflate(var7);
            out.add(Unpooled.wrappedBuffer(var7));
            this.inflater.reset();
         }
      }
   }

   public void setThreshold(int threshold) {
      this.threshold = threshold;
   }
}
