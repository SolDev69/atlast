package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class BarrierBlock extends Block {
   protected BarrierBlock() {
      super(Material.BARRIER);
      this.setUnbreakable();
      this.setResistance(6000001.0F);
      this.disableStats();
      this.isTranslucent = true;
   }

   @Override
   public int getRenderType() {
      return -1;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public float getAmbientOcclusionLight() {
      return 1.0F;
   }

   @Override
   public void dropItems(World world, BlockPos pos, BlockState state, float luck, int fortuneLevel) {
   }
}
