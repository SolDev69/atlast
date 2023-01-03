package net.minecraft.world.gen.chunk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import net.minecraft.world.biome.Biome;

public class GeneratorOptions {
   public final float coordinateScale;
   public final float heightScale;
   public final float upperLimitScale;
   public final float lowerLimitScale;
   public final float depthNoiseScaleX;
   public final float depthNoiseScaleZ;
   public final float depthNoiseScaleExponent;
   public final float mainNoiseScaleX;
   public final float mainNoiseScaleY;
   public final float mainNoiseScaleZ;
   public final float baseSize;
   public final float stretchY;
   public final float biomeDepthWeight;
   public final float biomeDepthOffset;
   public final float biomeScaleWeight;
   public final float biomeScaleOffset;
   public final int seaLevel;
   public final boolean useCaves;
   public final boolean useDungeons;
   public final int dungeonChance;
   public final boolean useStrongholds;
   public final boolean useVillages;
   public final boolean useMineshafts;
   public final boolean useTemples;
   public final boolean useMonuments;
   public final boolean useRavines;
   public final boolean useWaterLakes;
   public final int waterLakeChance;
   public final boolean useLavaLakes;
   public final int lavaLakeChance;
   public final boolean useLavaOceans;
   public final int fixedBiome;
   public final int biomeSize;
   public final int riverSize;
   public final int dirtSize;
   public final int dirtCount;
   public final int dirtMinHeight;
   public final int dirtMaxHeight;
   public final int gravelSize;
   public final int gravelCount;
   public final int gravelMinHeight;
   public final int gravelMaxHeight;
   public final int graniteSize;
   public final int graniteCount;
   public final int graniteMinHeight;
   public final int graniteMaxHeight;
   public final int dioriteSize;
   public final int dioriteCount;
   public final int dioriteMinHeight;
   public final int dioriteMaxHeight;
   public final int andesiteSize;
   public final int andesiteCount;
   public final int andesiteMinHeight;
   public final int andesiteMaxHeight;
   public final int coalSize;
   public final int coalCount;
   public final int coalMinHeight;
   public final int coalMaxHeight;
   public final int ironSize;
   public final int ironCount;
   public final int ironMinHeight;
   public final int ironMaxHeight;
   public final int goldSize;
   public final int goldCount;
   public final int goldMinHeight;
   public final int goldMaxHeight;
   public final int redstoneSize;
   public final int redstoneCount;
   public final int redstoneMinHeight;
   public final int redstoneMaxHeight;
   public final int diamondSize;
   public final int diamondCount;
   public final int diamondMinHeight;
   public final int diamondMaxHeight;
   public final int lapisSize;
   public final int lapisCount;
   public final int lapisMinHeight;
   public final int lapisMaxHeight;

   private GeneratorOptions(GeneratorOptions.Factory factory) {
      this.coordinateScale = factory.coordinateScale;
      this.heightScale = factory.heightScale;
      this.upperLimitScale = factory.upperLimitScale;
      this.lowerLimitScale = factory.lowerLimitScale;
      this.depthNoiseScaleX = factory.depthNoisescaleX;
      this.depthNoiseScaleZ = factory.depthNoiseScaleZ;
      this.depthNoiseScaleExponent = factory.depthNoiseScaleExponent;
      this.mainNoiseScaleX = factory.mainNoiseScaleX;
      this.mainNoiseScaleY = factory.mainNoiseScaleY;
      this.mainNoiseScaleZ = factory.mainNoiseScaleZ;
      this.baseSize = factory.baseSize;
      this.stretchY = factory.stretchY;
      this.biomeDepthWeight = factory.biomeDepthWeight;
      this.biomeDepthOffset = factory.biomeDepthOffset;
      this.biomeScaleWeight = factory.biomeScaleWeight;
      this.biomeScaleOffset = factory.biomeScaleOffset;
      this.seaLevel = factory.seaLevel;
      this.useCaves = factory.useCaves;
      this.useDungeons = factory.useDungeons;
      this.dungeonChance = factory.dungeonChance;
      this.useStrongholds = factory.useStrongholds;
      this.useVillages = factory.useVillages;
      this.useMineshafts = factory.useMineshafts;
      this.useTemples = factory.useTemples;
      this.useMonuments = factory.useMonuments;
      this.useRavines = factory.useRavines;
      this.useWaterLakes = factory.useWaterLakes;
      this.waterLakeChance = factory.waterLakeChance;
      this.useLavaLakes = factory.useLavaLakes;
      this.lavaLakeChance = factory.lavaLakeChance;
      this.useLavaOceans = factory.useLavaOceans;
      this.fixedBiome = factory.fixedBiome;
      this.biomeSize = factory.biomeSize;
      this.riverSize = factory.riverSize;
      this.dirtSize = factory.dirtSize;
      this.dirtCount = factory.dirtCount;
      this.dirtMinHeight = factory.dirtMinHeight;
      this.dirtMaxHeight = factory.dirtMaxHeight;
      this.gravelSize = factory.gravelSize;
      this.gravelCount = factory.gravelCount;
      this.gravelMinHeight = factory.gravelMinHeight;
      this.gravelMaxHeight = factory.gravelMaxHeight;
      this.graniteSize = factory.graniteSize;
      this.graniteCount = factory.graniteCount;
      this.graniteMinHeight = factory.graniteMinHeight;
      this.graniteMaxHeight = factory.graniteMaxHeight;
      this.dioriteSize = factory.dioriteSize;
      this.dioriteCount = factory.dioriteCount;
      this.dioriteMinHeight = factory.dioriteMinHeight;
      this.dioriteMaxHeight = factory.dioriteMaxHeight;
      this.andesiteSize = factory.andesiteSize;
      this.andesiteCount = factory.andesiteCount;
      this.andesiteMinHeight = factory.andesiteMinHeight;
      this.andesiteMaxHeight = factory.andesiteMaxHeight;
      this.coalSize = factory.coalSize;
      this.coalCount = factory.coalCount;
      this.coalMinHeight = factory.coalMinHeight;
      this.coalMaxHeight = factory.coalMaxHeight;
      this.ironSize = factory.ironSize;
      this.ironCount = factory.ironCount;
      this.ironMinHeight = factory.ironMinHeight;
      this.ironMaxHeight = factory.ironMaxHeight;
      this.goldSize = factory.goldSize;
      this.goldCount = factory.goldCount;
      this.goldMinHeight = factory.goldMinHeight;
      this.goldMaxHeight = factory.goldMaxHeight;
      this.redstoneSize = factory.redstoneSize;
      this.redstoneCount = factory.redstoneCount;
      this.redstoneMinHeight = factory.redstoneMinHeight;
      this.redstoneMaxHeight = factory.redstoneMaxHeight;
      this.diamondSize = factory.diamondSize;
      this.diamondCount = factory.diamondCount;
      this.diamondMinHeight = factory.diamondMinHeight;
      this.diamondMaxHeight = factory.diamondMaxHeight;
      this.lapisSize = factory.lapisSize;
      this.lapisCount = factory.lapisCount;
      this.lapisMinHeight = factory.lapisMinHeight;
      this.lapisMaxHeight = factory.lapisMaxHeight;
   }

