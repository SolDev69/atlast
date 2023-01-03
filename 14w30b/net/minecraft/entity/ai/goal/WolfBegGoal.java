package net.minecraft.entity.ai.goal;

import net.minecraft.entity.living.mob.passive.animal.tamable.WolfEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class WolfBegGoal extends Goal {
   private WolfEntity wolfEntity;
   private PlayerEntity playerEntity;
   private World world;
   private float begDistance;
   private int timer;

   public WolfBegGoal(WolfEntity c_68kzbahax, float f) {
      this.wolfEntity = c_68kzbahax;
      this.world = c_68kzbahax.world;
      this.begDistance = f;
      this.setControls(2);
   }

   @Override
   public boolean canStart() {
      this.playerEntity = this.world.getClosestPlayer(this.wolfEntity, (double)this.begDistance);
      return this.playerEntity == null ? false : this.isAttractive(this.playerEntity);
   }

   @Override
   public boolean shouldContinue() {
      if (!this.playerEntity.isAlive()) {
         return false;
      } else if (this.wolfEntity.getSquaredDistanceTo(this.playerEntity) > (double)(this.begDistance * this.begDistance)) {
         return false;
      } else {
         return this.timer > 0 && this.isAttractive(this.playerEntity);
      }
   }

   @Override
   public void start() {
      this.wolfEntity.setBegging(true);
      this.timer = 40 + this.wolfEntity.getRandom().nextInt(40);
   }

   @Override
   public void stop() {
      this.wolfEntity.setBegging(false);
      this.playerEntity = null;
   }

   @Override
   public void tick() {
      this.wolfEntity
         .getLookControl()
         .lookAt(
            this.playerEntity.x,
            this.playerEntity.y + (double)this.playerEntity.getEyeHeight(),
            this.playerEntity.z,
            10.0F,
            (float)this.wolfEntity.getLookPitchSpeed()
         );
      --this.timer;
   }

   private boolean isAttractive(PlayerEntity player) {
      ItemStack var2 = player.inventory.getMainHandStack();
      if (var2 == null) {
         return false;
      } else {
         return !this.wolfEntity.isTamed() && var2.getItem() == Items.BONE ? true : this.wolfEntity.isBreedingItem(var2);
      }
   }
}
