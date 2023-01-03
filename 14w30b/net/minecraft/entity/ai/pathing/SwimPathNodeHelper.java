package net.minecraft.entity.ai.pathing;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;

public class SwimPathNodeHelper extends AbstractPathNodeHelper {
   @Override
   public void m_41nzanimf(IWorld c_05ktcjdzx, Entity c_47ldwddrb) {
      super.m_41nzanimf(c_05ktcjdzx, c_47ldwddrb);
   }

   @Override
   public void m_46ezxzbdo() {
      super.m_46ezxzbdo();
   }

   @Override
   public PathNode m_17vajbguf(Entity c_47ldwddrb) {
      return this.m_28lslnoqn(
         MathHelper.floor(c_47ldwddrb.getBoundingBox().minX),
         MathHelper.floor(c_47ldwddrb.getBoundingBox().minY + 0.5),
         MathHelper.floor(c_47ldwddrb.getBoundingBox().minZ)
      );
   }

   @Override
   public PathNode m_97krmhugx(Entity c_47ldwddrb, double d, double e, double f) {
      return this.m_28lslnoqn(
         MathHelper.floor(d - (double)(c_47ldwddrb.width / 2.0F)), MathHelper.floor(e + 0.5), MathHelper.floor(f - (double)(c_47ldwddrb.width / 2.0F))
      );
   }

   @Override
   public int m_27ozwikog(PathNode[] c_27ysujmcns, Entity c_47ldwddrb, PathNode c_27ysujmcn, PathNode c_27ysujmcn2, float f) {
      int var6 = 0;

      for(Direction var10 : Direction.values()) {
         PathNode var11 = this.m_66iuoucnm(
            c_47ldwddrb, c_27ysujmcn.posX + var10.getOffsetX(), c_27ysujmcn.posY + var10.getOffsetY(), c_27ysujmcn.posZ + var10.getOffsetZ()
         );
         if (var11 != null && !var11.visited && var11.getDistanceTo(c_27ysujmcn2) < f) {
            c_27ysujmcns[var6++] = var11;
         }
      }

      return var6;
   }

   private PathNode m_66iuoucnm(Entity c_47ldwddrb, int i, int j, int k) {
      int var5 = this.m_22vxkmevd(c_47ldwddrb, i, j, k);
      return var5 == -1 ? this.m_28lslnoqn(i, j, k) : null;
   }

   private int m_22vxkmevd(Entity c_47ldwddrb, int i, int j, int k) {
      for(int var5 = i; var5 < i + this.f_87dzilfcv; ++var5) {
         for(int var6 = j; var6 < j + this.f_66qiwusrg; ++var6) {
            for(int var7 = k; var7 < k + this.f_63mfmhfjz; ++var7) {
               BlockPos var8 = new BlockPos(var5, var6, var7);
               Block var9 = this.world.getBlockState(var8).getBlock();
               if (var9.getMaterial() != Material.WATER) {
                  return 0;
               }
            }
         }
      }

      return -1;
   }
}
