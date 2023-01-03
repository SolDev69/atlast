package net.minecraft.network.packet.s2c.login;

import net.minecraft.client.network.handler.ClientLoginPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class LoginFailS2CPacket implements Packet {
   private Text reason;

   public LoginFailS2CPacket() {
   }

   public LoginFailS2CPacket(Text reason) {
      this.reason = reason;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.reason = buffer.readText();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeText(this.reason);
   }

   public void handle(ClientLoginPacketHandler c_67vhogdbn) {
      c_67vhogdbn.handleLoginFail(this);
   }

   @Environment(EnvType.CLIENT)
   public Text getReason() {
      return this.reason;
   }
}
