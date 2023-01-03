package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.entity.living.mob.hostile.SliverfishEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class InfestedBlock extends Block {
   public static final EnumProperty VARIANT = EnumProperty.of("variant", InfestedBlock.Variant.class);

   public InfestedBlock() {
      super(Material.CLAY);
      this.setDefaultState(this.stateDefinition.any().set(VARIANT, InfestedBlock.Variant.STONE));
      this.setStrength(0.0F);
      this.setItemGroup(ItemGroup.DECORATIONS);
   }

   @Override
   public int getBaseDropCount(Random random) {
      return 0;
   }

   public static boolean canBeInfested(BlockState state) {
      Block var1 = state.getBlock();
      return state == Blocks.STONE.defaultState().set(StoneBlock.VARIANT, StoneBlock.Variant.STONE)
         || var1 == Blocks.COBBLESTONE
         || var1 == Blocks.STONE_BRICKS;
   }

   @Override
   protected ItemStack getSilkTouchDrop(BlockState state) {
      switch((InfestedBlock.Variant)state.get(VARIANT)) {
         case COBBLESTONE:
            return new ItemStack(Blocks.COBBLESTONE);
         case STONEBRICK:
            return new ItemStack(Blocks.STONE_BRICKS);
         case MOSSY_STONEBRICK:
            return new ItemStack(Blocks.STONE_BRICKS, 1, StonebrickBlock.Variant.MOSSY.getIndex());
         case CRACKED_STONEBRICK:
            return new ItemStack(Blocks.STONE_BRICKS, 1, StonebrickBlock.Variant.CRACKED.getIndex());
         case CHISELED_STONEBRICK:
            return new ItemStack(Blocks.STONE_BRICKS, 1, StonebrickBlock.Variant.CHISELED.getIndex());
         default:
            return new ItemStack(Blocks.STONE);
      }
   }

   @Override
   public void dropItems(World world, BlockPos pos, BlockState state, float luck, int fortuneLevel) {
      if (!world.isClient && world.getGameRules().getBoolean("doTileDrops")) {
         SliverfishEntity var6 = new SliverfishEntity(world);
         var6.refreshPositionAndAngles((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, 0.0F, 0.0F);
         world.addEntity(var6);
         var6.doSpawnEffects();
      }
   }

   @Override
   public int getPickItemMetadata(World world, BlockPos pos) {
      BlockState var3 = world.getBlockState(pos);
      return var3.getBlock().getMetadataFromState(var3);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      for(InfestedBlock.Variant var7 : InfestedBlock.Variant.values()) {
         stacks.add(new ItemStack(item, 1, var7.getIndex()));
      }
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(VARIANT, InfestedBlock.Variant.byIndex(metadata));
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return ((InfestedBlock.Variant)state.get(VARIANT)).getIndex();
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, VARIANT);
   }

   public static enum Variant implements StringRepresentable {
      STONE(0, "stone") {
         @Override
         public BlockState getHostState() {
            return Blocks.STONE.defaultState().set(StoneBlock.VARIANT, StoneBlock.Variant.STONE);
         }
      },
      COBBLESTONE(1, "cobblestone", "cobble") {
         @Override
         public BlockState getHostState() {
            return Blocks.COBBLESTONE.defaultState();
         }
      },
      STONEBRICK(2, "stone_brick", "brick") {
         @Override
         public BlockState getHostState() {
            return Blocks.STONE_BRICKS.defaultState().set(StonebrickBlock.VARIANT, StonebrickBlock.Variant.DEFAULT);
         }
      },
      MOSSY_STONEBRICK(3, "mossy_brick", "mossybrick") {
         @Override
         public BlockState getHostState() {
            return Blocks.STONE_BRICKS.defaultState().set(StonebrickBlock.VARIANT, StonebrickBlock.Variant.MOSSY);
         }
      },
      CRACKED_STONEBRICK(4, "cracked_brick", "crackedbrick") {
         @Override
         public BlockState getHostState() {
            return Blocks.STONE_BRICKS.defaultState().set(StonebrickBlock.VARIANT, StonebrickBlock.Variant.CRACKED);
         }
      },
      CHISELED_STONEBRICK(5, "chiseled_brick", "chiseledbrick") {
         @Override
         public BlockState getHostState() {
            return Blocks.STONE_BRICKS.defaultState().set(StonebrickBlock.VARIANT, StonebrickBlock.Variant.CHISELED);
         }
      };

      private static final InfestedBlock.Variant[] ALL = new InfestedBlock.Variant[values().length];
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

      public static InfestedBlock.Variant byIndex(int index) {
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

      public abstract BlockState getHostState();

      public static InfestedBlock.Variant byHostState(BlockState state) {
         for(InfestedBlock.Variant var4 : values()) {
            if (state == var4.getHostState()) {
               return var4;
            }
         }

         return STONE;
      }

      static {
         for(InfestedBlock.Variant var3 : values()) {
            ALL[var3.getIndex()] = var3;
         }
      }
   }
}
