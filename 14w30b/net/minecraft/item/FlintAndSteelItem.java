package net.minecraft.item;

import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FlintAndSteelItem extends Item {
   public FlintAndSteelItem() {
      this.maxStackSize = 1;
      this.setMaxDamage(64);
      this.setItemGroup(ItemGroup.TOOLS);
   }

   @Override
   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      pos = pos.offset(face);
      if (!player.canUseItem(pos, face, stack)) {
         return false;
      } else {
         if (world.getBlockState(pos).getBlock().getMaterial() == Material.AIR) {
            world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "fire.ignite", 1.0F, random.nextFloat() * 0.4F + 0.8F);
            world.setBlockState(pos, Blocks.FIRE.defaultState());
         }

         stack.damageAndBreak(1, player);
         return true;
      }
   }
}
