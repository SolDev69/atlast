package net.minecraft.entity.living.mob.passive.animal.tamable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.AnimalBreedGoal;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.OwnerHurtGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.UntamedActiveTargetGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.goal.WolfBegGoal;
import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.mob.GhastEntity;
import net.minecraft.entity.living.mob.hostile.CreeperEntity;
import net.minecraft.entity.living.mob.hostile.SkeletonEntity;
import net.minecraft.entity.living.mob.passive.PassiveEntity;
import net.minecraft.entity.living.mob.passive.animal.AnimalEntity;
import net.minecraft.entity.living.mob.passive.animal.HorseBaseEntity;
import net.minecraft.entity.living.mob.passive.animal.SheepEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.FoodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;

public class WolfEntity extends TameableEntity {
   private float begAnimationProgress;
   private float lastBegAnimationProgress;
   private boolean furWet;
   private boolean canShakeWaterOff;
   private float shakeProgress;
   private float lastShakeProgress;

   public WolfEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.6F, 0.8F);
      ((MobEntityNavigation)this.getNavigation()).m_61diarbat(true);
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(2, this.sitGoal);
      this.goalSelector.addGoal(3, new PounceAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, true));
      this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
      this.goalSelector.addGoal(6, new AnimalBreedGoal(this, 1.0));
      this.goalSelector.addGoal(7, new WanderAroundGoal(this, 1.0));
      this.goalSelector.addGoal(8, new WolfBegGoal(this, 8.0F));
      this.goalSelector.addGoal(9, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(9, new LookAroundGoal(this));
      this.targetSelector.addGoal(1, new AttackWithOwnerGoal(this));
      this.targetSelector.addGoal(2, new OwnerHurtGoal(this));
      this.targetSelector.addGoal(3, new RevengeGoal(this, true));
      this.targetSelector.addGoal(4, new UntamedActiveTargetGoal(this, SheepEntity.class, false));
      this.targetSelector.addGoal(5, new ActiveTargetGoal(this, SkeletonEntity.class, false));
      this.setTamed(false);
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.3F);
      if (this.isTamed()) {
         this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(20.0);
      } else {
         this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(8.0);
      }

      this.getAttributes().registerAttribute(EntityAttributes.ATTACK_DAMAGE);
      this.initializeAttribute(EntityAttributes.ATTACK_DAMAGE).setBase(2.0);
   }

   @Override
   public void setAttackTarget(LivingEntity targetEntity) {
      super.setAttackTarget(targetEntity);
      if (targetEntity == null) {
         this.updateAnger(false);
      } else if (!this.isTamed()) {
         this.updateAnger(true);
      }
   }

   @Override
   protected void m_45jbqtvrb() {
      this.dataTracker.update(18, this.getHealth());
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(18, new Float(this.getHealth()));
      this.dataTracker.put(19, new Byte((byte)0));
      this.dataTracker.put(20, new Byte((byte)DyeColor.RED.getIndex()));
   }

   @Override
   protected void playStepSound(BlockPos x, Block y) {
      this.playSound("mob.wolf.step", 0.15F, 1.0F);
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putBoolean("Angry", this.isAngry());
      nbt.putByte("CollarColor", (byte)this.getCollarColor().getMetadata());
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.updateAnger(nbt.getBoolean("Angry"));
      if (nbt.isType("CollarColor", 99)) {
         this.setCollarColor(DyeColor.byMetadata(nbt.getByte("CollarColor")));
      }
   }

   @Override
   protected String getAmbientSound() {
      if (this.isAngry()) {
         return "mob.wolf.growl";
      } else if (this.random.nextInt(3) == 0) {
         return this.isTamed() && this.dataTracker.getFloat(18) < 10.0F ? "mob.wolf.whine" : "mob.wolf.panting";
      } else {
         return "mob.wolf.bark";
      }
   }

   @Override
   protected String getHurtSound() {
      return "mob.wolf.hurt";
   }

   @Override
   protected String getDeathSound() {
      return "mob.wolf.death";
   }

   @Override
   protected float getSoundVolume() {
      return 0.4F;
   }

   @Override
   protected Item getDefaultDropLoot() {
      return Item.byRawId(-1);
   }

   @Override
   public void tickAI() {
      super.tickAI();
      if (!this.world.isClient && this.furWet && !this.canShakeWaterOff && !this.m_59adgmjkb() && this.onGround) {
         this.canShakeWaterOff = true;
         this.shakeProgress = 0.0F;
         this.lastShakeProgress = 0.0F;
         this.world.doEntityEvent(this, (byte)8);
      }
   }

   @Override
   public void tick() {
      super.tick();
      this.lastBegAnimationProgress = this.begAnimationProgress;
      if (this.setBegging()) {
         this.begAnimationProgress += (1.0F - this.begAnimationProgress) * 0.4F;
      } else {
         this.begAnimationProgress += (0.0F - this.begAnimationProgress) * 0.4F;
      }

      if (this.isWet()) {
         this.furWet = true;
         this.canShakeWaterOff = false;
         this.shakeProgress = 0.0F;
         this.lastShakeProgress = 0.0F;
      } else if ((this.furWet || this.canShakeWaterOff) && this.canShakeWaterOff) {
         if (this.shakeProgress == 0.0F) {
            this.playSound("mob.wolf.shake", this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
         }

         this.lastShakeProgress = this.shakeProgress;
         this.shakeProgress += 0.05F;
         if (this.lastShakeProgress >= 2.0F) {
            this.furWet = false;
            this.canShakeWaterOff = false;
            this.lastShakeProgress = 0.0F;
            this.shakeProgress = 0.0F;
         }

         if (this.shakeProgress > 0.4F) {
            float var1 = (float)this.getBoundingBox().minY;
            int var2 = (int)(MathHelper.sin((this.shakeProgress - 0.4F) * (float) Math.PI) * 7.0F);

            for(int var3 = 0; var3 < var2; ++var3) {
               float var4 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
               float var5 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
               this.world
                  .addParticle(
                     ParticleType.WATER_SPLASH,
                     this.x + (double)var4,
                     (double)(var1 + 0.8F),
                     this.z + (double)var5,
                     this.velocityX,
                     this.velocityY,
                     this.velocityZ
                  );
            }
         }
      }
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public boolean isFurWet() {
      return this.furWet;
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public float shakeLerp(float tickDelta) {
      return 0.75F + (this.lastShakeProgress + (this.shakeProgress - this.lastShakeProgress) * tickDelta) / 2.0F * 0.25F;
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public float getShakeAngle(float tickdelta, float offset) {
      float var3 = (this.lastShakeProgress + (this.shakeProgress - this.lastShakeProgress) * tickdelta + offset) / 1.8F;
      if (var3 < 0.0F) {
         var3 = 0.0F;
      } else if (var3 > 1.0F) {
         var3 = 1.0F;
      }

      return MathHelper.sin(var3 * (float) Math.PI) * MathHelper.sin(var3 * (float) Math.PI * 11.0F) * 0.15F * (float) Math.PI;
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public float begLerp(float tickDelta) {
      return (this.lastBegAnimationProgress + (this.begAnimationProgress - this.lastBegAnimationProgress) * tickDelta) * 0.15F * (float) Math.PI;
   }

   @Override
   public float getEyeHeight() {
      return this.height * 0.8F;
   }

   @Override
   public int getLookPitchSpeed() {
      return this.isSitting() ? 20 : super.getLookPitchSpeed();
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerable(source)) {
         return false;
      } else {
         Entity var3 = source.getAttacker();
         this.sitGoal.setEnabledWithOwner(false);
         if (var3 != null && !(var3 instanceof PlayerEntity) && !(var3 instanceof ArrowEntity)) {
            amount = (amount + 1.0F) / 2.0F;
         }

         return super.damage(source, amount);
      }
   }

   @Override
   public boolean attack(Entity entity) {
      return entity.damage(DamageSource.mob(this), (float)((int)this.initializeAttribute(EntityAttributes.ATTACK_DAMAGE).get()));
   }

   @Override
   public void setTamed(boolean tamed) {
      super.setTamed(tamed);
      if (tamed) {
         this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(20.0);
      } else {
         this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(8.0);
      }

      this.initializeAttribute(EntityAttributes.ATTACK_DAMAGE).setBase(4.0);
   }

   @Override
   public boolean canInteract(PlayerEntity player) {
      ItemStack var2 = player.inventory.getMainHandStack();
      if (this.isTamed()) {
         if (var2 != null) {
            if (var2.getItem() instanceof FoodItem) {
               FoodItem var3 = (FoodItem)var2.getItem();
               if (var3.canBeCooked() && this.dataTracker.getFloat(18) < 20.0F) {
                  if (!player.abilities.creativeMode) {
                     --var2.size;
                  }

                  this.heal((float)var3.getHungerPoints(var2));
                  if (var2.size <= 0) {
                     player.inventory.setStack(player.inventory.selectedSlot, null);
                  }

                  return true;
               }
            } else if (var2.getItem() == Items.DYE) {
               DyeColor var4 = DyeColor.byMetadata(var2.getMetadata());
               if (var4 != this.getCollarColor()) {
                  this.setCollarColor(var4);
                  if (!player.abilities.creativeMode && --var2.size <= 0) {
                     player.inventory.setStack(player.inventory.selectedSlot, null);
                  }

                  return true;
               }
            }
         }

         if (this.m_77pxwyntx(player) && !this.world.isClient && !this.isBreedingItem(var2)) {
            this.sitGoal.setEnabledWithOwner(!this.isSitting());
            this.jumping = false;
            this.entityNavigation.stopCurrentNavigation();
            this.setAttackTarget(null);
         }
      } else if (var2 != null && var2.getItem() == Items.BONE && !this.isAngry()) {
         if (!player.abilities.creativeMode) {
            --var2.size;
         }

         if (var2.size <= 0) {
            player.inventory.setStack(player.inventory.selectedSlot, null);
         }

         if (!this.world.isClient) {
            if (this.random.nextInt(3) == 0) {
               this.setTamed(true);
               this.entityNavigation.stopCurrentNavigation();
               this.setAttackTarget(null);
               this.sitGoal.setEnabledWithOwner(true);
               this.setHealth(20.0F);
               this.setOwner(player.getUuid().toString());
               this.showEmoteParticle(true);
               this.world.doEntityEvent(this, (byte)7);
            } else {
               this.showEmoteParticle(false);
               this.world.doEntityEvent(this, (byte)6);
            }
         }

         return true;
      }

      return super.canInteract(player);
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   @Override
   public void doEvent(byte event) {
      if (event == 8) {
         this.canShakeWaterOff = true;
         this.shakeProgress = 0.0F;
         this.lastShakeProgress = 0.0F;
      } else {
         super.doEvent(event);
      }
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public float age() {
      if (this.isAngry()) {
         return 1.5393804F;
      } else {
         return this.isTamed() ? (0.55F - (20.0F - this.dataTracker.getFloat(18)) * 0.02F) * (float) Math.PI : (float) (Math.PI / 5);
      }
   }

   @Override
   public boolean isBreedingItem(ItemStack stack) {
      if (stack == null) {
         return false;
      } else {
         return !(stack.getItem() instanceof FoodItem) ? false : ((FoodItem)stack.getItem()).canBeCooked();
      }
   }

   @Override
   public int getLimitPerChunk() {
      return 8;
   }

   public boolean isAngry() {
      return (this.dataTracker.getByte(16) & 2) != 0;
   }

   public void updateAnger(boolean angry) {
      byte var2 = this.dataTracker.getByte(16);
      if (angry) {
         this.dataTracker.update(16, (byte)(var2 | 2));
      } else {
         this.dataTracker.update(16, (byte)(var2 & -3));
      }
   }

   public DyeColor getCollarColor() {
      return DyeColor.byMetadata(this.dataTracker.getByte(20) & 15);
   }

   public void setCollarColor(DyeColor color) {
      this.dataTracker.update(20, (byte)(color.getMetadata() & 15));
   }

   public WolfEntity makeChild(PassiveEntity c_19nmglwmx) {
      WolfEntity var2 = new WolfEntity(this.world);
      String var3 = this.getOwnerName();
      if (var3 != null && var3.trim().length() > 0) {
         var2.setOwner(var3);
         var2.setTamed(true);
      }

      return var2;
   }

   public void setBegging(boolean begging) {
      if (begging) {
         this.dataTracker.update(19, (byte)1);
      } else {
         this.dataTracker.update(19, (byte)0);
      }
   }

   @Override
   public boolean canBreedWith(AnimalEntity other) {
      if (other == this) {
         return false;
      } else if (!this.isTamed()) {
         return false;
      } else if (!(other instanceof WolfEntity)) {
         return false;
      } else {
         WolfEntity var2 = (WolfEntity)other;
         if (!var2.isTamed()) {
            return false;
         } else if (var2.isSitting()) {
            return false;
         } else {
            return this.isInLove() && var2.isInLove();
         }
      }
   }

   public boolean setBegging() {
      return this.dataTracker.getByte(19) == 1;
   }

   @Override
   protected boolean canDespawn() {
      return !this.isTamed() && this.time > 2400;
   }

   @Override
   public boolean shouldAttack(LivingEntity attackedEntity, LivingEntity attacker) {
      if (!(attackedEntity instanceof CreeperEntity) && !(attackedEntity instanceof GhastEntity)) {
         if (attackedEntity instanceof WolfEntity) {
            WolfEntity var3 = (WolfEntity)attackedEntity;
            if (var3.isTamed() && var3.getOwner() == attacker) {
               return false;
            }
         }

         if (attackedEntity instanceof PlayerEntity && attacker instanceof PlayerEntity && !((PlayerEntity)attacker).canAttack((PlayerEntity)attackedEntity)) {
            return false;
         } else {
            return !(attackedEntity instanceof HorseBaseEntity) || !((HorseBaseEntity)attackedEntity).isTame();
         }
      } else {
         return false;
      }
   }
}
