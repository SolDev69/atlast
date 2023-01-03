package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.SnowLayerBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SnowLayerItem extends BlockItem {
   public SnowLayerItem(Block c_68zcrzyxg) {
      super(c_68zcrzyxg);
      this.setMaxDamage(0);
      this.setStackable(true);
   }

   @Override
   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      if (stack.size == 0) {
         return false;
      } else if (!player.canUseItem(pos, face, stack)) {
         return false;
      } else {
         BlockState var9 = world.getBlockState(pos);
         Block var10 = var9.getBlock();
         if (var10 != this.block && face != Direction.UP) {
            pos = pos.offset(face);
            var9 = world.getBlockState(pos);
            var10 = var9.getBlock();
         }

         if (var10 == this.block) {
            int var11 = var9.get(SnowLayerBlock.LAYERS);
            if (var11 <= 7) {
               BlockState var12 = var9.set(SnowLayerBlock.LAYERS, var11 + 1);
               if (world.canBuildIn(this.block.getCollisionShape(world, pos, var12)) && world.setBlockState(pos, var12, 2)) {
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
         }

         return super.use(stack, player, world, pos, face, dx, dy, dz);
      }
   }

   @Override
   public int getBlockMetadata(int metadata) {
      return metadata;
   }
}
