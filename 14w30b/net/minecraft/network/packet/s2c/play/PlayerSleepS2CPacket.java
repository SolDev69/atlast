package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PlayerSleepS2CPacket implements Packet {
   private int id;
   private BlockPos pos;

   public PlayerSleepS2CPacket() {
   }

   public PlayerSleepS2CPacket(PlayerEntity player, BlockPos pos) {
      this.id = player.getNetworkId();
      this.pos = pos;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readVarInt();
      this.pos = buffer.readBlockPos();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.id);
      buffer.writeBlockPos(this.pos);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handlePlayerSleep(this);
   }

   @Environment(EnvType.CLIENT)
   public PlayerEntity getPlayer(World world) {
      return (PlayerEntity)world.getEntity(this.id);
   }

   @Environment(EnvType.CLIENT)
   public BlockPos getPos() {
      return this.pos;
   }
}
