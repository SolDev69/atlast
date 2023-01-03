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
import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.mob.passive.PassiveEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CowEntity extends AnimalEntity {
   public CowEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.9F, 1.3F);
      ((MobEntityNavigation)this.getNavigation()).m_61diarbat(true);
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new EscapeDangerGoal(this, 2.0));
      this.goalSelector.addGoal(2, new AnimalBreedGoal(this, 1.0));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, Items.WHEAT, false));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25));
      this.goalSelector.addGoal(5, new WanderAroundGoal(this, 1.0));
      this.goalSelector.addGoal(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(7, new LookAroundGoal(this));
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(10.0);
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.2F);
   }

   @Override
   protected String getAmbientSound() {
      return "mob.cow.say";
   }

   @Override
   protected String getHurtSound() {
      return "mob.cow.hurt";
   }

   @Override
   protected String getDeathSound() {
      return "mob.cow.hurt";
   }

   @Override
   protected void playStepSound(BlockPos x, Block y) {
      this.playSound("mob.cow.step", 0.15F, 1.0F);
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
   protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
      int var3 = this.random.nextInt(3) + this.random.nextInt(1 + lootingMultiplier);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.dropItem(Items.LEATHER, 1);
      }

      var3 = this.random.nextInt(3) + 1 + this.random.nextInt(1 + lootingMultiplier);

      for(int var6 = 0; var6 < var3; ++var6) {
         if (this.isOnFire()) {
            this.dropItem(Items.COOKED_BEEF, 1);
         } else {
            this.dropItem(Items.BEEF, 1);
         }
      }
   }

   @Override
   public boolean canInteract(PlayerEntity player) {
      ItemStack var2 = player.inventory.getMainHandStack();
      if (var2 != null && var2.getItem() == Items.BUCKET && !player.abilities.creativeMode) {
         if (var2.size-- == 1) {
            player.inventory.setStack(player.inventory.selectedSlot, new ItemStack(Items.MILK_BUCKET));
         } else if (!player.inventory.insertStack(new ItemStack(Items.MILK_BUCKET))) {
            player.dropItem(new ItemStack(Items.MILK_BUCKET, 1, 0), false);
         }

         return true;
      } else {
         return super.canInteract(player);
      }
   }

   public CowEntity makeChild(PassiveEntity c_19nmglwmx) {
      return new CowEntity(this.world);
   }

   @Override
   public float getEyeHeight() {
      return this.height;
   }
}
