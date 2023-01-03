package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.item.group.ItemGroup;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class GlassBlock extends TransparentBlock {
   public GlassBlock(Material c_57ywipuwq, boolean bl) {
      super(c_57ywipuwq, bl);
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public int getBaseDropCount(Random random) {
      return 0;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   protected boolean hasSilkTouchDrops() {
      return true;
   }
}
