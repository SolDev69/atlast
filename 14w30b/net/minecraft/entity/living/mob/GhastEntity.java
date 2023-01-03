package net.minecraft.entity.living.mob;

import java.util.Random;
import net.minecraft.entity.ai.control.MovementControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MobEntityPlayerTargetGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.achievement.Achievements;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;

public class GhastEntity extends FlyingEntity implements Monster {
   private int fireballStrength = 1;

   public GhastEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(4.0F, 4.0F);
      this.immuneToFire = true;
      this.experiencePoints = 5;
      this.movementControl = new GhastEntity.C_70tpbqmzx(this);
      this.goalSelector.addGoal(5, new GhastEntity.C_49icwjxcc(this));
      this.goalSelector.addGoal(7, new GhastEntity.C_52ynrqnkv(this));
      this.goalSelector.addGoal(7, new GhastEntity.C_47yhdujui(this));
      this.targetSelector.addGoal(1, new MobEntityPlayerTargetGoal(this));
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public boolean isShooting() {
      return this.dataTracker.getByte(16) != 0;
   }

   public void m_85jpgqvpf(boolean bl) {
      this.dataTracker.update(16, Byte.valueOf((byte)(bl ? 1 : 0)));
   }

   public int m_76yykhmgd() {
      return this.fireballStrength;
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.world.isClient && this.world.getDifficulty() == Difficulty.PEACEFUL) {
         this.remove();
      }
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerable(source)) {
         return false;
      } else if ("fireball".equals(source.getName()) && source.getAttacker() instanceof PlayerEntity) {
         super.damage(source, 1000.0F);
         ((PlayerEntity)source.getAttacker()).incrementStat(Achievements.KILL_GHAST_WITH_FIREBALL);
         return true;
      } else {
         return super.damage(source, amount);
      }
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(16, (byte)0);
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(10.0);
      this.initializeAttribute(EntityAttributes.FOLLOW_RANGE).setBase(100.0);
   }

   @Override
   protected String getAmbientSound() {
      return "mob.ghast.moan";
   }

   @Override
   protected String getHurtSound() {
      return "mob.ghast.scream";
   }

   @Override
   protected String getDeathSound() {
      return "mob.ghast.death";
   }

   @Override
   protected Item getDefaultDropLoot() {
      return Items.GUNPOWDER;
   }

   @Override
   protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
      int var3 = this.random.nextInt(2) + this.random.nextInt(1 + lootingMultiplier);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.dropItem(Items.GHAST_TEAR, 1);
      }

      var3 = this.random.nextInt(3) + this.random.nextInt(1 + lootingMultiplier);

      for(int var6 = 0; var6 < var3; ++var6) {
         this.dropItem(Items.GUNPOWDER, 1);
      }
   }

   @Override
   protected float getSoundVolume() {
      return 10.0F;
   }

   @Override
   public boolean canSpawn() {
      return this.random.nextInt(20) == 0 && super.canSpawn() && this.world.getDifficulty() != Difficulty.PEACEFUL;
   }

   @Override
   public int getLimitPerChunk() {
      return 1;
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putInt("ExplosionPower", this.fireballStrength);
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      if (nbt.isType("ExplosionPower", 99)) {
         this.fireballStrength = nbt.getInt("ExplosionPower");
      }
   }

   @Override
   public float getEyeHeight() {
      return 2.6F;
   }

   static class C_47yhdujui extends Goal {
      private GhastEntity f_00zekitnv;
      public int f_77ijdjbyx;

      public C_47yhdujui(GhastEntity c_65lktxchf) {
         this.f_00zekitnv = c_65lktxchf;
      }

      @Override
      public boolean canStart() {
         return this.f_00zekitnv.getTargetEntity() != null;
      }

      @Override
      public void start() {
         this.f_77ijdjbyx = 0;
      }

      @Override
      public void stop() {
         this.f_00zekitnv.m_85jpgqvpf(false);
      }

      @Override
      public void tick() {
         LivingEntity var1 = this.f_00zekitnv.getTargetEntity();
         double var2 = 64.0;
         if (var1.getSquaredDistanceTo(this.f_00zekitnv) < var2 * var2 && this.f_00zekitnv.canSee(var1)) {
            World var4 = this.f_00zekitnv.world;
            ++this.f_77ijdjbyx;
            if (this.f_77ijdjbyx == 10) {
               var4.doEvent(null, 1007, new BlockPos(this.f_00zekitnv), 0);
            }

            if (this.f_77ijdjbyx == 20) {
               double var5 = 4.0;
               Vec3d var7 = this.f_00zekitnv.m_01qqqsfds(1.0F);
               double var8 = var1.x - (this.f_00zekitnv.x + var7.x * var5);
               double var10 = var1.getBoundingBox().minY + (double)(var1.height / 2.0F) - (0.5 + this.f_00zekitnv.y + (double)(this.f_00zekitnv.height / 2.0F));
               double var12 = var1.z - (this.f_00zekitnv.z + var7.z * var5);
               var4.doEvent(null, 1008, new BlockPos(this.f_00zekitnv), 0);
               FireballEntity var14 = new FireballEntity(var4, this.f_00zekitnv, var8, var10, var12);
               var14.explosionPower = this.f_00zekitnv.m_76yykhmgd();
               var14.x = this.f_00zekitnv.x + var7.x * var5;
               var14.y = this.f_00zekitnv.y + (double)(this.f_00zekitnv.height / 2.0F) + 0.5;
               var14.z = this.f_00zekitnv.z + var7.z * var5;
               var4.addEntity(var14);
               this.f_77ijdjbyx = -40;
            }
         } else if (this.f_77ijdjbyx > 0) {
            --this.f_77ijdjbyx;
         }

         this.f_00zekitnv.m_85jpgqvpf(this.f_77ijdjbyx > 10);
      }
   }

   static class C_49icwjxcc extends Goal {
      private GhastEntity f_89hfaeics;

      public C_49icwjxcc(GhastEntity c_65lktxchf) {
         this.f_89hfaeics = c_65lktxchf;
         this.setControls(1);
      }

      @Override
      public boolean canStart() {
         MovementControl var1 = this.f_89hfaeics.getMovementControl();
         if (!var1.isUpdated()) {
            return true;
         } else {
            double var2 = var1.m_53wsenqsm() - this.f_89hfaeics.x;
            double var4 = var1.m_90fcnurdd() - this.f_89hfaeics.y;
            double var6 = var1.m_37abemtzt() - this.f_89hfaeics.z;
            double var8 = var2 * var2 + var4 * var4 + var6 * var6;
            return var8 < 1.0 || var8 > 3600.0;
         }
      }

      @Override
      public boolean shouldContinue() {
         return false;
      }

      @Override
      public void start() {
         Random var1 = this.f_89hfaeics.getRandom();
         double var2 = this.f_89hfaeics.x + (double)((var1.nextFloat() * 2.0F - 1.0F) * 16.0F);
         double var4 = this.f_89hfaeics.y + (double)((var1.nextFloat() * 2.0F - 1.0F) * 16.0F);
         double var6 = this.f_89hfaeics.z + (double)((var1.nextFloat() * 2.0F - 1.0F) * 16.0F);
         this.f_89hfaeics.getMovementControl().update(var2, var4, var6, 1.0);
      }
   }

   static class C_52ynrqnkv extends Goal {
      private GhastEntity f_02ykdiuss;

      public C_52ynrqnkv(GhastEntity c_65lktxchf) {
         this.f_02ykdiuss = c_65lktxchf;
         this.setControls(2);
      }

      @Override
      public boolean canStart() {
         return true;
      }

      @Override
      public void tick() {
         if (this.f_02ykdiuss.getTargetEntity() == null) {
            this.f_02ykdiuss.bodyYaw = this.f_02ykdiuss.yaw = -((float)Math.atan2(this.f_02ykdiuss.velocityX, this.f_02ykdiuss.velocityZ))
               * 180.0F
               / (float) Math.PI;
         } else {
            LivingEntity var1 = this.f_02ykdiuss.getTargetEntity();
            double var2 = 64.0;
            if (var1.getSquaredDistanceTo(this.f_02ykdiuss) < var2 * var2) {
               double var4 = var1.x - this.f_02ykdiuss.x;
               double var6 = var1.z - this.f_02ykdiuss.z;
               this.f_02ykdiuss.bodyYaw = this.f_02ykdiuss.yaw = -((float)Math.atan2(var4, var6)) * 180.0F / (float) Math.PI;
            }
         }
      }
   }

   static class C_70tpbqmzx extends MovementControl {
      private GhastEntity f_91fxshgdi;
      private int f_01wzwrjxs;

      public C_70tpbqmzx(GhastEntity c_65lktxchf) {
         super(c_65lktxchf);
         this.f_91fxshgdi = c_65lktxchf;
      }

      @Override
      public void tickUpdateMovement() {
         if (this.updated) {
            double var1 = this.x - this.f_91fxshgdi.x;
            double var3 = this.y - this.f_91fxshgdi.y;
            double var5 = this.z - this.f_91fxshgdi.z;
            double var7 = var1 * var1 + var3 * var3 + var5 * var5;
            if (this.f_01wzwrjxs-- <= 0) {
               this.f_01wzwrjxs += this.f_91fxshgdi.getRandom().nextInt(5) + 2;
               var7 = (double)MathHelper.sqrt(var7);
               if (this.m_30nktnvbx(this.x, this.y, this.z, var7)) {
                  this.f_91fxshgdi.velocityX += var1 / var7 * 0.1;
                  this.f_91fxshgdi.velocityY += var3 / var7 * 0.1;
                  this.f_91fxshgdi.velocityZ += var5 / var7 * 0.1;
               } else {
                  this.updated = false;
               }
            }
         }
      }

      private boolean m_30nktnvbx(double d, double e, double f, double g) {
         double var9 = (d - this.f_91fxshgdi.x) / g;
         double var11 = (e - this.f_91fxshgdi.y) / g;
         double var13 = (f - this.f_91fxshgdi.z) / g;
         Box var15 = this.f_91fxshgdi.getBoundingBox();

         for(int var16 = 1; (double)var16 < g; ++var16) {
            var15 = var15.move(var9, var11, var13);
            if (!this.f_91fxshgdi.world.getCollisions(this.f_91fxshgdi, var15).isEmpty()) {
               return false;
            }
         }

         return true;
      }
   }
}
