package net.minecraft.entity.living.mob.passive.animal.tamable;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.ai.goal.AnimalBreedGoal;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.OcelotSitOnBlockGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.UntamedActiveTargetGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.mob.passive.PassiveEntity;
import net.minecraft.entity.living.mob.passive.animal.AnimalEntity;
import net.minecraft.entity.living.mob.passive.animal.ChickenEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.locale.I18n;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class OcelotEntity extends TameableEntity {
   private FleeEntityGoal f_26nxlrbmv;
   private TemptGoal temptGoal;

   public OcelotEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.6F, 0.7F);
      ((MobEntityNavigation)this.getNavigation()).m_61diarbat(true);
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(2, this.sitGoal);
      this.goalSelector.addGoal(3, this.temptGoal = new TemptGoal(this, 0.6, Items.FISH, true));
      this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0, 10.0F, 5.0F));
      this.goalSelector.addGoal(6, new OcelotSitOnBlockGoal(this, 0.8));
      this.goalSelector.addGoal(7, new PounceAtTargetGoal(this, 0.3F));
      this.goalSelector.addGoal(8, new AttackGoal(this));
      this.goalSelector.addGoal(9, new AnimalBreedGoal(this, 0.8));
      this.goalSelector.addGoal(10, new WanderAroundGoal(this, 0.8));
      this.goalSelector.addGoal(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
      this.targetSelector.addGoal(1, new UntamedActiveTargetGoal(this, ChickenEntity.class, false));
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(18, (byte)0);
   }

   @Override
   public void m_45jbqtvrb() {
      if (this.getMovementControl().isUpdated()) {
         double var1 = this.getMovementControl().getSpeed();
         if (var1 == 0.6) {
            this.setSneaking(true);
            this.setSprinting(false);
         } else if (var1 == 1.33) {
            this.setSneaking(false);
            this.setSprinting(true);
         } else {
            this.setSneaking(false);
            this.setSprinting(false);
         }
      } else {
         this.setSneaking(false);
         this.setSprinting(false);
      }
   }

   @Override
   protected boolean canDespawn() {
      return !this.isTamed() && this.time > 2400;
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(10.0);
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.3F);
   }

   @Override
   public void applyFallDamage(float distance, float g) {
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putInt("CatType", this.getCatVariant());
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.setCatVariant(nbt.getInt("CatType"));
   }

   @Override
   protected String getAmbientSound() {
      if (this.isTamed()) {
         if (this.isInLove()) {
            return "mob.cat.purr";
         } else {
            return this.random.nextInt(4) == 0 ? "mob.cat.purreow" : "mob.cat.meow";
         }
      } else {
         return "";
      }
   }

   @Override
   protected String getHurtSound() {
      return "mob.cat.hitt";
   }

   @Override
   protected String getDeathSound() {
      return "mob.cat.hitt";
   }

   @Override
   protected float getSoundVolume() {
      return 0.4F;
   }

   @Override
   protected Item getDefaultDropLoot() {
      return Items.LEATHER;
   }

   @Override
   public boolean attack(Entity entity) {
      return entity.damage(DamageSource.mob(this), 3.0F);
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerable(source)) {
         return false;
      } else {
         this.sitGoal.setEnabledWithOwner(false);
         return super.damage(source, amount);
      }
   }

   @Override
   protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
   }

   @Override
   public boolean canInteract(PlayerEntity player) {
      ItemStack var2 = player.inventory.getMainHandStack();
      if (this.isTamed()) {
         if (this.m_77pxwyntx(player) && !this.world.isClient && !this.isBreedingItem(var2)) {
            this.sitGoal.setEnabledWithOwner(!this.isSitting());
         }
      } else if (this.temptGoal.isGoalActive() && var2 != null && var2.getItem() == Items.FISH && player.getSquaredDistanceTo(this) < 9.0) {
         if (!player.abilities.creativeMode) {
            --var2.size;
         }

         if (var2.size <= 0) {
            player.inventory.setStack(player.inventory.selectedSlot, null);
         }

         if (!this.world.isClient) {
            if (this.random.nextInt(3) == 0) {
               this.setTamed(true);
               this.setCatVariant(1 + this.world.random.nextInt(3));
               this.setOwner(player.getUuid().toString());
               this.showEmoteParticle(true);
               this.sitGoal.setEnabledWithOwner(true);
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

   public OcelotEntity makeChild(PassiveEntity c_19nmglwmx) {
      OcelotEntity var2 = new OcelotEntity(this.world);
      if (this.isTamed()) {
         var2.setOwner(this.getOwnerName());
         var2.setTamed(true);
         var2.setCatVariant(this.getCatVariant());
      }

      return var2;
   }

   @Override
   public boolean isBreedingItem(ItemStack stack) {
      return stack != null && stack.getItem() == Items.FISH;
   }

   @Override
   public boolean canBreedWith(AnimalEntity other) {
      if (other == this) {
         return false;
      } else if (!this.isTamed()) {
         return false;
      } else if (!(other instanceof OcelotEntity)) {
         return false;
      } else {
         OcelotEntity var2 = (OcelotEntity)other;
         if (!var2.isTamed()) {
            return false;
         } else {
            return this.isInLove() && var2.isInLove();
         }
      }
   }

   public int getCatVariant() {
      return this.dataTracker.getByte(18);
   }

   public void setCatVariant(int variant) {
      this.dataTracker.update(18, (byte)variant);
   }

   @Override
   public boolean canSpawn() {
      return this.world.random.nextInt(3) != 0;
   }

   @Override
   public boolean m_52qkzdxky() {
      if (this.world.canBuildIn(this.getBoundingBox(), this)
         && this.world.getCollisions(this, this.getBoundingBox()).isEmpty()
         && !this.world.containsLiquid(this.getBoundingBox())) {
         BlockPos var1 = new BlockPos(this.x, this.getBoundingBox().minY, this.z);
         if (var1.getY() < 63) {
            return false;
         }

         Block var2 = this.world.getBlockState(var1.down()).getBlock();
         if (var2 == Blocks.GRASS || var2.getMaterial() == Material.LEAVES) {
            return true;
         }
      }

      return false;
   }

   @Override
   public String getName() {
      if (this.hasCustomName()) {
         return this.getCustomName();
      } else {
         return this.isTamed() ? I18n.translate("entity.Cat.name") : super.getName();
      }
   }

   @Override
   public void setTamed(boolean tamed) {
      super.setTamed(tamed);
   }

   @Override
   protected void m_65opiswxw() {
      if (this.f_26nxlrbmv == null) {
         this.f_26nxlrbmv = new FleeEntityGoal(this, new Predicate() {
            public boolean apply(Entity c_47ldwddrb) {
               return c_47ldwddrb instanceof PlayerEntity;
            }
         }, 16.0F, 0.8, 1.33);
      }

      this.goalSelector.removeGoal(this.f_26nxlrbmv);
      if (!this.isTamed()) {
         this.goalSelector.addGoal(4, this.f_26nxlrbmv);
      }
   }

   @Override
   public EntityData initialize(LocalDifficulty localDifficulty, EntityData entityData) {
      entityData = super.initialize(localDifficulty, entityData);
      if (this.world.random.nextInt(7) == 0) {
         for(int var3 = 0; var3 < 2; ++var3) {
            OcelotEntity var4 = new OcelotEntity(this.world);
            var4.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
            var4.setBreedingAge(-24000);
            this.world.addEntity(var4);
         }
      }

      return entityData;
   }
}
