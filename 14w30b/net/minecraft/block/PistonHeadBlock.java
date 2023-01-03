package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.BooleanProperty;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PistonHeadBlock extends Block {
   public static final DirectionProperty FACING = DirectionProperty.of("facing");
   public static final EnumProperty TYPE = EnumProperty.of("type", PistonHeadBlock.Type.class);
   public static final BooleanProperty SHORT = BooleanProperty.of("short");

   public PistonHeadBlock() {
      super(Material.PISTON);
      this.setDefaultState(this.stateDefinition.any().set(FACING, Direction.NORTH).set(TYPE, PistonHeadBlock.Type.DEFAULT).set(SHORT, false));
      this.setSound(STONE_SOUND);
      this.setStrength(0.5F);
   }

   @Override
   public void beforeMinedByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      if (player.abilities.creativeMode) {
         Direction var5 = (Direction)state.get(FACING);
         if (var5 != null) {
            BlockPos var6 = pos.offset(var5.getOpposite());
            Block var7 = world.getBlockState(var6).getBlock();
            if (var7 == Blocks.PISTON || var7 == Blocks.STICKY_PISTON) {
               world.removeBlock(var6);
            }
         }
      }

      super.beforeMinedByPlayer(world, pos, state, player);
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      super.onRemoved(world, pos, state);
      Direction var4 = ((Direction)state.get(FACING)).getOpposite();
      pos = pos.offset(var4);
      BlockState var5 = world.getBlockState(pos);
      if ((var5.getBlock() == Blocks.PISTON || var5.getBlock() == Blocks.STICKY_PISTON) && var5.get(PistonBaseBlock.EXTENDED)) {
         var5.getBlock().dropItems(world, pos, var5, 0);
         world.removeBlock(pos);
      }
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
   public boolean canSurvive(World world, BlockPos pos) {
      return false;
   }

   @Override
   public boolean canPlace(World world, BlockPos pos, Direction dir) {
      return false;
   }

   @Override
   public int getBaseDropCount(Random random) {
      return 0;
   }

   @Override
   public void getCollisionBoxes(World world, BlockPos pos, BlockState state, Box entityBox, List boxes, Entity entity) {
      this.updateBoundingBox(state);
      super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      this.setBoundingBox(state);
      super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   private void setBoundingBox(BlockState state) {
      float var2 = 0.25F;
      float var3 = 0.375F;
      float var4 = 0.625F;
      float var5 = 0.25F;
      float var6 = 0.75F;
      switch((Direction)state.get(FACING)) {
         case DOWN:
            this.setShape(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F);
            break;
         case UP:
            this.setShape(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F);
            break;
         case NORTH:
            this.setShape(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F);
            break;
         case SOUTH:
            this.setShape(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F);
            break;
         case WEST:
            this.setShape(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F);
            break;
         case EAST:
            this.setShape(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F);
      }
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      this.updateBoundingBox(world.getBlockState(pos));
   }

   public void updateBoundingBox(BlockState state) {
      float var2 = 0.25F;
      Direction var3 = (Direction)state.get(FACING);
      if (var3 != null) {
         switch(var3) {
            case DOWN:
               this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
               break;
            case UP:
               this.setShape(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
               break;
            case NORTH:
               this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
               break;
            case SOUTH:
               this.setShape(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
               break;
            case WEST:
               this.setShape(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
               break;
            case EAST:
               this.setShape(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      Direction var5 = (Direction)state.get(FACING);
      BlockPos var6 = pos.offset(var5.getOpposite());
      BlockState var7 = world.getBlockState(var6);
      if (var7.getBlock() != Blocks.PISTON && var7.getBlock() != Blocks.STICKY_PISTON) {
         world.removeBlock(pos);
      } else {
         var7.getBlock().update(world, var6, var7, neighborBlock);
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean shouldRenderFace(IWorld world, BlockPos pos, Direction face) {
      return true;
   }

   public static Direction getFacingFromMetadata(int metadata) {
      int var1 = metadata & 7;
      return var1 > 5 ? null : Direction.byId(var1);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      return world.getBlockState(pos).get(TYPE) == PistonHeadBlock.Type.STICKY ? Item.byBlock(Blocks.STICKY_PISTON) : Item.byBlock(Blocks.PISTON);
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState()
         .set(FACING, getFacingFromMetadata(metadata))
         .set(TYPE, (metadata & 8) > 0 ? PistonHeadBlock.Type.STICKY : PistonHeadBlock.Type.DEFAULT);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      int var2 = 0;
      var2 |= ((Direction)state.get(FACING)).getId();
      if (state.get(TYPE) == PistonHeadBlock.Type.STICKY) {
         var2 |= 8;
      }

      return var2;
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, FACING, TYPE, SHORT);
   }

   public static enum Type implements StringRepresentable {
      DEFAULT("normal"),
      STICKY("sticky");

      private final String id;

      private Type(String id) {
         this.id = id;
      }

      @Override
      public String toString() {
         return this.id;
      }

      @Override
      public String getStringRepresentation() {
         return this.id;
      }
   }
}
