package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class BlockMiningProgressS2CPacket implements Packet {
   private int id;
   private BlockPos pos;
   private int progress;

   public BlockMiningProgressS2CPacket() {
   }

   public BlockMiningProgressS2CPacket(int id, BlockPos pos, int progress) {
      this.id = id;
      this.pos = pos;
      this.progress = progress;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readVarInt();
      this.pos = buffer.readBlockPos();
      this.progress = buffer.readUnsignedByte();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.id);
      buffer.writeBlockPos(this.pos);
      buffer.writeByte(this.progress);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleBlockMiningProgress(this);
   }

   @Environment(EnvType.CLIENT)
   public int getId() {
      return this.id;
   }

   @Environment(EnvType.CLIENT)
   public BlockPos getPos() {
      return this.pos;
   }

   @Environment(EnvType.CLIENT)
   public int getProgress() {
      return this.progress;
   }
}
