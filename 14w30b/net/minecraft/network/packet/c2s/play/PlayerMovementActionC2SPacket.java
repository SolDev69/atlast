package net.minecraft.network.packet.c2s.play;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PlayerMovementActionC2SPacket implements Packet {
   private int id;
   private PlayerMovementActionC2SPacket.Action action;
   private int data;

   public PlayerMovementActionC2SPacket() {
   }

   @Environment(EnvType.CLIENT)
   public PlayerMovementActionC2SPacket(Entity entity, PlayerMovementActionC2SPacket.Action action) {
      this(entity, action, 0);
   }

   @Environment(EnvType.CLIENT)
   public PlayerMovementActionC2SPacket(Entity entity, PlayerMovementActionC2SPacket.Action action, int data) {
      this.id = entity.getNetworkId();
      this.action = action;
      this.data = data;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readVarInt();
      this.action = (PlayerMovementActionC2SPacket.Action)buffer.readEnum(PlayerMovementActionC2SPacket.Action.class);
      this.data = buffer.readVarInt();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.id);
      buffer.writeEnum(this.action);
      buffer.writeVarInt(this.data);
   }

   public void handle(ServerPlayPacketHandler c_02lgcirvj) {
      c_02lgcirvj.handlePlayerMovementAction(this);
   }

   public PlayerMovementActionC2SPacket.Action getAction() {
      return this.action;
   }

   public int getData() {
      return this.data;
   }

   public static enum Action {
      START_SNEAKING,
      STOP_SNEAKING,
      STOP_SLEEPING,
      START_SPRINTING,
      STOP_SPRINTING,
      RIDING_JUMP,
      OPEN_HORSE_INVENTORY;
   }
}
