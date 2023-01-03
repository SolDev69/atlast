package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class CarpetBlock extends Block {
   public static final EnumProperty COLOR = EnumProperty.of("color", DyeColor.class);

   protected CarpetBlock() {
      super(Material.CARPET);
      this.setDefaultState(this.stateDefinition.any().set(COLOR, DyeColor.WHITE));
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
      this.setTicksRandomly(true);
      this.setItemGroup(ItemGroup.DECORATIONS);
      this.setBoundingBox(0);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public void setBlockItemBounds() {
      this.setBoundingBox(0);
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      this.setBoundingBox(0);
   }

   protected void setBoundingBox(int i) {
      byte var2 = 0;
      float var3 = (float)(1 * (1 + var2)) / 16.0F;
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, var3, 1.0F);
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      return super.canSurvive(world, pos) && this.isSupported(world, pos);
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      this.canSurviveOrBreak(world, pos, state);
   }

   private boolean canSurviveOrBreak(World world, BlockPos pos, BlockState state) {
      if (!this.isSupported(world, pos)) {
         this.dropItems(world, pos, state, 0);
         world.removeBlock(pos);
         return false;
      } else {
         return true;
      }
   }

   private boolean isSupported(World world, BlockPos pos) {
      return !world.isAir(pos.down());
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean shouldRenderFace(IWorld world, BlockPos pos, Direction face) {
      return face == Direction.UP ? true : super.shouldRenderFace(world, pos, face);
   }

   @Override
   public int getDropItemMetadata(BlockState state) {
      return ((DyeColor)state.get(COLOR)).getIndex();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      for(int var4 = 0; var4 < 16; ++var4) {
         stacks.add(new ItemStack(item, 1, var4));
      }
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(COLOR, DyeColor.byIndex(metadata));
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return ((DyeColor)state.get(COLOR)).getIndex();
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, COLOR);
   }
}
