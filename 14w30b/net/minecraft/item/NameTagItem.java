package net.minecraft.item;

import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;

public class NameTagItem extends Item {
   public NameTagItem() {
      this.setItemGroup(ItemGroup.TOOLS);
   }

   @Override
   public boolean canInteract(ItemStack stack, PlayerEntity player, LivingEntity entity) {
      if (!stack.hasCustomHoverName()) {
         return false;
      } else if (entity instanceof MobEntity) {
         MobEntity var4 = (MobEntity)entity;
         var4.setCustomName(stack.getHoverName());
         var4.setPersistent();
         --stack.size;
         return true;
      } else {
         return super.canInteract(stack, player, entity);
      }
   }
}
