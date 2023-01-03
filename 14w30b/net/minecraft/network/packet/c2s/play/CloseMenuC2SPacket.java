package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class CloseMenuC2SPacket implements Packet {
   private int id;

   public CloseMenuC2SPacket() {
   }

   @Environment(EnvType.CLIENT)
   public CloseMenuC2SPacket(int id) {
      this.id = id;
   }

   public void handle(ServerPlayPacketHandler c_02lgcirvj) {
      c_02lgcirvj.handleCloseMenu(this);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readByte();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeByte(this.id);
   }
}
