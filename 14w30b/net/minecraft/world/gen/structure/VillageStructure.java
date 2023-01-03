package net.minecraft.world.gen.structure;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class VillageStructure extends StructureFeature {
   public static final List VALID_BIOMES = Arrays.asList(Biome.PLAINS, Biome.DESERT, Biome.SAVANNA);
   private int size;
   private int distance = 32;
   private int minDistance = 8;

   public VillageStructure() {
   }

   public VillageStructure(Map options) {
      this();

      for(Entry var3 : options.entrySet()) {
         if (((String)var3.getKey()).equals("size")) {
            this.size = MathHelper.parseInt((String)var3.getValue(), this.size, 0);
         } else if (((String)var3.getKey()).equals("distance")) {
            this.distance = MathHelper.parseInt((String)var3.getValue(), this.distance, this.minDistance + 1);
         }
      }
   }

   @Override
   public String getName() {
      return "Village";
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
      Random var7 = this.world.setRandomSeed(var5, var6, 10387312);
      var5 *= this.distance;
      var6 *= this.distance;
      var5 += var7.nextInt(this.distance - this.minDistance);
      var6 += var7.nextInt(this.distance - this.minDistance);
      if (var3 == var5 && var4 == var6) {
         boolean var8 = this.world.getBiomeSource().areBiomesValid(var3 * 16 + 8, var4 * 16 + 8, 0, VALID_BIOMES);
         if (var8) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected StructureStart createStart(int x, int z) {
      return new VillageStructure.Start(this.world, this.random, x, z, this.size);
   }

   public static class Start extends StructureStart {
      private boolean valid;

      public Start() {
      }

      public Start(World world, Random random, int chunkX, int chunkZ, int size) {
         super(chunkX, chunkZ);
         List var6 = VillagePieces.getPieceChance(random, size);
         VillagePieces.Start var7 = new VillagePieces.Start(world.getBiomeSource(), 0, random, (chunkX << 4) + 2, (chunkZ << 4) + 2, var6, size);
         this.pieces.add(var7);
         var7.addChildren(var7, this.pieces, random);
         List var8 = var7.roadPieces;
         List var9 = var7.buildingPieces;

         while(!var8.isEmpty() || !var9.isEmpty()) {
            if (var8.isEmpty()) {
               int var10 = random.nextInt(var9.size());
               StructurePiece var11 = (StructurePiece)var9.remove(var10);
               var11.addChildren(var7, this.pieces, random);
            } else {
               int var13 = random.nextInt(var8.size());
               StructurePiece var15 = (StructurePiece)var8.remove(var13);
               var15.addChildren(var7, this.pieces, random);
            }
         }

         this.calculateBoundingBox();
         int var14 = 0;

         for(StructurePiece var12 : this.pieces) {
            if (!(var12 instanceof VillagePieces.RoadPiece)) {
               ++var14;
            }
         }

         this.valid = var14 > 2;
      }

      @Override
      public boolean isValid() {
         return this.valid;
      }

      @Override
      public void writeValidityNbt(NbtCompound nbt) {
         super.writeValidityNbt(nbt);
         nbt.putBoolean("Valid", this.valid);
      }

      @Override
      public void readValidityNbt(NbtCompound nbt) {
         super.readValidityNbt(nbt);
         this.valid = nbt.getBoolean("Valid");
      }
   }
}
