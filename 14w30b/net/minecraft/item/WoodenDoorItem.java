package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WoodenDoorItem extends Item {
   private Material material;

   public WoodenDoorItem(Material material) {
      this.material = material;
      this.setItemGroup(ItemGroup.REDSTONE);
   }

   @Override
   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      if (face != Direction.UP) {
         return false;
      } else {
         BlockState var9 = world.getBlockState(pos);
         Block var10 = var9.getBlock();
         if (!var10.canBeReplaced(world, pos)) {
            pos = pos.offset(face);
         }

         Block var11 = this.material == Material.WOOD ? Blocks.WOODEN_DOOR : Blocks.IRON_DOOR;
         if (!player.canUseItem(pos, face, stack)) {
            return false;
         } else if (!var11.canSurvive(world, pos)) {
            return false;
         } else {
            place(world, pos, Direction.byRotation((double)player.yaw), var11);
            --stack.size;
            return true;
         }
      }
   }

   public static void place(World world, BlockPos x, Direction y, Block z) {
      BlockPos var4 = x.offset(y.clockwiseY());
      BlockPos var5 = x.offset(y.counterClockwiseY());
      int var6 = (world.getBlockState(var5).getBlock().isConductor() ? 1 : 0) + (world.getBlockState(var5.up()).getBlock().isConductor() ? 1 : 0);
      int var7 = (world.getBlockState(var4).getBlock().isConductor() ? 1 : 0) + (world.getBlockState(var4.up()).getBlock().isConductor() ? 1 : 0);
      boolean var8 = world.getBlockState(var5).getBlock() == z || world.getBlockState(var5.up()).getBlock() == z;
      boolean var9 = world.getBlockState(var4).getBlock() == z || world.getBlockState(var4.up()).getBlock() == z;
      boolean var10 = false;
      if (var8 && !var9 || var7 > var6) {
         var10 = true;
      }

      BlockPos var11 = x.up();
      BlockState var12 = z.defaultState().set(DoorBlock.FACING, y).set(DoorBlock.HINGE, var10 ? DoorBlock.Hinge.RIGHT : DoorBlock.Hinge.LEFT);
      world.setBlockState(x, var12.set(DoorBlock.HALF, DoorBlock.Half.LOWER), 2);
      world.setBlockState(var11, var12.set(DoorBlock.HALF, DoorBlock.Half.UPPER), 2);
      world.updateNeighbors(x, z);
      world.updateNeighbors(var11, z);
   }
}
