package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirtBlock;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class GiantSpruceTreeFeature extends GiantTreeFeature {
   private boolean sparseLeaves;

   public GiantSpruceTreeFeature(boolean notifyNeighbors, boolean sparseLeaves) {
      super(notifyNeighbors, 13, 15, PlanksBlock.Variant.SPRUCE.getIndex(), PlanksBlock.Variant.SPRUCE.getIndex());
      this.sparseLeaves = sparseLeaves;
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      int var4 = this.getRandomHeight(random);
      if (!this.canGrow(world, random, pos, var4)) {
         return false;
      } else {
         this.placeLeaves(world, pos.getX(), pos.getZ(), pos.getY() + var4, 0, random);

         for(int var5 = 0; var5 < var4; ++var5) {
            Block var6 = world.getBlockState(pos.up(var5)).getBlock();
            if (var6.getMaterial() == Material.AIR || var6.getMaterial() == Material.LEAVES) {
               this.setBlockWithMetadata(world, pos.up(var5), Blocks.LOG, this.logVariant);
            }

            if (var5 < var4 - 1) {
               var6 = world.getBlockState(pos.add(1, var5, 0)).getBlock();
               if (var6.getMaterial() == Material.AIR || var6.getMaterial() == Material.LEAVES) {
                  this.setBlockWithMetadata(world, pos.add(1, var5, 0), Blocks.LOG, this.logVariant);
               }

               var6 = world.getBlockState(pos.add(1, var5, 1)).getBlock();
               if (var6.getMaterial() == Material.AIR || var6.getMaterial() == Material.LEAVES) {
                  this.setBlockWithMetadata(world, pos.add(1, var5, 1), Blocks.LOG, this.logVariant);
               }

               var6 = world.getBlockState(pos.add(0, var5, 1)).getBlock();
               if (var6.getMaterial() == Material.AIR || var6.getMaterial() == Material.LEAVES) {
                  this.setBlockWithMetadata(world, pos.add(0, var5, 1), Blocks.LOG, this.logVariant);
               }
            }
         }

         return true;
      }
   }

   private void placeLeaves(World world, int x, int z, int y, int baseRadius, Random random) {
      int var7 = random.nextInt(5) + (this.sparseLeaves ? this.baseHeight : 3);
      int var8 = 0;

      for(int var9 = y - var7; var9 <= y; ++var9) {
         int var10 = y - var9;
         int var11 = baseRadius + MathHelper.floor((float)var10 / (float)var7 * 3.5F);
         this.placeLeavesRingStrict(world, new BlockPos(x, var9, z), var11 + (var10 > 0 && var11 == var8 && (var9 & 1) == 0 ? 1 : 0));
         var8 = var11;
      }
   }

   @Override
   public void placeSoil(World world, Random random, BlockPos pos) {
      this.placePodzolPatch(world, pos.west().north());
      this.placePodzolPatch(world, pos.east(2).north());
      this.placePodzolPatch(world, pos.west().south(2));
      this.placePodzolPatch(world, pos.east(2).south(2));

      for(int var4 = 0; var4 < 5; ++var4) {
         int var5 = random.nextInt(64);
         int var6 = var5 % 8;
         int var7 = var5 / 8;
         if (var6 == 0 || var6 == 7 || var7 == 0 || var7 == 7) {
            this.placePodzolPatch(world, pos.add(-3 + var6, 0, -3 + var7));
         }
      }
   }

   private void placePodzolPatch(World world, BlockPos pos) {
      for(int var3 = -2; var3 <= 2; ++var3) {
         for(int var4 = -2; var4 <= 2; ++var4) {
            if (Math.abs(var3) != 2 || Math.abs(var4) != 2) {
               this.placePodzol(world, pos.add(var3, 0, var4));
            }
         }
      }
   }

   private void placePodzol(World world, BlockPos pos) {
      for(int var3 = 2; var3 >= -3; --var3) {
         BlockPos var4 = pos.up(var3);
         Block var5 = world.getBlockState(var4).getBlock();
         if (var5 == Blocks.GRASS || var5 == Blocks.DIRT) {
            this.setBlockWithMetadata(world, var4, Blocks.DIRT, DirtBlock.Variant.PODZOL.getIndex());
            break;
         }

         if (var5.getMaterial() != Material.AIR && var3 < 0) {
            break;
         }
      }
   }
}
