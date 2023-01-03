package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PlayerSpawnPointS2CPacket implements Packet {
   private BlockPos spawnPoint;

   public PlayerSpawnPointS2CPacket() {
   }

   public PlayerSpawnPointS2CPacket(BlockPos spawnPoint) {
      this.spawnPoint = spawnPoint;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.spawnPoint = buffer.readBlockPos();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeBlockPos(this.spawnPoint);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handlePlayerSpawnPoint(this);
   }

   @Environment(EnvType.CLIENT)
   public BlockPos getSpawnPoint() {
      return this.spawnPoint;
   }
}
