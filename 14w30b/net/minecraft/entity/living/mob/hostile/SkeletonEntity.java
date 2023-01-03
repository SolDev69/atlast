package net.minecraft.entity.living.mob.hostile;

import com.google.common.base.Predicate;
import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.AvoidSunlightGoal;
import net.minecraft.entity.ai.goal.EscapeSunlightGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.LivingEntityType;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.entity.living.mob.IronGolemEntity;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.entity.living.mob.RangedAttackMob;
import net.minecraft.entity.living.mob.passive.animal.tamable.WolfEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.achievement.Achievements;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.NetherDimension;

public class SkeletonEntity extends HostileEntity implements RangedAttackMob {
   private ProjectileAttackGoal rangeAttackGoal = new ProjectileAttackGoal(this, 1.0, 20, 60, 15.0F);
   private MeleeAttackGoal meleeAttackGoal = new MeleeAttackGoal(this, PlayerEntity.class, 1.2, false);

   public SkeletonEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(2, new AvoidSunlightGoal(this));
      this.goalSelector.addGoal(2, this.fleeExplodingCreeperGoal);
      this.goalSelector.addGoal(3, new EscapeSunlightGoal(this, 1.0));
      this.goalSelector.addGoal(3, new FleeEntityGoal(this, new Predicate() {
         public boolean apply(Entity c_47ldwddrb) {
            return c_47ldwddrb instanceof WolfEntity;
         }
      }, 6.0F, 1.0, 1.2));
      this.goalSelector.addGoal(4, new WanderAroundGoal(this, 1.0));
      this.goalSelector.addGoal(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(6, new LookAroundGoal(this));
      this.targetSelector.addGoal(1, new RevengeGoal(this, false));
      this.targetSelector.addGoal(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
      this.targetSelector.addGoal(3, new ActiveTargetGoal(this, IronGolemEntity.class, true));
      if (c_54ruxjwzt != null && !c_54ruxjwzt.isClient) {
         this.updateAttackType();
      }
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.25);
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(13, new Byte((byte)0));
   }

   @Override
   protected String getAmbientSound() {
      return "mob.skeleton.say";
   }

   @Override
   protected String getHurtSound() {
      return "mob.skeleton.hurt";
   }

   @Override
   protected String getDeathSound() {
      return "mob.skeleton.death";
   }

   @Override
   protected void playStepSound(BlockPos x, Block y) {
      this.playSound("mob.skeleton.step", 0.15F, 1.0F);
   }

