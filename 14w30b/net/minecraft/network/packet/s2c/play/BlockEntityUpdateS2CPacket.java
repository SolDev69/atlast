package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class BlockEntityUpdateS2CPacket implements Packet {
   private BlockPos pos;
   private int type;
   private NbtCompound nbt;

   public BlockEntityUpdateS2CPacket() {
   }

   public BlockEntityUpdateS2CPacket(BlockPos pos, int type, NbtCompound nbt) {
      this.pos = pos;
      this.type = type;
      this.nbt = nbt;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.pos = buffer.readBlockPos();
      this.type = buffer.readUnsignedByte();
      this.nbt = buffer.readNbtCompound();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeBlockPos(this.pos);
      buffer.writeByte((byte)this.type);
      buffer.writeNbtCompound(this.nbt);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleBlockEntityUpdate(this);
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
   public NbtCompound getNbt() {
      return this.nbt;
   }
}