   public static class Factory {
      static final Gson GSON = new GsonBuilder().registerTypeAdapter(GeneratorOptions.Factory.class, new GeneratorOptions.Serializer()).create();
      public float coordinateScale = 684.412F;
      public float heightScale = 684.412F;
      public float upperLimitScale = 512.0F;
      public float lowerLimitScale = 512.0F;
      public float depthNoisescaleX = 200.0F;
      public float depthNoiseScaleZ = 200.0F;
      public float depthNoiseScaleExponent = 0.5F;
      public float mainNoiseScaleX = 80.0F;
      public float mainNoiseScaleY = 160.0F;
      public float mainNoiseScaleZ = 80.0F;
      public float baseSize = 8.5F;
      public float stretchY = 12.0F;
      public float biomeDepthWeight = 1.0F;
      public float biomeDepthOffset = 0.0F;
      public float biomeScaleWeight = 1.0F;
      public float biomeScaleOffset = 0.0F;
      public int seaLevel = 63;
      public boolean useCaves = true;
      public boolean useDungeons = true;
      public int dungeonChance = 8;
      public boolean useStrongholds = true;
      public boolean useVillages = true;
      public boolean useMineshafts = true;
      public boolean useTemples = true;
      public boolean useMonuments = true;
      public boolean useRavines = true;
      public boolean useWaterLakes = true;
      public int waterLakeChance = 4;
      public boolean useLavaLakes = true;
      public int lavaLakeChance = 80;
      public boolean useLavaOceans = false;
      public int fixedBiome = -1;
      public int biomeSize = 4;
      public int riverSize = 4;
      public int dirtSize = 33;
      public int dirtCount = 10;
      public int dirtMinHeight = 0;
      public int dirtMaxHeight = 256;
      public int gravelSize = 33;
      public int gravelCount = 8;
      public int gravelMinHeight = 0;
      public int gravelMaxHeight = 256;
      public int graniteSize = 33;
      public int graniteCount = 10;
      public int graniteMinHeight = 0;
      public int graniteMaxHeight = 80;
      public int dioriteSize = 33;
      public int dioriteCount = 10;
      public int dioriteMinHeight = 0;
      public int dioriteMaxHeight = 80;
      public int andesiteSize = 33;
      public int andesiteCount = 10;
      public int andesiteMinHeight = 0;
      public int andesiteMaxHeight = 80;
      public int coalSize = 17;
      public int coalCount = 20;
      public int coalMinHeight = 0;
      public int coalMaxHeight = 128;
      public int ironSize = 9;
      public int ironCount = 20;
      public int ironMinHeight = 0;
      public int ironMaxHeight = 64;
      public int goldSize = 9;
      public int goldCount = 2;
      public int goldMinHeight = 0;
      public int goldMaxHeight = 32;
      public int redstoneSize = 8;
      public int redstoneCount = 8;
      public int redstoneMinHeight = 0;
      public int redstoneMaxHeight = 16;
      public int diamondSize = 8;
      public int diamondCount = 1;
      public int diamondMinHeight = 0;
      public int diamondMaxHeight = 16;
      public int lapisSize = 7;
      public int lapisCount = 1;
      public int lapisMinHeight = 16;
      public int lapisMaxHeight = 16;

      public static GeneratorOptions.Factory fromJson(String json) {
         if (json.length() == 0) {
            return new GeneratorOptions.Factory();
         } else {
            try {
               return (GeneratorOptions.Factory)GSON.fromJson(json, GeneratorOptions.Factory.class);
            } catch (Exception var2) {
               return new GeneratorOptions.Factory();
            }
         }
      }

      @Override
      public String toString() {
         return GSON.toJson(this);
      }

      public Factory() {
         this.reset();
      }

