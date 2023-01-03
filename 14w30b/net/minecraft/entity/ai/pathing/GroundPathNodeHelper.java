package net.minecraft.entity.ai.pathing;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;

public class GroundPathNodeHelper extends AbstractPathNodeHelper {
   private boolean f_02xicymdp;
   private boolean f_82bqbdgpi;
   private boolean f_89qdftjie;
   private boolean canSwim;
   private boolean f_31qanrqqf;

   @Override
   public void m_41nzanimf(IWorld c_05ktcjdzx, Entity c_47ldwddrb) {
      super.m_41nzanimf(c_05ktcjdzx, c_47ldwddrb);
      this.f_31qanrqqf = this.f_89qdftjie;
   }

   @Override
   public void m_46ezxzbdo() {
      super.m_46ezxzbdo();
      this.f_89qdftjie = this.f_31qanrqqf;
   }

   @Override
   public PathNode m_17vajbguf(Entity c_47ldwddrb) {
      int var2;
      if (this.canSwim && c_47ldwddrb.isInWater()) {
         var2 = (int)c_47ldwddrb.getBoundingBox().minY;
         Block var3 = this.world.getBlockState(new BlockPos(MathHelper.floor(c_47ldwddrb.x), var2, MathHelper.floor(c_47ldwddrb.z))).getBlock();

         while(var3 == Blocks.FLOWING_WATER || var3 == Blocks.WATER) {
            var3 = this.world.getBlockState(new BlockPos(MathHelper.floor(c_47ldwddrb.x), ++var2, MathHelper.floor(c_47ldwddrb.z))).getBlock();
         }

         this.f_89qdftjie = false;
      } else {
         var2 = MathHelper.floor(c_47ldwddrb.getBoundingBox().minY + 0.5);
      }

      return this.m_28lslnoqn(MathHelper.floor(c_47ldwddrb.getBoundingBox().minX), var2, MathHelper.floor(c_47ldwddrb.getBoundingBox().minZ));
   }

   @Override
   public PathNode m_97krmhugx(Entity c_47ldwddrb, double d, double e, double f) {
      return this.m_28lslnoqn(
         MathHelper.floor(d - (double)(c_47ldwddrb.width / 2.0F)), MathHelper.floor(e), MathHelper.floor(f - (double)(c_47ldwddrb.width / 2.0F))
      );
   }

   @Override
   public int m_27ozwikog(PathNode[] c_27ysujmcns, Entity c_47ldwddrb, PathNode c_27ysujmcn, PathNode c_27ysujmcn2, float f) {
      int var6 = 0;
      byte var7 = 0;
      if (this.m_18xkpworm(c_47ldwddrb, c_27ysujmcn.posX, c_27ysujmcn.posY + 1, c_27ysujmcn.posZ) == 1) {
         var7 = 1;
      }

      PathNode var8 = this.m_73yzzkhgh(c_47ldwddrb, c_27ysujmcn.posX, c_27ysujmcn.posY, c_27ysujmcn.posZ + 1, var7);
      PathNode var9 = this.m_73yzzkhgh(c_47ldwddrb, c_27ysujmcn.posX - 1, c_27ysujmcn.posY, c_27ysujmcn.posZ, var7);
      PathNode var10 = this.m_73yzzkhgh(c_47ldwddrb, c_27ysujmcn.posX + 1, c_27ysujmcn.posY, c_27ysujmcn.posZ, var7);
      PathNode var11 = this.m_73yzzkhgh(c_47ldwddrb, c_27ysujmcn.posX, c_27ysujmcn.posY, c_27ysujmcn.posZ - 1, var7);
      if (var8 != null && !var8.visited && var8.getDistanceTo(c_27ysujmcn2) < f) {
         c_27ysujmcns[var6++] = var8;
      }

      if (var9 != null && !var9.visited && var9.getDistanceTo(c_27ysujmcn2) < f) {
         c_27ysujmcns[var6++] = var9;
      }

      if (var10 != null && !var10.visited && var10.getDistanceTo(c_27ysujmcn2) < f) {
         c_27ysujmcns[var6++] = var10;
      }

      if (var11 != null && !var11.visited && var11.getDistanceTo(c_27ysujmcn2) < f) {
         c_27ysujmcns[var6++] = var11;
      }

      return var6;
   }

