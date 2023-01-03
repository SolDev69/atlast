package net.minecraft.entity.living.mob.hostile;

import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;

public class BlazeEntity extends HostileEntity {
   private float eyeOffset = 0.5F;
   private int fireActivation;

   public BlazeEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.immuneToFire = true;
      this.experiencePoints = 10;
      this.goalSelector.addGoal(4, new BlazeEntity.C_10rhnupxp(this));
      this.goalSelector.addGoal(5, new GoToWalkTargetGoal(this, 1.0));
      this.goalSelector.addGoal(7, new WanderAroundGoal(this, 1.0));
      this.goalSelector.addGoal(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(8, new LookAroundGoal(this));
      this.targetSelector.addGoal(1, new RevengeGoal(this, true));
      this.targetSelector.addGoal(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.ATTACK_DAMAGE).setBase(6.0);
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.23F);
      this.initializeAttribute(EntityAttributes.FOLLOW_RANGE).setBase(48.0);
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(16, new Byte((byte)0));
   }

   @Override
   protected String getAmbientSound() {
      return "mob.blaze.breathe";
   }

   @Override
   protected String getHurtSound() {
      return "mob.blaze.hit";
   }

   @Override
   protected String getDeathSound() {
      return "mob.blaze.death";
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   @Override
   public int getLightLevel(float tickDelta) {
      return 15728880;
   }

   @Override
   public float getBrightness(float tickDelta) {
      return 1.0F;
   }

   @Override
   public void tickAI() {
      if (!this.onGround && this.velocityY < 0.0) {
         this.velocityY *= 0.6;
      }

      if (this.world.isClient) {
         if (this.random.nextInt(24) == 0 && !this.isSilent()) {
            this.world
               .playSound(this.x + 0.5, this.y + 0.5, this.z + 0.5, "fire.fire", 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
         }

         for(int var1 = 0; var1 < 2; ++var1) {
            this.world
               .addParticle(
                  ParticleType.SMOKE_LARGE,
                  this.x + (this.random.nextDouble() - 0.5) * (double)this.width,
                  this.y + this.random.nextDouble() * (double)this.height,
                  this.z + (this.random.nextDouble() - 0.5) * (double)this.width,
                  0.0,
                  0.0,
                  0.0
               );
         }
      }

      super.tickAI();
   }

   @Override
   protected void m_45jbqtvrb() {
      if (this.isWet()) {
         this.damage(DamageSource.DROWN, 1.0F);
      }

      --this.fireActivation;
      if (this.fireActivation <= 0) {
         this.fireActivation = 100;
         this.eyeOffset = 0.5F + (float)this.random.nextGaussian() * 3.0F;
      }

      LivingEntity var1 = this.getTargetEntity();
      if (var1 != null && var1.y + (double)var1.getEyeHeight() > this.y + (double)this.getEyeHeight() + (double)this.eyeOffset) {
         this.velocityY += (0.3F - this.velocityY) * 0.3F;
         this.velocityDirty = true;
      }

      super.m_45jbqtvrb();
   }

   @Override
   public void applyFallDamage(float distance, float g) {
   }

   @Override
   protected Item getDefaultDropLoot() {
      return Items.BLAZE_ROD;
   }

   @Override
   public boolean isOnFire() {
      return this.isFireActive();
   }

   @Override
   protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
      if (allowDrops) {
         int var3 = this.random.nextInt(2 + lootingMultiplier);

         for(int var4 = 0; var4 < var3; ++var4) {
            this.dropItem(Items.BLAZE_ROD, 1);
         }
      }
   }

   public boolean isFireActive() {
      return (this.dataTracker.getByte(16) & 1) != 0;
   }

   public void setFireActive(boolean active) {
      byte var2 = this.dataTracker.getByte(16);
      if (active) {
         var2 = (byte)(var2 | 1);
      } else {
         var2 = (byte)(var2 & -2);
      }

      this.dataTracker.update(16, var2);
   }

   @Override
   protected boolean canSpawnAtLightLevel() {
      return true;
   }

   static class C_10rhnupxp extends Goal {
      private BlazeEntity f_10ipfkhis;
      private int f_14tsbkduv;
      private int f_88vaafuxg;

      public C_10rhnupxp(BlazeEntity c_57tlcvvsi) {
         this.f_10ipfkhis = c_57tlcvvsi;
         this.setControls(3);
      }

      @Override
      public boolean canStart() {
         LivingEntity var1 = this.f_10ipfkhis.getTargetEntity();
         return var1 != null && var1.isAlive();
      }

      @Override
      public void start() {
         this.f_14tsbkduv = 0;
      }

      @Override
      public void stop() {
         this.f_10ipfkhis.setFireActive(false);
      }

      @Override
      public void tick() {
         --this.f_88vaafuxg;
         LivingEntity var1 = this.f_10ipfkhis.getTargetEntity();
         double var2 = this.f_10ipfkhis.getSquaredDistanceTo(var1);
         if (var2 < 4.0) {
            if (this.f_88vaafuxg <= 0) {
               this.f_88vaafuxg = 20;
               this.f_10ipfkhis.attack(var1);
            }

            this.f_10ipfkhis.getMovementControl().update(var1.x, var1.y, var1.z, 1.0);
         } else if (var2 < 256.0) {
            double var4 = var1.x - this.f_10ipfkhis.x;
            double var6 = var1.getBoundingBox().minY + (double)(var1.height / 2.0F) - (this.f_10ipfkhis.y + (double)(this.f_10ipfkhis.height / 2.0F));
            double var8 = var1.z - this.f_10ipfkhis.z;
            if (this.f_88vaafuxg <= 0) {
               ++this.f_14tsbkduv;
               if (this.f_14tsbkduv == 1) {
                  this.f_88vaafuxg = 60;
                  this.f_10ipfkhis.setFireActive(true);
               } else if (this.f_14tsbkduv <= 4) {
                  this.f_88vaafuxg = 6;
               } else {
                  this.f_88vaafuxg = 100;
                  this.f_14tsbkduv = 0;
                  this.f_10ipfkhis.setFireActive(false);
               }

               if (this.f_14tsbkduv > 1) {
                  float var10 = MathHelper.sqrt(MathHelper.sqrt(var2)) * 0.5F;
                  this.f_10ipfkhis.world.doEvent(null, 1009, new BlockPos((int)this.f_10ipfkhis.x, (int)this.f_10ipfkhis.y, (int)this.f_10ipfkhis.z), 0);

                  for(int var11 = 0; var11 < 1; ++var11) {
                     SmallFireballEntity var12 = new SmallFireballEntity(
                        this.f_10ipfkhis.world,
                        this.f_10ipfkhis,
                        var4 + this.f_10ipfkhis.getRandom().nextGaussian() * (double)var10,
                        var6,
                        var8 + this.f_10ipfkhis.getRandom().nextGaussian() * (double)var10
                     );
                     var12.y = this.f_10ipfkhis.y + (double)(this.f_10ipfkhis.height / 2.0F) + 0.5;
                     this.f_10ipfkhis.world.addEntity(var12);
                  }
               }
            }

            this.f_10ipfkhis.getLookControl().setLookatValues(var1, 10.0F, 10.0F);
         } else {
            this.f_10ipfkhis.getNavigation().stopCurrentNavigation();
            this.f_10ipfkhis.getMovementControl().update(var1.x, var1.y, var1.z, 1.0);
         }

         super.tick();
      }
   }
}
