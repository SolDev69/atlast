package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FlowerPotBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.block.state.property.IntegerProperty;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class FlowerPotBlock extends BlockWithBlockEntity {
   public static final IntegerProperty LEGACY_DATA = IntegerProperty.of("legacy_data", 0, 15);
   public static final EnumProperty CONTENTS = EnumProperty.of("contents", FlowerPotBlock.Contents.class);

   public FlowerPotBlock() {
      super(Material.DECORATION);
      this.setDefaultState(this.stateDefinition.any().set(CONTENTS, FlowerPotBlock.Contents.EMPTY).set(LEGACY_DATA, 0));
      this.setBlockItemBounds();
   }

   @Override
   public void setBlockItemBounds() {
      float var1 = 0.375F;
      float var2 = var1 / 2.0F;
      this.setShape(0.5F - var2, 0.0F, 0.5F - var2, 0.5F + var2, var1, 0.5F + var2);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor(IWorld world, BlockPos pos, int tint) {
      BlockEntity var4 = world.getBlockEntity(pos);
      if (var4 instanceof FlowerPotBlockEntity) {
         Item var5 = ((FlowerPotBlockEntity)var4).getPlant();
         if (var5 instanceof BlockItem) {
            return Block.byItem(var5).getColor(world, pos, tint);
         }
      }

      return 16777215;
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      ItemStack var9 = player.inventory.getMainHandStack();
      if (var9 != null && var9.getItem() instanceof BlockItem) {
         FlowerPotBlockEntity var10 = this.getFlowerPotBlockEntity(world, pos);
         if (var10 == null) {
            return false;
         } else if (var10.getPlant() != null) {
            return false;
         } else {
            Block var11 = Block.byItem(var9.getItem());
            if (!this.isPottablePlant(var11, var9.getMetadata())) {
               return false;
            } else {
               var10.setPlant(var9.getItem(), var9.getMetadata());
               var10.markDirty();
               world.onBlockChanged(pos);
               if (!player.abilities.creativeMode && --var9.size <= 0) {
                  player.inventory.setStack(player.inventory.selectedSlot, null);
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   private boolean isPottablePlant(Block block, int metadata) {
      if (block == Blocks.YELLOW_FLOWER
         || block == Blocks.RED_FLOWER
         || block == Blocks.CACTUS
         || block == Blocks.BROWN_MUSHROOM
         || block == Blocks.RED_MUSHROOM
         || block == Blocks.SAPLING
         || block == Blocks.DEADBUSH) {
         return true;
      } else {
         return block == Blocks.TALLGRASS && metadata == TallPlantBlock.Type.FERN.getIndex();
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Item getPickItem(World world, BlockPos pos) {
      FlowerPotBlockEntity var3 = this.getFlowerPotBlockEntity(world, pos);
      return var3 != null && var3.getPlant() != null ? var3.getPlant() : Items.FLOWER_POT;
   }

   @Override
   public int getPickItemMetadata(World world, BlockPos pos) {
      FlowerPotBlockEntity var3 = this.getFlowerPotBlockEntity(world, pos);
      return var3 != null && var3.getPlant() != null ? var3.getMetadata() : 0;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean hasPickItemMetadata() {
      return true;
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      return super.canSurvive(world, pos) && World.hasSolidTop(world, pos.down());
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      if (!World.hasSolidTop(world, pos.down())) {
         this.dropItems(world, pos, state, 0);
         world.removeBlock(pos);
      }
   }

   @Override
   public void onRemoved(World world, BlockPos pos, BlockState state) {
      FlowerPotBlockEntity var4 = this.getFlowerPotBlockEntity(world, pos);
      if (var4 != null && var4.getPlant() != null) {
         this.dropItems(world, pos, new ItemStack(var4.getPlant(), 1, var4.getMetadata()));
      }

      super.onRemoved(world, pos, state);
   }

   @Override
   public void beforeMinedByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      super.beforeMinedByPlayer(world, pos, state, player);
      if (player.abilities.creativeMode) {
         FlowerPotBlockEntity var5 = this.getFlowerPotBlockEntity(world, pos);
         if (var5 != null) {
            var5.setPlant(Item.byRawId(0), 0);
         }
      }
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Items.FLOWER_POT;
   }

   private FlowerPotBlockEntity getFlowerPotBlockEntity(World world, BlockPos x) {
      BlockEntity var3 = world.getBlockEntity(x);
      return var3 instanceof FlowerPotBlockEntity ? (FlowerPotBlockEntity)var3 : null;
   }

   @Override
   public BlockEntity createBlockEntity(World world, int metadata) {
      Object var3 = null;
      int var4 = 0;
      switch(metadata) {
         case 1:
            var3 = Blocks.RED_FLOWER;
            var4 = FlowerBlock.Type.POPPY.getIndex();
            break;
         case 2:
            var3 = Blocks.YELLOW_FLOWER;
            break;
         case 3:
            var3 = Blocks.SAPLING;
            var4 = PlanksBlock.Variant.OAK.getIndex();
            break;
         case 4:
            var3 = Blocks.SAPLING;
            var4 = PlanksBlock.Variant.SPRUCE.getIndex();
            break;
         case 5:
            var3 = Blocks.SAPLING;
            var4 = PlanksBlock.Variant.BIRCH.getIndex();
            break;
         case 6:
            var3 = Blocks.SAPLING;
            var4 = PlanksBlock.Variant.JUNGLE.getIndex();
            break;
         case 7:
            var3 = Blocks.RED_MUSHROOM;
            break;
         case 8:
            var3 = Blocks.BROWN_MUSHROOM;
            break;
         case 9:
            var3 = Blocks.CACTUS;
            break;
         case 10:
            var3 = Blocks.DEADBUSH;
            break;
         case 11:
            var3 = Blocks.TALLGRASS;
            var4 = TallPlantBlock.Type.FERN.getIndex();
            break;
         case 12:
            var3 = Blocks.SAPLING;
            var4 = PlanksBlock.Variant.ACACIA.getIndex();
            break;
         case 13:
            var3 = Blocks.SAPLING;
            var4 = PlanksBlock.Variant.DARK_OAK.getIndex();
      }

      return new FlowerPotBlockEntity(Item.byBlock((Block)var3), var4);
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, CONTENTS, LEGACY_DATA);
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return state.get(LEGACY_DATA);
   }

   @Override
   public BlockState updateShape(BlockState state, IWorld world, BlockPos pos) {
      FlowerPotBlock.Contents var4 = FlowerPotBlock.Contents.EMPTY;
      BlockEntity var5 = world.getBlockEntity(pos);
      if (var5 instanceof FlowerPotBlockEntity) {
         FlowerPotBlockEntity var6 = (FlowerPotBlockEntity)var5;
         Item var7 = var6.getPlant();
         if (var7 instanceof BlockItem) {
            int var8 = var6.getMetadata();
            Block var9 = Block.byItem(var7);
            if (var9 == Blocks.SAPLING) {
               switch(PlanksBlock.Variant.byIndex(var8)) {
                  case OAK:
                     var4 = FlowerPotBlock.Contents.OAK_SAPLING;
                     break;
                  case SPRUCE:
                     var4 = FlowerPotBlock.Contents.SPRUCE_SAPLING;
                     break;
                  case BIRCH:
                     var4 = FlowerPotBlock.Contents.BIRCH_SAPLING;
                     break;
                  case JUNGLE:
                     var4 = FlowerPotBlock.Contents.JUNGLE_SAPLING;
                     break;
                  case ACACIA:
                     var4 = FlowerPotBlock.Contents.ACACIA_SAPLING;
                     break;
                  case DARK_OAK:
                     var4 = FlowerPotBlock.Contents.DARK_OAK_SAPLING;
                     break;
                  default:
                     var4 = FlowerPotBlock.Contents.EMPTY;
               }
            } else if (var9 == Blocks.TALLGRASS) {
               switch(var8) {
                  case 0:
                     var4 = FlowerPotBlock.Contents.DEAD_BUSH;
                     break;
                  case 2:
                     var4 = FlowerPotBlock.Contents.FERN;
                     break;
                  default:
                     var4 = FlowerPotBlock.Contents.EMPTY;
               }
            } else if (var9 == Blocks.YELLOW_FLOWER) {
               var4 = FlowerPotBlock.Contents.DANDELION;
            } else if (var9 == Blocks.RED_FLOWER) {
               switch(FlowerBlock.Type.byIndex(FlowerBlock.Group.RED, var8)) {
                  case POPPY:
                     var4 = FlowerPotBlock.Contents.POPPY;
                     break;
                  case BLUE_ORCHID:
                     var4 = FlowerPotBlock.Contents.BLUE_ORCHID;
                     break;
                  case ALLIUM:
                     var4 = FlowerPotBlock.Contents.ALLIUM;
                     break;
                  case HOUSTONIA:
                     var4 = FlowerPotBlock.Contents.HOUSTONIA;
                     break;
                  case RED_TULIP:
                     var4 = FlowerPotBlock.Contents.RED_TULIP;
                     break;
                  case ORANGE_TULIP:
                     var4 = FlowerPotBlock.Contents.ORANGE_TULIP;
                     break;
                  case WHITE_TULIP:
                     var4 = FlowerPotBlock.Contents.WHITE_TULIP;
                     break;
                  case PINK_TULIP:
                     var4 = FlowerPotBlock.Contents.PINK_TULIP;
                     break;
                  case OXEY_DAISY:
                     var4 = FlowerPotBlock.Contents.OXEYE_DAISY;
                     break;
                  default:
                     var4 = FlowerPotBlock.Contents.EMPTY;
               }
            } else if (var9 == Blocks.RED_MUSHROOM) {
               var4 = FlowerPotBlock.Contents.MUSHROOM_RED;
            } else if (var9 == Blocks.BROWN_MUSHROOM) {
               var4 = FlowerPotBlock.Contents.MUSHROOM_BROWN;
            } else if (var9 == Blocks.DEADBUSH) {
               var4 = FlowerPotBlock.Contents.DEAD_BUSH;
            } else if (var9 == Blocks.CACTUS) {
               var4 = FlowerPotBlock.Contents.CACTUS;
            }
         }
      }

      return state.set(CONTENTS, var4);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   public static enum Contents implements StringRepresentable {
      EMPTY("empty"),
      POPPY("rose"),
      BLUE_ORCHID("blue_orchid"),
      ALLIUM("allium"),
      HOUSTONIA("houstonia"),
      RED_TULIP("red_tulip"),
      ORANGE_TULIP("orange_tulip"),
      WHITE_TULIP("white_tulip"),
      PINK_TULIP("pink_tulip"),
      OXEYE_DAISY("oxeye_daisy"),
      DANDELION("dandelion"),
      OAK_SAPLING("oak_sapling"),
      SPRUCE_SAPLING("spruce_sapling"),
      BIRCH_SAPLING("birch_sapling"),
      JUNGLE_SAPLING("jungle_sapling"),
      ACACIA_SAPLING("acacia_sapling"),
      DARK_OAK_SAPLING("dark_oak_sapling"),
      MUSHROOM_RED("mushroom_red"),
      MUSHROOM_BROWN("mushroom_brown"),
      DEAD_BUSH("dead_bush"),
      FERN("fern"),
      CACTUS("cactus");

      private final String id;

      private Contents(String id) {
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
