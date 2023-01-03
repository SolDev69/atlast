package net.minecraft.network.packet.c2s.play;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PlayerInteractEntityC2SPacket implements Packet {
   private int targetId;
   private PlayerInteractEntityC2SPacket.Action action;

   public PlayerInteractEntityC2SPacket() {
   }

   @Environment(EnvType.CLIENT)
   public PlayerInteractEntityC2SPacket(Entity target, PlayerInteractEntityC2SPacket.Action action) {
      this.targetId = target.getNetworkId();
      this.action = action;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.targetId = buffer.readVarInt();
      this.action = (PlayerInteractEntityC2SPacket.Action)buffer.readEnum(PlayerInteractEntityC2SPacket.Action.class);
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.targetId);
      buffer.writeEnum(this.action);
   }

   public void handle(ServerPlayPacketHandler c_02lgcirvj) {
      c_02lgcirvj.handleInteractEntity(this);
   }

   public Entity getInteractTarget(World world) {
      return world.getEntity(this.targetId);
   }

   public PlayerInteractEntityC2SPacket.Action getAction() {
      return this.action;
   }

   public static enum Action {
      INTERACT,
      ATTACK;
   }
}
