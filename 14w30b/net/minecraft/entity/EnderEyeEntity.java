package net.minecraft.entity;

import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EnderEyeEntity extends Entity {
   private double targetX;
   private double targetY;
   private double targetZ;
   private int lifespan;
   private boolean dropsItem;

   public EnderEyeEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.25F, 0.25F);
   }

   @Override
   protected void initDataTracker() {
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean isWithinViewDistance(double distance) {
      double var3 = this.getBoundingBox().getAverageSideLength() * 4.0;
      var3 *= 64.0;
      return distance < var3 * var3;
   }

   public EnderEyeEntity(World world, double x, double y, double f) {
      super(world);
      this.lifespan = 0;
      this.setDimensions(0.25F, 0.25F);
      this.setPosition(x, y, f);
   }

   public void setTarget(BlockPos x) {
      double var2 = (double)x.getX();
      int var4 = x.getY();
      double var5 = (double)x.getZ();
      double var7 = var2 - this.x;
      double var9 = var5 - this.z;
      float var11 = MathHelper.sqrt(var7 * var7 + var9 * var9);
      if (var11 > 12.0F) {
         this.targetX = this.x + var7 / (double)var11 * 12.0;
         this.targetZ = this.z + var9 / (double)var11 * 12.0;
         this.targetY = this.y + 8.0;
      } else {
         this.targetX = var2;
         this.targetY = (double)var4;
         this.targetZ = var5;
      }

      this.lifespan = 0;
      this.dropsItem = this.random.nextInt(5) > 0;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void setVelocity(double velocityX, double velocityY, double velocityZ) {
      this.velocityX = velocityX;
      this.velocityY = velocityY;
      this.velocityZ = velocityZ;
      if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
         float var7 = MathHelper.sqrt(velocityX * velocityX + velocityZ * velocityZ);
         this.prevYaw = this.yaw = (float)(Math.atan2(velocityX, velocityZ) * 180.0 / (float) Math.PI);
         this.prevPitch = this.pitch = (float)(Math.atan2(velocityY, (double)var7) * 180.0 / (float) Math.PI);
      }
   }

   @Override
   public void tick() {
      this.prevTickX = this.x;
      this.prevTickY = this.y;
      this.prevTickZ = this.z;
      super.tick();
      this.x += this.velocityX;
      this.y += this.velocityY;
      this.z += this.velocityZ;
      float var1 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
      this.yaw = (float)(Math.atan2(this.velocityX, this.velocityZ) * 180.0 / (float) Math.PI);
      this.pitch = (float)(Math.atan2(this.velocityY, (double)var1) * 180.0 / (float) Math.PI);

      while(this.pitch - this.prevPitch < -180.0F) {
         this.prevPitch -= 360.0F;
      }

      while(this.pitch - this.prevPitch >= 180.0F) {
         this.prevPitch += 360.0F;
      }

      while(this.yaw - this.prevYaw < -180.0F) {
         this.prevYaw -= 360.0F;
      }

      while(this.yaw - this.prevYaw >= 180.0F) {
         this.prevYaw += 360.0F;
      }

      this.pitch = this.prevPitch + (this.pitch - this.prevPitch) * 0.2F;
      this.yaw = this.prevYaw + (this.yaw - this.prevYaw) * 0.2F;
      if (!this.world.isClient) {
         double var2 = this.targetX - this.x;
         double var4 = this.targetZ - this.z;
         float var6 = (float)Math.sqrt(var2 * var2 + var4 * var4);
         float var7 = (float)Math.atan2(var4, var2);
         double var8 = (double)var1 + (double)(var6 - var1) * 0.0025;
         if (var6 < 1.0F) {
            var8 *= 0.8;
            this.velocityY *= 0.8;
         }

         this.velocityX = Math.cos((double)var7) * var8;
         this.velocityZ = Math.sin((double)var7) * var8;
         if (this.y < this.targetY) {
            this.velocityY += (1.0 - this.velocityY) * 0.015F;
         } else {
            this.velocityY += (-1.0 - this.velocityY) * 0.015F;
         }
      }

      float var10 = 0.25F;
      if (this.isInWater()) {
         for(int var3 = 0; var3 < 4; ++var3) {
            this.world
               .addParticle(
                  ParticleType.WATER_BUBBLE,
                  this.x - this.velocityX * (double)var10,
                  this.y - this.velocityY * (double)var10,
                  this.z - this.velocityZ * (double)var10,
                  this.velocityX,
                  this.velocityY,
                  this.velocityZ
               );
         }
      } else {
         this.world
            .addParticle(
               ParticleType.PORTAL,
               this.x - this.velocityX * (double)var10 + this.random.nextDouble() * 0.6 - 0.3,
               this.y - this.velocityY * (double)var10 - 0.5,
               this.z - this.velocityZ * (double)var10 + this.random.nextDouble() * 0.6 - 0.3,
               this.velocityX,
               this.velocityY,
               this.velocityZ
            );
      }

      if (!this.world.isClient) {
         this.setPosition(this.x, this.y, this.z);
         ++this.lifespan;
         if (this.lifespan > 80 && !this.world.isClient) {
            this.remove();
            if (this.dropsItem) {
               this.world.addEntity(new ItemEntity(this.world, this.x, this.y, this.z, new ItemStack(Items.ENDER_EYE)));
            } else {
               this.world.doEvent(2003, new BlockPos(this), 0);
            }
         }
      }
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
   }

   @Override
   public float getBrightness(float tickDelta) {
      return 1.0F;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getLightLevel(float tickDelta) {
      return 15728880;
   }

   @Override
   public boolean canBePunched() {
      return false;
   }
}