      public void reset() {
         this.coordinateScale = 684.412F;
         this.heightScale = 684.412F;
         this.upperLimitScale = 512.0F;
         this.lowerLimitScale = 512.0F;
         this.depthNoisescaleX = 200.0F;
         this.depthNoiseScaleZ = 200.0F;
         this.depthNoiseScaleExponent = 0.5F;
         this.mainNoiseScaleX = 80.0F;
         this.mainNoiseScaleY = 160.0F;
         this.mainNoiseScaleZ = 80.0F;
         this.baseSize = 8.5F;
         this.stretchY = 12.0F;
         this.biomeDepthWeight = 1.0F;
         this.biomeDepthOffset = 0.0F;
         this.biomeScaleWeight = 1.0F;
         this.biomeScaleOffset = 0.0F;
         this.seaLevel = 63;
         this.useCaves = true;
         this.useDungeons = true;
         this.dungeonChance = 8;
         this.useStrongholds = true;
         this.useVillages = true;
         this.useMineshafts = true;
         this.useTemples = true;
         this.useMonuments = true;
         this.useRavines = true;
         this.useWaterLakes = true;
         this.waterLakeChance = 4;
         this.useLavaLakes = true;
         this.lavaLakeChance = 80;
         this.useLavaOceans = false;
         this.fixedBiome = -1;
         this.biomeSize = 4;
         this.riverSize = 4;
         this.dirtSize = 33;
         this.dirtCount = 10;
         this.dirtMinHeight = 0;
         this.dirtMaxHeight = 256;
         this.gravelSize = 33;
         this.gravelCount = 8;
         this.gravelMinHeight = 0;
         this.gravelMaxHeight = 256;
         this.graniteSize = 33;
         this.graniteCount = 10;
         this.graniteMinHeight = 0;
         this.graniteMaxHeight = 80;
         this.dioriteSize = 33;
         this.dioriteCount = 10;
         this.dioriteMinHeight = 0;
         this.dioriteMaxHeight = 80;
         this.andesiteSize = 33;
         this.andesiteCount = 10;
         this.andesiteMinHeight = 0;
         this.andesiteMaxHeight = 80;
         this.coalSize = 17;
         this.coalCount = 20;
         this.coalMinHeight = 0;
         this.coalMaxHeight = 128;
         this.ironSize = 9;
         this.ironCount = 20;
         this.ironMinHeight = 0;
         this.ironMaxHeight = 64;
         this.goldSize = 9;
         this.goldCount = 2;
         this.goldMinHeight = 0;
         this.goldMaxHeight = 32;
         this.redstoneSize = 8;
         this.redstoneCount = 8;
         this.redstoneMinHeight = 0;
         this.redstoneMaxHeight = 16;
         this.diamondSize = 8;
         this.diamondCount = 1;
         this.diamondMinHeight = 0;
         this.diamondMaxHeight = 16;
         this.lapisSize = 7;
         this.lapisCount = 1;
         this.lapisMinHeight = 16;
         this.lapisMaxHeight = 16;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj != null && this.getClass() == obj.getClass()) {
            GeneratorOptions.Factory var2 = (GeneratorOptions.Factory)obj;
            if (this.andesiteCount != var2.andesiteCount) {
               return false;
            } else if (this.andesiteMaxHeight != var2.andesiteMaxHeight) {
               return false;
            } else if (this.andesiteMinHeight != var2.andesiteMinHeight) {
               return false;
            } else if (this.andesiteSize != var2.andesiteSize) {
               return false;
            } else if (Float.compare(var2.baseSize, this.baseSize) != 0) {
               return false;
            } else if (Float.compare(var2.biomeDepthOffset, this.biomeDepthOffset) != 0) {
               return false;
            } else if (Float.compare(var2.biomeDepthWeight, this.biomeDepthWeight) != 0) {
               return false;
            } else if (Float.compare(var2.biomeScaleOffset, this.biomeScaleOffset) != 0) {
               return false;
            } else if (Float.compare(var2.biomeScaleWeight, this.biomeScaleWeight) != 0) {
               return false;
            } else if (this.biomeSize != var2.biomeSize) {
               return false;
            } else if (this.coalCount != var2.coalCount) {
               return false;
            } else if (this.coalMaxHeight != var2.coalMaxHeight) {
               return false;
            } else if (this.coalMinHeight != var2.coalMinHeight) {
               return false;
            } else if (this.coalSize != var2.coalSize) {
               return false;
            } else if (Float.compare(var2.coordinateScale, this.coordinateScale) != 0) {
               return false;
            } else if (Float.compare(var2.depthNoiseScaleExponent, this.depthNoiseScaleExponent) != 0) {
               return false;
            } else if (Float.compare(var2.depthNoisescaleX, this.depthNoisescaleX) != 0) {
               return false;
            } else if (Float.compare(var2.depthNoiseScaleZ, this.depthNoiseScaleZ) != 0) {
               return false;
            } else if (this.diamondCount != var2.diamondCount) {
               return false;
            } else if (this.diamondMaxHeight != var2.diamondMaxHeight) {
               return false;
            } else if (this.diamondMinHeight != var2.diamondMinHeight) {
               return false;
            } else if (this.diamondSize != var2.diamondSize) {
               return false;
            } else if (this.dioriteCount != var2.dioriteCount) {
               return false;
            } else if (this.dioriteMaxHeight != var2.dioriteMaxHeight) {
               return false;
            } else if (this.dioriteMinHeight != var2.dioriteMinHeight) {
               return false;
            } else if (this.dioriteSize != var2.dioriteSize) {
               return false;
            } else if (this.dirtCount != var2.dirtCount) {
               return false;
            } else if (this.dirtMaxHeight != var2.dirtMaxHeight) {
               return false;
            } else if (this.dirtMinHeight != var2.dirtMinHeight) {
               return false;
            } else if (this.dirtSize != var2.dirtSize) {
               return false;
            } else if (this.dungeonChance != var2.dungeonChance) {
               return false;
            } else if (this.fixedBiome != var2.fixedBiome) {
               return false;
            } else if (this.goldCount != var2.goldCount) {
               return false;
            } else if (this.goldMaxHeight != var2.goldMaxHeight) {
               return false;
            } else if (this.goldMinHeight != var2.goldMinHeight) {
               return false;
            } else if (this.goldSize != var2.goldSize) {
               return false;
            } else if (this.graniteCount != var2.graniteCount) {
               return false;
            } else if (this.graniteMaxHeight != var2.graniteMaxHeight) {
               return false;
            } else if (this.graniteMinHeight != var2.graniteMinHeight) {
               return false;
            } else if (this.graniteSize != var2.graniteSize) {
               return false;
            } else if (this.gravelCount != var2.gravelCount) {
               return false;
            } else if (this.gravelMaxHeight != var2.gravelMaxHeight) {
               return false;
            } else if (this.gravelMinHeight != var2.gravelMinHeight) {
               return false;
            } else if (this.gravelSize != var2.gravelSize) {
               return false;
            } else if (Float.compare(var2.heightScale, this.heightScale) != 0) {
               return false;
            } else if (this.ironCount != var2.ironCount) {
               return false;
            } else if (this.ironMaxHeight != var2.ironMaxHeight) {
               return false;
            } else if (this.ironMinHeight != var2.ironMinHeight) {
               return false;
            } else if (this.ironSize != var2.ironSize) {
               return false;
            } else if (this.lapisMinHeight != var2.lapisMinHeight) {
               return false;
            } else if (this.lapisCount != var2.lapisCount) {
               return false;
            } else if (this.lapisSize != var2.lapisSize) {
               return false;
            } else if (this.lapisMaxHeight != var2.lapisMaxHeight) {
               return false;
            } else if (this.lavaLakeChance != var2.lavaLakeChance) {
               return false;
            } else if (Float.compare(var2.lowerLimitScale, this.lowerLimitScale) != 0) {
               return false;
            } else if (Float.compare(var2.mainNoiseScaleX, this.mainNoiseScaleX) != 0) {
               return false;
            } else if (Float.compare(var2.mainNoiseScaleY, this.mainNoiseScaleY) != 0) {
               return false;
            } else if (Float.compare(var2.mainNoiseScaleZ, this.mainNoiseScaleZ) != 0) {
               return false;
            } else if (this.redstoneCount != var2.redstoneCount) {
               return false;
            } else if (this.redstoneMaxHeight != var2.redstoneMaxHeight) {
               return false;
            } else if (this.redstoneMinHeight != var2.redstoneMinHeight) {
               return false;
            } else if (this.redstoneSize != var2.redstoneSize) {
               return false;
            } else if (this.riverSize != var2.riverSize) {
               return false;
            } else if (this.seaLevel != var2.seaLevel) {
               return false;
            } else if (Float.compare(var2.stretchY, this.stretchY) != 0) {
               return false;
            } else if (Float.compare(var2.upperLimitScale, this.upperLimitScale) != 0) {
               return false;
            } else if (this.useCaves != var2.useCaves) {
               return false;
            } else if (this.useDungeons != var2.useDungeons) {
               return false;
            } else if (this.useLavaLakes != var2.useLavaLakes) {
               return false;
            } else if (this.useLavaOceans != var2.useLavaOceans) {
               return false;
            } else if (this.useMineshafts != var2.useMineshafts) {
               return false;
            } else if (this.useRavines != var2.useRavines) {
               return false;
            } else if (this.useStrongholds != var2.useStrongholds) {
               return false;
            } else if (this.useTemples != var2.useTemples) {
               return false;
            } else if (this.useMonuments != var2.useMonuments) {
               return false;
            } else if (this.useVillages != var2.useVillages) {
               return false;
            } else if (this.useWaterLakes != var2.useWaterLakes) {
               return false;
            } else {
               return this.waterLakeChance == var2.waterLakeChance;
            }
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         int var1 = this.coordinateScale != 0.0F ? Float.floatToIntBits(this.coordinateScale) : 0;
         var1 = 31 * var1 + (this.heightScale != 0.0F ? Float.floatToIntBits(this.heightScale) : 0);
         var1 = 31 * var1 + (this.upperLimitScale != 0.0F ? Float.floatToIntBits(this.upperLimitScale) : 0);
         var1 = 31 * var1 + (this.lowerLimitScale != 0.0F ? Float.floatToIntBits(this.lowerLimitScale) : 0);
         var1 = 31 * var1 + (this.depthNoisescaleX != 0.0F ? Float.floatToIntBits(this.depthNoisescaleX) : 0);
         var1 = 31 * var1 + (this.depthNoiseScaleZ != 0.0F ? Float.floatToIntBits(this.depthNoiseScaleZ) : 0);
         var1 = 31 * var1 + (this.depthNoiseScaleExponent != 0.0F ? Float.floatToIntBits(this.depthNoiseScaleExponent) : 0);
         var1 = 31 * var1 + (this.mainNoiseScaleX != 0.0F ? Float.floatToIntBits(this.mainNoiseScaleX) : 0);
         var1 = 31 * var1 + (this.mainNoiseScaleY != 0.0F ? Float.floatToIntBits(this.mainNoiseScaleY) : 0);
         var1 = 31 * var1 + (this.mainNoiseScaleZ != 0.0F ? Float.floatToIntBits(this.mainNoiseScaleZ) : 0);
         var1 = 31 * var1 + (this.baseSize != 0.0F ? Float.floatToIntBits(this.baseSize) : 0);
         var1 = 31 * var1 + (this.stretchY != 0.0F ? Float.floatToIntBits(this.stretchY) : 0);
         var1 = 31 * var1 + (this.biomeDepthWeight != 0.0F ? Float.floatToIntBits(this.biomeDepthWeight) : 0);
         var1 = 31 * var1 + (this.biomeDepthOffset != 0.0F ? Float.floatToIntBits(this.biomeDepthOffset) : 0);
         var1 = 31 * var1 + (this.biomeScaleWeight != 0.0F ? Float.floatToIntBits(this.biomeScaleWeight) : 0);
         var1 = 31 * var1 + (this.biomeScaleOffset != 0.0F ? Float.floatToIntBits(this.biomeScaleOffset) : 0);
         var1 = 31 * var1 + this.seaLevel;
         var1 = 31 * var1 + (this.useCaves ? 1 : 0);
         var1 = 31 * var1 + (this.useDungeons ? 1 : 0);
         var1 = 31 * var1 + this.dungeonChance;
         var1 = 31 * var1 + (this.useStrongholds ? 1 : 0);
         var1 = 31 * var1 + (this.useVillages ? 1 : 0);
         var1 = 31 * var1 + (this.useMineshafts ? 1 : 0);
         var1 = 31 * var1 + (this.useTemples ? 1 : 0);
         var1 = 31 * var1 + (this.useMonuments ? 1 : 0);
         var1 = 31 * var1 + (this.useRavines ? 1 : 0);
         var1 = 31 * var1 + (this.useWaterLakes ? 1 : 0);
         var1 = 31 * var1 + this.waterLakeChance;
         var1 = 31 * var1 + (this.useLavaLakes ? 1 : 0);
         var1 = 31 * var1 + this.lavaLakeChance;
         var1 = 31 * var1 + (this.useLavaOceans ? 1 : 0);
         var1 = 31 * var1 + this.fixedBiome;
         var1 = 31 * var1 + this.biomeSize;
         var1 = 31 * var1 + this.riverSize;
         var1 = 31 * var1 + this.dirtSize;
         var1 = 31 * var1 + this.dirtCount;
         var1 = 31 * var1 + this.dirtMinHeight;
         var1 = 31 * var1 + this.dirtMaxHeight;
         var1 = 31 * var1 + this.gravelSize;
         var1 = 31 * var1 + this.gravelCount;
         var1 = 31 * var1 + this.gravelMinHeight;
         var1 = 31 * var1 + this.gravelMaxHeight;
         var1 = 31 * var1 + this.graniteSize;
         var1 = 31 * var1 + this.graniteCount;
         var1 = 31 * var1 + this.graniteMinHeight;
         var1 = 31 * var1 + this.graniteMaxHeight;
         var1 = 31 * var1 + this.dioriteSize;
         var1 = 31 * var1 + this.dioriteCount;
         var1 = 31 * var1 + this.dioriteMinHeight;
         var1 = 31 * var1 + this.dioriteMaxHeight;
         var1 = 31 * var1 + this.andesiteSize;
         var1 = 31 * var1 + this.andesiteCount;
         var1 = 31 * var1 + this.andesiteMinHeight;
         var1 = 31 * var1 + this.andesiteMaxHeight;
         var1 = 31 * var1 + this.coalSize;
         var1 = 31 * var1 + this.coalCount;
         var1 = 31 * var1 + this.coalMinHeight;
         var1 = 31 * var1 + this.coalMaxHeight;
         var1 = 31 * var1 + this.ironSize;
         var1 = 31 * var1 + this.ironCount;
         var1 = 31 * var1 + this.ironMinHeight;
         var1 = 31 * var1 + this.ironMaxHeight;
         var1 = 31 * var1 + this.goldSize;
         var1 = 31 * var1 + this.goldCount;
         var1 = 31 * var1 + this.goldMinHeight;
         var1 = 31 * var1 + this.goldMaxHeight;
         var1 = 31 * var1 + this.redstoneSize;
         var1 = 31 * var1 + this.redstoneCount;
         var1 = 31 * var1 + this.redstoneMinHeight;
         var1 = 31 * var1 + this.redstoneMaxHeight;
         var1 = 31 * var1 + this.diamondSize;
         var1 = 31 * var1 + this.diamondCount;
         var1 = 31 * var1 + this.diamondMinHeight;
         var1 = 31 * var1 + this.diamondMaxHeight;
         var1 = 31 * var1 + this.lapisSize;
         var1 = 31 * var1 + this.lapisCount;
         var1 = 31 * var1 + this.lapisMinHeight;
         return 31 * var1 + this.lapisMaxHeight;
      }

