package net.minecraft.entity.living.mob.passive.animal;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.goal.AnimalBreedGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.PlayerControlGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.mob.hostile.ZombiePigmanEntity;
import net.minecraft.entity.living.mob.passive.PassiveEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.weather.LightningBoltEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.achievement.Achievements;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PigEntity extends AnimalEntity {
   private final PlayerControlGoal playerControlGoal;

   public PigEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.9F, 0.9F);
      ((MobEntityNavigation)this.getNavigation()).m_61diarbat(true);
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new EscapeDangerGoal(this, 1.25));
      this.goalSelector.addGoal(2, this.playerControlGoal = new PlayerControlGoal(this, 0.3F));
      this.goalSelector.addGoal(3, new AnimalBreedGoal(this, 1.0));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.2, Items.CARROT_ON_A_STICK, false));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.2, Items.CARROT, false));
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(6, new WanderAroundGoal(this, 1.0));
      this.goalSelector.addGoal(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(8, new LookAroundGoal(this));
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(10.0);
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.25);
   }

   @Override
   public boolean canBeControlledByRider() {
      ItemStack var1 = ((PlayerEntity)this.rider).getStackInHand();
      return var1 != null && var1.getItem() == Items.CARROT_ON_A_STICK;
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(16, (byte)0);
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putBoolean("Saddle", this.isSaddled());
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.setSaddled(nbt.getBoolean("Saddle"));
   }

   @Override
   protected String getAmbientSound() {
      return "mob.pig.say";
   }

   @Override
   protected String getHurtSound() {
      return "mob.pig.say";
   }

   @Override
   protected String getDeathSound() {
      return "mob.pig.death";
   }

   @Override
   protected void playStepSound(BlockPos x, Block y) {
      this.playSound("mob.pig.step", 0.15F, 1.0F);
   }

   @Override
   public boolean canInteract(PlayerEntity player) {
      if (super.canInteract(player)) {
         return true;
      } else if (!this.isSaddled() || this.world.isClient || this.rider != null && this.rider != player) {
         return false;
      } else {
         player.startRiding(this);
         return true;
      }
   }

   @Override
   protected Item getDefaultDropLoot() {
      return this.isOnFire() ? Items.COOKED_PORKCHOP : Items.PORKCHOP;
   }

   @Override
   protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
      int var3 = this.random.nextInt(3) + 1 + this.random.nextInt(1 + lootingMultiplier);

      for(int var4 = 0; var4 < var3; ++var4) {
         if (this.isOnFire()) {
            this.dropItem(Items.COOKED_PORKCHOP, 1);
         } else {
            this.dropItem(Items.PORKCHOP, 1);
         }
      }

      if (this.isSaddled()) {
         this.dropItem(Items.SADDLE, 1);
      }
   }

   public boolean isSaddled() {
      return (this.dataTracker.getByte(16) & 1) != 0;
   }

   public void setSaddled(boolean saddled) {
      if (saddled) {
         this.dataTracker.update(16, (byte)1);
      } else {
         this.dataTracker.update(16, (byte)0);
      }
   }

   @Override
   public void onLightningStrike(LightningBoltEntity lightning) {
      if (!this.world.isClient) {
         ZombiePigmanEntity var2 = new ZombiePigmanEntity(this.world);
         var2.setEquipmentStack(0, new ItemStack(Items.GOLDEN_SWORD));
         var2.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
         this.world.addEntity(var2);
         this.remove();
      }
   }

   @Override
   public void applyFallDamage(float distance, float g) {
      super.applyFallDamage(distance, g);
      if (distance > 5.0F && this.rider instanceof PlayerEntity) {
         ((PlayerEntity)this.rider).incrementStat(Achievements.RIDE_PIG_OFF_CLIFF);
      }
   }

   public PigEntity makeChild(PassiveEntity c_19nmglwmx) {
      return new PigEntity(this.world);
   }

   @Override
   public boolean isBreedingItem(ItemStack stack) {
      return stack != null && stack.getItem() == Items.CARROT;
   }

   public PlayerControlGoal getPlayerControlGoal() {
      return this.playerControlGoal;
   }
}