   @Override
   public boolean attack(Entity entity) {
      if (super.attack(entity)) {
         if (this.getType() == 1 && entity instanceof LivingEntity) {
            ((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffect.WITHER.id, 200));
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public LivingEntityType getMobType() {
      return LivingEntityType.UNDEAD;
   }

   @Override
   public void tickAI() {
      if (this.world.isSunny() && !this.world.isClient) {
         float var1 = this.getBrightness(1.0F);
         BlockPos var2 = new BlockPos(this.x, (double)Math.round(this.y), this.z);
         if (var1 > 0.5F && this.random.nextFloat() * 30.0F < (var1 - 0.4F) * 2.0F && this.world.hasSkyAccess(var2)) {
            boolean var3 = true;
            ItemStack var4 = this.getStackInInventory(4);
            if (var4 != null) {
               if (var4.isDamageable()) {
                  var4.setDamage(var4.getDamage() + this.random.nextInt(2));
                  if (var4.getDamage() >= var4.getMaxDamage()) {
                     this.renderBrokenItem(var4);
                     this.setEquipmentStack(4, null);
                  }
               }

               var3 = false;
            }

            if (var3) {
               this.setOnFireFor(8);
            }
         }
      }

      if (this.world.isClient && this.getType() == 1) {
         this.setDimensions(0.72F, 2.535F);
      }

      super.tickAI();
   }

   @Override
   public void tickRiding() {
      super.tickRiding();
      if (this.vehicle instanceof PathAwareEntity) {
         PathAwareEntity var1 = (PathAwareEntity)this.vehicle;
         this.bodyYaw = var1.bodyYaw;
      }
   }

   @Override
   public void onKilled(DamageSource source) {
      super.onKilled(source);
      if (source.getSource() instanceof ArrowEntity && source.getAttacker() instanceof PlayerEntity) {
         PlayerEntity var2 = (PlayerEntity)source.getAttacker();
         double var3 = var2.x - this.x;
         double var5 = var2.z - this.z;
         if (var3 * var3 + var5 * var5 >= 2500.0) {
            var2.incrementStat(Achievements.KILL_SKELETON_FROM_DISTANCE);
         }
      } else if (source.getAttacker() instanceof CreeperEntity
         && ((CreeperEntity)source.getAttacker()).isCharged()
         && ((CreeperEntity)source.getAttacker()).shouldDropMobHead()) {
         ((CreeperEntity)source.getAttacker()).addMobHeadDrop();
         this.dropItem(new ItemStack(Items.SKULL, 1, this.getType() == 1 ? 1 : 0), 0.0F);
      }
   }

   @Override
   protected Item getDefaultDropLoot() {
      return Items.ARROW;
   }

   @Override
   protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
      if (this.getType() == 1) {
         int var3 = this.random.nextInt(3 + lootingMultiplier) - 1;

         for(int var4 = 0; var4 < var3; ++var4) {
            this.dropItem(Items.COAL, 1);
         }
      } else {
         int var5 = this.random.nextInt(3 + lootingMultiplier);

         for(int var7 = 0; var7 < var5; ++var7) {
            this.dropItem(Items.ARROW, 1);
         }
      }

      int var6 = this.random.nextInt(3 + lootingMultiplier);

      for(int var8 = 0; var8 < var6; ++var8) {
         this.dropItem(Items.BONE, 1);
      }
   }

   @Override
   protected void dropRareItem() {
      if (this.getType() == 1) {
         this.dropItem(new ItemStack(Items.SKULL, 1, 1), 0.0F);
      }
   }

   @Override
   protected void initSpawningEquipment(LocalDifficulty localDifficulty) {
      super.initSpawningEquipment(localDifficulty);
      this.setEquipmentStack(0, new ItemStack(Items.BOW));
   }

   @Override
   public EntityData initialize(LocalDifficulty localDifficulty, EntityData entityData) {
      entityData = super.initialize(localDifficulty, entityData);
      if (this.world.dimension instanceof NetherDimension && this.getRandom().nextInt(5) > 0) {
         this.goalSelector.addGoal(4, this.meleeAttackGoal);
         this.setType(1);
         this.setEquipmentStack(0, new ItemStack(Items.STONE_SWORD));
         this.initializeAttribute(EntityAttributes.ATTACK_DAMAGE).setBase(4.0);
      } else {
         this.goalSelector.addGoal(4, this.rangeAttackGoal);
         this.initSpawningEquipment(localDifficulty);
         this.enchantEquipmentItemStack(localDifficulty);
      }

      this.setCanPickupLoot(this.random.nextFloat() < 0.55F * localDifficulty.getMultiplier());
      if (this.getStackInInventory(4) == null) {
         Calendar var3 = this.world.getCalendar();
         if (var3.get(2) + 1 == 10 && var3.get(5) == 31 && this.random.nextFloat() < 0.25F) {
            this.setEquipmentStack(4, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.LIT_PUMPKIN : Blocks.PUMPKIN));
            this.inventoryDropChances[4] = 0.0F;
         }
      }

      return entityData;
   }

   public void updateAttackType() {
      this.goalSelector.removeGoal(this.meleeAttackGoal);
      this.goalSelector.removeGoal(this.rangeAttackGoal);
      ItemStack var1 = this.getStackInHand();
      if (var1 != null && var1.getItem() == Items.BOW) {
         this.goalSelector.addGoal(4, this.rangeAttackGoal);
      } else {
         this.goalSelector.addGoal(4, this.meleeAttackGoal);
      }
   }

   @Override
   public void doRangedAttack(LivingEntity target, float range) {
      ArrowEntity var3 = new ArrowEntity(this.world, this, target, 1.6F, (float)(14 - this.world.getDifficulty().getIndex() * 4));
      int var4 = EnchantmentHelper.getLevel(Enchantment.POWER.id, this.getStackInHand());
      int var5 = EnchantmentHelper.getLevel(Enchantment.PUNCH.id, this.getStackInHand());
      var3.setDamage((double)(range * 2.0F) + this.random.nextGaussian() * 0.25 + (double)((float)this.world.getDifficulty().getIndex() * 0.11F));
      if (var4 > 0) {
         var3.setDamage(var3.getDamage() + (double)var4 * 0.5 + 0.5);
      }

      if (var5 > 0) {
         var3.setPunchLevel(var5);
      }

      if (EnchantmentHelper.getLevel(Enchantment.FLAME.id, this.getStackInHand()) > 0 || this.getType() == 1) {
         var3.setOnFireFor(100);
      }

      this.playSound("random.bow", 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
      this.world.addEntity(var3);
   }

   public int getType() {
      return this.dataTracker.getByte(13);
   }

   public void setType(int type) {
      this.dataTracker.update(13, (byte)type);
      this.immuneToFire = type == 1;
      if (type == 1) {
         this.setDimensions(0.72F, 2.535F);
      } else {
         this.setDimensions(0.6F, 1.95F);
      }
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      if (nbt.isType("SkeletonType", 99)) {
         byte var2 = nbt.getByte("SkeletonType");
         this.setType(var2);
      }

      this.updateAttackType();
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putByte("SkeletonType", (byte)this.getType());
   }

   @Override
   public void setEquipmentStack(int slot, ItemStack stack) {
      super.setEquipmentStack(slot, stack);
      if (!this.world.isClient && slot == 0) {
         this.updateAttackType();
      }
   }

   @Override
   public float getEyeHeight() {
      return this.getType() == 1 ? super.getEyeHeight() : 1.74F;
   }

   @Override
   public double getRideHeight() {
      return super.getRideHeight() - 0.5;
   }
}
