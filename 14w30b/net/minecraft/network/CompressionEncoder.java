package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Deflater;

public class CompressionEncoder extends MessageToByteEncoder {
   private final byte[] buffer = new byte[8192];
   private final Deflater deflator;
   private int threshold;

   public CompressionEncoder(int threshold) {
      this.threshold = threshold;
      this.deflator = new Deflater();
   }

   protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) {
      int var4 = byteBuf.readableBytes();
      PacketByteBuf var5 = new PacketByteBuf(byteBuf2);
      if (var4 < this.threshold) {
         var5.writeVarInt(0);
         var5.writeBytes(byteBuf);
      } else {
         byte[] var6 = new byte[var4];
         byteBuf.readBytes(var6);
         var5.writeVarInt(var6.length);
         this.deflator.setInput(var6, 0, var4);
         this.deflator.finish();

         while(!this.deflator.finished()) {
            int var7 = this.deflator.deflate(this.buffer);
            var5.writeBytes(this.buffer, 0, var7);
         }

         this.deflator.reset();
      }
   }

   public void setThreshold(int threshold) {
      this.threshold = threshold;
   }
}
