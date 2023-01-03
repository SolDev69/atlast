package net.minecraft.network.packet.s2c.play;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class BlockUpdateS2CPacket implements Packet {
   private BlockPos pos;
   private BlockState state;

   public BlockUpdateS2CPacket() {
   }

   public BlockUpdateS2CPacket(World world, BlockPos pos) {
      this.pos = pos;
      this.state = world.getBlockState(pos);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.pos = buffer.readBlockPos();
      this.state = (BlockState)Block.STATE_REGISTRY.get(buffer.readVarInt());
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeBlockPos(this.pos);
      buffer.writeVarInt(Block.STATE_REGISTRY.getId(this.state));
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleBlockUpdate(this);
   }

   @Environment(EnvType.CLIENT)
   public BlockState getBlockState() {
      return this.state;
   }

   @Environment(EnvType.CLIENT)
   public BlockPos getPos() {
      return this.pos;
   }
}
