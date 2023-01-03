package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EntityEventS2CPacket implements Packet {
   private int id;
   private byte event;

   public EntityEventS2CPacket() {
   }

   public EntityEventS2CPacket(Entity entity, byte event) {
      this.id = entity.getNetworkId();
      this.event = event;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readInt();
      this.event = buffer.readByte();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeInt(this.id);
      buffer.writeByte(this.event);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleEntityEvent(this);
   }

   @Environment(EnvType.CLIENT)
   public Entity getEntity(World world) {
      return world.getEntity(this.id);
   }

   @Environment(EnvType.CLIENT)
   public byte getEvent() {
      return this.event;
   }
}
