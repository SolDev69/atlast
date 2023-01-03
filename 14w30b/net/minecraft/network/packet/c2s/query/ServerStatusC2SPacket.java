package net.minecraft.network.packet.c2s.query;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerQueryPacketHandler;

public class ServerStatusC2SPacket implements Packet {
   @Override
   public void write(PacketByteBuf buffer) {
   }

   @Override
   public void read(PacketByteBuf buffer) {
   }

   public void handle(ServerQueryPacketHandler c_57rlldbju) {
      c_57rlldbju.handleServerStatus(this);
   }
}
