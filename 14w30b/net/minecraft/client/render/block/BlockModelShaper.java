package net.minecraft.client.render.block;

import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.AbstractLeavesBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.ColoredBlock;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.DirtBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.DropperBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.InfestedBlock;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.Leaves2Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Log2Block;
import net.minecraft.block.LogBlock;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.PrismarineBlock;
import net.minecraft.block.QuartzBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.SandBlock;
import net.minecraft.block.SandstoneBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.StoneBlock;
import net.minecraft.block.StoneSlabBlock;
import net.minecraft.block.StonebrickBlock;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.block.TripwireBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.resource.ModelIdentifier;
import net.minecraft.client.resource.model.AbstractBlockModelProvider;
import net.minecraft.client.resource.model.BakedModel;
import net.minecraft.client.resource.model.BlockModelProvider;
import net.minecraft.client.resource.model.BlockModels;
import net.minecraft.client.resource.model.ModelManager;
import net.minecraft.client.resource.model.VariantBlockModelProvider;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.Direction;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockModelShaper {
   private final Map modelCache = Maps.newIdentityHashMap();
   private final BlockModels models = new BlockModels();
   private final ModelManager manager;

   public BlockModelShaper(ModelManager manager) {
      this.manager = manager;
      this.init();
   }

   public BlockModels getModels() {
      return this.models;
   }

   public TextureAtlasSprite getParticleIcon(BlockState state) {
      Block var2 = state.getBlock();
      BakedModel var3 = this.getModel(state);
      if (var3 == null || var3 == this.manager.getMissingModel()) {
         if (var2 == Blocks.WALL_SIGN
            || var2 == Blocks.STANDING_SIGN
            || var2 == Blocks.CHEST
            || var2 == Blocks.TRAPPED_CHEST
            || var2 == Blocks.STANDING_BANNER
            || var2 == Blocks.WALL_BANNER) {
            return this.manager.getBlocksSprite().getSprite("minecraft:blocks/planks_oak");
         }

         if (var2 == Blocks.ENDER_CHEST) {
            return this.manager.getBlocksSprite().getSprite("minecraft:blocks/obsidian");
         }

         if (var2 == Blocks.FLOWING_LAVA || var2 == Blocks.LAVA) {
            return this.manager.getBlocksSprite().getSprite("minecraft:blocks/lava_still");
         }

         if (var2 == Blocks.FLOWING_WATER || var2 == Blocks.WATER) {
            return this.manager.getBlocksSprite().getSprite("minecraft:blocks/water_still");
         }

         if (var2 == Blocks.SKULL) {
            return this.manager.getBlocksSprite().getSprite("minecraft:blocks/soul_sand");
         }

         if (var2 == Blocks.BARRIER) {
            return this.manager.getBlocksSprite().getSprite("minecraft:items/barrier");
         }
      }

      if (var3 == null) {
         var3 = this.manager.getMissingModel();
      }

      return var3.getParticleIcon();
   }

   public BakedModel getModel(BlockState state) {
      BakedModel var2 = (BakedModel)this.modelCache.get(state);
      if (var2 == null) {
         var2 = this.manager.getMissingModel();
      }

      return var2;
   }

   public ModelManager getManager() {
      return this.manager;
   }

   public void rebuildCache() {
      this.modelCache.clear();

      for(Entry var2 : this.models.provide().entrySet()) {
         this.modelCache.put(var2.getKey(), this.manager.getModel((ModelIdentifier)var2.getValue()));
      }
   }

   public void addProvider(Block block, BlockModelProvider provider) {
      this.models.addProvider(block, provider);
   }

   public void addCustom(Block... blocks) {
      this.models.addCustom(blocks);
   }

   private void init() {
      this.addCustom(
         Blocks.AIR,
         Blocks.FLOWING_WATER,
         Blocks.WATER,
         Blocks.FLOWING_LAVA,
         Blocks.LAVA,
         Blocks.MOVING_BLOCK,
         Blocks.CHEST,
         Blocks.ENDER_CHEST,
         Blocks.TRAPPED_CHEST,
         Blocks.STANDING_SIGN,
         Blocks.SKULL,
         Blocks.END_PORTAL,
         Blocks.BARRIER,
         Blocks.WALL_SIGN,
         Blocks.WALL_BANNER,
         Blocks.STANDING_BANNER
      );
      this.addProvider(Blocks.STONE, new VariantBlockModelProvider.Builder().setProperty(StoneBlock.VARIANT).build());
      this.addProvider(Blocks.PRISMARINE, new VariantBlockModelProvider.Builder().setProperty(PrismarineBlock.VARIANT).build());
      this.addProvider(
         Blocks.LEAVES,
         new VariantBlockModelProvider.Builder()
            .setProperty(LeavesBlock.VARIANT)
            .setVariant("_leaves")
            .setUnusedProperties(AbstractLeavesBlock.CHECK_DECAY, AbstractLeavesBlock.DECAYABLE)
            .build()
      );
      this.addProvider(
         Blocks.LEAVES2,
         new VariantBlockModelProvider.Builder()
            .setProperty(Leaves2Block.VARIANT)
            .setVariant("_leaves")
            .setUnusedProperties(AbstractLeavesBlock.CHECK_DECAY, AbstractLeavesBlock.DECAYABLE)
            .build()
      );
      this.addProvider(Blocks.CACTUS, new VariantBlockModelProvider.Builder().setUnusedProperties(CactusBlock.AGE).build());
      this.addProvider(Blocks.REEDS, new VariantBlockModelProvider.Builder().setUnusedProperties(SugarCaneBlock.AGE).build());
      this.addProvider(Blocks.JUKEBOX, new VariantBlockModelProvider.Builder().setUnusedProperties(JukeboxBlock.HAS_RECORD).build());
      this.addProvider(Blocks.COMMAND_BLOCK, new VariantBlockModelProvider.Builder().setUnusedProperties(CommandBlock.TRIGGERED).build());
      this.addProvider(Blocks.COBBLESTONE_WALL, new VariantBlockModelProvider.Builder().setProperty(WallBlock.VARIANT).setVariant("_wall").build());
      this.addProvider(Blocks.DOUBLE_PLANT, new VariantBlockModelProvider.Builder().setProperty(DoublePlantBlock.VARIANT).build());
      this.addProvider(Blocks.FENCE_GATE, new VariantBlockModelProvider.Builder().setUnusedProperties(FenceGateBlock.POWERED).build());
      this.addProvider(Blocks.TRIPWIRE, new VariantBlockModelProvider.Builder().setUnusedProperties(TripwireBlock.DISARMED, TripwireBlock.POWERED).build());
      this.addProvider(Blocks.DOUBLE_WOODEN_SLAB, new VariantBlockModelProvider.Builder().setProperty(PlanksBlock.VARIANT).setVariant("_double_slab").build());
      this.addProvider(Blocks.WOODEN_SLAB, new VariantBlockModelProvider.Builder().setProperty(PlanksBlock.VARIANT).setVariant("_slab").build());
      this.addProvider(Blocks.TNT, new VariantBlockModelProvider.Builder().setUnusedProperties(TntBlock.EXPLODE).build());
      this.addProvider(Blocks.FIRE, new VariantBlockModelProvider.Builder().setUnusedProperties(FireBlock.AGE).build());
      this.addProvider(Blocks.REDSTONE_WIRE, new VariantBlockModelProvider.Builder().setUnusedProperties(RedstoneWireBlock.POWER).build());
      this.addProvider(Blocks.WOODEN_DOOR, new VariantBlockModelProvider.Builder().setUnusedProperties(DoorBlock.POWERED).build());
      this.addProvider(Blocks.IRON_DOOR, new VariantBlockModelProvider.Builder().setUnusedProperties(DoorBlock.POWERED).build());
      this.addProvider(Blocks.WOOL, new VariantBlockModelProvider.Builder().setProperty(ColoredBlock.COLOR).setVariant("_wool").build());
      this.addProvider(Blocks.CARPET, new VariantBlockModelProvider.Builder().setProperty(ColoredBlock.COLOR).setVariant("_carpet").build());
      this.addProvider(
         Blocks.STAINED_HARDENED_CLAY, new VariantBlockModelProvider.Builder().setProperty(ColoredBlock.COLOR).setVariant("_stained_hardened_clay").build()
      );
      this.addProvider(
         Blocks.STAINED_GLASS_PANE, new VariantBlockModelProvider.Builder().setProperty(ColoredBlock.COLOR).setVariant("_stained_glass_pane").build()
      );
      this.addProvider(Blocks.STAINED_GLASS, new VariantBlockModelProvider.Builder().setProperty(ColoredBlock.COLOR).setVariant("_stained_glass").build());
      this.addProvider(Blocks.SANDSTONE, new VariantBlockModelProvider.Builder().setProperty(SandstoneBlock.TYPE).build());
      this.addProvider(Blocks.TALLGRASS, new VariantBlockModelProvider.Builder().setProperty(TallPlantBlock.TYPE).build());
      this.addProvider(Blocks.BED, new VariantBlockModelProvider.Builder().setUnusedProperties(BedBlock.OCCUPIED).build());
      this.addProvider(Blocks.YELLOW_FLOWER, new VariantBlockModelProvider.Builder().setProperty(Blocks.YELLOW_FLOWER.getTypeProperty()).build());
      this.addProvider(Blocks.RED_FLOWER, new VariantBlockModelProvider.Builder().setProperty(Blocks.RED_FLOWER.getTypeProperty()).build());
      this.addProvider(Blocks.STONE_SLAB, new VariantBlockModelProvider.Builder().setProperty(StoneSlabBlock.VARIANT).setVariant("_slab").build());
      this.addProvider(Blocks.MONSTER_EGG, new VariantBlockModelProvider.Builder().setProperty(InfestedBlock.VARIANT).setVariant("_monster_egg").build());
      this.addProvider(Blocks.STONE_BRICKS, new VariantBlockModelProvider.Builder().setProperty(StonebrickBlock.VARIANT).build());
      this.addProvider(Blocks.DISPENSER, new VariantBlockModelProvider.Builder().setUnusedProperties(DispenserBlock.TRIGGERED).build());
      this.addProvider(Blocks.DROPPER, new VariantBlockModelProvider.Builder().setUnusedProperties(DropperBlock.TRIGGERED).build());
      this.addProvider(Blocks.LOG, new VariantBlockModelProvider.Builder().setProperty(LogBlock.VARIANT).setVariant("_log").build());
      this.addProvider(Blocks.LOG2, new VariantBlockModelProvider.Builder().setProperty(Log2Block.VARIANT).setVariant("_log").build());
      this.addProvider(Blocks.PLANKS, new VariantBlockModelProvider.Builder().setProperty(PlanksBlock.VARIANT).setVariant("_planks").build());
      this.addProvider(Blocks.SAPLING, new VariantBlockModelProvider.Builder().setProperty(SaplingBlock.TYPE).setVariant("_sapling").build());
      this.addProvider(Blocks.SAND, new VariantBlockModelProvider.Builder().setProperty(SandBlock.VARIANT).build());
      this.addProvider(Blocks.HOPPER, new VariantBlockModelProvider.Builder().setUnusedProperties(HopperBlock.ENABLED).build());
      this.addProvider(Blocks.FLOWER_POT, new VariantBlockModelProvider.Builder().setUnusedProperties(FlowerPotBlock.LEGACY_DATA).build());
      this.addProvider(Blocks.QUARTZ_BLOCK, new AbstractBlockModelProvider() {
         @Override
         protected ModelIdentifier provide(BlockState state) {
            QuartzBlock.Variant var2 = (QuartzBlock.Variant)state.get(QuartzBlock.VARIANT);
            switch(var2) {
               case DEFAULT:
               default:
                  return new ModelIdentifier("quartz_block", "normal");
               case CHISELED:
                  return new ModelIdentifier("chiseled_quartz_block", "normal");
               case LINES_Y:
                  return new ModelIdentifier("quartz_column", "axis=y");
               case LINES_X:
                  return new ModelIdentifier("quartz_column", "axis=x");
               case LINES_Z:
                  return new ModelIdentifier("quartz_column", "axis=z");
            }
         }
      });
      this.addProvider(Blocks.DEADBUSH, new AbstractBlockModelProvider() {
         @Override
         protected ModelIdentifier provide(BlockState state) {
            return new ModelIdentifier("dead_bush", "normal");
         }
      });
      this.addProvider(Blocks.PUMPKIN_STEM, new AbstractBlockModelProvider() {
         @Override
         protected ModelIdentifier provide(BlockState state) {
            LinkedHashMap var2 = Maps.newLinkedHashMap(state.values());
            if (state.get(StemBlock.FACING) != Direction.UP) {
               var2.remove(StemBlock.AGE);
            }

            return new ModelIdentifier((Identifier)Block.REGISTRY.getKey(state.getBlock()), this.propertiesAsString(var2));
         }
      });
      this.addProvider(Blocks.MELON_STEM, new AbstractBlockModelProvider() {
         @Override
         protected ModelIdentifier provide(BlockState state) {
            LinkedHashMap var2 = Maps.newLinkedHashMap(state.values());
            if (state.get(StemBlock.FACING) != Direction.UP) {
               var2.remove(StemBlock.AGE);
            }

            return new ModelIdentifier((Identifier)Block.REGISTRY.getKey(state.getBlock()), this.propertiesAsString(var2));
         }
      });
      this.addProvider(Blocks.DIRT, new AbstractBlockModelProvider() {
         @Override
         protected ModelIdentifier provide(BlockState state) {
            LinkedHashMap var2 = Maps.newLinkedHashMap(state.values());
            String var3 = DirtBlock.VARIANT.getName((Comparable)var2.remove(DirtBlock.VARIANT));
            if (DirtBlock.Variant.PODZOL != state.get(DirtBlock.VARIANT)) {
               var2.remove(DirtBlock.SNOWY);
            }

            return new ModelIdentifier(var3, this.propertiesAsString(var2));
         }
      });
      this.addProvider(Blocks.DOUBLE_STONE_SLAB, new AbstractBlockModelProvider() {
         @Override
         protected ModelIdentifier provide(BlockState state) {
            LinkedHashMap var2 = Maps.newLinkedHashMap(state.values());
            String var3 = StoneSlabBlock.VARIANT.getName((Comparable)var2.remove(StoneSlabBlock.VARIANT));
            var2.remove(StoneSlabBlock.SEAMLESS);
            String var4 = state.get(StoneSlabBlock.SEAMLESS) ? "all" : "normal";
            return new ModelIdentifier(var3 + "_double_slab", var4);
         }
      });
   }
}
