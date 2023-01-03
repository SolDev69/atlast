package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EntityAttachS2CPacket implements Packet {
   private int type;
   private int id;
   private int holderId;

   public EntityAttachS2CPacket() {
   }

   public EntityAttachS2CPacket(int type, Entity entity, Entity holder) {
      this.type = type;
      this.id = entity.getNetworkId();
      this.holderId = holder != null ? holder.getNetworkId() : -1;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readInt();
      this.holderId = buffer.readInt();
      this.type = buffer.readUnsignedByte();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeInt(this.id);
      buffer.writeInt(this.holderId);
      buffer.writeByte(this.type);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleEntityAttach(this);
   }

   @Environment(EnvType.CLIENT)
   public int getType() {
      return this.type;
   }

   @Environment(EnvType.CLIENT)
   public int getId() {
      return this.id;
   }

   @Environment(EnvType.CLIENT)
   public int getHolderId() {
      return this.holderId;
   }
}
