package net.minecraft.network.packet.c2s.play;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;
import net.minecraft.server.world.ServerWorld;

public class PlayerSpectateC2SPacket implements Packet {
   private UUID targetUuid;

   public PlayerSpectateC2SPacket() {
   }

   public PlayerSpectateC2SPacket(UUID targetUuid) {
      this.targetUuid = targetUuid;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.targetUuid = buffer.readUuid();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeUuid(this.targetUuid);
   }

   public void handle(ServerPlayPacketHandler c_02lgcirvj) {
      c_02lgcirvj.handlePlayerSpectate(this);
   }

   public Entity getSpectateTarget(ServerWorld world) {
      return world.getEntity(this.targetUuid);
   }
}
