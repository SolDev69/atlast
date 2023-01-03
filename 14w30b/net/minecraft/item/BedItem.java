package net.minecraft.item;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BedItem extends Item {
   public BedItem() {
      this.setItemGroup(ItemGroup.DECORATIONS);
   }

   @Override
   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      if (world.isClient) {
         return true;
      } else if (face != Direction.UP) {
         return false;
      } else {
         BlockState var9 = world.getBlockState(pos);
         Block var10 = var9.getBlock();
         boolean var11 = var10.canBeReplaced(world, pos);
         if (!var11) {
            pos = pos.up();
         }

         int var12 = MathHelper.floor((double)(player.yaw * 4.0F / 360.0F) + 0.5) & 3;
         Direction var13 = Direction.byIdHorizontal(var12);
         BlockPos var14 = pos.offset(var13);
         boolean var15 = var10.canBeReplaced(world, var14);
         boolean var16 = world.isAir(pos) || var11;
         boolean var17 = world.isAir(var14) || var15;
         if (!player.canUseItem(pos, face, stack) || !player.canUseItem(var14, face, stack)) {
            return false;
         } else if (var16 && var17 && World.hasSolidTop(world, pos.down()) && World.hasSolidTop(world, var14.down())) {
            int var18 = var13.getIdHorizontal();
            BlockState var19 = Blocks.BED.defaultState().set(BedBlock.OCCUPIED, false).set(BedBlock.FACING, var13).set(BedBlock.PART, BedBlock.Part.FOOT);
            if (world.setBlockState(pos, var19, 3)) {
               BlockState var20 = var19.set(BedBlock.PART, BedBlock.Part.HEAD);
               world.setBlockState(var14, var20, 3);
            }

            --stack.size;
            return true;
         } else {
            return false;
         }
      }
   }
}
