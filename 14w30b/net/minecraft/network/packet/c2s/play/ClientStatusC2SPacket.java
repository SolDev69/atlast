package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;

public class ClientStatusC2SPacket implements Packet {
   private ClientStatusC2SPacket.Status status;

   public ClientStatusC2SPacket() {
   }

   public ClientStatusC2SPacket(ClientStatusC2SPacket.Status status) {
      this.status = status;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.status = (ClientStatusC2SPacket.Status)buffer.readEnum(ClientStatusC2SPacket.Status.class);
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeEnum(this.status);
   }

   public void handle(ServerPlayPacketHandler c_02lgcirvj) {
      c_02lgcirvj.handleClientStatus(this);
   }

   public ClientStatusC2SPacket.Status getStatus() {
      return this.status;
   }

   public static enum Status {
      PERFORM_RESPAWN,
      REQUEST_STATS,
      OPEN_INVENTORY_ACHIEVEMENT;
   }
}
