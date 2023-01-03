package net.minecraft.world.dimension;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeSource;
import net.minecraft.world.biome.SingleBiomeSource;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.gen.FlatWorldGenerator;
import net.minecraft.world.gen.WorldGeneratorType;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.OverworldChunkGenerator;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class Dimension {
   public static final float[] MOON_PHASE_TO_SIZE = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
   protected World world;
   private WorldGeneratorType generatorType;
   private String generatorOptions;
   protected BiomeSource biomeSource;
   protected boolean yeetsWater;
   protected boolean isDark;
   protected final float[] brightnessTable = new float[16];
   protected int id;
   private final float[] backgroundColor = new float[4];

   public final void init(World world) {
      this.world = world;
      this.generatorType = world.getData().getGeneratorType();
      this.generatorOptions = world.getData().getGeneratorOptions();
      this.createBiomeSource();
      this.populateBrightnessTable();
   }

   protected void populateBrightnessTable() {
      float var1 = 0.0F;

      for(int var2 = 0; var2 <= 15; ++var2) {
         float var3 = 1.0F - (float)var2 / 15.0F;
         this.brightnessTable[var2] = (1.0F - var3) / (var3 * 3.0F + 1.0F) * (1.0F - var1) + var1;
      }
   }

   protected void createBiomeSource() {
      WorldGeneratorType var1 = this.world.getData().getGeneratorType();
      if (var1 == WorldGeneratorType.FLAT) {
         FlatWorldGenerator var2 = FlatWorldGenerator.of(this.world.getData().getGeneratorOptions());
         this.biomeSource = new SingleBiomeSource(Biome.byIdOrDefault(var2.getBiomeId(), Biome.DEFAULT), 0.5F);
      } else if (var1 == WorldGeneratorType.DEBUG_ALL_BLOCK_STATES) {
         this.biomeSource = new SingleBiomeSource(Biome.PLAINS, 0.0F);
      } else {
         this.biomeSource = new BiomeSource(this.world);
      }
   }

   public ChunkSource createChunkGenerator() {
      if (this.generatorType == WorldGeneratorType.FLAT) {
         return new FlatChunkGenerator(this.world, this.world.getSeed(), this.world.getData().allowStructures(), this.generatorOptions);
      } else if (this.generatorType == WorldGeneratorType.DEBUG_ALL_BLOCK_STATES) {
         return new DebugChunkGenerator(this.world);
      } else {
         return this.generatorType == WorldGeneratorType.CUSTOMIZED
            ? new OverworldChunkGenerator(this.world, this.world.getSeed(), this.world.getData().allowStructures(), this.generatorOptions)
            : new OverworldChunkGenerator(this.world, this.world.getSeed(), this.world.getData().allowStructures(), this.generatorOptions);
      }
   }

   public boolean isValidSpawnPos(int x, int z) {
      return this.world.getSurfaceBlock(new BlockPos(x, 0, z)) == Blocks.GRASS;
   }

   public float getTimeOfDay(long time, float tickDelta) {
      int var4 = (int)(time % 24000L);
      float var5 = ((float)var4 + tickDelta) / 24000.0F - 0.25F;
      if (var5 < 0.0F) {
         ++var5;
      }

      if (var5 > 1.0F) {
         --var5;
      }

      float var7 = 1.0F - (float)((Math.cos((double)var5 * Math.PI) + 1.0) / 2.0);
      return var5 + (var7 - var5) / 3.0F;
   }

   public int getMoonPhase(long time) {
      return (int)(time / 24000L % 8L + 8L) % 8;
   }

   public boolean isOverworld() {
      return true;
   }

   @Environment(EnvType.CLIENT)
   public float[] getBackgroundColor(float timeOfDay, float tickDelta) {
      float var3 = 0.4F;
      float var4 = MathHelper.cos(timeOfDay * (float) Math.PI * 2.0F) - 0.0F;
      float var5 = -0.0F;
      if (var4 >= var5 - var3 && var4 <= var5 + var3) {
         float var6 = (var4 - var5) / var3 * 0.5F + 0.5F;
         float var7 = 1.0F - (1.0F - MathHelper.sin(var6 * (float) Math.PI)) * 0.99F;
         var7 *= var7;
         this.backgroundColor[0] = var6 * 0.3F + 0.7F;
         this.backgroundColor[1] = var6 * var6 * 0.7F + 0.2F;
         this.backgroundColor[2] = var6 * var6 * 0.0F + 0.2F;
         this.backgroundColor[3] = var7;
         return this.backgroundColor;
      } else {
         return null;
      }
   }

   @Environment(EnvType.CLIENT)
   public Vec3d getFogColor(float timeOfDay, float tickDelta) {
      float var3 = MathHelper.cos(timeOfDay * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
      var3 = MathHelper.clamp(var3, 0.0F, 1.0F);
      float var4 = 0.7529412F;
      float var5 = 0.84705883F;
      float var6 = 1.0F;
      var4 *= var3 * 0.94F + 0.06F;
      var5 *= var3 * 0.94F + 0.06F;
      var6 *= var3 * 0.91F + 0.09F;
      return new Vec3d((double)var4, (double)var5, (double)var6);
   }

   public boolean hasWorldSpawn() {
      return true;
   }

   public static Dimension fromId(int id) {
      if (id == -1) {
         return new NetherDimension();
      } else if (id == 0) {
         return new OverworldDimension();
      } else {
         return id == 1 ? new TheEndDimension() : null;
      }
   }

   @Environment(EnvType.CLIENT)
   public float getCloudHeight() {
      return 128.0F;
   }

   @Environment(EnvType.CLIENT)
   public boolean hasGround() {
      return true;
   }

   public BlockPos getForcedSpawnPoint() {
      return null;
   }

   public int getMinSpawnY() {
      return this.generatorType == WorldGeneratorType.FLAT ? 4 : 64;
   }

   @Environment(EnvType.CLIENT)
   public boolean doesWaterVaporize() {
      return this.generatorType != WorldGeneratorType.FLAT && !this.isDark;
   }

   @Environment(EnvType.CLIENT)
   public double getFogSize() {
      return this.generatorType == WorldGeneratorType.FLAT ? 1.0 : 0.03125;
   }

   @Environment(EnvType.CLIENT)
   public boolean isFogThick(int x, int z) {
      return false;
   }

   public abstract String getName();

   public abstract String getDataSuffix();

   public BiomeSource getBiomeSource() {
      return this.biomeSource;
   }

   public boolean yeetsWater() {
      return this.yeetsWater;
   }

   public boolean isDark() {
      return this.isDark;
   }

   public float[] getBrightnessTable() {
      return this.brightnessTable;
   }

   public int getId() {
      return this.id;
   }

   public WorldBorder getWorldBorder() {
      return new WorldBorder();
   }
}
