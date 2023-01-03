package net.minecraft;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class C_61rczfvzv extends Goal {
   private final PathAwareEntity f_24ekdxmse;
   private final double f_52nkfniqs;
   protected int f_56jbshthq;
   private int f_85vhshinn;
   private int f_04iebwcxn;
   protected BlockPos f_46iwgcxts = BlockPos.ORIGIN;
   private boolean f_38qjwthvk;
   private int f_08ksitxcp;

   public C_61rczfvzv(PathAwareEntity c_60guwxsid, double d, int i) {
      this.f_24ekdxmse = c_60guwxsid;
      this.f_52nkfniqs = d;
      this.f_08ksitxcp = i;
      this.setControls(5);
   }

   @Override
   public boolean canStart() {
      if (this.f_56jbshthq > 0) {
         --this.f_56jbshthq;
         return false;
      } else {
         this.f_56jbshthq = 200 + this.f_24ekdxmse.getRandom().nextInt(200);
         return this.m_48xiiyqei();
      }
   }

   @Override
   public boolean shouldContinue() {
      return this.f_85vhshinn >= -this.f_04iebwcxn && this.f_85vhshinn <= 1200 && this.canSitOnBlock(this.f_24ekdxmse.world, this.f_46iwgcxts);
   }

   @Override
   public void start() {
      this.f_24ekdxmse
         .getNavigation()
         .startMovingTo(
            (double)((float)this.f_46iwgcxts.getX()) + 0.5,
            (double)(this.f_46iwgcxts.getY() + 1),
            (double)((float)this.f_46iwgcxts.getZ()) + 0.5,
            this.f_52nkfniqs
         );
      this.f_85vhshinn = 0;
      this.f_04iebwcxn = this.f_24ekdxmse.getRandom().nextInt(this.f_24ekdxmse.getRandom().nextInt(1200) + 1200) + 1200;
   }

   @Override
   public void stop() {
   }

   @Override
   public void tick() {
      if (this.f_24ekdxmse.getSquaredDistanceToCenter(this.f_46iwgcxts.up()) > 1.0) {
         this.f_38qjwthvk = false;
         ++this.f_85vhshinn;
         if (this.f_85vhshinn % 40 == 0) {
            this.f_24ekdxmse
               .getNavigation()
               .startMovingTo(
                  (double)((float)this.f_46iwgcxts.getX()) + 0.5,
                  (double)(this.f_46iwgcxts.getY() + 1),
                  (double)((float)this.f_46iwgcxts.getZ()) + 0.5,
                  this.f_52nkfniqs
               );
         }
      } else {
         this.f_38qjwthvk = true;
         --this.f_85vhshinn;
      }
   }

   protected boolean m_73tnmggyc() {
      return this.f_38qjwthvk;
   }

   private boolean m_48xiiyqei() {
      int var1 = this.f_08ksitxcp;
      boolean var2 = true;
      BlockPos var3 = new BlockPos(this.f_24ekdxmse);

      for(int var4 = 0; var4 <= 1; var4 = var4 > 0 ? -var4 : 1 - var4) {
         for(int var5 = 0; var5 < var1; ++var5) {
            for(int var6 = 0; var6 <= var5; var6 = var6 > 0 ? -var6 : 1 - var6) {
               for(int var7 = var6 < var5 && var6 > -var5 ? var5 : 0; var7 <= var5; var7 = var7 > 0 ? -var7 : 1 - var7) {
                  BlockPos var8 = var3.add(var6, var4 - 1, var7);
                  if (this.f_24ekdxmse.isPosInVillage(var8) && this.canSitOnBlock(this.f_24ekdxmse.world, var8)) {
                     this.f_46iwgcxts = var8;
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   protected abstract boolean canSitOnBlock(World c_54ruxjwzt, BlockPos c_76varpwca);
}
