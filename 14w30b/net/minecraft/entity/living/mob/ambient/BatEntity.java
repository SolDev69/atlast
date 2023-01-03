package net.minecraft.entity.living.mob.ambient;

import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BatEntity extends AmbientEntity {
   private BlockPos hangPos;

   public BatEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.5F, 0.9F);
      this.setRoosting(true);
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(16, new Byte((byte)0));
   }

   @Override
   protected float getSoundVolume() {
      return 0.1F;
   }

   @Override
   protected float getSoundPitch() {
      return super.getSoundPitch() * 0.95F;
   }

   @Override
   protected String getAmbientSound() {
      return this.isRoosting() && this.random.nextInt(4) != 0 ? null : "mob.bat.idle";
   }

   @Override
   protected String getHurtSound() {
      return "mob.bat.hurt";
   }

   @Override
   protected String getDeathSound() {
      return "mob.bat.death";
   }

   @Override
   public boolean isPushable() {
      return false;
   }

   @Override
   protected void pushAway(Entity entity) {
   }

   @Override
   protected void pushAwayCollidingEntities() {
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(6.0);
   }

   public boolean isRoosting() {
      return (this.dataTracker.getByte(16) & 1) != 0;
   }

   public void setRoosting(boolean roosting) {
      byte var2 = this.dataTracker.getByte(16);
      if (roosting) {
         this.dataTracker.update(16, (byte)(var2 | 1));
      } else {
         this.dataTracker.update(16, (byte)(var2 & -2));
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.isRoosting()) {
         this.velocityX = this.velocityY = this.velocityZ = 0.0;
         this.y = (double)MathHelper.floor(this.y) + 1.0 - (double)this.height;
      } else {
         this.velocityY *= 0.6F;
      }
   }

   @Override
   protected void m_45jbqtvrb() {
      super.m_45jbqtvrb();
      BlockPos var1 = new BlockPos(this);
      BlockPos var2 = var1.up();
      if (this.isRoosting()) {
         if (!this.world.getBlockState(var2).getBlock().isConductor()) {
            this.setRoosting(false);
            this.world.doEvent(null, 1015, var1, 0);
         } else {
            if (this.random.nextInt(200) == 0) {
               this.headYaw = (float)this.random.nextInt(360);
            }

            if (this.world.getClosestPlayer(this, 4.0) != null) {
               this.setRoosting(false);
               this.world.doEvent(null, 1015, var1, 0);
            }
         }
      } else {
         if (this.hangPos != null && (!this.world.isAir(this.hangPos) || this.hangPos.getY() < 1)) {
            this.hangPos = null;
         }

         if (this.hangPos == null
            || this.random.nextInt(30) == 0
            || this.hangPos.squaredDistanceTo((double)((int)this.x), (double)((int)this.y), (double)((int)this.z)) < 4.0) {
            this.hangPos = new BlockPos(
               (int)this.x + this.random.nextInt(7) - this.random.nextInt(7),
               (int)this.y + this.random.nextInt(6) - 2,
               (int)this.z + this.random.nextInt(7) - this.random.nextInt(7)
            );
         }

         double var3 = (double)this.hangPos.getX() + 0.5 - this.x;
         double var5 = (double)this.hangPos.getY() + 0.1 - this.y;
         double var7 = (double)this.hangPos.getZ() + 0.5 - this.z;
         this.velocityX += (Math.signum(var3) * 0.5 - this.velocityX) * 0.1F;
         this.velocityY += (Math.signum(var5) * 0.7F - this.velocityY) * 0.1F;
         this.velocityZ += (Math.signum(var7) * 0.5 - this.velocityZ) * 0.1F;
         float var9 = (float)(Math.atan2(this.velocityZ, this.velocityX) * 180.0 / (float) Math.PI) - 90.0F;
         float var10 = MathHelper.wrapDegrees(var9 - this.yaw);
         this.forwardSpeed = 0.5F;
         this.yaw += var10;
         if (this.random.nextInt(100) == 0 && this.world.getBlockState(var2).getBlock().isConductor()) {
            this.setRoosting(true);
         }
      }
   }

   @Override
   protected boolean canClimb() {
      return false;
   }

   @Override
   public void applyFallDamage(float distance, float g) {
   }

   @Override
   protected void onFall(double dy, boolean landed, Block block, BlockPos pos) {
   }

   @Override
   public boolean canAvoidTraps() {
      return true;
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerable(source)) {
         return false;
      } else {
         if (!this.world.isClient && this.isRoosting()) {
            this.setRoosting(false);
         }

         return super.damage(source, amount);
      }
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.dataTracker.update(16, nbt.getByte("BatFlags"));
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putByte("BatFlags", this.dataTracker.getByte(16));
   }

   @Override
   public boolean canSpawn() {
      BlockPos var1 = new BlockPos(this.x, this.getBoundingBox().minY, this.z);
      if (var1.getY() >= 63) {
         return false;
      } else {
         int var2 = this.world.getRawBrightness(var1);
         byte var3 = 4;
         if (this.m_08cacxdds(this.world.getCalendar())) {
            var3 = 7;
         } else if (this.random.nextBoolean()) {
            return false;
         }

         return var2 > this.random.nextInt(var3) ? false : super.canSpawn();
      }
   }

   private boolean m_08cacxdds(Calendar calendar) {
      return calendar.get(2) + 1 == 10 && calendar.get(5) >= 20 || calendar.get(2) + 1 == 11 && calendar.get(5) <= 3;
   }

   @Override
   public float getEyeHeight() {
      return this.height / 2.0F;
   }
}
