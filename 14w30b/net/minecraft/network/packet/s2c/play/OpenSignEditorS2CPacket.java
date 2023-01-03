package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class OpenSignEditorS2CPacket implements Packet {
   private BlockPos pos;

   public OpenSignEditorS2CPacket() {
   }

   public OpenSignEditorS2CPacket(BlockPos x) {
      this.pos = x;
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleOpenSignEditor(this);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.pos = buffer.readBlockPos();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeBlockPos(this.pos);
   }

   @Environment(EnvType.CLIENT)
   public BlockPos getPos() {
      return this.pos;
   }
}
