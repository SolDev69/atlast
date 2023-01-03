package net.minecraft.block.pattern;

import com.google.common.base.Predicate;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockPointer {
   private final World world;
   private final BlockPos pos;
   private BlockState state;
   private BlockEntity blockEntity;
   private boolean hasBlockEntity;

   public BlockPointer(World world, BlockPos pos) {
      this.world = world;
      this.pos = pos;
   }

   public BlockState getState() {
      if (this.state == null && this.world.isLoaded(this.pos)) {
         this.state = this.world.getBlockState(this.pos);
      }

      return this.state;
   }

   public BlockEntity getBlockEntity() {
      if (this.blockEntity == null && !this.hasBlockEntity) {
         this.blockEntity = this.world.getBlockEntity(this.pos);
         this.hasBlockEntity = true;
      }

      return this.blockEntity;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public static Predicate hasState(Predicate predicate) {
      return new Predicate() {
         public boolean apply(BlockPointer c_96zktyemb) {
            return c_96zktyemb != null && predicate.apply(c_96zktyemb.getState());
         }
      };
   }
}
