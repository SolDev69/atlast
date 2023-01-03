package net.minecraft.entity.living.mob.passive.animal;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.goal.AnimalBreedGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.mob.passive.PassiveEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WheatSeedsItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ChickenEntity extends AnimalEntity {
   public float flapProgress;
   public float maxWingDeviation;
   public float prevMaxWingDeviation;
   public float prevFlapProgress;
   public float flapSpeed = 1.0F;
   public int eggLayTime;
   public boolean f_77hveswun;

   public ChickenEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.4F, 0.7F);
      this.eggLayTime = this.random.nextInt(6000) + 6000;
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new EscapeDangerGoal(this, 1.4));
      this.goalSelector.addGoal(2, new AnimalBreedGoal(this, 1.0));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.0, Items.WHEAT_SEEDS, false));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(5, new WanderAroundGoal(this, 1.0));
      this.goalSelector.addGoal(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(7, new LookAroundGoal(this));
   }

   @Override
   public float getEyeHeight() {
      return this.height;
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(4.0);
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.25);
   }

   @Override
   public void tickAI() {
      super.tickAI();
      this.prevFlapProgress = this.flapProgress;
      this.prevMaxWingDeviation = this.maxWingDeviation;
      this.maxWingDeviation = (float)((double)this.maxWingDeviation + (double)(this.onGround ? -1 : 4) * 0.3);
      this.maxWingDeviation = MathHelper.clamp(this.maxWingDeviation, 0.0F, 1.0F);
      if (!this.onGround && this.flapSpeed < 1.0F) {
         this.flapSpeed = 1.0F;
      }

      this.flapSpeed = (float)((double)this.flapSpeed * 0.9);
      if (!this.onGround && this.velocityY < 0.0) {
         this.velocityY *= 0.6;
      }

      this.flapProgress += this.flapSpeed * 2.0F;
      if (!this.world.isClient && !this.isBaby() && !this.m_12gpacvwn() && --this.eggLayTime <= 0) {
         this.playSound("mob.chicken.plop", 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
         this.dropItem(Items.EGG, 1);
         this.eggLayTime = this.random.nextInt(6000) + 6000;
      }
   }

   @Override
   public void applyFallDamage(float distance, float g) {
   }

   @Override
   protected String getAmbientSound() {
      return "mob.chicken.say";
   }

   @Override
   protected String getHurtSound() {
      return "mob.chicken.hurt";
   }

   @Override
   protected String getDeathSound() {
      return "mob.chicken.hurt";
   }

   @Override
   protected void playStepSound(BlockPos x, Block y) {
      this.playSound("mob.chicken.step", 0.15F, 1.0F);
   }

   @Override
   protected Item getDefaultDropLoot() {
      return Items.FEATHER;
   }

   @Override
   protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
      int var3 = this.random.nextInt(3) + this.random.nextInt(1 + lootingMultiplier);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.dropItem(Items.FEATHER, 1);
      }

      if (this.isOnFire()) {
         this.dropItem(Items.COOKED_CHICKEN, 1);
      } else {
         this.dropItem(Items.CHICKEN, 1);
      }
   }

   public ChickenEntity makeChild(PassiveEntity c_19nmglwmx) {
      return new ChickenEntity(this.world);
   }

   @Override
   public boolean isBreedingItem(ItemStack stack) {
      return stack != null && stack.getItem() instanceof WheatSeedsItem;
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.f_77hveswun = nbt.getBoolean("IsChickenJockey");
   }

   @Override
   protected int getXpDrop(PlayerEntity playerEntity) {
      return this.m_12gpacvwn() ? 10 : super.getXpDrop(playerEntity);
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putBoolean("IsChickenJockey", this.f_77hveswun);
   }

   @Override
   protected boolean canDespawn() {
      return this.m_12gpacvwn() && this.rider == null;
   }

   @Override
   public void updateRiderPositon() {
      super.updateRiderPositon();
      float var1 = MathHelper.sin(this.bodyYaw * (float) Math.PI / 180.0F);
      float var2 = MathHelper.cos(this.bodyYaw * (float) Math.PI / 180.0F);
      float var3 = 0.1F;
      float var4 = 0.0F;
      this.rider
         .setPosition(
            this.x + (double)(var3 * var1), this.y + (double)(this.height * 0.5F) + this.rider.getRideHeight() + (double)var4, this.z - (double)(var3 * var2)
         );
      if (this.rider instanceof LivingEntity) {
         ((LivingEntity)this.rider).bodyYaw = this.bodyYaw;
      }
   }

   public boolean m_12gpacvwn() {
      return this.f_77hveswun;
   }

   public void m_55jhlznqt(boolean bl) {
      this.f_77hveswun = bl;
   }
}
