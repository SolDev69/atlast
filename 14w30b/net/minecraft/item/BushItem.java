package net.minecraft.item;

import com.google.common.base.Function;
import net.minecraft.block.Block;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.client.world.color.GrassColors;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class BushItem extends VariantBlockItem {
   public BushItem(Block c_68zcrzyxg, Block c_68zcrzyxg2, Function function) {
      super(c_68zcrzyxg, c_68zcrzyxg2, function);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getDisplayColor(ItemStack stack, int color) {
      DoublePlantBlock.Variant var3 = DoublePlantBlock.Variant.byIndex(stack.getMetadata());
      return var3 != DoublePlantBlock.Variant.GRASS && var3 != DoublePlantBlock.Variant.FERN
         ? super.getDisplayColor(stack, color)
         : GrassColors.getColor(0.5, 1.0);
   }
}
