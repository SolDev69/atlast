package net.minecraft.item;

import net.minecraft.block.Blocks;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SignItem extends Item {
   public SignItem() {
      this.maxStackSize = 16;
      this.setItemGroup(ItemGroup.DECORATIONS);
   }

   @Override
   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      if (face == Direction.DOWN) {
         return false;
      } else if (!world.getBlockState(pos).getBlock().getMaterial().isSolid()) {
         return false;
      } else {
         pos = pos.offset(face);
         if (!player.canUseItem(pos, face, stack)) {
            return false;
         } else if (!Blocks.STANDING_SIGN.canSurvive(world, pos)) {
            return false;
         } else if (world.isClient) {
            return true;
         } else {
            if (face == Direction.UP) {
               int var9 = MathHelper.floor((double)((player.yaw + 180.0F) * 16.0F / 360.0F) + 0.5) & 15;
               world.setBlockState(pos, Blocks.STANDING_SIGN.defaultState().set(StandingSignBlock.ROTATION, var9), 3);
            } else {
               world.setBlockState(pos, Blocks.WALL_SIGN.defaultState().set(WallSignBlock.FACING, face), 3);
            }

            --stack.size;
            BlockEntity var11 = world.getBlockEntity(pos);
            if (var11 instanceof SignBlockEntity && !BlockItem.setBlockNbt(world, pos, stack)) {
               player.openSignEditor((SignBlockEntity)var11);
            }

            return true;
         }
      }
   }
}
