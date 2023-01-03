package net.minecraft.item;

import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.passive.animal.PigEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;

public class SaddleItem extends Item {
   public SaddleItem() {
      this.maxStackSize = 1;
      this.setItemGroup(ItemGroup.TRANSPORTATION);
   }

   @Override
   public boolean canInteract(ItemStack stack, PlayerEntity player, LivingEntity entity) {
      if (entity instanceof PigEntity) {
         PigEntity var4 = (PigEntity)entity;
         if (!var4.isSaddled() && !var4.isBaby()) {
            var4.setSaddled(true);
            var4.world.playSound(var4, "mob.horse.leather", 0.5F, 1.0F);
            --stack.size;
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean attackEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      this.canInteract(stack, null, target);
      return true;
   }
}
