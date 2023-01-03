package net.minecraft.network.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import javax.crypto.Cipher;

public class PacketEncryptionManager {
   private final Cipher cipher;
   private byte[] conversionBuffer = new byte[0];
   private byte[] encryptionBuffer = new byte[0];

   protected PacketEncryptionManager(Cipher cipher) {
      this.cipher = cipher;
   }

   private byte[] toByteArray(ByteBuf buffer) {
      int var2 = buffer.readableBytes();
      if (this.conversionBuffer.length < var2) {
         this.conversionBuffer = new byte[var2];
      }

      buffer.readBytes(this.conversionBuffer, 0, var2);
      return this.conversionBuffer;
   }

   protected ByteBuf decrypt(ChannelHandlerContext context, ByteBuf buffer) {
      int var3 = buffer.readableBytes();
      byte[] var4 = this.toByteArray(buffer);
      ByteBuf var5 = context.alloc().heapBuffer(this.cipher.getOutputSize(var3));
      var5.writerIndex(this.cipher.update(var4, 0, var3, var5.array(), var5.arrayOffset()));
      return var5;
   }

   protected void encrypt(ByteBuf bufferIn, ByteBuf bufferOut) {
      int var3 = bufferIn.readableBytes();
      byte[] var4 = this.toByteArray(bufferIn);
      int var5 = this.cipher.getOutputSize(var3);
      if (this.encryptionBuffer.length < var5) {
         this.encryptionBuffer = new byte[var5];
      }

      bufferOut.writeBytes(this.encryptionBuffer, 0, this.cipher.update(var4, 0, var3, this.encryptionBuffer));
   }
}
