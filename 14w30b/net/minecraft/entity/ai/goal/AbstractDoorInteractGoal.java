package net.minecraft.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractDoorInteractGoal extends Goal {
   protected MobEntity mob;
   protected BlockPos f_62uuvqjwl = BlockPos.ORIGIN;
   protected DoorBlock doorBlock;
   boolean shouldStop;
   float offsetX;
   float offsetZ;

   public AbstractDoorInteractGoal(MobEntity mob) {
      this.mob = mob;
      if (!(mob.getNavigation() instanceof MobEntityNavigation)) {
         throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
      }
   }

   @Override
   public boolean canStart() {
      if (!this.mob.collidingHorizontally) {
         return false;
      } else {
         MobEntityNavigation var1 = (MobEntityNavigation)this.mob.getNavigation();
         Path var2 = var1.getCurrentPath();
         if (var2 != null && !var2.reachedTarget() && var1.canInteractWithDoors()) {
            for(int var3 = 0; var3 < Math.min(var2.getIndexInPath() + 2, var2.getPathLength()); ++var3) {
               PathNode var4 = var2.getPathNode(var3);
               this.f_62uuvqjwl = new BlockPos(var4.posX, var4.posY + 1, var4.posZ);
               if (!(this.mob.getSquaredDistanceTo((double)this.f_62uuvqjwl.getX(), this.mob.y, (double)this.f_62uuvqjwl.getZ()) > 2.25)) {
                  this.doorBlock = this.getDoorBlock(this.f_62uuvqjwl);
                  if (this.doorBlock != null) {
                     return true;
                  }
               }
            }

            this.f_62uuvqjwl = new BlockPos(this.mob).up();
            this.doorBlock = this.getDoorBlock(this.f_62uuvqjwl);
            return this.doorBlock != null;
         } else {
            return false;
         }
      }
   }

   @Override
   public boolean shouldContinue() {
      return !this.shouldStop;
   }

   @Override
   public void start() {
      this.shouldStop = false;
      this.offsetX = (float)((double)((float)this.f_62uuvqjwl.getX() + 0.5F) - this.mob.x);
      this.offsetZ = (float)((double)((float)this.f_62uuvqjwl.getZ() + 0.5F) - this.mob.z);
   }

   @Override
   public void tick() {
      float var1 = (float)((double)((float)this.f_62uuvqjwl.getX() + 0.5F) - this.mob.x);
      float var2 = (float)((double)((float)this.f_62uuvqjwl.getZ() + 0.5F) - this.mob.z);
      float var3 = this.offsetX * var1 + this.offsetZ * var2;
      if (var3 < 0.0F) {
         this.shouldStop = true;
      }
   }

   private DoorBlock getDoorBlock(BlockPos x) {
      Block var2 = this.mob.world.getBlockState(x).getBlock();
      return var2 != Blocks.WOODEN_DOOR ? null : (DoorBlock)var2;
   }
}
