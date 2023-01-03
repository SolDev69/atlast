package net.minecraft.item;

import net.minecraft.block.Blocks;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RedstoneItem extends Item {
   public RedstoneItem() {
      this.setItemGroup(ItemGroup.REDSTONE);
   }

   @Override
   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      if (world.getBlockState(pos).getBlock() != Blocks.SNOW_LAYER) {
         pos = pos.offset(face);
         if (!world.isAir(pos)) {
            return false;
         }
      }

      if (!player.canUseItem(pos, face, stack)) {
         return false;
      } else if (Blocks.REDSTONE_WIRE.canSurvive(world, pos)) {
         --stack.size;
         world.setBlockState(pos, Blocks.REDSTONE_WIRE.defaultState());
         return true;
      } else {
         return false;
      }
   }
}
