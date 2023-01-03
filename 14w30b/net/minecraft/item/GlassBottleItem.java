package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GlassBottleItem extends Item {
   public GlassBottleItem() {
      this.setItemGroup(ItemGroup.BREWING);
   }

   @Override
   public ItemStack startUsing(ItemStack stack, World world, PlayerEntity player) {
      HitResult var4 = this.getUseTarget(world, player, true);
      if (var4 == null) {
         return stack;
      } else {
         if (var4.type == HitResult.Type.BLOCK) {
            BlockPos var5 = var4.getBlockPos();
            if (!world.canModify(player, var5)) {
               return stack;
            }

            if (!player.canUseItem(var5.offset(var4.face), var4.face, stack)) {
               return stack;
            }

            if (world.getBlockState(var5).getBlock().getMaterial() == Material.WATER) {
               --stack.size;
               player.incrementStat(Stats.ITEMS_USED[Item.getRawId(this)]);
               if (stack.size <= 0) {
                  return new ItemStack(Items.POTION);
               }

               if (!player.inventory.insertStack(new ItemStack(Items.POTION))) {
                  player.dropItem(new ItemStack(Items.POTION, 1, 0), false);
               }
            }
         }

         return stack;
      }
   }
}
