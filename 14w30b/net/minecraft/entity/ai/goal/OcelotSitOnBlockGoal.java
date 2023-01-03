package net.minecraft.entity.ai.goal;

import net.minecraft.C_61rczfvzv;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.mob.passive.animal.tamable.OcelotEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OcelotSitOnBlockGoal extends C_61rczfvzv {
   private final OcelotEntity ocelot;

   public OcelotSitOnBlockGoal(OcelotEntity ocelot, double speed) {
      super(ocelot, speed, 8);
      this.ocelot = ocelot;
   }

   @Override
   public boolean canStart() {
      return this.ocelot.isTamed() && !this.ocelot.isSitting() && super.canStart();
   }

   @Override
   public boolean shouldContinue() {
      return super.shouldContinue();
   }

   @Override
   public void start() {
      super.start();
      this.ocelot.getSitGoal().setEnabledWithOwner(false);
   }

   @Override
   public void stop() {
      super.stop();
      this.ocelot.setSitting(false);
   }

   @Override
   public void tick() {
      super.tick();
      this.ocelot.getSitGoal().setEnabledWithOwner(false);
      if (!this.m_73tnmggyc()) {
         this.ocelot.setSitting(false);
      } else if (!this.ocelot.isSitting()) {
         this.ocelot.setSitting(true);
      }
   }

   @Override
   protected boolean canSitOnBlock(World c_54ruxjwzt, BlockPos c_76varpwca) {
      if (!c_54ruxjwzt.isAir(c_76varpwca.up())) {
         return false;
      } else {
         BlockState var3 = c_54ruxjwzt.getBlockState(c_76varpwca);
         Block var4 = var3.getBlock();
         if (var4 == Blocks.CHEST) {
            BlockEntity var5 = c_54ruxjwzt.getBlockEntity(c_76varpwca);
            if (var5 instanceof ChestBlockEntity && ((ChestBlockEntity)var5).viewerCount < 1) {
               return true;
            }
         } else {
            if (var4 == Blocks.LIT_FURNACE) {
               return true;
            }

            if (var4 == Blocks.BED && var3.get(BedBlock.PART) != BedBlock.Part.HEAD) {
               return true;
            }
         }

         return false;
      }
   }
}
