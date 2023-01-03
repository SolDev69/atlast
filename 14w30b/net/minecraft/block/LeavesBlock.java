package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.client.world.color.FoliageColors;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class LeavesBlock extends AbstractLeavesBlock {
   public static final EnumProperty VARIANT = EnumProperty.of("variant", PlanksBlock.Variant.class, new Predicate() {
      public boolean apply(PlanksBlock.Variant c_23rxmddrl) {
         return c_23rxmddrl.getIndex() < 4;
      }
   });

   public LeavesBlock() {
      this.setDefaultState(this.stateDefinition.any().set(VARIANT, PlanksBlock.Variant.OAK).set(CHECK_DECAY, true).set(DECAYABLE, true));
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor(int tint) {
      PlanksBlock.Variant var2 = PlanksBlock.Variant.byIndex(tint);
      if (var2 == PlanksBlock.Variant.SPRUCE) {
         return FoliageColors.getSpruceColor();
      } else {
         return var2 == PlanksBlock.Variant.BIRCH ? FoliageColors.getBirchColor() : super.getColor(tint);
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor(IWorld world, BlockPos pos, int tint) {
      PlanksBlock.Variant var4 = (PlanksBlock.Variant)world.getBlockState(pos).get(VARIANT);
      if (var4 == PlanksBlock.Variant.SPRUCE) {
         return FoliageColors.getSpruceColor();
      } else {
         return var4 == PlanksBlock.Variant.BIRCH ? FoliageColors.getBirchColor() : super.getColor(world, pos, tint);
      }
   }

   @Override
   protected void dropAppleWithChance(World world, BlockPos pos, BlockState state, int chance) {
      if (state.get(VARIANT) == PlanksBlock.Variant.OAK && world.random.nextInt(chance) == 0) {
         this.dropItems(world, pos, new ItemStack(Items.APPLE, 1, 0));
      }
   }

   @Override
   protected int getSaplingDropChance(BlockState state) {
      return state.get(VARIANT) == PlanksBlock.Variant.JUNGLE ? 40 : super.getSaplingDropChance(state);
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
   protected ItemStack getSilkTouchDrop(BlockState state) {
      return new ItemStack(Item.byBlock(this), 1, ((PlanksBlock.Variant)state.get(VARIANT)).getIndex());
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(VARIANT, this.getVariant(metadata)).set(DECAYABLE, (metadata & 4) == 0).set(CHECK_DECAY, (metadata & 8) > 0);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= ((PlanksBlock.Variant)state.get(VARIANT)).getIndex();
      if (!state.get(DECAYABLE)) {
         var2 |= 4;
      }

      if (state.get(CHECK_DECAY)) {
         var2 |= 8;
      }

      return var2;
   }

   @Override
   public PlanksBlock.Variant getVariant(int index) {
      return PlanksBlock.Variant.byIndex((index & 3) % 4);
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, VARIANT, CHECK_DECAY, DECAYABLE);
   }

   @Override
   public int getDropItemMetadata(BlockState state) {
      return ((PlanksBlock.Variant)state.get(VARIANT)).getIndex();
   }

   @Override
   public void afterMinedByPlayer(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
      if (!world.isClient && player.getMainHandStack() != null && player.getMainHandStack().getItem() == Items.SHEARS) {
         player.incrementStat(Stats.BLOCKS_MINED[Block.getRawId(this)]);
         this.dropItems(world, pos, new ItemStack(Item.byBlock(this), 1, ((PlanksBlock.Variant)state.get(VARIANT)).getIndex()));
      } else {
         super.afterMinedByPlayer(world, player, pos, state, blockEntity);
      }
   }
}
