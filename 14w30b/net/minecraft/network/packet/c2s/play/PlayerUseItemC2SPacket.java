package net.minecraft.network.packet.c2s.play;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;
import net.minecraft.util.math.BlockPos;

public class PlayerUseItemC2SPacket implements Packet {
   private static final BlockPos INVALID_POS = new BlockPos(-1, -1, -1);
   private BlockPos pos;
   private int face;
   private ItemStack stack;
   private float dx;
   private float dy;
   private float dz;

   public PlayerUseItemC2SPacket() {
   }

   public PlayerUseItemC2SPacket(ItemStack stack) {
      this(INVALID_POS, 255, stack, 0.0F, 0.0F, 0.0F);
   }

   public PlayerUseItemC2SPacket(BlockPos pos, int face, ItemStack stack, float dx, float dy, float dz) {
      this.pos = pos;
      this.face = face;
      this.stack = stack != null ? stack.copy() : null;
      this.dx = dx;
      this.dy = dy;
      this.dz = dz;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.pos = buffer.readBlockPos();
      this.face = buffer.readUnsignedByte();
      this.stack = buffer.readItemStack();
      this.dx = (float)buffer.readUnsignedByte() / 16.0F;
      this.dy = (float)buffer.readUnsignedByte() / 16.0F;
      this.dz = (float)buffer.readUnsignedByte() / 16.0F;
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeBlockPos(this.pos);
      buffer.writeByte(this.face);
      buffer.writeItemStack(this.stack);
      buffer.writeByte((int)(this.dx * 16.0F));
      buffer.writeByte((int)(this.dy * 16.0F));
      buffer.writeByte((int)(this.dz * 16.0F));
   }

   public void handle(ServerPlayPacketHandler c_02lgcirvj) {
      c_02lgcirvj.handlePlayerUseItem(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int getFace() {
      return this.face;
   }

   public ItemStack getStack() {
      return this.stack;
   }

   public float getDx() {
      return this.dx;
   }

   public float getDy() {
      return this.dy;
   }

   public float getDz() {
      return this.dz;
   }
}
