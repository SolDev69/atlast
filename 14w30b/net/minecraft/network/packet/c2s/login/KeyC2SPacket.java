package net.minecraft.network.packet.c2s.login;

import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.EncryptionUtils;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerLoginPacketHandler;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class KeyC2SPacket implements Packet {
   private byte[] keyBytes = new byte[0];
   private byte[] nonceBytes = new byte[0];

   public KeyC2SPacket() {
   }

   @Environment(EnvType.CLIENT)
   public KeyC2SPacket(SecretKey secretKey, PublicKey publicKey, byte[] data) {
      this.keyBytes = EncryptionUtils.encrypt(publicKey, secretKey.getEncoded());
      this.nonceBytes = EncryptionUtils.encrypt(publicKey, data);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.keyBytes = buffer.readByteArray();
      this.nonceBytes = buffer.readByteArray();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeByteArray(this.keyBytes);
      buffer.writeByteArray(this.nonceBytes);
   }

   public void handle(ServerLoginPacketHandler c_33nfqyvka) {
      c_33nfqyvka.handleKey(this);
   }

   public SecretKey getSecretKey(PrivateKey privateKey) {
      return EncryptionUtils.decryptSecretKey(privateKey, this.keyBytes);
   }

   public byte[] getNonce(PrivateKey privateKey) {
      return privateKey == null ? this.nonceBytes : EncryptionUtils.decrypt(privateKey, this.nonceBytes);
   }
}
