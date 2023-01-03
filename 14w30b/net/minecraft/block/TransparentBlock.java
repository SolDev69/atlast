package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class TransparentBlock extends Block {
   private boolean sideVisible;

   protected TransparentBlock(Material material, boolean sideVisible) {
      super(material);
      this.sideVisible = sideVisible;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean shouldRenderFace(IWorld world, BlockPos pos, Direction face) {
      BlockState var4 = world.getBlockState(pos);
      Block var5 = var4.getBlock();
      if (this == Blocks.GLASS || this == Blocks.STAINED_GLASS) {
         if (world.getBlockState(pos.offset(face.getOpposite())) != var4) {
            return true;
         }

         if (var5 == this) {
            return false;
         }
      }

      return !this.sideVisible && var5 == this ? false : super.shouldRenderFace(world, pos, face);
   }
}
