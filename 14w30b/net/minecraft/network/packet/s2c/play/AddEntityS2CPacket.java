package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class AddEntityS2CPacket implements Packet {
   private int id;
   private int x;
   private int y;
   private int z;
   private int velocityX;
   private int velocityY;
   private int velocityZ;
   private int pitch;
   private int yaw;
   private int type;
   private int data;

   public AddEntityS2CPacket() {
   }

   public AddEntityS2CPacket(Entity entity, int type) {
      this(entity, type, 0);
   }

   public AddEntityS2CPacket(Entity entity, int type, int data) {
      this.id = entity.getNetworkId();
      this.x = MathHelper.floor(entity.x * 32.0);
      this.y = MathHelper.floor(entity.y * 32.0);
      this.z = MathHelper.floor(entity.z * 32.0);
      this.pitch = MathHelper.floor(entity.pitch * 256.0F / 360.0F);
      this.yaw = MathHelper.floor(entity.yaw * 256.0F / 360.0F);
      this.type = type;
      this.data = data;
      if (data > 0) {
         double var4 = entity.velocityX;
         double var6 = entity.velocityY;
         double var8 = entity.velocityZ;
         double var10 = 3.9;
         if (var4 < -var10) {
            var4 = -var10;
         }

         if (var6 < -var10) {
            var6 = -var10;
         }

         if (var8 < -var10) {
            var8 = -var10;
         }

         if (var4 > var10) {
            var4 = var10;
         }

         if (var6 > var10) {
            var6 = var10;
         }

         if (var8 > var10) {
            var8 = var10;
         }

         this.velocityX = (int)(var4 * 8000.0);
         this.velocityY = (int)(var6 * 8000.0);
         this.velocityZ = (int)(var8 * 8000.0);
      }
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readVarInt();
      this.type = buffer.readByte();
      this.x = buffer.readInt();
      this.y = buffer.readInt();
      this.z = buffer.readInt();
      this.pitch = buffer.readByte();
      this.yaw = buffer.readByte();
      this.data = buffer.readInt();
      if (this.data > 0) {
         this.velocityX = buffer.readShort();
         this.velocityY = buffer.readShort();
         this.velocityZ = buffer.readShort();
      }
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.id);
      buffer.writeByte(this.type);
      buffer.writeInt(this.x);
      buffer.writeInt(this.y);
      buffer.writeInt(this.z);
      buffer.writeByte(this.pitch);
      buffer.writeByte(this.yaw);
      buffer.writeInt(this.data);
      if (this.data > 0) {
         buffer.writeShort(this.velocityX);
         buffer.writeShort(this.velocityY);
         buffer.writeShort(this.velocityZ);
      }
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleAddEntity(this);
   }

   @Environment(EnvType.CLIENT)
   public int getId() {
      return this.id;
   }

   @Environment(EnvType.CLIENT)
   public int getX() {
      return this.x;
   }

   @Environment(EnvType.CLIENT)
   public int getY() {
      return this.y;
   }

   @Environment(EnvType.CLIENT)
   public int getZ() {
      return this.z;
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

   @Environment(EnvType.CLIENT)
   public int getPitch() {
      return this.pitch;
   }

   @Environment(EnvType.CLIENT)
   public int getYaw() {
      return this.yaw;
   }

   @Environment(EnvType.CLIENT)
   public int getType() {
      return this.type;
   }

   @Environment(EnvType.CLIENT)
   public int getData() {
      return this.data;
   }

   public void setX(int x) {
      this.x = x;
   }

   public void setY(int y) {
      this.y = y;
   }

   public void setZ(int z) {
      this.z = z;
   }

   public void setVelocityX(int velocityX) {
      this.velocityX = velocityX;
   }

   public void setVelocityY(int velocityY) {
      this.velocityY = velocityY;
   }

   public void setVelocityZ(int velocityZ) {
      this.velocityZ = velocityZ;
   }

   @Environment(EnvType.CLIENT)
   public void setData(int data) {
      this.data = data;
   }
}
