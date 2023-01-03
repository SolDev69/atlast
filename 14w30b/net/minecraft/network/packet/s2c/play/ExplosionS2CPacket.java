package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class ExplosionS2CPacket implements Packet {
   private double x;
   private double y;
   private double z;
   private float power;
   private List damagedBlocks;
   private float playerVelocityX;
   private float playerVelocityY;
   private float playerVelocityZ;

   public ExplosionS2CPacket() {
   }

   public ExplosionS2CPacket(double x, double y, double z, float power, List damagedBlocks, Vec3d playerVelocity) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.power = power;
      this.damagedBlocks = Lists.newArrayList(damagedBlocks);
      if (playerVelocity != null) {
         this.playerVelocityX = (float)playerVelocity.x;
         this.playerVelocityY = (float)playerVelocity.y;
         this.playerVelocityZ = (float)playerVelocity.z;
      }
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.x = (double)buffer.readFloat();
      this.y = (double)buffer.readFloat();
      this.z = (double)buffer.readFloat();
      this.power = buffer.readFloat();
      int var2 = buffer.readInt();
      this.damagedBlocks = Lists.newArrayListWithCapacity(var2);
      int var3 = (int)this.x;
      int var4 = (int)this.y;
      int var5 = (int)this.z;

      for(int var6 = 0; var6 < var2; ++var6) {
         int var7 = buffer.readByte() + var3;
         int var8 = buffer.readByte() + var4;
         int var9 = buffer.readByte() + var5;
         this.damagedBlocks.add(new BlockPos(var7, var8, var9));
      }

      this.playerVelocityX = buffer.readFloat();
      this.playerVelocityY = buffer.readFloat();
      this.playerVelocityZ = buffer.readFloat();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeFloat((float)this.x);
      buffer.writeFloat((float)this.y);
      buffer.writeFloat((float)this.z);
      buffer.writeFloat(this.power);
      buffer.writeInt(this.damagedBlocks.size());
      int var2 = (int)this.x;
      int var3 = (int)this.y;
      int var4 = (int)this.z;

      for(BlockPos var6 : this.damagedBlocks) {
         int var7 = var6.getX() - var2;
         int var8 = var6.getY() - var3;
         int var9 = var6.getZ() - var4;
         buffer.writeByte(var7);
         buffer.writeByte(var8);
         buffer.writeByte(var9);
      }

      buffer.writeFloat(this.playerVelocityX);
      buffer.writeFloat(this.playerVelocityY);
      buffer.writeFloat(this.playerVelocityZ);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleExplosion(this);
   }

   @Environment(EnvType.CLIENT)
   public float getPlayerVelocityX() {
      return this.playerVelocityX;
   }

   @Environment(EnvType.CLIENT)
   public float getPlayerVelocityY() {
      return this.playerVelocityY;
   }

   @Environment(EnvType.CLIENT)
   public float getPlayerVelocityZ() {
      return this.playerVelocityZ;
   }

   @Environment(EnvType.CLIENT)
   public double getX() {
      return this.x;
   }

   @Environment(EnvType.CLIENT)
   public double getY() {
      return this.y;
   }

   @Environment(EnvType.CLIENT)
   public double getZ() {
      return this.z;
   }

   @Environment(EnvType.CLIENT)
   public float getPower() {
      return this.power;
   }

   @Environment(EnvType.CLIENT)
   public List getDamagedBlocks() {
      return this.damagedBlocks;
   }
}
