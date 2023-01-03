package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class LogBlock extends AbstractLogBlock {
   public static final EnumProperty VARIANT = EnumProperty.of("variant", PlanksBlock.Variant.class, new Predicate() {
      public boolean apply(PlanksBlock.Variant c_23rxmddrl) {
         return c_23rxmddrl.getIndex() < 4;
      }
   });

   public LogBlock() {
      this.setDefaultState(this.stateDefinition.any().set(VARIANT, PlanksBlock.Variant.OAK).set(LOG_AXIS, AbstractLogBlock.LogAxis.Y));
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      stacks.add(new ItemStack(item, 1, PlanksBlock.Variant.OAK.getIndex()));
      stacks.add(new ItemStack(item, 1, PlanksBlock.Variant.SPRUCE.getIndex()));
      stacks.add(new ItemStack(item, 1, PlanksBlock.Variant.BIRCH.getIndex()));
      stacks.add(new ItemStack(item, 1, PlanksBlock.Variant.JUNGLE.getIndex()));
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      BlockState var2 = this.defaultState().set(VARIANT, PlanksBlock.Variant.byIndex((metadata & 3) % 4));
      switch(metadata & 12) {
         case 0:
            var2 = var2.set(LOG_AXIS, AbstractLogBlock.LogAxis.Y);
            break;
         case 4:
            var2 = var2.set(LOG_AXIS, AbstractLogBlock.LogAxis.X);
            break;
         case 8:
            var2 = var2.set(LOG_AXIS, AbstractLogBlock.LogAxis.Z);
            break;
         default:
            var2 = var2.set(LOG_AXIS, AbstractLogBlock.LogAxis.NONE);
      }

      return var2;
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= ((PlanksBlock.Variant)state.get(VARIANT)).getIndex();
      switch((AbstractLogBlock.LogAxis)state.get(LOG_AXIS)) {
         case X:
            var2 |= 4;
            break;
         case Z:
            var2 |= 8;
            break;
         case NONE:
            var2 |= 12;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, VARIANT, LOG_AXIS);
   }

   @Override
   protected ItemStack getSilkTouchDrop(BlockState state) {
      return new ItemStack(Item.byBlock(this), 1, ((PlanksBlock.Variant)state.get(VARIANT)).getIndex());
   }

   @Override
   public int getDropItemMetadata(BlockState state) {
      return ((PlanksBlock.Variant)state.get(VARIANT)).getIndex();
   }
}
