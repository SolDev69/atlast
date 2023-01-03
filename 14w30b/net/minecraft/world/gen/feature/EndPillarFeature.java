package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EnderCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EndPillarFeature extends Feature {
   private Block block;

   public EndPillarFeature(Block block) {
      this.block = block;
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      if (world.isAir(pos) && world.getBlockState(pos.down()).getBlock() == this.block) {
         int var4 = random.nextInt(32) + 6;
         int var5 = random.nextInt(4) + 1;

         for(int var6 = pos.getX() - var5; var6 <= pos.getX() + var5; ++var6) {
            for(int var7 = pos.getZ() - var5; var7 <= pos.getZ() + var5; ++var7) {
               int var8 = var6 - pos.getX();
               int var9 = var7 - pos.getZ();
               if (var8 * var8 + var9 * var9 <= var5 * var5 + 1 && world.getBlockState(new BlockPos(var6, pos.getY() - 1, var7)).getBlock() != this.block) {
                  return false;
               }
            }
         }

         for(int var11 = pos.getY(); var11 < pos.getY() + var4 && var11 < 256; ++var11) {
            for(int var13 = pos.getX() - var5; var13 <= pos.getX() + var5; ++var13) {
               for(int var14 = pos.getZ() - var5; var14 <= pos.getZ() + var5; ++var14) {
                  int var15 = var13 - pos.getX();
                  int var10 = var14 - pos.getZ();
                  if (var15 * var15 + var10 * var10 <= var5 * var5 + 1) {
                     world.setBlockState(new BlockPos(var13, var11, var14), Blocks.OBSIDIAN.defaultState(), 2);
                  }
               }
            }
         }

         EnderCrystalEntity var12 = new EnderCrystalEntity(world);
         var12.refreshPositionAndAngles(
            (double)((float)pos.getX() + 0.5F), (double)(pos.getY() + var4), (double)((float)pos.getZ() + 0.5F), random.nextFloat() * 360.0F, 0.0F
         );
         world.addEntity(var12);
         world.setBlockState(pos.up(var4), Blocks.BEDROCK.defaultState(), 2);
         return true;
      } else {
         return false;
      }
   }
}
