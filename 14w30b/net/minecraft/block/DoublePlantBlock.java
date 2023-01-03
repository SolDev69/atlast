package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.client.world.color.BiomeColors;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class DoublePlantBlock extends PlantBlock implements Fertilizable {
   public static final EnumProperty VARIANT = EnumProperty.of("variant", DoublePlantBlock.Variant.class);
   public static final EnumProperty HALF = EnumProperty.of("half", DoublePlantBlock.Half.class);

   public DoublePlantBlock() {
      super(Material.REPLACEABLE_PLANT);
      this.setDefaultState(this.stateDefinition.any().set(VARIANT, DoublePlantBlock.Variant.SUNFLOWER).set(HALF, DoublePlantBlock.Half.LOWER));
      this.setStrength(0.0F);
      this.setSound(GRASS_SOUND);
      this.setId("doublePlant");
   }

   @Override
   public void updateShape(IWorld world, BlockPos pos) {
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   public DoublePlantBlock.Variant getVariant(IWorld world, BlockPos pos) {
      BlockState var3 = world.getBlockState(pos);
      var3 = this.updateShape(var3, world, pos);
      return (DoublePlantBlock.Variant)var3.get(VARIANT);
   }

   @Override
   public boolean canSurvive(World world, BlockPos pos) {
      return super.canSurvive(world, pos) && world.isAir(pos.up());
   }

   @Override
   public boolean canBeReplaced(World world, BlockPos pos) {
      return true;
   }

   @Override
   protected void canSurviveOrBreak(World world, BlockPos pos, BlockState state) {
      if (!this.isSupported(world, pos, state)) {
         boolean var4 = state.get(HALF) == DoublePlantBlock.Half.UPPER;
         BlockPos var5 = var4 ? pos : pos.up();
         BlockPos var6 = var4 ? pos.down() : pos;
         Object var7 = var4 ? this : world.getBlockState(var5).getBlock();
         Object var8 = var4 ? world.getBlockState(var6).getBlock() : this;
         if (var7 == this) {
            world.setBlockState(var5, Blocks.AIR.defaultState(), 3);
         }

         if (var8 == this) {
            world.setBlockState(var6, Blocks.AIR.defaultState(), 3);
            if (!var4) {
               this.dropItems(world, var6, state, 0);
            }
         }
      }
   }

   @Override
   public boolean isSupported(World world, BlockPos pos, BlockState state) {
      if (state.get(HALF) == DoublePlantBlock.Half.UPPER) {
         return world.getBlockState(pos.down()).getBlock() == this;
      } else {
         BlockState var4 = world.getBlockState(pos.up());
         return var4.getBlock() == this && super.isSupported(world, pos, var4);
      }
   }

   @Override
   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      if (state.get(HALF) == DoublePlantBlock.Half.UPPER) {
         return null;
      } else {
         DoublePlantBlock.Variant var4 = (DoublePlantBlock.Variant)state.get(VARIANT);
         if (var4 == DoublePlantBlock.Variant.FERN) {
            return null;
         } else if (var4 == DoublePlantBlock.Variant.GRASS) {
            return random.nextInt(8) == 0 ? Items.WHEAT_SEEDS : null;
         } else {
            return Item.byBlock(this);
         }
      }
   }

   @Override
   public int getDropItemMetadata(BlockState state) {
      return state.get(HALF) != DoublePlantBlock.Half.UPPER && state.get(VARIANT) != DoublePlantBlock.Variant.GRASS
         ? ((DoublePlantBlock.Variant)state.get(VARIANT)).getIndex()
         : 0;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getColor(IWorld world, BlockPos pos, int tint) {
      DoublePlantBlock.Variant var4 = this.getVariant(world, pos);
      return var4 != DoublePlantBlock.Variant.GRASS && var4 != DoublePlantBlock.Variant.FERN ? 16777215 : BiomeColors.getGrassColor(world, pos);
   }

   public void setVariant(World world, BlockPos pos, DoublePlantBlock.Variant variant, int flags) {
      world.setBlockState(pos, this.defaultState().set(HALF, DoublePlantBlock.Half.LOWER).set(VARIANT, variant), flags);
      world.setBlockState(pos.up(), this.defaultState().set(HALF, DoublePlantBlock.Half.UPPER), flags);
   }

   @Override
   public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
      world.setBlockState(pos.up(), this.defaultState().set(HALF, DoublePlantBlock.Half.UPPER), 2);
   }

   @Override
   public void afterMinedByPlayer(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
      if (world.isClient
         || player.getMainHandStack() == null
         || player.getMainHandStack().getItem() != Items.SHEARS
         || state.get(HALF) != DoublePlantBlock.Half.LOWER
         || !this.onMinedByPlayer(world, pos, state, player)) {
         super.afterMinedByPlayer(world, player, pos, state, blockEntity);
      }
   }

   @Override
   public void beforeMinedByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      if (state.get(HALF) == DoublePlantBlock.Half.UPPER) {
         if (world.getBlockState(pos.down()).getBlock() == this) {
            if (!player.abilities.creativeMode) {
               BlockState var5 = world.getBlockState(pos.down());
               DoublePlantBlock.Variant var6 = (DoublePlantBlock.Variant)var5.get(VARIANT);
               if (var6 != DoublePlantBlock.Variant.FERN && var6 != DoublePlantBlock.Variant.GRASS) {
                  world.breakBlock(pos.down(), true);
               } else {
                  if (!world.isClient && player.getMainHandStack() != null && player.getMainHandStack().getItem() == Items.SHEARS) {
                     this.onMinedByPlayer(world, pos, var5, player);
                  }

                  world.removeBlock(pos.down());
               }
            } else {
               world.removeBlock(pos.down());
            }
         }
      } else if (player.abilities.creativeMode && world.getBlockState(pos.up()).getBlock() == this) {
         world.setBlockState(pos.up(), Blocks.AIR.defaultState(), 2);
      }

      super.beforeMinedByPlayer(world, pos, state, player);
   }

   private boolean onMinedByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      DoublePlantBlock.Variant var5 = (DoublePlantBlock.Variant)state.get(VARIANT);
      if (var5 != DoublePlantBlock.Variant.FERN && var5 != DoublePlantBlock.Variant.GRASS) {
         return false;
      } else {
         player.incrementStat(Stats.BLOCKS_MINED[Block.getRawId(this)]);
         this.dropItems(world, pos, new ItemStack(Blocks.TALLGRASS, 2, var5.getIndex()));
         return true;
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      for(DoublePlantBlock.Variant var7 : DoublePlantBlock.Variant.values()) {
         stacks.add(new ItemStack(item, 1, var7.getIndex()));
      }
   }

   @Override
   public int getPickItemMetadata(World world, BlockPos pos) {
      return this.getVariant(world, pos).getIndex();
   }

   @Override
   public boolean canGrow(World world, BlockPos pos, BlockState state, boolean bl) {
      DoublePlantBlock.Variant var5 = this.getVariant(world, pos);
      return var5 != DoublePlantBlock.Variant.GRASS && var5 != DoublePlantBlock.Variant.FERN;
   }

   @Override
   public boolean canBeFertilized(World world, Random rand, BlockPos pos, BlockState state) {
      return true;
   }

   @Override
   public void grow(World world, Random rand, BlockPos pos, BlockState state) {
      this.dropItems(world, pos, new ItemStack(this, 1, this.getVariant(world, pos).getIndex()));
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return (metadata & 8) > 0
         ? this.defaultState().set(HALF, DoublePlantBlock.Half.UPPER)
         : this.defaultState().set(HALF, DoublePlantBlock.Half.LOWER).set(VARIANT, DoublePlantBlock.Variant.byIndex(metadata & 7));
   }

   @Override
   public BlockState updateShape(BlockState state, IWorld world, BlockPos pos) {
      if (state.get(HALF) == DoublePlantBlock.Half.UPPER) {
         BlockState var4 = world.getBlockState(pos.down());
         if (var4.getBlock() == this) {
            state = state.set(VARIANT, var4.get(VARIANT));
         }
      }

      return state;
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return state.get(HALF) == DoublePlantBlock.Half.UPPER ? 8 : ((DoublePlantBlock.Variant)state.get(VARIANT)).getIndex();
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, HALF, VARIANT);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Block.OffsetType getOffsetType() {
      return Block.OffsetType.XZ;
   }

   public static enum Half implements StringRepresentable {
      UPPER,
      LOWER;

      @Override
      public String toString() {
         return this.getStringRepresentation();
      }

      @Override
      public String getStringRepresentation() {
         return this == UPPER ? "upper" : "lower";
      }
   }

   public static enum Variant implements StringRepresentable {
      SUNFLOWER(0, "sunflower"),
      SYRINGA(1, "syringa"),
      GRASS(2, "double_grass", "grass"),
      FERN(3, "double_fern", "fern"),
      ROSE(4, "double_rose", "rose"),
      PAEONIA(5, "paeonia");

      private static final DoublePlantBlock.Variant[] ALL = new DoublePlantBlock.Variant[values().length];
      private final int index;
      private final String id;
      private final String name;

      private Variant(int index, String id) {
         this(index, id, id);
      }

      private Variant(int index, String id, String name) {
         this.index = index;
         this.id = id;
         this.name = name;
      }

      public int getIndex() {
         return this.index;
      }

      @Override
      public String toString() {
         return this.id;
      }

      public static DoublePlantBlock.Variant byIndex(int index) {
         if (index < 0 || index >= ALL.length) {
            index = 0;
         }

         return ALL[index];
      }

      @Override
      public String getStringRepresentation() {
         return this.id;
      }

      public String getName() {
         return this.name;
      }

      static {
         for(DoublePlantBlock.Variant var3 : values()) {
            ALL[var3.getIndex()] = var3;
         }
      }
   }
}
