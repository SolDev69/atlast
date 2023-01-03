package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class WorldEventS2CPacket implements Packet {
   private int event;
   private BlockPos pos;
   private int data;
   private boolean global;

   public WorldEventS2CPacket() {
   }

   public WorldEventS2CPacket(int event, BlockPos pos, int data, boolean global) {
      this.event = event;
      this.pos = pos;
      this.data = data;
      this.global = global;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.event = buffer.readInt();
      this.pos = buffer.readBlockPos();
      this.data = buffer.readInt();
      this.global = buffer.readBoolean();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeInt(this.event);
      buffer.writeBlockPos(this.pos);
      buffer.writeInt(this.data);
      buffer.writeBoolean(this.global);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleWorldEvent(this);
   }

   @Environment(EnvType.CLIENT)
   public boolean isGlobal() {
      return this.global;
   }

   @Environment(EnvType.CLIENT)
   public int getEvent() {
      return this.event;
   }

   @Environment(EnvType.CLIENT)
   public int getData() {
      return this.data;
   }

   @Environment(EnvType.CLIENT)
   public BlockPos getPos() {
      return this.pos;
   }
}
