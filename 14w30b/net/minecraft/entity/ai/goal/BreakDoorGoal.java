package net.minecraft.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.world.Difficulty;

public class BreakDoorGoal extends AbstractDoorInteractGoal {
   private int breakProgress;
   private int previousBreakProgress = -1;

   public BreakDoorGoal(MobEntity c_81psrrogw) {
      super(c_81psrrogw);
   }

   @Override
   public boolean canStart() {
      if (!super.canStart()) {
         return false;
      } else if (!this.mob.world.getGameRules().getBoolean("mobGriefing")) {
         return false;
      } else {
         return !DoorBlock.getOpenFromMetadata(this.mob.world, this.f_62uuvqjwl);
      }
   }

   @Override
   public void start() {
      super.start();
      this.breakProgress = 0;
   }

   @Override
   public boolean shouldContinue() {
      double var1 = this.mob.getSquaredDistanceTo(this.f_62uuvqjwl);
      return this.breakProgress <= 240 && !DoorBlock.getOpenFromMetadata(this.mob.world, this.f_62uuvqjwl) && var1 < 4.0;
   }

   @Override
   public void stop() {
      super.stop();
      this.mob.world.updateBlockMiningProgress(this.mob.getNetworkId(), this.f_62uuvqjwl, -1);
   }

   @Override
   public void tick() {
      super.tick();
      if (this.mob.getRandom().nextInt(20) == 0) {
         this.mob.world.doEvent(1010, this.f_62uuvqjwl, 0);
      }

      ++this.breakProgress;
      int var1 = (int)((float)this.breakProgress / 240.0F * 10.0F);
      if (var1 != this.previousBreakProgress) {
         this.mob.world.updateBlockMiningProgress(this.mob.getNetworkId(), this.f_62uuvqjwl, var1);
         this.previousBreakProgress = var1;
      }

      if (this.breakProgress == 240 && this.mob.world.getDifficulty() == Difficulty.HARD) {
         this.mob.world.removeBlock(this.f_62uuvqjwl);
         this.mob.world.doEvent(1012, this.f_62uuvqjwl, 0);
         this.mob.world.doEvent(2001, this.f_62uuvqjwl, Block.getRawId(this.doorBlock));
      }
   }
}
