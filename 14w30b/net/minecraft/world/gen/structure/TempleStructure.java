package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.entity.living.mob.hostile.WitchEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class TempleStructure extends StructureFeature {
   private static final List VALID_BIOMES = Arrays.asList(Biome.DESERT, Biome.DESERT_HILLS, Biome.JUNGLE, Biome.JUNGLE_HILLS, Biome.SWAMPLAND);
   private List spawnEntries = Lists.newArrayList();
   private int distance = 32;
   private int minDistance = 8;

   public TempleStructure() {
      this.spawnEntries.add(new Biome.SpawnEntry(WitchEntity.class, 1, 1, 1));
   }

   public TempleStructure(Map options) {
      this();

      for(Entry var3 : options.entrySet()) {
         if (((String)var3.getKey()).equals("distance")) {
            this.distance = MathHelper.parseInt((String)var3.getValue(), this.distance, this.minDistance + 1);
         }
      }
   }

   @Override
   public String getName() {
      return "Temple";
   }

   @Override
   protected boolean isFeatureChunk(int chunkX, int chunkZ) {
      int var3 = chunkX;
      int var4 = chunkZ;
      if (chunkX < 0) {
         chunkX -= this.distance - 1;
      }

      if (chunkZ < 0) {
         chunkZ -= this.distance - 1;
      }

      int var5 = chunkX / this.distance;
      int var6 = chunkZ / this.distance;
      Random var7 = this.world.setRandomSeed(var5, var6, 14357617);
      var5 *= this.distance;
      var6 *= this.distance;
      var5 += var7.nextInt(this.distance - this.minDistance);
      var6 += var7.nextInt(this.distance - this.minDistance);
      if (var3 == var5 && var4 == var6) {
         Biome var8 = this.world.getBiomeSource().getBiome(new BlockPos(var3 * 16 + 8, 0, var4 * 16 + 8));
         if (var8 == null) {
            return false;
         }

         for(Biome var10 : VALID_BIOMES) {
            if (var8 == var10) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   protected StructureStart createStart(int x, int z) {
      return new TempleStructure.Start(this.world, this.random, x, z);
   }

   public boolean isValid(BlockPos x) {
      StructureStart var2 = this.getStructure(x);
      if (var2 != null && var2 instanceof TempleStructure.Start && !var2.pieces.isEmpty()) {
         StructurePiece var3 = (StructurePiece)var2.pieces.getFirst();
         return var3 instanceof TemplePieces.WitchHut;
      } else {
         return false;
      }
   }

   public List getSpawnEntries() {
      return this.spawnEntries;
   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(World world, Random random, int chunkX, int chunkZ) {
         super(chunkX, chunkZ);
         Biome var5 = world.getBiome(new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8));
         if (var5 == Biome.JUNGLE || var5 == Biome.JUNGLE_HILLS) {
            TemplePieces.JungleTemple var8 = new TemplePieces.JungleTemple(random, chunkX * 16, chunkZ * 16);
            this.pieces.add(var8);
         } else if (var5 == Biome.SWAMPLAND) {
            TemplePieces.WitchHut var6 = new TemplePieces.WitchHut(random, chunkX * 16, chunkZ * 16);
            this.pieces.add(var6);
         } else if (var5 == Biome.DESERT || var5 == Biome.DESERT_HILLS) {
            TemplePieces.DesertPyramid var7 = new TemplePieces.DesertPyramid(random, chunkX * 16, chunkZ * 16);
            this.pieces.add(var7);
         }

         this.calculateBoundingBox();
      }
   }
}
