package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PlayerHandActionC2SPacket implements Packet {
   private BlockPos pos;
   private Direction face;
   private PlayerHandActionC2SPacket.Action action;

   public PlayerHandActionC2SPacket() {
   }

   @Environment(EnvType.CLIENT)
   public PlayerHandActionC2SPacket(PlayerHandActionC2SPacket.Action action, BlockPos pos, Direction face) {
      this.action = action;
      this.pos = pos;
      this.face = face;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.action = (PlayerHandActionC2SPacket.Action)buffer.readEnum(PlayerHandActionC2SPacket.Action.class);
      this.pos = buffer.readBlockPos();
      this.face = Direction.byId(buffer.readUnsignedByte());
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeEnum(this.action);
      buffer.writeBlockPos(this.pos);
      buffer.writeByte(this.face.getId());
   }

   public void handle(ServerPlayPacketHandler c_02lgcirvj) {
      c_02lgcirvj.handlePlayerHandAction(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Direction getFace() {
      return this.face;
   }

   public PlayerHandActionC2SPacket.Action getAction() {
      return this.action;
   }

   public static enum Action {
      START_DESTROY_BLOCK,
      ABORT_DESTROY_BLOCK,
      STOP_DESTROY_BLOCK,
      DROP_ALL_ITEMS,
      DROP_ITEM,
      RELEASE_USE_ITEM;
   }
}
