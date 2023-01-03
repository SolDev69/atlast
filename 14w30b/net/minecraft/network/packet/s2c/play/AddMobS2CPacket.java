package net.minecraft.network.packet.s2c.play;

import java.util.List;
import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.entity.DataTracker;
import net.minecraft.entity.Entities;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class AddMobS2CPacket implements Packet {
   private int id;
   private int type;
   private int x;
   private int y;
   private int z;
   private int velocityX;
   private int velocityY;
   private int velocityZ;
   private byte yaw;
   private byte pitch;
   private byte headYaw;
   private DataTracker tracker;
   private List entries;

   public AddMobS2CPacket() {
   }

   public AddMobS2CPacket(LivingEntity entity) {
      this.id = entity.getNetworkId();
      this.type = (byte)Entities.getRawId(entity);
      this.x = MathHelper.floor(entity.x * 32.0);
      this.y = MathHelper.floor(entity.y * 32.0);
      this.z = MathHelper.floor(entity.z * 32.0);
      this.yaw = (byte)((int)(entity.yaw * 256.0F / 360.0F));
      this.pitch = (byte)((int)(entity.pitch * 256.0F / 360.0F));
      this.headYaw = (byte)((int)(entity.headYaw * 256.0F / 360.0F));
      double var2 = 3.9;
      double var4 = entity.velocityX;
      double var6 = entity.velocityY;
      double var8 = entity.velocityZ;
      if (var4 < -var2) {
         var4 = -var2;
      }

      if (var6 < -var2) {
         var6 = -var2;
      }

      if (var8 < -var2) {
         var8 = -var2;
      }

      if (var4 > var2) {
         var4 = var2;
      }

      if (var6 > var2) {
         var6 = var2;
      }

      if (var8 > var2) {
         var8 = var2;
      }

      this.velocityX = (int)(var4 * 8000.0);
      this.velocityY = (int)(var6 * 8000.0);
      this.velocityZ = (int)(var8 * 8000.0);
      this.tracker = entity.getDataTracker();
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readVarInt();
      this.type = buffer.readByte() & 255;
      this.x = buffer.readInt();
      this.y = buffer.readInt();
      this.z = buffer.readInt();
      this.yaw = buffer.readByte();
      this.pitch = buffer.readByte();
      this.headYaw = buffer.readByte();
      this.velocityX = buffer.readShort();
      this.velocityY = buffer.readShort();
      this.velocityZ = buffer.readShort();
      this.entries = DataTracker.read(buffer);
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.id);
      buffer.writeByte(this.type & 0xFF);
      buffer.writeInt(this.x);
      buffer.writeInt(this.y);
      buffer.writeInt(this.z);
      buffer.writeByte(this.yaw);
      buffer.writeByte(this.pitch);
      buffer.writeByte(this.headYaw);
      buffer.writeShort(this.velocityX);
      buffer.writeShort(this.velocityY);
      buffer.writeShort(this.velocityZ);
      this.tracker.write(buffer);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleAddMob(this);
   }

   @Environment(EnvType.CLIENT)
   public List getEntries() {
      if (this.entries == null) {
         this.entries = this.tracker.collectEntries();
      }

      return this.entries;
   }

   @Environment(EnvType.CLIENT)
   public int getId() {
      return this.id;
   }

   @Environment(EnvType.CLIENT)
   public int getType() {
      return this.type;
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
   public byte getYaw() {
      return this.yaw;
   }

   @Environment(EnvType.CLIENT)
   public byte getPitch() {
      return this.pitch;
   }

   @Environment(EnvType.CLIENT)
   public byte getHeadYaw() {
      return this.headYaw;
   }
}
