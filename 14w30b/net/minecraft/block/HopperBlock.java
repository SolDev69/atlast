package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.InventoryUtils;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class HopperBlock extends BlockWithBlockEntity {
   public static final DirectionProperty FACING = DirectionProperty.of("facing", new Predicate() {
      public boolean apply(Direction c_69garkogr) {
         return c_69garkogr != Direction.UP;
      }
   });
   public static final BooleanProperty ENABLED = BooleanProperty.of("enabled");

   public HopperBlock() {
      super(Material.IRON);
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.DOWN).set(ENABLED, true));
      this.setItemGroup(ItemGroup.REDSTONE);
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void getCollisionBoxes(World world, BlockPos pos, BlockState state, Box entityBox, List boxes, Entity entity) {
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
      super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      float var7 = 0.125F;
      this.setShape(0.0F, 0.0F, 0.0F, var7, 1.0F, 1.0F);
      super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var7);
      super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      this.setShape(1.0F - var7, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      this.setShape(0.0F, 0.0F, 1.0F - var7, 1.0F, 1.0F, 1.0F);
      super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      Direction var9 = dir.getOpposite();
      if (var9 == Direction.UP) {
         var9 = Direction.DOWN;
      }

      return this.defaultState().set(FACING, var9).set(ENABLED, true);
   }

   @Override
   public BlockEntity createBlockEntity(World world, int metadata) {
      return new HopperBlockEntity();
   }

   @Override
   public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
      super.onPlaced(world, pos, state, entity, stack);
      if (stack.hasCustomHoverName()) {
         BlockEntity var6 = world.getBlockEntity(pos);
         if (var6 instanceof HopperBlockEntity) {
            ((HopperBlockEntity)var6).setCustomName(stack.getHoverName());
         }
      }
   }

   @Override
   public void onAdded(World world, BlockPos pos, BlockState state) {
      this.updateEnabled(world, pos, state);
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (world.isClient) {
         return true;
      } else {
         BlockEntity var9 = world.getBlockEntity(pos);
         if (var9 instanceof HopperBlockEntity) {
            player.openInventoryMenu((HopperBlockEntity)var9);
         }

         return true;
      }
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      this.updateEnabled(world, pos, state);
   }

   private void updateEnabled(World world, BlockPos pos, BlockState state) {
      boolean var4 = !world.isReceivingPower(pos);
      if (var4 != state.get(ENABLED)) {
         world.setBlockState(pos, state.set(ENABLED, var4), 4);
      }
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      BlockEntity var4 = world.getBlockEntity(pos);
      if (var4 instanceof HopperBlockEntity) {
         InventoryUtils.dropContents(world, pos, (HopperBlockEntity)var4);
         world.updateComparators(pos, this);
      }

      super.onRemoved(world, pos, state);
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean shouldRenderFace(IWorld world, BlockPos pos, Direction face) {
      return true;
   }

   public static Direction getFacingFromMetadata(int metadata) {
      return Direction.byId(metadata & 7);
   }

   public static boolean getEnabledFromMetadata(int metadata) {
      return (metadata & 8) != 8;
   }

   @Override
   public boolean hasAnalogOutput() {
      return true;
   }

   @Override
   public int getAnalogOutput(World world, BlockPos pos) {
      return InventoryMenu.getAnalogOutput(world.getBlockEntity(pos));
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT_MIPPED;
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(FACING, getFacingFromMetadata(metadata)).set(ENABLED, getEnabledFromMetadata(metadata));
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= ((Direction)state.get(FACING)).getId();
      if (!state.get(ENABLED)) {
         var2 |= 8;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING, ENABLED);
   }
}
