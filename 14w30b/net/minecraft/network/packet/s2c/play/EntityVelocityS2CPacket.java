package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EntityVelocityS2CPacket implements Packet {
   private int id;
   private int velocityX;
   private int velocityY;
   private int velocityZ;

   public EntityVelocityS2CPacket() {
   }

   public EntityVelocityS2CPacket(Entity entity) {
      this(entity.getNetworkId(), entity.velocityX, entity.velocityY, entity.velocityZ);
   }

   public EntityVelocityS2CPacket(int id, double velocityX, double velocityY, double velocityZ) {
      this.id = id;
      double var8 = 3.9;
      if (velocityX < -var8) {
         velocityX = -var8;
      }

      if (velocityY < -var8) {
         velocityY = -var8;
      }

      if (velocityZ < -var8) {
         velocityZ = -var8;
      }

      if (velocityX > var8) {
         velocityX = var8;
      }

      if (velocityY > var8) {
         velocityY = var8;
      }

      if (velocityZ > var8) {
         velocityZ = var8;
      }

      this.velocityX = (int)(velocityX * 8000.0);
      this.velocityY = (int)(velocityY * 8000.0);
      this.velocityZ = (int)(velocityZ * 8000.0);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readVarInt();
      this.velocityX = buffer.readShort();
      this.velocityY = buffer.readShort();
      this.velocityZ = buffer.readShort();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.id);
      buffer.writeShort(this.velocityX);
      buffer.writeShort(this.velocityY);
      buffer.writeShort(this.velocityZ);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleEntityVelocity(this);
   }

   @Environment(EnvType.CLIENT)
   public int getId() {
      return this.id;
   }

   @Environment(EnvType.CLIENT)
   public int getVelocityX() {
      return this.velocityX;
   }

   @Environment(EnvType.CLIENT)
   public int getVelocityY() {
      return this.velocityY;
   }

   @Environment(EnvType.CLIENT)
   public int getVelocityZ() {
      return this.velocityZ;
   }
}
