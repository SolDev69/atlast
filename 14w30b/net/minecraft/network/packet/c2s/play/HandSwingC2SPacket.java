package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;

public class HandSwingC2SPacket implements Packet {
   @Override
   public void write(PacketByteBuf buffer) {
   }

   @Override
   public void read(PacketByteBuf buffer) {
   }

   public void handle(ServerPlayPacketHandler c_02lgcirvj) {
      c_02lgcirvj.handleHandSwing(this);
   }
}
