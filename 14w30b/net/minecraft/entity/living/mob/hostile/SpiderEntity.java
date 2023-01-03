package net.minecraft.entity.living.mob.hostile;

import java.util.Random;
import net.minecraft.C_78ehokafe;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.LivingEntityType;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.entity.living.mob.IronGolemEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class SpiderEntity extends HostileEntity {
   public SpiderEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(1.4F, 0.9F);
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(2, this.fleeExplodingCreeperGoal);
      this.goalSelector.addGoal(3, new PounceAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(4, new SpiderEntity.C_83ypeycif(this, PlayerEntity.class));
      this.goalSelector.addGoal(4, new SpiderEntity.C_83ypeycif(this, IronGolemEntity.class));
      this.goalSelector.addGoal(5, new WanderAroundGoal(this, 0.8));
      this.goalSelector.addGoal(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(6, new LookAroundGoal(this));
      this.targetSelector.addGoal(1, new RevengeGoal(this, false));
      this.targetSelector.addGoal(2, new SpiderEntity.C_69hgjzpkv(this, PlayerEntity.class));
      this.targetSelector.addGoal(3, new SpiderEntity.C_69hgjzpkv(this, IronGolemEntity.class));
   }

   @Override
   protected EntityNavigation createNavigation(World world) {
      return new C_78ehokafe(this, world);
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(16, new Byte((byte)0));
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.world.isClient) {
         this.setClimbingWall(this.collidingHorizontally);
      }
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(16.0);
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.3F);
   }

   @Override
   protected String getAmbientSound() {
      return "mob.spider.say";
   }

   @Override
   protected String getHurtSound() {
      return "mob.spider.say";
   }

   @Override
   protected String getDeathSound() {
      return "mob.spider.death";
   }

   @Override
   protected void playStepSound(BlockPos x, Block y) {
      this.playSound("mob.spider.step", 0.15F, 1.0F);
   }

   @Override
   protected Item getDefaultDropLoot() {
      return Items.STRING;
   }

   @Override
   protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
      super.dropLoot(allowDrops, lootingMultiplier);
      if (allowDrops && (this.random.nextInt(3) == 0 || this.random.nextInt(1 + lootingMultiplier) > 0)) {
         this.dropItem(Items.SPIDER_EYE, 1);
      }
   }

   @Override
   public boolean isClimbing() {
      return this.isClimbingWall();
   }

   @Override
   public void onCobwebCollision() {
   }

   @Override
   public LivingEntityType getMobType() {
      return LivingEntityType.ARTHROPOD;
   }

   @Override
   public boolean canHaveStatusEffect(StatusEffectInstance effect) {
      return effect.getId() == StatusEffect.POISON.id ? false : super.canHaveStatusEffect(effect);
   }

   public boolean isClimbingWall() {
      return (this.dataTracker.getByte(16) & 1) != 0;
   }

   public void setClimbingWall(boolean climbing) {
      byte var2 = this.dataTracker.getByte(16);
      if (climbing) {
         var2 = (byte)(var2 | 1);
      } else {
         var2 = (byte)(var2 & -2);
      }

      this.dataTracker.update(16, var2);
   }

   @Override
   public EntityData initialize(LocalDifficulty localDifficulty, EntityData entityData) {
      entityData = super.initialize(localDifficulty, entityData);
      if (this.world.random.nextInt(100) == 0) {
         SkeletonEntity var3 = new SkeletonEntity(this.world);
         var3.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
         var3.initialize(localDifficulty, null);
         this.world.addEntity(var3);
         var3.startRiding(this);
      }

      if (entityData == null) {
         entityData = new SpiderEntity.Data();
         if (this.world.getDifficulty() == Difficulty.HARD && this.world.random.nextFloat() < 0.1F * localDifficulty.getMultiplier()) {
            ((SpiderEntity.Data)entityData).setEffect(this.world.random);
         }
      }

      if (entityData instanceof SpiderEntity.Data) {
         int var5 = ((SpiderEntity.Data)entityData).effect;
         if (var5 > 0 && StatusEffect.BY_ID[var5] != null) {
            this.addStatusEffect(new StatusEffectInstance(var5, Integer.MAX_VALUE));
         }
      }

      return entityData;
   }

   @Override
   public float getEyeHeight() {
      return 0.65F;
   }

   static class C_69hgjzpkv extends ActiveTargetGoal {
      public C_69hgjzpkv(SpiderEntity c_83fmezsnj, Class class_) {
         super(c_83fmezsnj, class_, true);
      }

      @Override
      public boolean canStart() {
         float var1 = this.entity.getBrightness(1.0F);
         return var1 >= 0.5F ? false : super.canStart();
      }
   }

   static class C_83ypeycif extends MeleeAttackGoal {
      public C_83ypeycif(SpiderEntity c_83fmezsnj, Class class_) {
         super(c_83fmezsnj, class_, 1.0, true);
      }

      @Override
      public boolean shouldContinue() {
         float var1 = this.entity.getBrightness(1.0F);
         if (var1 >= 0.5F && this.entity.getRandom().nextInt(100) == 0) {
            this.entity.setAttackTarget(null);
            return false;
         } else {
            return super.shouldContinue();
         }
      }

      @Override
      protected double m_29wetoiav(LivingEntity c_97zulxhng) {
         return (double)(4.0F + c_97zulxhng.width);
      }
   }

   public static class Data implements EntityData {
      public int effect;

      public void setEffect(Random random) {
         int var2 = random.nextInt(5);
         if (var2 <= 1) {
            this.effect = StatusEffect.SPEED.id;
         } else if (var2 <= 2) {
            this.effect = StatusEffect.STRENGTH.id;
         } else if (var2 <= 3) {
            this.effect = StatusEffect.REGENERATION.id;
         } else if (var2 <= 4) {
            this.effect = StatusEffect.INVISIBILITY.id;
         }
      }
   }
}
