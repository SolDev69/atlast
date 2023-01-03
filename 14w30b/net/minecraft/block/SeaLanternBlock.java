package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.MathHelper;

public class SeaLanternBlock extends Block {
   public SeaLanternBlock(Material c_57ywipuwq) {
      super(c_57ywipuwq);
      this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
   }

   @Override
   public int getBaseDropCount(Random random) {
      return 2 + random.nextInt(2);
   }

   @Override
   public int getDropCount(int fortuneLevel, Random random) {
      return MathHelper.clamp(this.getBaseDropCount(random) + random.nextInt(fortuneLevel + 1), 1, 5);
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Items.PRISMARINE_CRYSTALS;
   }

   @Override
   public MaterialColor getMaterialColor(BlockState state) {
      return MaterialColor.QUARTZ;
   }

   @Override
   protected boolean hasSilkTouchDrops() {
      return true;
   }
}
