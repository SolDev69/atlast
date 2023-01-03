package net.minecraft.block.piston;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBaseBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PistonMoveStructureResolver {
   private final World world;
   private final BlockPos pistonPos;
   private final BlockPos startPos;
   private final Direction moveDir;
   private final List toMove = Lists.newArrayList();
   private final List toBreak = Lists.newArrayList();

   public PistonMoveStructureResolver(World world, BlockPos pistonPos, Direction pistonDir, boolean extend) {
      this.world = world;
      this.pistonPos = pistonPos;
      if (extend) {
         this.moveDir = pistonDir;
         this.startPos = pistonPos.offset(pistonDir);
      } else {
         this.moveDir = pistonDir.getOpposite();
         this.startPos = pistonPos.offset(pistonDir, 2);
      }
   }

   public boolean resolve() {
      this.toMove.clear();
      this.toBreak.clear();
      Block var1 = this.world.getBlockState(this.startPos).getBlock();
      if (!PistonBaseBlock.canMoveBlock(var1, this.world, this.startPos, false)) {
         if (var1.getPistonMoveBehavior() != 1) {
            return false;
         } else {
            this.toBreak.add(this.startPos);
            return true;
         }
      } else if (!this.addColumn(this.startPos)) {
         return false;
      } else {
         for(int var2 = 0; var2 < this.toMove.size(); ++var2) {
            BlockPos var3 = (BlockPos)this.toMove.get(var2);
            if (this.world.getBlockState(var3).getBlock() == Blocks.SLIME && !this.addNeighborColumns(var3)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean addColumn(BlockPos pos) {
      Block var2 = this.world.getBlockState(pos).getBlock();
      if (var2.getMaterial() == Material.AIR) {
         return true;
      } else if (!PistonBaseBlock.canMoveBlock(var2, this.world, pos, false)) {
         return true;
      } else if (pos.equals(this.pistonPos)) {
         return true;
      } else if (this.toMove.contains(pos)) {
         return true;
      } else {
         int var3 = 1;
         if (var3 + this.toMove.size() > 12) {
            return false;
         } else {
            while(var2 == Blocks.SLIME) {
               BlockPos var4 = pos.offset(this.moveDir.getOpposite(), var3);
               var2 = this.world.getBlockState(var4).getBlock();
               if (var2.getMaterial() == Material.AIR || !PistonBaseBlock.canMoveBlock(var2, this.world, var4, false) || var4.equals(this.pistonPos)) {
                  break;
               }

               if (++var3 + this.toMove.size() > 12) {
                  return false;
               }
            }

            int var11 = 0;

            for(int var5 = var3 - 1; var5 >= 0; --var5) {
               this.toMove.add(pos.offset(this.moveDir.getOpposite(), var5));
               ++var11;
            }

            int var12 = 1;

            while(true) {
               BlockPos var6 = pos.offset(this.moveDir, var12);
               int var7 = this.toMove.indexOf(var6);
               if (var7 > -1) {
                  this.insertColumn(var11, var7);

                  for(int var8 = 0; var8 <= var7 + var11; ++var8) {
                     BlockPos var9 = (BlockPos)this.toMove.get(var8);
                     if (this.world.getBlockState(var9).getBlock() == Blocks.SLIME && !this.addNeighborColumns(var9)) {
                        return false;
                     }
                  }

                  return true;
               }

               var2 = this.world.getBlockState(var6).getBlock();
               if (var2.getMaterial() == Material.AIR) {
                  return true;
               }

               if (!PistonBaseBlock.canMoveBlock(var2, this.world, var6, true) || var6.equals(this.pistonPos)) {
                  return false;
               }

               if (var2.getPistonMoveBehavior() == 1) {
                  this.toBreak.add(var6);
                  return true;
               }

               if (this.toMove.size() >= 12) {
                  return false;
               }

               this.toMove.add(var6);
               ++var11;
               ++var12;
            }
         }
      }
   }

   private void insertColumn(int length, int index) {
      ArrayList var3 = Lists.newArrayList();
      ArrayList var4 = Lists.newArrayList();
      ArrayList var5 = Lists.newArrayList();
      var3.addAll(this.toMove.subList(0, index));
      var4.addAll(this.toMove.subList(this.toMove.size() - length, this.toMove.size()));
      var5.addAll(this.toMove.subList(index, this.toMove.size() - length));
      this.toMove.clear();
      this.toMove.addAll(var3);
      this.toMove.addAll(var4);
      this.toMove.addAll(var5);
   }

   private boolean addNeighborColumns(BlockPos pos) {
      for(Direction var5 : Direction.values()) {
         if (var5.getAxis() != this.moveDir.getAxis() && !this.addColumn(pos.offset(var5))) {
            return false;
         }
      }

      return true;
   }

   public List getToMove() {
      return this.toMove;
   }

   public List getToBreak() {
      return this.toBreak;
   }
}
