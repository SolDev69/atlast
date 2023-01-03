package net.minecraft.network.packet.s2c.play;

import net.minecraft.block.Block;
import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class BlockEventS2CPacket implements Packet {
   private BlockPos pos;
   private int type;
   private int data;
   private Block block;

   public BlockEventS2CPacket() {
   }

   public BlockEventS2CPacket(BlockPos pos, Block block, int type, int data) {
      this.pos = pos;
      this.type = type;
      this.data = data;
      this.block = block;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.pos = buffer.readBlockPos();
      this.type = buffer.readUnsignedByte();
      this.data = buffer.readUnsignedByte();
      this.block = Block.byRawId(buffer.readVarInt() & 4095);
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeBlockPos(this.pos);
      buffer.writeByte(this.type);
      buffer.writeByte(this.data);
      buffer.writeVarInt(Block.getRawId(this.block) & 4095);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleBlockEvent(this);
   }

   @Environment(EnvType.CLIENT)
   public BlockPos getPos() {
      return this.pos;
   }

   @Environment(EnvType.CLIENT)
   public int getType() {
      return this.type;
   }

   @Environment(EnvType.CLIENT)
   public int getData() {
      return this.data;
   }

   @Environment(EnvType.CLIENT)
   public Block getBlock() {
      return this.block;
   }
}
