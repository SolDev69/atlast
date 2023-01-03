package net.minecraft.network.packet.s2c.login;

import java.security.PublicKey;
import net.minecraft.client.network.handler.ClientLoginPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.EncryptionUtils;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class HelloS2CPacket implements Packet {
   private String serverId;
   private PublicKey publicKey;
   private byte[] nonce;

   public HelloS2CPacket() {
   }

   public HelloS2CPacket(String serverId, PublicKey publicKey, byte[] nonce) {
      this.serverId = serverId;
      this.publicKey = publicKey;
      this.nonce = nonce;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.serverId = buffer.readString(20);
      this.publicKey = EncryptionUtils.reconstitutePublicKey(buffer.readByteArray());
      this.nonce = buffer.readByteArray();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeString(this.serverId);
      buffer.writeByteArray(this.publicKey.getEncoded());
      buffer.writeByteArray(this.nonce);
   }

   public void handle(ClientLoginPacketHandler c_67vhogdbn) {
      c_67vhogdbn.handleHello(this);
   }

   @Environment(EnvType.CLIENT)
   public String getServerId() {
      return this.serverId;
   }

   @Environment(EnvType.CLIENT)
   public PublicKey getPublicKey() {
      return this.publicKey;
   }

   @Environment(EnvType.CLIENT)
   public byte[] getNonce() {
      return this.nonce;
   }
}
