package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.AbstractLogBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class LargeOakTreeFeature extends AbstractTreeFeature {
   private Random random;
   private World world;
   private BlockPos origin = BlockPos.ORIGIN;
   int height;
   int trunkHeight;
   double trunkScale = 0.618;
   double branchSlope = 0.381;
   double branchLengthScale = 1.0;
   double branchDensity = 1.0;
   int trunkWidth = 1;
   int maxTrunkHeight = 12;
   int foliageClusterHeight = 4;
   List branches;

   public LargeOakTreeFeature(boolean bl) {
      super(bl);
   }

   void makeBranches() {
      this.trunkHeight = (int)((double)this.height * this.trunkScale);
      if (this.trunkHeight >= this.height) {
         this.trunkHeight = this.height - 1;
      }

      int var1 = (int)(1.382 + Math.pow(this.branchDensity * (double)this.height / 13.0, 2.0));
      if (var1 < 1) {
         var1 = 1;
      }

      int var2 = this.origin.getY() + this.trunkHeight;
      int var3 = this.height - this.foliageClusterHeight;
      this.branches = Lists.newArrayList();
      this.branches.add(new LargeOakTreeFeature.BranchPos(this.origin.up(var3), var2));

      for(; var3 >= 0; --var3) {
         float var4 = this.getTreeShape(var3);
         if (!(var4 < 0.0F)) {
            for(int var5 = 0; var5 < var1; ++var5) {
               double var6 = this.branchLengthScale * (double)var4 * ((double)this.random.nextFloat() + 0.328);
               double var8 = (double)(this.random.nextFloat() * 2.0F) * Math.PI;
               double var10 = var6 * Math.sin(var8) + 0.5;
               double var12 = var6 * Math.cos(var8) + 0.5;
               BlockPos var14 = this.origin.add(var10, (double)(var3 - 1), var12);
               BlockPos var15 = var14.up(this.foliageClusterHeight);
               if (this.tryBranch(var14, var15) == -1) {
                  int var16 = this.origin.getX() - var14.getX();
                  int var17 = this.origin.getZ() - var14.getZ();
                  double var18 = (double)var14.getY() - Math.sqrt((double)(var16 * var16 + var17 * var17)) * this.branchSlope;
                  int var20 = var18 > (double)var2 ? var2 : (int)var18;
                  BlockPos var21 = new BlockPos(this.origin.getX(), var20, this.origin.getZ());
                  if (this.tryBranch(var21, var14) == -1) {
                     this.branches.add(new LargeOakTreeFeature.BranchPos(var14, var21.getY()));
                  }
               }
            }
         }
      }
   }

   void placeCluster(BlockPos pos, float shape, Block clusterBlock) {
      int var4 = (int)((double)shape + 0.618);

      for(int var5 = -var4; var5 <= var4; ++var5) {
         for(int var6 = -var4; var6 <= var4; ++var6) {
            if (Math.pow((double)Math.abs(var5) + 0.5, 2.0) + Math.pow((double)Math.abs(var6) + 0.5, 2.0) <= (double)(shape * shape)) {
               BlockPos var7 = pos.add(var5, 0, var6);
               Material var8 = this.world.getBlockState(var7).getBlock().getMaterial();
               if (var8 == Material.AIR || var8 == Material.LEAVES) {
                  this.setBlockWithMetadata(this.world, var7, clusterBlock, 0);
               }
            }
         }
      }
   }

   float getTreeShape(int height) {
      if ((float)height < (float)this.height * 0.3F) {
         return -1.0F;
      } else {
         float var2 = (float)this.height / 2.0F;
         float var3 = var2 - (float)height;
         float var4 = MathHelper.sqrt(var2 * var2 - var3 * var3);
         if (var3 == 0.0F) {
            var4 = var2;
         } else if (Math.abs(var3) >= var2) {
            return 0.0F;
         }

         return var4 * 0.5F;
      }
   }

   float getClusterShape(int layer) {
      if (layer < 0 || layer >= this.foliageClusterHeight) {
         return -1.0F;
      } else {
         return layer != 0 && layer != this.foliageClusterHeight - 1 ? 3.0F : 2.0F;
      }
   }

   void placeFoliageCluster(BlockPos pos) {
      for(int var2 = 0; var2 < this.foliageClusterHeight; ++var2) {
         this.placeCluster(pos.up(var2), this.getClusterShape(var2), Blocks.LEAVES);
      }
   }

   void placeBranch(BlockPos from, BlockPos to, Block log) {
      BlockPos var4 = to.add(-from.getX(), -from.getY(), -from.getZ());
      int var5 = this.max(var4);
      float var6 = (float)var4.getX() / (float)var5;
      float var7 = (float)var4.getY() / (float)var5;
      float var8 = (float)var4.getZ() / (float)var5;

      for(int var9 = 0; var9 <= var5; ++var9) {
         BlockPos var10 = from.add((double)(0.5F + (float)var9 * var6), (double)(0.5F + (float)var9 * var7), (double)(0.5F + (float)var9 * var8));
         AbstractLogBlock.LogAxis var11 = this.getLogAxis(from, var10);
         this.setBlockState(this.world, var10, log.defaultState().set(AbstractLogBlock.LOG_AXIS, var11));
      }
   }

   private int max(BlockPos coordinates) {
      int var2 = MathHelper.abs(coordinates.getX());
      int var3 = MathHelper.abs(coordinates.getY());
      int var4 = MathHelper.abs(coordinates.getZ());
      if (var4 > var2 && var4 > var3) {
         return var4;
      } else {
         return var3 > var2 ? var3 : var2;
      }
   }

   private AbstractLogBlock.LogAxis getLogAxis(BlockPos from, BlockPos to) {
      AbstractLogBlock.LogAxis var3 = AbstractLogBlock.LogAxis.Y;
      int var4 = Math.abs(to.getX() - from.getX());
      int var5 = Math.abs(to.getZ() - from.getZ());
      int var6 = Math.max(var4, var5);
      if (var6 > 0) {
         if (var4 == var6) {
            var3 = AbstractLogBlock.LogAxis.X;
         } else if (var5 == var6) {
            var3 = AbstractLogBlock.LogAxis.Z;
         }
      }

      return var3;
   }

   void placeFoliage() {
      for(LargeOakTreeFeature.BranchPos var2 : this.branches) {
         this.placeFoliageCluster(var2);
      }
   }

   boolean shouldPlaceBranch(int height) {
      return (double)height >= (double)this.height * 0.2;
   }

   void placeTrunk() {
      BlockPos var1 = this.origin;
      BlockPos var2 = this.origin.up(this.trunkHeight);
      Block var3 = Blocks.LOG;
      this.placeBranch(var1, var2, var3);
      if (this.trunkWidth == 2) {
         this.placeBranch(var1.east(), var2.east(), var3);
         this.placeBranch(var1.east().south(), var2.east().south(), var3);
         this.placeBranch(var1.south(), var2.south(), var3);
      }
   }

   void placeBranches() {
      for(LargeOakTreeFeature.BranchPos var2 : this.branches) {
         int var3 = var2.getBaseY();
         BlockPos var4 = new BlockPos(this.origin.getX(), var3, this.origin.getZ());
         if (this.shouldPlaceBranch(var3 - this.origin.getY())) {
            this.placeBranch(var4, var2, Blocks.LOG);
         }
      }
   }

   int tryBranch(BlockPos from, BlockPos to) {
      BlockPos var3 = to.add(-from.getX(), -from.getY(), -from.getZ());
      int var4 = this.max(var3);
      float var5 = (float)var3.getX() / (float)var4;
      float var6 = (float)var3.getY() / (float)var4;
      float var7 = (float)var3.getZ() / (float)var4;
      if (var4 == 0) {
         return -1;
      } else {
         for(int var8 = 0; var8 <= var4; ++var8) {
            BlockPos var9 = from.add((double)(0.5F + (float)var8 * var5), (double)(0.5F + (float)var8 * var6), (double)(0.5F + (float)var8 * var7));
            if (!this.canReplace(this.world.getBlockState(var9).getBlock())) {
               return var8;
            }
         }

         return -1;
      }
   }

   @Override
   public void prepare() {
      this.foliageClusterHeight = 5;
   }

   @Override
   public boolean place(World world, Random random, BlockPos pos) {
      this.world = world;
      this.origin = pos;
      this.random = new Random(random.nextLong());
      if (this.height == 0) {
         this.height = 5 + this.random.nextInt(this.maxTrunkHeight);
      }

      if (!this.canPlace()) {
         return false;
      } else {
         this.makeBranches();
         this.placeFoliage();
         this.placeTrunk();
         this.placeBranches();
         return true;
      }
   }

   private boolean canPlace() {
      Block var1 = this.world.getBlockState(this.origin.down()).getBlock();
      if (var1 != Blocks.DIRT && var1 != Blocks.GRASS && var1 != Blocks.FARMLAND) {
         return false;
      } else {
         int var2 = this.tryBranch(this.origin, this.origin.up(this.height - 1));
         if (var2 == -1) {
            return true;
         } else if (var2 < 6) {
            return false;
         } else {
            this.height = var2;
            return true;
         }
      }
   }

   static class BranchPos extends BlockPos {
      private final int baseY;

      public BranchPos(BlockPos tip, int baseY) {
         super(tip.getX(), tip.getY(), tip.getZ());
         this.baseY = baseY;
      }

      public int getBaseY() {
         return this.baseY;
      }
   }
}
