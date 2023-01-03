package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.entity.decoration.PaintingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class AddPaintingS2CPacket implements Packet {
   private int id;
   private BlockPos pos;
   private Direction facing;
   private String motive;

   public AddPaintingS2CPacket() {
   }

   public AddPaintingS2CPacket(PaintingEntity painting) {
      this.id = painting.getNetworkId();
      this.pos = painting.getBlockPos();
      this.facing = painting.getFacing;
      this.motive = painting.motive.name;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readVarInt();
      this.motive = buffer.readString(PaintingEntity.Motive.SKULL_AND_ROSES_LENGTH);
      this.pos = buffer.readBlockPos();
      this.facing = Direction.byIdHorizontal(buffer.readUnsignedByte());
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.id);
      buffer.writeString(this.motive);
      buffer.writeBlockPos(this.pos);
      buffer.writeByte(this.facing.getIdHorizontal());
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleAddPainting(this);
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
   public Direction getFacing() {
      return this.facing;
   }

   @Environment(EnvType.CLIENT)
   public String getMotive() {
      return this.motive;
   }
}
