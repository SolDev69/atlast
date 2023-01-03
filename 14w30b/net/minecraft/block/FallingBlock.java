package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FallingBlock extends Block {
   public static boolean fallImmediately;

   public FallingBlock() {
      super(Material.SAND);
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   public FallingBlock(Material c_57ywipuwq) {
      super(c_57ywipuwq);
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      world.scheduleTick(pos, this, this.getTickRate(world));
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      world.scheduleTick(pos, this, this.getTickRate(world));
   }

   @Override
   public void tick(World world, BlockPos pos, BlockState state, Random random) {
      if (!world.isClient) {
         this.tryFall(world, pos);
      }
   }

   private void tryFall(World world, BlockPos pos) {
      if (canFallThrough(world, pos.down()) && pos.getY() >= 0) {
         byte var3 = 32;
         if (fallImmediately || !world.isRegionLoaded(pos.add(-var3, -var3, -var3), pos.add(var3, var3, var3))) {
            world.removeBlock(pos);
            BlockPos var5 = pos.down();

            while(canFallThrough(world, var5) && var5.getY() > 0) {
               var5 = var5.down();
            }

            if (var5.getY() > 0) {
               world.setBlockState(var5.up(), this.defaultState());
            }
         } else if (!world.isClient) {
            FallingBlockEntity var4 = new FallingBlockEntity(
               world, (double)((float)pos.getX() + 0.5F), (double)pos.getY(), (double)((float)pos.getZ() + 0.5F), world.getBlockState(pos)
            );
            this.beforeStartFalling(var4);
            world.addEntity(var4);
         }
      }
   }

   protected void beforeStartFalling(FallingBlockEntity fallingBlockEntity) {
   }

   @Override
   public int getTickRate(World world) {
      return 2;
   }

   public static boolean canFallThrough(World world, BlockPos pos) {
      Block var2 = world.getBlockState(pos).getBlock();
      Material var3 = var2.material;
      return var2 == Blocks.FIRE || var3 == Material.AIR || var3 == Material.WATER || var3 == Material.LAVA;
   }

   public void onTickFallingBlockEntity(World world, BlockPos pos) {
   }
}
