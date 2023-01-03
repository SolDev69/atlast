package net.minecraft.entity.ai.pathing;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.hostile.ZombieEntity;
import net.minecraft.entity.living.mob.passive.animal.ChickenEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MobEntityNavigation extends EntityNavigation {
   protected GroundPathNodeHelper pathNodeHelper;
   private boolean shouldAvoidSunLight;

   public MobEntityNavigation(MobEntity c_81psrrogw, World c_54ruxjwzt) {
      super(c_81psrrogw, c_54ruxjwzt);
   }

   @Override
   protected PathHelper createPathHelper() {
      this.pathNodeHelper = new GroundPathNodeHelper();
      this.pathNodeHelper.m_50gulttgl(true);
      return new PathHelper(this.pathNodeHelper);
   }

   @Override
   protected boolean canPathFind() {
      return this.mob.onGround
         || this.canSwim() && this.mobEntityInLiquid()
         || this.mob.hasVehicle() && this.mob instanceof ZombieEntity && this.mob.vehicle instanceof ChickenEntity;
   }

   @Override
   protected Vec3d getEntityPosAboveWater() {
      return new Vec3d(this.mob.x, (double)this.getPathY(), this.mob.z);
   }

   private int getPathY() {
      if (this.mob.isInWater() && this.canSwim()) {
         int var1 = (int)this.mob.getBoundingBox().minY;
         Block var2 = this.world.getBlockState(new BlockPos(MathHelper.floor(this.mob.x), var1, MathHelper.floor(this.mob.z))).getBlock();
         int var3 = 0;

         while(var2 == Blocks.FLOWING_WATER || var2 == Blocks.WATER) {
            var2 = this.world.getBlockState(new BlockPos(MathHelper.floor(this.mob.x), ++var1, MathHelper.floor(this.mob.z))).getBlock();
            if (++var3 > 16) {
               return (int)this.mob.getBoundingBox().minY;
            }
         }

         return var1;
      } else {
         return (int)(this.mob.getBoundingBox().minY + 0.5);
      }
   }

   @Override
   protected void avoidSunLight() {
      super.avoidSunLight();
      if (this.shouldAvoidSunLight) {
         if (this.world.hasSkyAccess(new BlockPos(MathHelper.floor(this.mob.x), (int)(this.mob.getBoundingBox().minY + 0.5), MathHelper.floor(this.mob.z)))) {
            return;
         }

         for(int var1 = 0; var1 < this.currentPath.getPathLength(); ++var1) {
            PathNode var2 = this.currentPath.getPathNode(var1);
            if (this.world.hasSkyAccess(new BlockPos(var2.posX, var2.posY, var2.posZ))) {
               this.currentPath.setPathLength(var1 - 1);
               return;
            }
         }
      }
   }

   @Override
   protected boolean isCurrentPathNode(Vec3d posAboveWater, Vec3d pathNode, int mobEntityWidth, int mobEntityHeight, int mobEntityDepth) {
      int var6 = MathHelper.floor(posAboveWater.x);
      int var7 = MathHelper.floor(posAboveWater.z);
      double var8 = pathNode.x - posAboveWater.x;
      double var10 = pathNode.z - posAboveWater.z;
      double var12 = var8 * var8 + var10 * var10;
      if (var12 < 1.0E-8) {
         return false;
      } else {
         double var14 = 1.0 / Math.sqrt(var12);
         var8 *= var14;
         var10 *= var14;
         mobEntityWidth += 2;
         mobEntityDepth += 2;
         if (!this.m_56qostfkl(var6, (int)posAboveWater.y, var7, mobEntityWidth, mobEntityHeight, mobEntityDepth, posAboveWater, var8, var10)) {
            return false;
         } else {
            mobEntityWidth -= 2;
            mobEntityDepth -= 2;
            double var16 = 1.0 / Math.abs(var8);
            double var18 = 1.0 / Math.abs(var10);
            double var20 = (double)(var6 * 1) - posAboveWater.x;
            double var22 = (double)(var7 * 1) - posAboveWater.z;
            if (var8 >= 0.0) {
               ++var20;
            }

            if (var10 >= 0.0) {
               ++var22;
            }

            var20 /= var8;
            var22 /= var10;
            int var24 = var8 < 0.0 ? -1 : 1;
            int var25 = var10 < 0.0 ? -1 : 1;
            int var26 = MathHelper.floor(pathNode.x);
            int var27 = MathHelper.floor(pathNode.z);
            int var28 = var26 - var6;
            int var29 = var27 - var7;

            while(var28 * var24 > 0 || var29 * var25 > 0) {
               if (var20 < var22) {
                  var20 += var16;
                  var6 += var24;
                  var28 = var26 - var6;
               } else {
                  var22 += var18;
                  var7 += var25;
                  var29 = var27 - var7;
               }

               if (!this.m_56qostfkl(var6, (int)posAboveWater.y, var7, mobEntityWidth, mobEntityHeight, mobEntityDepth, posAboveWater, var8, var10)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private boolean m_56qostfkl(int i, int j, int k, int l, int m, int n, Vec3d c_60pmlswez, double d, double e) {
      int var12 = i - l / 2;
      int var13 = k - n / 2;
      if (!this.m_26qlpqfjx(var12, j, var13, l, m, n, c_60pmlswez, d, e)) {
         return false;
      } else {
         for(int var14 = var12; var14 < var12 + l; ++var14) {
            for(int var15 = var13; var15 < var13 + n; ++var15) {
               double var16 = (double)var14 + 0.5 - c_60pmlswez.x;
               double var18 = (double)var15 + 0.5 - c_60pmlswez.z;
               if (!(var16 * d + var18 * e < 0.0)) {
                  Block var20 = this.world.getBlockState(new BlockPos(var14, j - 1, var15)).getBlock();
                  Material var21 = var20.getMaterial();
                  if (var21 == Material.AIR) {
                     return false;
                  }

                  if (var21 == Material.WATER && !this.mob.isInWater()) {
                     return false;
                  }

                  if (var21 == Material.LAVA) {
                     return false;
                  }
               }
            }
         }

         return true;
      }
   }

   private boolean m_26qlpqfjx(int i, int j, int k, int l, int m, int n, Vec3d c_60pmlswez, double d, double e) {
      for(BlockPos var13 : BlockPos.iterateRegion(new BlockPos(i, j, k), new BlockPos(i + l - 1, j + m - 1, k + n - 1))) {
         double var14 = (double)var13.getX() + 0.5 - c_60pmlswez.x;
         double var16 = (double)var13.getZ() + 0.5 - c_60pmlswez.z;
         if (!(var14 * d + var16 * e < 0.0)) {
            Block var18 = this.world.getBlockState(var13).getBlock();
            if (!var18.canWalkThrough(this.world, var13)) {
               return false;
            }
         }
      }

      return true;
   }

   public void m_61diarbat(boolean bl) {
      this.pathNodeHelper.m_13ledylzx(bl);
   }

   public boolean m_10fpgovhb() {
      return this.pathNodeHelper.m_24rstbtgs();
   }

   public void m_54onmfdow(boolean bl) {
      this.pathNodeHelper.m_59dqsodjb(bl);
   }

   public void m_91qjgongu(boolean bl) {
      this.pathNodeHelper.m_50gulttgl(bl);
   }

   public boolean canInteractWithDoors() {
      return this.pathNodeHelper.canInteractWithDoors();
   }

   public void setCanSwim(boolean canSwim) {
      this.pathNodeHelper.setCanSwim(canSwim);
   }

   public boolean canSwim() {
      return this.pathNodeHelper.canSwim();
   }

   public void setAvoidSunLight(boolean avoidSunLight) {
      this.shouldAvoidSunLight = avoidSunLight;
   }
}
