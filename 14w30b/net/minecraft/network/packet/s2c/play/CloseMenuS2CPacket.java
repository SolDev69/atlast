package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;

public class CloseMenuS2CPacket implements Packet {
   private int id;

   public CloseMenuS2CPacket() {
   }

   public CloseMenuS2CPacket(int id) {
      this.id = id;
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleCloseMenu(this);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readUnsignedByte();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeByte(this.id);
   }
}
