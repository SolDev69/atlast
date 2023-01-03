package net.minecraft.item;

import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FireChargeItem extends Item {
   public FireChargeItem() {
      this.setItemGroup(ItemGroup.MISC);
   }

   @Override
   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      if (world.isClient) {
         return true;
      } else {
         pos = pos.offset(face);
         if (!player.canUseItem(pos, face, stack)) {
            return false;
         } else {
            if (world.getBlockState(pos).getBlock().getMaterial() == Material.AIR) {
               world.playSound(
                  (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "fire.ignite", 1.0F, random.nextFloat() * 0.4F + 0.8F
               );
               world.setBlockState(pos, Blocks.FIRE.defaultState());
            }

            if (!player.abilities.creativeMode) {
               --stack.size;
            }

            return true;
         }
      }
   }
}
