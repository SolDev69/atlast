package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.WheatBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.mob.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class C_54zeovuss extends C_61rczfvzv {
   private final VillagerEntity f_41rbxqngq;
   private boolean f_36vzjumrv;
   private boolean f_45trmqogc;
   private int f_98evxvlpx;

   public C_54zeovuss(VillagerEntity c_21keykoxl, double d) {
      super(c_21keykoxl, d, 16);
      this.f_41rbxqngq = c_21keykoxl;
   }

   @Override
   public boolean canStart() {
      if (this.f_56jbshthq <= 0) {
         if (!this.f_41rbxqngq.world.getGameRules().getBoolean("mobGriefing")) {
            return false;
         }

         this.f_98evxvlpx = -1;
         this.f_36vzjumrv = this.f_41rbxqngq.m_73suzockq();
         this.f_45trmqogc = this.f_41rbxqngq.m_19khndpdy();
      }

      return super.canStart();
   }

   @Override
   public boolean shouldContinue() {
      return this.f_98evxvlpx >= 0 && super.shouldContinue();
   }

   @Override
   public void start() {
      super.start();
   }

   @Override
   public void stop() {
      super.stop();
   }

   @Override
   public void tick() {
      super.tick();
      this.f_41rbxqngq
         .getLookControl()
         .lookAt(
            (double)this.f_46iwgcxts.getX() + 0.5,
            (double)(this.f_46iwgcxts.getY() + 1),
            (double)this.f_46iwgcxts.getZ() + 0.5,
            10.0F,
            (float)this.f_41rbxqngq.getLookPitchSpeed()
         );
      if (this.m_73tnmggyc()) {
         World var1 = this.f_41rbxqngq.world;
         BlockPos var2 = this.f_46iwgcxts.up();
         BlockState var3 = var1.getBlockState(var2);
         Block var4 = var3.getBlock();
         if (this.f_98evxvlpx == 0 && var4 instanceof WheatBlock && var3.get(WheatBlock.AGE) == 7) {
            var1.breakBlock(var2, true);
         } else if (this.f_98evxvlpx == 1 && var4 == Blocks.AIR) {
            SimpleInventory var5 = this.f_41rbxqngq.m_73ivhbact();

            for(int var6 = 0; var6 < var5.getSize(); ++var6) {
               ItemStack var7 = var5.getStack(var6);
               boolean var8 = false;
               if (var7 != null) {
                  if (var7.getItem() == Items.WHEAT_SEEDS) {
                     var1.setBlockState(var2, Blocks.WHEAT.defaultState(), 3);
                     var8 = true;
                  } else if (var7.getItem() == Items.POTATO) {
                     var1.setBlockState(var2, Blocks.POTATOES.defaultState(), 3);
                     var8 = true;
                  } else if (var7.getItem() == Items.CARROT) {
                     var1.setBlockState(var2, Blocks.CARROTS.defaultState(), 3);
                     var8 = true;
                  }
               }

               if (var8) {
                  --var7.size;
                  if (var7.size <= 0) {
                     var5.setStack(var6, null);
                  }
                  break;
               }
            }
         }

         this.f_98evxvlpx = -1;
         this.f_56jbshthq = 10;
      }
   }

   @Override
   protected boolean canSitOnBlock(World c_54ruxjwzt, BlockPos c_76varpwca) {
      Block var3 = c_54ruxjwzt.getBlockState(c_76varpwca).getBlock();
      if (var3 == Blocks.FARMLAND) {
         c_76varpwca = c_76varpwca.up();
         BlockState var4 = c_54ruxjwzt.getBlockState(c_76varpwca);
         var3 = var4.getBlock();
         if (var3 instanceof WheatBlock && var4.get(WheatBlock.AGE) == 7 && this.f_45trmqogc && (this.f_98evxvlpx == 0 || this.f_98evxvlpx < 0)) {
            this.f_98evxvlpx = 0;
            return true;
         }

         if (var3 == Blocks.AIR && this.f_36vzjumrv && (this.f_98evxvlpx == 1 || this.f_98evxvlpx < 0)) {
            this.f_98evxvlpx = 1;
            return true;
         }
      }

      return false;
   }
}
