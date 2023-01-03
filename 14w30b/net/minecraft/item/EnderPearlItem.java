package net.minecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.thrown.EnderPearlEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

public class EnderPearlItem extends Item {
   public EnderPearlItem() {
      this.maxStackSize = 16;
      this.setItemGroup(ItemGroup.MISC);
   }

   @Override
   public ItemStack startUsing(ItemStack stack, World world, PlayerEntity player) {
      if (player.abilities.creativeMode) {
         return stack;
      } else {
         --stack.size;
         world.playSound((Entity)player, "random.bow", 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
         if (!world.isClient) {
            world.addEntity(new EnderPearlEntity(world, player));
         }

         player.incrementStat(Stats.ITEMS_USED[Item.getRawId(this)]);
         return stack;
      }
   }
}
