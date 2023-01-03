package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowLayerBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PlaceableItem extends Item {
   private Block block;

   public PlaceableItem(Block block) {
      this.block = block;
   }

   @Override
   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      BlockState var9 = world.getBlockState(pos);
      Block var10 = var9.getBlock();
      if (var10 == Blocks.SNOW_LAYER && var9.get(SnowLayerBlock.LAYERS) < 1) {
         face = Direction.UP;
      } else if (!var10.canBeReplaced(world, pos)) {
         pos = pos.offset(face);
      }

      if (!player.canUseItem(pos, face, stack)) {
         return false;
      } else if (stack.size == 0) {
         return false;
      } else {
         if (world.canReplace(this.block, pos, false, face, null, stack)) {
            BlockState var11 = this.block.getPlacementState(world, pos, face, dx, dy, dz, 0, player);
            if (world.setBlockState(pos, var11, 3)) {
               var11 = world.getBlockState(pos);
               if (var11.getBlock() == this.block) {
                  BlockItem.setBlockNbt(world, pos, stack);
                  var11.getBlock().onPlaced(world, pos, var11, player, stack);
               }

               world.playSound(
                  (double)((float)pos.getX() + 0.5F),
                  (double)((float)pos.getY() + 0.5F),
                  (double)((float)pos.getZ() + 0.5F),
                  this.block.sound.getSound(),
                  (this.block.sound.getVolume() + 1.0F) / 2.0F,
                  this.block.sound.getPitch() * 0.8F
               );
               --stack.size;
               return true;
            }
         }

         return false;
      }
   }
}
