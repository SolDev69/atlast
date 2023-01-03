package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EntityHeadAnglesS2CPacket implements Packet {
   private int id;
   private byte headYaw;

   public EntityHeadAnglesS2CPacket() {
   }

   public EntityHeadAnglesS2CPacket(Entity entity, byte headYaw) {
      this.id = entity.getNetworkId();
      this.headYaw = headYaw;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readVarInt();
      this.headYaw = buffer.readByte();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.id);
      buffer.writeByte(this.headYaw);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleEntityHeadAngles(this);
   }

   @Environment(EnvType.CLIENT)
   public Entity getEntity(World world) {
      return world.getEntity(this.id);
   }

   @Environment(EnvType.CLIENT)
   public byte getHeadYaw() {
      return this.headYaw;
   }
}