      public GeneratorOptions create() {
         return new GeneratorOptions(this);
      }
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public GeneratorOptions.Factory deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
         JsonObject var4 = jsonElement.getAsJsonObject();
         GeneratorOptions.Factory var5 = new GeneratorOptions.Factory();

         try {
            var5.coordinateScale = JsonUtils.getFloatOrDefault(var4, "coordinateScale", var5.coordinateScale);
            var5.heightScale = JsonUtils.getFloatOrDefault(var4, "heightScale", var5.heightScale);
            var5.lowerLimitScale = JsonUtils.getFloatOrDefault(var4, "lowerLimitScale", var5.lowerLimitScale);
            var5.upperLimitScale = JsonUtils.getFloatOrDefault(var4, "upperLimitScale", var5.upperLimitScale);
            var5.depthNoisescaleX = JsonUtils.getFloatOrDefault(var4, "depthNoiseScaleX", var5.depthNoisescaleX);
            var5.depthNoiseScaleZ = JsonUtils.getFloatOrDefault(var4, "depthNoiseScaleZ", var5.depthNoiseScaleZ);
            var5.depthNoiseScaleExponent = JsonUtils.getFloatOrDefault(var4, "depthNoiseScaleExponent", var5.depthNoiseScaleExponent);
            var5.mainNoiseScaleX = JsonUtils.getFloatOrDefault(var4, "mainNoiseScaleX", var5.mainNoiseScaleX);
            var5.mainNoiseScaleY = JsonUtils.getFloatOrDefault(var4, "mainNoiseScaleY", var5.mainNoiseScaleY);
            var5.mainNoiseScaleZ = JsonUtils.getFloatOrDefault(var4, "mainNoiseScaleZ", var5.mainNoiseScaleZ);
            var5.baseSize = JsonUtils.getFloatOrDefault(var4, "baseSize", var5.baseSize);
            var5.stretchY = JsonUtils.getFloatOrDefault(var4, "stretchY", var5.stretchY);
            var5.biomeDepthWeight = JsonUtils.getFloatOrDefault(var4, "biomeDepthWeight", var5.biomeDepthWeight);
            var5.biomeDepthOffset = JsonUtils.getFloatOrDefault(var4, "biomeDepthOffset", var5.biomeDepthOffset);
            var5.biomeScaleWeight = JsonUtils.getFloatOrDefault(var4, "biomeScaleWeight", var5.biomeScaleWeight);
            var5.biomeScaleOffset = JsonUtils.getFloatOrDefault(var4, "biomeScaleOffset", var5.biomeScaleOffset);
            var5.seaLevel = JsonUtils.getIntegerOrDefault(var4, "seaLevel", var5.seaLevel);
            var5.useCaves = JsonUtils.getBooleanOrDefault(var4, "useCaves", var5.useCaves);
            var5.useDungeons = JsonUtils.getBooleanOrDefault(var4, "useDungeons", var5.useDungeons);
            var5.dungeonChance = JsonUtils.getIntegerOrDefault(var4, "dungeonChance", var5.dungeonChance);
            var5.useStrongholds = JsonUtils.getBooleanOrDefault(var4, "useStrongholds", var5.useStrongholds);
            var5.useVillages = JsonUtils.getBooleanOrDefault(var4, "useVillages", var5.useVillages);
            var5.useMineshafts = JsonUtils.getBooleanOrDefault(var4, "useMineShafts", var5.useMineshafts);
            var5.useTemples = JsonUtils.getBooleanOrDefault(var4, "useTemples", var5.useTemples);
            var5.useMonuments = JsonUtils.getBooleanOrDefault(var4, "useMonuments", var5.useMonuments);
            var5.useRavines = JsonUtils.getBooleanOrDefault(var4, "useRavines", var5.useRavines);
            var5.useWaterLakes = JsonUtils.getBooleanOrDefault(var4, "useWaterLakes", var5.useWaterLakes);
            var5.waterLakeChance = JsonUtils.getIntegerOrDefault(var4, "waterLakeChance", var5.waterLakeChance);
            var5.useLavaLakes = JsonUtils.getBooleanOrDefault(var4, "useLavaLakes", var5.useLavaLakes);
            var5.lavaLakeChance = JsonUtils.getIntegerOrDefault(var4, "lavaLakeChance", var5.lavaLakeChance);
            var5.useLavaOceans = JsonUtils.getBooleanOrDefault(var4, "useLavaOceans", var5.useLavaOceans);
            var5.fixedBiome = JsonUtils.getIntegerOrDefault(var4, "fixedBiome", var5.fixedBiome);
            if (var5.fixedBiome >= 38 || var5.fixedBiome < -1) {
               var5.fixedBiome = -1;
            } else if (var5.fixedBiome >= Biome.HELL.id) {
               var5.fixedBiome += 2;
            }

            var5.biomeSize = JsonUtils.getIntegerOrDefault(var4, "biomeSize", var5.biomeSize);
            var5.riverSize = JsonUtils.getIntegerOrDefault(var4, "riverSize", var5.riverSize);
            var5.dirtSize = JsonUtils.getIntegerOrDefault(var4, "dirtSize", var5.dirtSize);
            var5.dirtCount = JsonUtils.getIntegerOrDefault(var4, "dirtCount", var5.dirtCount);
            var5.dirtMinHeight = JsonUtils.getIntegerOrDefault(var4, "dirtMinHeight", var5.dirtMinHeight);
            var5.dirtMaxHeight = JsonUtils.getIntegerOrDefault(var4, "dirtMaxHeight", var5.dirtMaxHeight);
            var5.gravelSize = JsonUtils.getIntegerOrDefault(var4, "gravelSize", var5.gravelSize);
            var5.gravelCount = JsonUtils.getIntegerOrDefault(var4, "gravelCount", var5.gravelCount);
            var5.gravelMinHeight = JsonUtils.getIntegerOrDefault(var4, "gravelMinHeight", var5.gravelMinHeight);
            var5.gravelMaxHeight = JsonUtils.getIntegerOrDefault(var4, "gravelMaxHeight", var5.gravelMaxHeight);
            var5.graniteSize = JsonUtils.getIntegerOrDefault(var4, "graniteSize", var5.graniteSize);
            var5.graniteCount = JsonUtils.getIntegerOrDefault(var4, "graniteCount", var5.graniteCount);
            var5.graniteMinHeight = JsonUtils.getIntegerOrDefault(var4, "graniteMinHeight", var5.graniteMinHeight);
            var5.graniteMaxHeight = JsonUtils.getIntegerOrDefault(var4, "graniteMaxHeight", var5.graniteMaxHeight);
            var5.dioriteSize = JsonUtils.getIntegerOrDefault(var4, "dioriteSize", var5.dioriteSize);
            var5.dioriteCount = JsonUtils.getIntegerOrDefault(var4, "dioriteCount", var5.dioriteCount);
            var5.dioriteMinHeight = JsonUtils.getIntegerOrDefault(var4, "dioriteMinHeight", var5.dioriteMinHeight);
            var5.dioriteMaxHeight = JsonUtils.getIntegerOrDefault(var4, "dioriteMaxHeight", var5.dioriteMaxHeight);
            var5.andesiteSize = JsonUtils.getIntegerOrDefault(var4, "andesiteSize", var5.andesiteSize);
            var5.andesiteCount = JsonUtils.getIntegerOrDefault(var4, "andesiteCount", var5.andesiteCount);
            var5.andesiteMinHeight = JsonUtils.getIntegerOrDefault(var4, "andesiteMinHeight", var5.andesiteMinHeight);
            var5.andesiteMaxHeight = JsonUtils.getIntegerOrDefault(var4, "andesiteMaxHeight", var5.andesiteMaxHeight);
            var5.coalSize = JsonUtils.getIntegerOrDefault(var4, "coalSize", var5.coalSize);
            var5.coalCount = JsonUtils.getIntegerOrDefault(var4, "coalCount", var5.coalCount);
            var5.coalMinHeight = JsonUtils.getIntegerOrDefault(var4, "coalMinHeight", var5.coalMinHeight);
            var5.coalMaxHeight = JsonUtils.getIntegerOrDefault(var4, "coalMaxHeight", var5.coalMaxHeight);
            var5.ironSize = JsonUtils.getIntegerOrDefault(var4, "ironSize", var5.ironSize);
            var5.ironCount = JsonUtils.getIntegerOrDefault(var4, "ironCount", var5.ironCount);
            var5.ironMinHeight = JsonUtils.getIntegerOrDefault(var4, "ironMinHeight", var5.ironMinHeight);
            var5.ironMaxHeight = JsonUtils.getIntegerOrDefault(var4, "ironMaxHeight", var5.ironMaxHeight);
            var5.goldSize = JsonUtils.getIntegerOrDefault(var4, "goldSize", var5.goldSize);
            var5.goldCount = JsonUtils.getIntegerOrDefault(var4, "goldCount", var5.goldCount);
            var5.goldMinHeight = JsonUtils.getIntegerOrDefault(var4, "goldMinHeight", var5.goldMinHeight);
            var5.goldMaxHeight = JsonUtils.getIntegerOrDefault(var4, "goldMaxHeight", var5.goldMaxHeight);
            var5.redstoneSize = JsonUtils.getIntegerOrDefault(var4, "redstoneSize", var5.redstoneSize);
            var5.redstoneCount = JsonUtils.getIntegerOrDefault(var4, "redstoneCount", var5.redstoneCount);
            var5.redstoneMinHeight = JsonUtils.getIntegerOrDefault(var4, "redstoneMinHeight", var5.redstoneMinHeight);
            var5.redstoneMaxHeight = JsonUtils.getIntegerOrDefault(var4, "redstoneMaxHeight", var5.redstoneMaxHeight);
            var5.diamondSize = JsonUtils.getIntegerOrDefault(var4, "diamondSize", var5.diamondSize);
            var5.diamondCount = JsonUtils.getIntegerOrDefault(var4, "diamondCount", var5.diamondCount);
            var5.diamondMinHeight = JsonUtils.getIntegerOrDefault(var4, "diamondMinHeight", var5.diamondMinHeight);
            var5.diamondMaxHeight = JsonUtils.getIntegerOrDefault(var4, "diamondMaxHeight", var5.diamondMaxHeight);
            var5.lapisSize = JsonUtils.getIntegerOrDefault(var4, "lapisSize", var5.lapisSize);
            var5.lapisCount = JsonUtils.getIntegerOrDefault(var4, "lapisCount", var5.lapisCount);
            var5.lapisMinHeight = JsonUtils.getIntegerOrDefault(var4, "lapisCenterHeight", var5.lapisMinHeight);
            var5.lapisMaxHeight = JsonUtils.getIntegerOrDefault(var4, "lapisSpread", var5.lapisMaxHeight);
         } catch (Exception var7) {
         }