   private PathNode m_73yzzkhgh(Entity c_47ldwddrb, int i, int j, int k, int l) {
      PathNode var6 = null;
      int var7 = this.m_18xkpworm(c_47ldwddrb, i, j, k);
      if (var7 == 2) {
         return this.m_28lslnoqn(i, j, k);
      } else {
         if (var7 == 1) {
            var6 = this.m_28lslnoqn(i, j, k);
         }

         if (var6 == null && l > 0 && var7 != -3 && var7 != -4 && this.m_18xkpworm(c_47ldwddrb, i, j + l, k) == 1) {
            var6 = this.m_28lslnoqn(i, j + l, k);
            j += l;
         }

         if (var6 != null) {
            int var8 = 0;

            int var9;
            for(var9 = 0; j > 0; var6 = this.m_28lslnoqn(i, j, k)) {
               var9 = this.m_18xkpworm(c_47ldwddrb, i, j - 1, k);
               if (this.f_89qdftjie && var9 == -1) {
                  return null;
               }

               if (var9 != 1) {
                  break;
               }

               if (var8++ >= c_47ldwddrb.getSafeFallDistance()) {
                  return null;
               }

               if (--j <= 0) {
                  return null;
               }
            }

            if (var9 == -2) {
               return null;
            }
         }

         return var6;
      }
   }

   private int m_18xkpworm(Entity c_47ldwddrb, int i, int j, int k) {
      return m_96ogompic(
         this.world, c_47ldwddrb, i, j, k, this.f_87dzilfcv, this.f_66qiwusrg, this.f_63mfmhfjz, this.f_89qdftjie, this.f_82bqbdgpi, this.f_02xicymdp
      );
   }

   public static int m_96ogompic(IWorld c_05ktcjdzx, Entity c_47ldwddrb, int i, int j, int k, int l, int m, int n, boolean bl, boolean bl2, boolean bl3) {
      boolean var11 = false;
      BlockPos var12 = new BlockPos(c_47ldwddrb);

      for(int var13 = i; var13 < i + l; ++var13) {
         for(int var14 = j; var14 < j + m; ++var14) {
            for(int var15 = k; var15 < k + n; ++var15) {
               BlockPos var16 = new BlockPos(var13, var14, var15);
               Block var17 = c_05ktcjdzx.getBlockState(var16).getBlock();
               if (var17.getMaterial() != Material.AIR) {
                  if (var17 == Blocks.TRAPDOOR || var17 == Blocks.IRON_TRAPDOOR) {
                     var11 = true;
                  } else if (var17 != Blocks.FLOWING_WATER && var17 != Blocks.WATER) {
                     if (!bl3 && var17 == Blocks.WOODEN_DOOR) {
                        return 0;
                     }
                  } else {
                     if (bl) {
                        return -1;
                     }

                     var11 = true;
                  }

                  if (c_47ldwddrb.world.getBlockState(var16).getBlock() instanceof AbstractRailBlock) {
                     if (!(c_47ldwddrb.world.getBlockState(var12).getBlock() instanceof AbstractRailBlock)
                        && !(c_47ldwddrb.world.getBlockState(var12.down()).getBlock() instanceof AbstractRailBlock)) {
                        return -3;
                     }
                  } else if (!var17.canWalkThrough(c_05ktcjdzx, var16) && (!bl2 || var17 != Blocks.WOODEN_DOOR)) {
                     if (var17 instanceof FenceBlock || var17 == Blocks.FENCE_GATE || var17 instanceof WallBlock) {
                        return -3;
                     }

                     if (var17 == Blocks.TRAPDOOR || var17 == Blocks.IRON_TRAPDOOR) {
                        return -4;
                     }

                     Material var18 = var17.getMaterial();
                     if (var18 != Material.LAVA) {
                        return 0;
                     }

                     if (!c_47ldwddrb.isInLava()) {
                        return -2;
                     }
                  }
               }
            }
         }
      }

      return var11 ? 2 : 1;
   }

   public void m_50gulttgl(boolean bl) {
      this.f_02xicymdp = bl;
   }

   public void m_59dqsodjb(boolean bl) {
      this.f_82bqbdgpi = bl;
   }

   public void m_13ledylzx(boolean bl) {
      this.f_89qdftjie = bl;
   }

   public void setCanSwim(boolean canSwim) {
      this.canSwim = canSwim;
   }

   public boolean canInteractWithDoors() {
      return this.f_02xicymdp;
   }

   public boolean canSwim() {
      return this.canSwim;
   }

   public boolean m_24rstbtgs() {
      return this.f_89qdftjie;
   }
}
