package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.item.group.ItemGroup;

public class PackedIceBlock extends Block {
   public PackedIceBlock() {
      super(Material.PACKED_ICE);
      this.slipperiness = 0.98F;
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public int getBaseDropCount(Random random) {
      return 0;
   }
}
