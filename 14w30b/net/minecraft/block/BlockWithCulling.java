package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class BlockWithCulling extends Block {
   protected boolean fancyGraphics;

   protected BlockWithCulling(Material material, boolean fancyGraphics) {
      super(material);
      this.fancyGraphics = fancyGraphics;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean shouldRenderFace(IWorld world, BlockPos pos, Direction face) {
      return !this.fancyGraphics && world.getBlockState(pos).getBlock() == this ? false : super.shouldRenderFace(world, pos, face);
   }
}
