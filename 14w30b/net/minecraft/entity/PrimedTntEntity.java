package net.minecraft.entity;

import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class PrimedTntEntity extends Entity {
   public int fuseTimer;
   private LivingEntity igniter;

   public PrimedTntEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.blocksBuilding = true;
      this.setDimensions(0.98F, 0.98F);
   }

   public PrimedTntEntity(World world, double x, double y, double z, LivingEntity igniter) {
      this(world);
      this.setPosition(x, y, z);
      float var9 = (float)(Math.random() * (float) Math.PI * 2.0);
      this.velocityX = (double)(-((float)Math.sin((double)var9)) * 0.02F);
      this.velocityY = 0.2F;
      this.velocityZ = (double)(-((float)Math.cos((double)var9)) * 0.02F);
      this.fuseTimer = 80;
      this.prevX = x;
      this.prevY = y;
      this.prevZ = z;
      this.igniter = igniter;
   }

   @Override
   protected void initDataTracker() {
   }

   @Override
   protected boolean canClimb() {
      return false;
   }

   @Override
   public boolean hasCollision() {
      return !this.removed;
   }

   @Override
   public void tick() {
      this.prevX = this.x;
      this.prevY = this.y;
      this.prevZ = this.z;
      this.velocityY -= 0.04F;
      this.move(this.velocityX, this.velocityY, this.velocityZ);
      this.velocityX *= 0.98F;
      this.velocityY *= 0.98F;
      this.velocityZ *= 0.98F;
      if (this.onGround) {
         this.velocityX *= 0.7F;
         this.velocityZ *= 0.7F;
         this.velocityY *= -0.5;
      }

      if (this.fuseTimer-- <= 0) {
         this.remove();
         if (!this.world.isClient) {
            this.explode();
         }
      } else {
         this.checkWaterCollision();
         this.world.addParticle(ParticleType.SMOKE_NORMAL, this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
      }
   }

   private void explode() {
      float var1 = 4.0F;
      this.world.explode(this, this.x, this.y, this.z, var1, true);
   }

   @Override
   protected void writeCustomNbt(NbtCompound nbt) {
      nbt.putByte("Fuse", (byte)this.fuseTimer);
   }

   @Override
   protected void readCustomNbt(NbtCompound nbt) {
      this.fuseTimer = nbt.getByte("Fuse");
   }

   public LivingEntity getIgniter() {
      return this.igniter;
   }

   @Override
   public float getEyeHeight() {
      return 0.0F;
   }
}
