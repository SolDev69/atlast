package net.minecraft.network.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import javax.crypto.Cipher;

public class PacketEncryptor extends MessageToByteEncoder {
   private final PacketEncryptionManager encryptionManager;

   public PacketEncryptor(Cipher cipher) {
      this.encryptionManager = new PacketEncryptionManager(cipher);
   }

   protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) {
      this.encryptionManager.encrypt(byteBuf, byteBuf2);
   }
}
