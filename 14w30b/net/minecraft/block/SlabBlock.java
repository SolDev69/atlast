package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.block.state.property.Property;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class SlabBlock extends Block {
   public static final EnumProperty HALF = EnumProperty.of("half", SlabBlock.Half.class);

   public SlabBlock(Material c_57ywipuwq) {
      super(c_57ywipuwq);
      if (this.isDouble()) {
         this.opaqueCube = true;
      } else {
         this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      }

      this.setOpacity(255);
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      if (this.isDouble()) {
         this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else {
         BlockState var3 = world.getBlockState(pos);
         if (var3.getBlock() == this) {
            if (var3.get(HALF) == SlabBlock.Half.TOP) {
               this.setShape(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
               this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
            }
         }
      }
   }

   @Override
   public void setBlockItemBounds() {
      if (this.isDouble()) {
         this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else {
         this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      }
   }

   @Override
   public void getCollisionBoxes(World world, BlockPos pos, BlockState state, Box entityBox, List boxes, Entity entity) {
      this.updateShape(world, pos);
      super.getCollisionBoxes(world, pos, state, entityBox, boxes, entity);
   }

   @Override
   public boolean isOpaqueCube() {
      return this.isDouble();
   }

   @Override
   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      BlockState var9 = super.getPlacementState(world, pos, dir, dx, dy, dz, metadata, entity).set(HALF, SlabBlock.Half.BOTTOM);
      if (this.isDouble()) {
         return var9;
      } else {
         return dir != Direction.DOWN && (dir == Direction.UP || !((double)dy > 0.5)) ? var9 : var9.set(HALF, SlabBlock.Half.TOP);
      }
   }

   @Override
   public int getBaseDropCount(Random random) {
      return this.isDouble() ? 2 : 1;
   }

   @Override
   public boolean isFullCube() {
      return this.isDouble();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean shouldRenderFace(IWorld world, BlockPos pos, Direction face) {
      if (this.isDouble()) {
         return super.shouldRenderFace(world, pos, face);
      } else if (face != Direction.UP && face != Direction.DOWN && !super.shouldRenderFace(world, pos, face)) {
         return false;
      } else {
         BlockPos var4 = pos.offset(face.getOpposite());
         BlockState var5 = world.getBlockState(pos);
         BlockState var6 = world.getBlockState(var4);
         boolean var7 = isSingleSlab(var5.getBlock()) && var5.get(HALF) == SlabBlock.Half.TOP;
         boolean var8 = isSingleSlab(var6.getBlock()) && var6.get(HALF) == SlabBlock.Half.TOP;
         if (var8) {
            if (face == Direction.DOWN) {
               return true;
            } else if (face == Direction.UP && super.shouldRenderFace(world, pos, face)) {
               return true;
            } else {
               return !isSingleSlab(var5.getBlock()) || !var7;
            }
         } else if (face == Direction.UP) {
            return true;
         } else if (face == Direction.DOWN && super.shouldRenderFace(world, pos, face)) {
            return true;
         } else {
            return !isSingleSlab(var5.getBlock()) || var7;
         }
      }
   }

   @Environment(EnvType.CLIENT)
   private static boolean isSingleSlab(Block block) {
      return block == Blocks.STONE_SLAB || block == Blocks.WOODEN_SLAB;
   }

   public abstract String getName(int variant);

   @Override
   public int getPickItemMetadata(World world, BlockPos pos) {
      return super.getPickItemMetadata(world, pos) & 7;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      if (isSingleSlab(this)) {
         return Item.byBlock(this);
      } else if (this == Blocks.DOUBLE_STONE_SLAB) {
         return Item.byBlock(Blocks.STONE_SLAB);
      } else {
         return this == Blocks.DOUBLE_WOODEN_SLAB ? Item.byBlock(Blocks.WOODEN_SLAB) : Item.byBlock(Blocks.STONE_SLAB);
      }
   }

   public abstract boolean isDouble();

   public abstract Property getVariantProperty();

   public abstract Object getVariant(ItemStack stack);

   public static enum Half implements StringRepresentable {
      TOP("top"),
      BOTTOM("bottom");

      private final String id;

      private Half(String id) {
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
