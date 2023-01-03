package net.minecraft.block.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.util.Tickable;

public class EnderChestBlockEntity extends BlockEntity implements Tickable {
   public float animationProgress;
   public float lastAnimationProgress;
   public int viewerCount;
   private int ticks;

   @Override
   public void tick() {
      if (++this.ticks % 20 * 4 == 0) {
         this.world.addBlockEvent(this.pos, Blocks.ENDER_CHEST, 1, this.viewerCount);
      }

      this.lastAnimationProgress = this.animationProgress;
      int var1 = this.pos.getX();
      int var2 = this.pos.getY();
      int var3 = this.pos.getZ();
      float var4 = 0.1F;
      if (this.viewerCount > 0 && this.animationProgress == 0.0F) {
         double var5 = (double)var1 + 0.5;
         double var7 = (double)var3 + 0.5;
         this.world.playSound(var5, (double)var2 + 0.5, var7, "random.chestopen", 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
      }

      if (this.viewerCount == 0 && this.animationProgress > 0.0F || this.viewerCount > 0 && this.animationProgress < 1.0F) {
         float var11 = this.animationProgress;
         if (this.viewerCount > 0) {
            this.animationProgress += var4;
         } else {
            this.animationProgress -= var4;
         }

         if (this.animationProgress > 1.0F) {
            this.animationProgress = 1.0F;
         }

         float var6 = 0.5F;
         if (this.animationProgress < var6 && var11 >= var6) {
            double var12 = (double)var1 + 0.5;
            double var9 = (double)var3 + 0.5;
            this.world.playSound(var12, (double)var2 + 0.5, var9, "random.chestclosed", 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
         }

         if (this.animationProgress < 0.0F) {
            this.animationProgress = 0.0F;
         }
      }
   }

   @Override
   public boolean doEvent(int type, int data) {
      if (type == 1) {
         this.viewerCount = data;
         return true;
      } else {
         return super.doEvent(type, data);
      }
   }

   @Override
   public void markRemoved() {
      this.clearBlockCache();
      super.markRemoved();
   }

   public void onOpen() {
      ++this.viewerCount;
      this.world.addBlockEvent(this.pos, Blocks.ENDER_CHEST, 1, this.viewerCount);
   }

   public void onClose() {
      --this.viewerCount;
      this.world.addBlockEvent(this.pos, Blocks.ENDER_CHEST, 1, this.viewerCount);
   }

   public boolean isValid(PlayerEntity player) {
      if (this.world.getBlockEntity(this.pos) != this) {
         return false;
      } else {
         return !(player.getSquaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
      }
   }
}