         return var5;
      }

      public JsonElement serialize(GeneratorOptions.Factory c_28uqpzzjq, Type type, JsonSerializationContext jsonSerializationContext) {
         JsonObject var4 = new JsonObject();
         var4.addProperty("coordinateScale", c_28uqpzzjq.coordinateScale);
         var4.addProperty("heightScale", c_28uqpzzjq.heightScale);
         var4.addProperty("lowerLimitScale", c_28uqpzzjq.lowerLimitScale);
         var4.addProperty("upperLimitScale", c_28uqpzzjq.upperLimitScale);
         var4.addProperty("depthNoiseScaleX", c_28uqpzzjq.depthNoisescaleX);
         var4.addProperty("depthNoiseScaleZ", c_28uqpzzjq.depthNoiseScaleZ);
         var4.addProperty("depthNoiseScaleExponent", c_28uqpzzjq.depthNoiseScaleExponent);
         var4.addProperty("mainNoiseScaleX", c_28uqpzzjq.mainNoiseScaleX);
         var4.addProperty("mainNoiseScaleY", c_28uqpzzjq.mainNoiseScaleY);
         var4.addProperty("mainNoiseScaleZ", c_28uqpzzjq.mainNoiseScaleZ);
         var4.addProperty("baseSize", c_28uqpzzjq.baseSize);
         var4.addProperty("stretchY", c_28uqpzzjq.stretchY);
         var4.addProperty("biomeDepthWeight", c_28uqpzzjq.biomeDepthWeight);
         var4.addProperty("biomeDepthOffset", c_28uqpzzjq.biomeDepthOffset);
         var4.addProperty("biomeScaleWeight", c_28uqpzzjq.biomeScaleWeight);
         var4.addProperty("biomeScaleOffset", c_28uqpzzjq.biomeScaleOffset);
         var4.addProperty("seaLevel", c_28uqpzzjq.seaLevel);
         var4.addProperty("useCaves", c_28uqpzzjq.useCaves);
         var4.addProperty("useDungeons", c_28uqpzzjq.useDungeons);
         var4.addProperty("dungeonChance", c_28uqpzzjq.dungeonChance);
         var4.addProperty("useStrongholds", c_28uqpzzjq.useStrongholds);
         var4.addProperty("useVillages", c_28uqpzzjq.useVillages);
         var4.addProperty("useMineShafts", c_28uqpzzjq.useMineshafts);
         var4.addProperty("useTemples", c_28uqpzzjq.useTemples);
         var4.addProperty("useMonuments", c_28uqpzzjq.useMonuments);
         var4.addProperty("useRavines", c_28uqpzzjq.useRavines);
         var4.addProperty("useWaterLakes", c_28uqpzzjq.useWaterLakes);
         var4.addProperty("waterLakeChance", c_28uqpzzjq.waterLakeChance);
         var4.addProperty("useLavaLakes", c_28uqpzzjq.useLavaLakes);
         var4.addProperty("lavaLakeChance", c_28uqpzzjq.lavaLakeChance);
         var4.addProperty("useLavaOceans", c_28uqpzzjq.useLavaOceans);
         var4.addProperty("fixedBiome", c_28uqpzzjq.fixedBiome);
         var4.addProperty("biomeSize", c_28uqpzzjq.biomeSize);
         var4.addProperty("riverSize", c_28uqpzzjq.riverSize);
         var4.addProperty("dirtSize", c_28uqpzzjq.dirtSize);
         var4.addProperty("dirtCount", c_28uqpzzjq.dirtCount);
         var4.addProperty("dirtMinHeight", c_28uqpzzjq.dirtMinHeight);
         var4.addProperty("dirtMaxHeight", c_28uqpzzjq.dirtMaxHeight);
         var4.addProperty("gravelSize", c_28uqpzzjq.gravelSize);
         var4.addProperty("gravelCount", c_28uqpzzjq.gravelCount);
         var4.addProperty("gravelMinHeight", c_28uqpzzjq.gravelMinHeight);
         var4.addProperty("gravelMaxHeight", c_28uqpzzjq.gravelMaxHeight);
         var4.addProperty("graniteSize", c_28uqpzzjq.graniteSize);
         var4.addProperty("graniteCount", c_28uqpzzjq.graniteCount);
         var4.addProperty("graniteMinHeight", c_28uqpzzjq.graniteMinHeight);
         var4.addProperty("graniteMaxHeight", c_28uqpzzjq.graniteMaxHeight);
         var4.addProperty("dioriteSize", c_28uqpzzjq.dioriteSize);
         var4.addProperty("dioriteCount", c_28uqpzzjq.dioriteCount);
         var4.addProperty("dioriteMinHeight", c_28uqpzzjq.dioriteMinHeight);
         var4.addProperty("dioriteMaxHeight", c_28uqpzzjq.dioriteMaxHeight);
         var4.addProperty("andesiteSize", c_28uqpzzjq.andesiteSize);
         var4.addProperty("andesiteCount", c_28uqpzzjq.andesiteCount);
         var4.addProperty("andesiteMinHeight", c_28uqpzzjq.andesiteMinHeight);
         var4.addProperty("andesiteMaxHeight", c_28uqpzzjq.andesiteMaxHeight);
         var4.addProperty("coalSize", c_28uqpzzjq.coalSize);
         var4.addProperty("coalCount", c_28uqpzzjq.coalCount);
         var4.addProperty("coalMinHeight", c_28uqpzzjq.coalMinHeight);
         var4.addProperty("coalMaxHeight", c_28uqpzzjq.coalMaxHeight);
         var4.addProperty("ironSize", c_28uqpzzjq.ironSize);
         var4.addProperty("ironCount", c_28uqpzzjq.ironCount);
         var4.addProperty("ironMinHeight", c_28uqpzzjq.ironMinHeight);
         var4.addProperty("ironMaxHeight", c_28uqpzzjq.ironMaxHeight);
         var4.addProperty("goldSize", c_28uqpzzjq.goldSize);
         var4.addProperty("goldCount", c_28uqpzzjq.goldCount);
         var4.addProperty("goldMinHeight", c_28uqpzzjq.goldMinHeight);
         var4.addProperty("goldMaxHeight", c_28uqpzzjq.goldMaxHeight);
         var4.addProperty("redstoneSize", c_28uqpzzjq.redstoneSize);
         var4.addProperty("redstoneCount", c_28uqpzzjq.redstoneCount);
         var4.addProperty("redstoneMinHeight", c_28uqpzzjq.redstoneMinHeight);
         var4.addProperty("redstoneMaxHeight", c_28uqpzzjq.redstoneMaxHeight);
         var4.addProperty("diamondSize", c_28uqpzzjq.diamondSize);
         var4.addProperty("diamondCount", c_28uqpzzjq.diamondCount);
         var4.addProperty("diamondMinHeight", c_28uqpzzjq.diamondMinHeight);
         var4.addProperty("diamondMaxHeight", c_28uqpzzjq.diamondMaxHeight);
         var4.addProperty("lapisSize", c_28uqpzzjq.lapisSize);
         var4.addProperty("lapisCount", c_28uqpzzjq.lapisCount);
         var4.addProperty("lapisCenterHeight", c_28uqpzzjq.lapisMinHeight);
         var4.addProperty("lapisSpread", c_28uqpzzjq.lapisMaxHeight);
         return var4;
      }
   }
}
