package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class DisconnectS2CPacket implements Packet {
   private Text reason;

   public DisconnectS2CPacket() {
   }

   public DisconnectS2CPacket(Text reason) {
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

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleDisconnect(this);
   }

   @Environment(EnvType.CLIENT)
   public Text getReason() {
      return this.reason;
   }
}
