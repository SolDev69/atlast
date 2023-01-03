package net.minecraft.entity;

import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class FireworksEntity extends Entity {
   private int age;
   private int maxAge;

   public FireworksEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.25F, 0.25F);
   }

   @Override
   protected void initDataTracker() {
      this.dataTracker.add(8, 5);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean isWithinViewDistance(double distance) {
      return distance < 4096.0;
   }

   public FireworksEntity(World world, double x, double y, double z, ItemStack stack) {
      super(world);
      this.age = 0;
      this.setDimensions(0.25F, 0.25F);
      this.setPosition(x, y, z);
      int var9 = 1;
      if (stack != null && stack.hasNbt()) {
         this.dataTracker.update(8, stack);
         NbtCompound var10 = stack.getNbt();
         NbtCompound var11 = var10.getCompound("Fireworks");
         if (var11 != null) {
            var9 += var11.getByte("Flight");
         }
      }

      this.velocityX = this.random.nextGaussian() * 0.001;
      this.velocityZ = this.random.nextGaussian() * 0.001;
      this.velocityY = 0.05;
      this.maxAge = 10 * var9 + this.random.nextInt(6) + this.random.nextInt(7);
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
      this.velocityX *= 1.15;
      this.velocityZ *= 1.15;
      this.velocityY += 0.04;
      this.move(this.velocityX, this.velocityY, this.velocityZ);
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
      if (this.age == 0 && !this.isSilent()) {
         this.world.playSound(this, "fireworks.launch", 3.0F, 1.0F);
      }

      ++this.age;
      if (this.world.isClient && this.age % 2 < 2) {
         this.world
            .addParticle(
               ParticleType.FIREWORKS_SPARK,
               this.x,
               this.y - 0.3,
               this.z,
               this.random.nextGaussian() * 0.05,
               -this.velocityY * 0.5,
               this.random.nextGaussian() * 0.05
            );
      }

      if (!this.world.isClient && this.age > this.maxAge) {
         this.world.doEntityEvent(this, (byte)17);
         this.remove();
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void doEvent(byte event) {
      if (event == 17 && this.world.isClient) {
         ItemStack var2 = this.dataTracker.getStack(8);
         NbtCompound var3 = null;
         if (var2 != null && var2.hasNbt()) {
            var3 = var2.getNbt().getCompound("Fireworks");
         }

         this.world.addFireworksParticle(this.x, this.y, this.z, this.velocityX, this.velocityY, this.velocityZ, var3);
      }

      super.doEvent(event);
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      nbt.putInt("Life", this.age);
      nbt.putInt("LifeTime", this.maxAge);
      ItemStack var2 = this.dataTracker.getStack(8);
      if (var2 != null) {
         NbtCompound var3 = new NbtCompound();
         var2.writeNbt(var3);
         nbt.put("FireworksItem", var3);
      }
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      this.age = nbt.getInt("Life");
      this.maxAge = nbt.getInt("LifeTime");
      NbtCompound var2 = nbt.getCompound("FireworksItem");
      if (var2 != null) {
         ItemStack var3 = ItemStack.fromNbt(var2);
         if (var3 != null) {
            this.dataTracker.update(8, var3);
         }
      }
   }

   @Override
   public float getBrightness(float tickDelta) {
      return super.getBrightness(tickDelta);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getLightLevel(float tickDelta) {
      return super.getLightLevel(tickDelta);
   }

   @Override
   public boolean canBePunched() {
      return false;
   }
}
