package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class MovingBlockEntity extends BlockEntity implements Tickable {
   private BlockState movedState;
   private Direction facing;
   private boolean extending;
   private boolean source;
   private float progress;
   private float lastProgress;
   private List movedEntities = Lists.newArrayList();

   public MovingBlockEntity() {
   }

   public MovingBlockEntity(BlockState movedState, Direction facing, boolean extending, boolean source) {
      this.movedState = movedState;
      this.facing = facing;
      this.extending = extending;
      this.source = source;
   }

   public BlockState getMovedState() {
      return this.movedState;
   }

   @Override
   public int getCachedMetadata() {
      return 0;
   }

   public boolean isExtending() {
      return this.extending;
   }

   public Direction getFacing() {
      return this.facing;
   }

   @Environment(EnvType.CLIENT)
   public boolean isSource() {
      return this.source;
   }

   public float getProgress(float tickDelta) {
      if (tickDelta > 1.0F) {
         tickDelta = 1.0F;
      }

      return this.lastProgress + (this.progress - this.lastProgress) * tickDelta;
   }

   @Environment(EnvType.CLIENT)
   public float getRenderOffsetX(float tickDelta) {
      return this.extending
         ? (this.getProgress(tickDelta) - 1.0F) * (float)this.facing.getOffsetX()
         : (1.0F - this.getProgress(tickDelta)) * (float)this.facing.getOffsetX();
   }

   @Environment(EnvType.CLIENT)
   public float getRenderOffsetY(float tickDelta) {
      return this.extending
         ? (this.getProgress(tickDelta) - 1.0F) * (float)this.facing.getOffsetY()
         : (1.0F - this.getProgress(tickDelta)) * (float)this.facing.getOffsetY();
   }

   @Environment(EnvType.CLIENT)
   public float getRenderOffsetZ(float tickDelta) {
      return this.extending
         ? (this.getProgress(tickDelta) - 1.0F) * (float)this.facing.getOffsetZ()
         : (1.0F - this.getProgress(tickDelta)) * (float)this.facing.getOffsetZ();
   }

   private void moveEntities(float progress, float amount) {
      if (this.extending) {
         progress = 1.0F - progress;
      } else {
         --progress;
      }

      Box var3 = Blocks.MOVING_BLOCK.getCollisionShape(this.world, this.pos, this.movedState, progress, this.facing);
      if (var3 != null) {
         List var4 = this.world.getEntities(null, var3);
         if (!var4.isEmpty()) {
            this.movedEntities.addAll(var4);

            for(Entity var6 : this.movedEntities) {
               if (this.movedState.getBlock() == Blocks.SLIME && this.extending) {
                  switch(this.facing.getAxis()) {
                     case X:
                        var6.velocityX = (double)this.facing.getOffsetX();
                        break;
                     case Y:
                        var6.velocityY = (double)this.facing.getOffsetY();
                        break;
                     case Z:
                        var6.velocityZ = (double)this.facing.getOffsetZ();
                  }
               } else {
                  var6.move(
                     (double)(amount * (float)this.facing.getOffsetX()),
                     (double)(amount * (float)this.facing.getOffsetY()),
                     (double)(amount * (float)this.facing.getOffsetZ())
                  );
               }
            }

            this.movedEntities.clear();
         }
      }
   }

   public void finish() {
      if (this.lastProgress < 1.0F && this.world != null) {
         this.lastProgress = this.progress = 1.0F;
         this.world.removeBlockEntity(this.pos);
         this.markRemoved();
         if (this.world.getBlockState(this.pos).getBlock() == Blocks.MOVING_BLOCK) {
            this.world.setBlockState(this.pos, this.movedState, 3);
            this.world.updateBlock(this.pos, this.movedState.getBlock());
         }
      }
   }

   @Override
   public void tick() {
      this.lastProgress = this.progress;
      if (this.lastProgress >= 1.0F) {
         this.moveEntities(1.0F, 0.25F);
         this.world.removeBlockEntity(this.pos);
         this.markRemoved();
         if (this.world.getBlockState(this.pos).getBlock() == Blocks.MOVING_BLOCK) {
            this.world.setBlockState(this.pos, this.movedState, 3);
            this.world.updateBlock(this.pos, this.movedState.getBlock());
         }
      } else {
         this.progress += 0.5F;
         if (this.progress >= 1.0F) {
            this.progress = 1.0F;
         }

         if (this.extending) {
            this.moveEntities(this.progress, this.progress - this.lastProgress + 0.0625F);
         }
      }
   }

   @Override
   public void readNbt(NbtCompound nbt) {
      super.readNbt(nbt);
      this.movedState = Block.byRawId(nbt.getInt("blockId")).getStateFromMetadata(nbt.getInt("blockData"));
      this.facing = Direction.byId(nbt.getInt("facing"));
      this.lastProgress = this.progress = nbt.getFloat("progress");
      this.extending = nbt.getBoolean("extending");
   }

   @Override
   public void writeNbt(NbtCompound nbt) {
      super.writeNbt(nbt);
      nbt.putInt("blockId", Block.getRawId(this.movedState.getBlock()));
      nbt.putInt("blockData", this.movedState.getBlock().getMetadataFromState(this.movedState));
      nbt.putInt("facing", this.facing.getId());
      nbt.putFloat("progress", this.lastProgress);
      nbt.putBoolean("extending", this.extending);
   }
}
