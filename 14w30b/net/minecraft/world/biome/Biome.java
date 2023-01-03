package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.world.color.FoliageColors;
import net.minecraft.client.world.color.GrassColors;
import net.minecraft.entity.living.mob.MobSpawnGroup;
import net.minecraft.entity.living.mob.SlimeEntity;
import net.minecraft.entity.living.mob.ambient.BatEntity;
import net.minecraft.entity.living.mob.hostile.CreeperEntity;
import net.minecraft.entity.living.mob.hostile.EndermanEntity;
import net.minecraft.entity.living.mob.hostile.SkeletonEntity;
import net.minecraft.entity.living.mob.hostile.SpiderEntity;
import net.minecraft.entity.living.mob.hostile.WitchEntity;
import net.minecraft.entity.living.mob.hostile.ZombieEntity;
import net.minecraft.entity.living.mob.passive.animal.ChickenEntity;
import net.minecraft.entity.living.mob.passive.animal.CowEntity;
import net.minecraft.entity.living.mob.passive.animal.PigEntity;
import net.minecraft.entity.living.mob.passive.animal.RabbitEntity;
import net.minecraft.entity.living.mob.passive.animal.SheepEntity;
import net.minecraft.entity.living.mob.water.SquidEntity;
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateStorage;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.DoublePlantFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.LargeOakTreeFeature;
import net.minecraft.world.gen.feature.SwampTreeFeature;
import net.minecraft.world.gen.feature.TallPlantFeature;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.noise.PerlinNoiseGenerator;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Biome {
   private static final Logger LOGGER = LogManager.getLogger();
   protected static final Biome.Height DEFAULT_HEIGHT = new Biome.Height(0.1F, 0.2F);
   protected static final Biome.Height RIVER_HEIGHT = new Biome.Height(-0.5F, 0.0F);
   protected static final Biome.Height OCEAN_HEIGHT = new Biome.Height(-1.0F, 0.1F);
   protected static final Biome.Height DEEP_OCEAN_HEIGHT = new Biome.Height(-1.8F, 0.1F);
   protected static final Biome.Height PLAINS_HEIGHT = new Biome.Height(0.125F, 0.05F);
   protected static final Biome.Height TAIGA_HEIGHT = new Biome.Height(0.2F, 0.2F);
   protected static final Biome.Height HILLS_HEIGHT = new Biome.Height(0.45F, 0.3F);
   protected static final Biome.Height PLATEAU_HEIGHT = new Biome.Height(1.5F, 0.025F);
   protected static final Biome.Height EXTREME_HILLS_HEIGHT = new Biome.Height(1.0F, 0.5F);
   protected static final Biome.Height MUSHROOM_SHORE_HEIGHT = new Biome.Height(0.0F, 0.025F);
   protected static final Biome.Height STONE_BEACH_HEIGHT = new Biome.Height(0.1F, 0.8F);
   protected static final Biome.Height MUSHROOM_HEIGHT = new Biome.Height(0.2F, 0.3F);
   protected static final Biome.Height SWAMP_HEIGHT = new Biome.Height(-0.2F, 0.1F);
   private static final Biome[] ALL = new Biome[256];
   public static final Set EXPLORABLE = Sets.newHashSet();
   public static final Map BY_NAME = Maps.newHashMap();
   public static final Biome OCEAN = new OceanBiome(0).setColor(112).setName("Ocean").setHeight(OCEAN_HEIGHT);
   public static final Biome PLAINS = new PlainsBiome(1).setColor(9286496).setName("Plains");
   public static final Biome DESERT = new DesertBiome(2)
      .setColor(16421912)
      .setName("Desert")
      .disableRain()
      .setTemperatureAndDownfall(2.0F, 0.0F)
      .setHeight(PLAINS_HEIGHT);
   public static final Biome EXTREME_HILLS = new ExtremeHillsBiome(3, false)
      .setColor(6316128)
      .setName("Extreme Hills")
      .setHeight(EXTREME_HILLS_HEIGHT)
      .setTemperatureAndDownfall(0.2F, 0.3F);
   public static final Biome FOREST = new ForestBiome(4, 0).setColor(353825).setName("Forest");
   public static final Biome TAIGA = new TaigaBiome(5, 0)
      .setColor(747097)
      .setName("Taiga")
      .setMutatedColor(5159473)
      .setTemperatureAndDownfall(0.25F, 0.8F)
      .setHeight(TAIGA_HEIGHT);
   public static final Biome SWAMPLAND = new SwampBiome(6)
      .setColor(522674)
      .setName("Swampland")
      .setMutatedColor(9154376)
      .setHeight(SWAMP_HEIGHT)
      .setTemperatureAndDownfall(0.8F, 0.9F);
   public static final Biome RIVER = new RiverBiome(7).setColor(255).setName("River").setHeight(RIVER_HEIGHT);
   public static final Biome HELL = new HellBiome(8).setColor(16711680).setName("Hell").disableRain().setTemperatureAndDownfall(2.0F, 0.0F);
   public static final Biome THE_END = new TheEndBiome(9).setColor(8421631).setName("The End").disableRain();
   public static final Biome FROZEN_OCEAN = new OceanBiome(10)
      .setColor(9474208)
      .setName("FrozenOcean")
      .setSnowy()
      .setHeight(OCEAN_HEIGHT)
      .setTemperatureAndDownfall(0.0F, 0.5F);
   public static final Biome FROZEN_RIVER = new RiverBiome(11)
      .setColor(10526975)
      .setName("FrozenRiver")
      .setSnowy()
      .setHeight(RIVER_HEIGHT)
      .setTemperatureAndDownfall(0.0F, 0.5F);
   public static final Biome ICE_PLAINS = new IceBiome(12, false)
      .setColor(16777215)
      .setName("Ice Plains")
      .setSnowy()
      .setTemperatureAndDownfall(0.0F, 0.5F)
      .setHeight(PLAINS_HEIGHT);
   public static final Biome ICE_MOUNTAINS = new IceBiome(13, false)
      .setColor(10526880)
      .setName("Ice Mountains")
      .setSnowy()
      .setHeight(HILLS_HEIGHT)
      .setTemperatureAndDownfall(0.0F, 0.5F);
   public static final Biome MUSHROOM_ISLAND = new MushroomBiome(14)
      .setColor(16711935)
      .setName("MushroomIsland")
      .setTemperatureAndDownfall(0.9F, 1.0F)
      .setHeight(MUSHROOM_HEIGHT);
   public static final Biome MUSHROOM_ISLAND_SHORE = new MushroomBiome(15)
      .setColor(10486015)
      .setName("MushroomIslandShore")
      .setTemperatureAndDownfall(0.9F, 1.0F)
      .setHeight(MUSHROOM_SHORE_HEIGHT);
   public static final Biome BEACH = new BeachBiome(16)
      .setColor(16440917)
      .setName("Beach")
      .setTemperatureAndDownfall(0.8F, 0.4F)
      .setHeight(MUSHROOM_SHORE_HEIGHT);
   public static final Biome DESERT_HILLS = new DesertBiome(17)
      .setColor(13786898)
      .setName("DesertHills")
      .disableRain()
      .setTemperatureAndDownfall(2.0F, 0.0F)
      .setHeight(HILLS_HEIGHT);
   public static final Biome FOREST_HILLS = new ForestBiome(18, 0).setColor(2250012).setName("ForestHills").setHeight(HILLS_HEIGHT);
   public static final Biome TAIGA_HILLS = new TaigaBiome(19, 0)
      .setColor(1456435)
      .setName("TaigaHills")
      .setMutatedColor(5159473)
      .setTemperatureAndDownfall(0.25F, 0.8F)
      .setHeight(HILLS_HEIGHT);
   public static final Biome EXTREME_HILLS_EDGE = new ExtremeHillsBiome(20, true)
      .setColor(7501978)
      .setName("Extreme Hills Edge")
      .setHeight(EXTREME_HILLS_HEIGHT.diminish())
      .setTemperatureAndDownfall(0.2F, 0.3F);
   public static final Biome JUNGLE = new JungleBiome(21, false)
      .setColor(5470985)
      .setName("Jungle")
      .setMutatedColor(5470985)
      .setTemperatureAndDownfall(0.95F, 0.9F);
   public static final Biome JUNGLE_HILLS = new JungleBiome(22, false)
      .setColor(2900485)
      .setName("JungleHills")
      .setMutatedColor(5470985)
      .setTemperatureAndDownfall(0.95F, 0.9F)
      .setHeight(HILLS_HEIGHT);
   public static final Biome JUNGLE_EDGE = new JungleBiome(23, true)
      .setColor(6458135)
      .setName("JungleEdge")
      .setMutatedColor(5470985)
      .setTemperatureAndDownfall(0.95F, 0.8F);
   public static final Biome DEEP_OCEAN = new OceanBiome(24).setColor(48).setName("Deep Ocean").setHeight(DEEP_OCEAN_HEIGHT);
   public static final Biome STONE_BEACH = new StoneBeachBiome(25)
      .setColor(10658436)
      .setName("Stone Beach")
      .setTemperatureAndDownfall(0.2F, 0.3F)
      .setHeight(STONE_BEACH_HEIGHT);
   public static final Biome COLD_BEACH = new BeachBiome(26)
      .setColor(16445632)
      .setName("Cold Beach")
      .setTemperatureAndDownfall(0.05F, 0.3F)
      .setHeight(MUSHROOM_SHORE_HEIGHT)
      .setSnowy();
   public static final Biome BIRCH_FOREST = new ForestBiome(27, 2).setName("Birch Forest").setColor(3175492);
   public static final Biome BIRCH_FOREST_HILLS = new ForestBiome(28, 2).setName("Birch Forest Hills").setColor(2055986).setHeight(HILLS_HEIGHT);
   public static final Biome ROOFED_FOREST = new ForestBiome(29, 3).setColor(4215066).setName("Roofed Forest");
   public static final Biome COLD_TAIGA = new TaigaBiome(30, 0)
      .setColor(3233098)
      .setName("Cold Taiga")
      .setMutatedColor(5159473)
      .setSnowy()
      .setTemperatureAndDownfall(-0.5F, 0.4F)
      .setHeight(TAIGA_HEIGHT)
      .setBaseColor(16777215);
   public static final Biome COLD_TAIGA_HILLS = new TaigaBiome(31, 0)
      .setColor(2375478)
      .setName("Cold Taiga Hills")
      .setMutatedColor(5159473)
      .setSnowy()
      .setTemperatureAndDownfall(-0.5F, 0.4F)
      .setHeight(HILLS_HEIGHT)
      .setBaseColor(16777215);
   public static final Biome MEGA_TAIGA = new TaigaBiome(32, 1)
      .setColor(5858897)
      .setName("Mega Taiga")
      .setMutatedColor(5159473)
      .setTemperatureAndDownfall(0.3F, 0.8F)
      .setHeight(TAIGA_HEIGHT);
   public static final Biome MEGA_TAIGA_HILLS = new TaigaBiome(33, 1)
      .setColor(4542270)
      .setName("Mega Taiga Hills")
      .setMutatedColor(5159473)
      .setTemperatureAndDownfall(0.3F, 0.8F)
      .setHeight(HILLS_HEIGHT);
   public static final Biome EXTREME_HILLS_PLUS = new ExtremeHillsBiome(34, true)
      .setColor(5271632)
      .setName("Extreme Hills+")
      .setHeight(EXTREME_HILLS_HEIGHT)
      .setTemperatureAndDownfall(0.2F, 0.3F);
   public static final Biome SAVANNA = new SavannaBiome(35)
      .setColor(12431967)
      .setName("Savanna")
      .setTemperatureAndDownfall(1.2F, 0.0F)
      .disableRain()
      .setHeight(PLAINS_HEIGHT);
   public static final Biome SAVANNA_PLATEAU = new SavannaBiome(36)
      .setColor(10984804)
      .setName("Savanna Plateau")
      .setTemperatureAndDownfall(1.0F, 0.0F)
      .disableRain()
      .setHeight(PLATEAU_HEIGHT);
   public static final Biome MESA = new MesaBiome(37, false, false).setColor(14238997).setName("Mesa");
   public static final Biome MESA_PLATEAU_F = new MesaBiome(38, false, true).setColor(11573093).setName("Mesa Plateau F").setHeight(PLATEAU_HEIGHT);
   public static final Biome MESA_PLATEAU = new MesaBiome(39, false, false).setColor(13274213).setName("Mesa Plateau").setHeight(PLATEAU_HEIGHT);
   public static final Biome DEFAULT = OCEAN;
   protected static final PerlinNoiseGenerator TEMPERATURE_NOISE;
   protected static final PerlinNoiseGenerator FOLIAGE_NOISE;
   protected static final DoublePlantFeature DOUBLE_PLANT;
   public String name;
   public int biomeColor;
   public int baseColor;
   public BlockState surfaceBlock = Blocks.GRASS.defaultState();
   public BlockState subsurfaceBlock = Blocks.DIRT.defaultState();
   public int mutatedColor = 5169201;
   public float baseHeight;
   public float heightVariation;
   public float temperature;
   public float downfall;
   public int waterColor;
   public BiomeDecorator decorator;
   protected List monsterEntries;
   protected List passiveEntries;
   protected List waterEntries;
   protected List caveEntries;
   protected boolean snowy;
   protected boolean hasRain;
   public final int id;
   protected TreeFeature tree;
   protected LargeOakTreeFeature largeTree;
   protected SwampTreeFeature swampTree;

   protected Biome(int id) {
      this.baseHeight = DEFAULT_HEIGHT.baseHeight;
      this.heightVariation = DEFAULT_HEIGHT.heightModifier;
      this.temperature = 0.5F;
      this.downfall = 0.5F;
      this.waterColor = 16777215;
      this.monsterEntries = Lists.newArrayList();
      this.passiveEntries = Lists.newArrayList();
      this.waterEntries = Lists.newArrayList();
      this.caveEntries = Lists.newArrayList();
      this.hasRain = true;
      this.tree = new TreeFeature(false);
      this.largeTree = new LargeOakTreeFeature(false);
      this.swampTree = new SwampTreeFeature();
      this.id = id;
      ALL[id] = this;
      this.decorator = this.createDecorator();
      this.passiveEntries.add(new Biome.SpawnEntry(SheepEntity.class, 12, 4, 4));
      this.passiveEntries.add(new Biome.SpawnEntry(RabbitEntity.class, 10, 3, 3));
      this.passiveEntries.add(new Biome.SpawnEntry(PigEntity.class, 10, 4, 4));
      this.passiveEntries.add(new Biome.SpawnEntry(ChickenEntity.class, 10, 4, 4));
      this.passiveEntries.add(new Biome.SpawnEntry(CowEntity.class, 8, 4, 4));
      this.monsterEntries.add(new Biome.SpawnEntry(SpiderEntity.class, 100, 4, 4));
      this.monsterEntries.add(new Biome.SpawnEntry(ZombieEntity.class, 100, 4, 4));
      this.monsterEntries.add(new Biome.SpawnEntry(SkeletonEntity.class, 100, 4, 4));
      this.monsterEntries.add(new Biome.SpawnEntry(CreeperEntity.class, 100, 4, 4));
      this.monsterEntries.add(new Biome.SpawnEntry(SlimeEntity.class, 100, 4, 4));
      this.monsterEntries.add(new Biome.SpawnEntry(EndermanEntity.class, 10, 1, 4));
      this.monsterEntries.add(new Biome.SpawnEntry(WitchEntity.class, 5, 1, 1));
      this.waterEntries.add(new Biome.SpawnEntry(SquidEntity.class, 10, 4, 4));
      this.caveEntries.add(new Biome.SpawnEntry(BatEntity.class, 10, 8, 8));
   }

   protected BiomeDecorator createDecorator() {
      return new BiomeDecorator();
   }

   protected Biome setTemperatureAndDownfall(float temperature, float downfall) {
      if (temperature > 0.1F && temperature < 0.2F) {
         throw new IllegalArgumentException("Please avoid temperatures in the range 0.1 - 0.2 because of snow");
      } else {
         this.temperature = temperature;
         this.downfall = downfall;
         return this;
      }
   }

   protected final Biome setHeight(Biome.Height height) {
      this.baseHeight = height.baseHeight;
      this.heightVariation = height.heightModifier;
      return this;
   }

   protected Biome disableRain() {
      this.hasRain = false;
      return this;
   }

   public AbstractTreeFeature getRandomTree(Random random) {
      return (AbstractTreeFeature)(random.nextInt(10) == 0 ? this.largeTree : this.tree);
   }

   public Feature getRandomGrass(Random random) {
      return new TallPlantFeature(TallPlantBlock.Type.GRASS);
   }

   public FlowerBlock.Type getRandomFlower(Random random, BlockPos pos) {
      return random.nextInt(3) > 0 ? FlowerBlock.Type.DANDELION : FlowerBlock.Type.POPPY;
   }

   protected Biome setSnowy() {
      this.snowy = true;
      return this;
   }

   protected Biome setName(String name) {
      this.name = name;
      return this;
   }

   protected Biome setMutatedColor(int color) {
      this.mutatedColor = color;
      return this;
   }

   protected Biome setColor(int modifier) {
      this.setColor(modifier, false);
      return this;
   }

   protected Biome setBaseColor(int color) {
      this.baseColor = color;
      return this;
   }

   protected Biome setColor(int color, boolean modifyBaseColor) {
      this.biomeColor = color;
      if (modifyBaseColor) {
         this.baseColor = (color & 16711422) >> 1;
      } else {
         this.baseColor = color;
      }

      return this;
   }

   @Environment(EnvType.CLIENT)
   public int getSkyColor(float temperature) {
      temperature /= 3.0F;
      temperature = MathHelper.clamp(temperature, -1.0F, 1.0F);
      return Color.getHSBColor(0.62222224F - temperature * 0.05F, 0.5F + temperature * 0.1F, 1.0F).getRGB();
   }

   public List getSpawnEntries(MobSpawnGroup spawnGroup) {
      switch(spawnGroup) {
         case MONSTER:
            return this.monsterEntries;
         case CREATURE:
            return this.passiveEntries;
         case WATER_CREATURE:
            return this.waterEntries;
         case AMBIENT:
            return this.caveEntries;
         default:
            return Collections.emptyList();
      }
   }

   public boolean canSnow() {
      return this.isSnowy();
   }

   public boolean canRain() {
      return this.isSnowy() ? false : this.hasRain;
   }

   public boolean isHumid() {
      return this.downfall > 0.85F;
   }

   public float getSpawnChance() {
      return 0.1F;
   }

   public final int getScaledDownfall() {
      return (int)(this.downfall * 65536.0F);
   }

   @Environment(EnvType.CLIENT)
   public final float getDownfall() {
      return this.downfall;
   }

   public final float getTemperature(BlockPos pos) {
      if (pos.getY() > 64) {
         float var2 = (float)(TEMPERATURE_NOISE.getNoise((double)pos.getX() * 1.0 / 8.0, (double)pos.getZ() * 1.0 / 8.0) * 4.0);
         return this.temperature - (var2 + (float)pos.getY() - 64.0F) * 0.05F / 30.0F;
      } else {
         return this.temperature;
      }
   }

   public void decorate(World world, Random random, BlockPos pos) {
      this.decorator.decorate(world, random, this, pos);
   }

   @Environment(EnvType.CLIENT)
   public int getGrassColor(BlockPos pos) {
      double var2 = (double)MathHelper.clamp(this.getTemperature(pos), 0.0F, 1.0F);
      double var4 = (double)MathHelper.clamp(this.getDownfall(), 0.0F, 1.0F);
      return GrassColors.getColor(var2, var4);
   }

   @Environment(EnvType.CLIENT)
   public int getFoliageColor(BlockPos pos) {
      double var2 = (double)MathHelper.clamp(this.getTemperature(pos), 0.0F, 1.0F);
      double var4 = (double)MathHelper.clamp(this.getDownfall(), 0.0F, 1.0F);
      return FoliageColors.get(var2, var4);
   }

   public boolean isSnowy() {
      return this.snowy;
   }

   public void populateChunk(World world, Random random, BlockStateStorage blocks, int x, int z, double noise) {
      this.populate(world, random, blocks, x, z, noise);
   }

   public final void populate(World world, Random random, BlockStateStorage blocks, int x, int z, double noise) {
      boolean var8 = true;
      BlockState var9 = this.surfaceBlock;
      BlockState var10 = this.subsurfaceBlock;
      int var11 = -1;
      int var12 = (int)(noise / 3.0 + 3.0 + random.nextDouble() * 0.25);
      int var13 = x & 15;
      int var14 = z & 15;

      for(int var15 = 255; var15 >= 0; --var15) {
         if (var15 <= random.nextInt(5)) {
            blocks.set(var14, var15, var13, Blocks.BEDROCK.defaultState());
         } else {
            BlockState var16 = blocks.get(var14, var15, var13);
            if (var16.getBlock().getMaterial() == Material.AIR) {
               var11 = -1;
            } else if (var16.getBlock() == Blocks.STONE) {
               if (var11 == -1) {
                  if (var12 <= 0) {
                     var9 = null;
                     var10 = Blocks.STONE.defaultState();
                  } else if (var15 >= 59 && var15 <= 64) {
                     var9 = this.surfaceBlock;
                     var10 = this.subsurfaceBlock;
                  }

                  if (var15 < 63 && (var9 == null || var9.getBlock().getMaterial() == Material.AIR)) {
                     if (this.getTemperature(new BlockPos(x, var15, z)) < 0.15F) {
                        var9 = Blocks.ICE.defaultState();
                     } else {
                        var9 = Blocks.WATER.defaultState();
                     }
                  }

                  var11 = var12;
                  if (var15 >= 62) {
                     blocks.set(var14, var15, var13, var9);
                  } else if (var15 < 56 - var12) {
                     var9 = null;
                     var10 = Blocks.STONE.defaultState();
                     blocks.set(var14, var15, var13, Blocks.GRAVEL.defaultState());
                  } else {
                     blocks.set(var14, var15, var13, var10);
                  }
               } else if (var11 > 0) {
                  --var11;
                  blocks.set(var14, var15, var13, var10);
                  if (var11 == 0 && var10.getBlock() == Blocks.SAND) {
                     var11 = random.nextInt(4) + Math.max(0, var15 - 63);
                     var10 = Blocks.SANDSTONE.defaultState();
                  }
               }
            }
         }
      }
   }

   protected Biome mutate() {
      return this.mutate(this.id + 128);
   }

   protected Biome mutate(int id) {
      return new MutatedBiome(id, this);
   }

   public Class getType() {
      return this.getClass();
   }

   public boolean is(Biome biome) {
      if (biome == this) {
         return true;
      } else if (biome == null) {
         return false;
      } else {
         return this.getType() == biome.getType();
      }
   }

   public Biome.TemperatureCategory getTemperatureCategory() {
      if ((double)this.temperature < 0.2) {
         return Biome.TemperatureCategory.COLD;
      } else {
         return (double)this.temperature < 1.0 ? Biome.TemperatureCategory.MEDIUM : Biome.TemperatureCategory.WARM;
      }
   }

   public static Biome[] getAll() {
      return ALL;
   }

   public static Biome byId(int id) {
      return byIdOrDefault(id, null);
   }

   public static Biome byIdOrDefault(int id, Biome defaultValue) {
      if (id >= 0 && id <= ALL.length) {
         Biome var2 = ALL[id];
         return var2 == null ? defaultValue : var2;
      } else {
         LOGGER.warn("Biome ID is out of bounds: " + id + ", defaulting to 0 (Ocean)");
         return OCEAN;
      }
   }

   static {
      PLAINS.mutate();
      DESERT.mutate();
      FOREST.mutate();
      TAIGA.mutate();
      SWAMPLAND.mutate();
      ICE_PLAINS.mutate();
      JUNGLE.mutate();
      JUNGLE_EDGE.mutate();
      COLD_TAIGA.mutate();
      SAVANNA.mutate();
      SAVANNA_PLATEAU.mutate();
      MESA.mutate();
      MESA_PLATEAU_F.mutate();
      MESA_PLATEAU.mutate();
      BIRCH_FOREST.mutate();
      BIRCH_FOREST_HILLS.mutate();
      ROOFED_FOREST.mutate();
      MEGA_TAIGA.mutate();
      EXTREME_HILLS.mutate();
      EXTREME_HILLS_PLUS.mutate();
      MEGA_TAIGA.mutate(MEGA_TAIGA_HILLS.id + 128).setName("Redwood Taiga Hills M");

      for(Biome var3 : ALL) {
         if (var3 != null) {
            if (BY_NAME.containsKey(var3.name)) {
               throw new Error("Biome \"" + var3.name + "\" is defined as both ID " + ((Biome)BY_NAME.get(var3.name)).id + " and " + var3.id);
            }

            BY_NAME.put(var3.name, var3);
            if (var3.id < 128) {
               EXPLORABLE.add(var3);
            }
         }
      }

      EXPLORABLE.remove(HELL);
      EXPLORABLE.remove(THE_END);
      EXPLORABLE.remove(FROZEN_OCEAN);
      EXPLORABLE.remove(EXTREME_HILLS_EDGE);
      TEMPERATURE_NOISE = new PerlinNoiseGenerator(new Random(1234L), 1);
      FOLIAGE_NOISE = new PerlinNoiseGenerator(new Random(2345L), 1);
      DOUBLE_PLANT = new DoublePlantFeature();
   }

   public static class Height {
      public float baseHeight;
      public float heightModifier;

      public Height(float baseHeight, float heightModifier) {
         this.baseHeight = baseHeight;
         this.heightModifier = heightModifier;
      }

      public Biome.Height diminish() {
         return new Biome.Height(this.baseHeight * 0.8F, this.heightModifier * 0.6F);
      }
   }

   public static class SpawnEntry extends WeightedPicker.Entry {
      public Class type;
      public int minGroupSize;
      public int maxGroupSize;

      public SpawnEntry(Class type, int weight, int minGroupSize, int maxGroupSize) {
         super(weight);
         this.type = type;
         this.minGroupSize = minGroupSize;
         this.maxGroupSize = maxGroupSize;
      }

      @Override
      public String toString() {
         return this.type.getSimpleName() + "*(" + this.minGroupSize + "-" + this.maxGroupSize + "):" + this.weight;
      }
   }

   public static enum TemperatureCategory {
      OCEAN,
      COLD,
      MEDIUM,
      WARM;
   }
}
