package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class StrongholdStructure extends StructureFeature {
   private List validBiomes;
   private boolean isValid;
   private ChunkPos[] positions = new ChunkPos[3];
   private double distance = 32.0;
   private int spread = 3;

   public StrongholdStructure() {
      this.validBiomes = Lists.newArrayList();

      for(Biome var4 : Biome.getAll()) {
         if (var4 != null && var4.baseHeight > 0.0F) {
            this.validBiomes.add(var4);
         }
      }
   }

   public StrongholdStructure(Map options) {
      this();

      for(Entry var3 : options.entrySet()) {
         if (((String)var3.getKey()).equals("distance")) {
            this.distance = MathHelper.parseDouble((String)var3.getValue(), this.distance, 1.0);
         } else if (((String)var3.getKey()).equals("count")) {
            this.positions = new ChunkPos[MathHelper.parseInt((String)var3.getValue(), this.positions.length, 1)];
         } else if (((String)var3.getKey()).equals("spread")) {
            this.spread = MathHelper.parseInt((String)var3.getValue(), this.spread, 1);
         }
      }
   }

   @Override
   public String getName() {
      return "Stronghold";
   }

   @Override
   protected boolean isFeatureChunk(int chunkX, int chunkZ) {
      if (!this.isValid) {
         Random var3 = new Random();
         var3.setSeed(this.world.getSeed());
         double var4 = var3.nextDouble() * Math.PI * 2.0;
         int var6 = 1;

         for(int var7 = 0; var7 < this.positions.length; ++var7) {
            double var8 = (1.25 * (double)var6 + var3.nextDouble()) * this.distance * (double)var6;
            int var10 = (int)Math.round(Math.cos(var4) * var8);
            int var11 = (int)Math.round(Math.sin(var4) * var8);
            BlockPos var12 = this.world.getBiomeSource().getBiomePos((var10 << 4) + 8, (var11 << 4) + 8, 112, this.validBiomes, var3);
            if (var12 != null) {
               var10 = var12.getX() >> 4;
               var11 = var12.getZ() >> 4;
            }

            this.positions[var7] = new ChunkPos(var10, var11);
            var4 += (Math.PI * 2) * (double)var6 / (double)this.spread;
            if (var7 == this.spread) {
               var6 += 2 + var3.nextInt(5);
               this.spread += 1 + var3.nextInt(2);
            }
         }

         this.isValid = true;
      }

      for(ChunkPos var15 : this.positions) {
         if (chunkX == var15.x && chunkZ == var15.z) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected List getPotentialStructureChunkPos() {
      ArrayList var1 = Lists.newArrayList();

      for(ChunkPos var5 : this.positions) {
         if (var5 != null) {
            var1.add(var5.getCenterBlockPos(64));
         }
      }

      return var1;
   }

   @Override
   protected StructureStart createStart(int x, int z) {
      StrongholdStructure.Start var3 = new StrongholdStructure.Start(this.world, this.random, x, z);

      while(var3.getPieces().isEmpty() || ((StrongholdPieces.Start)var3.getPieces().get(0)).endPortalRoom == null) {
         var3 = new StrongholdStructure.Start(this.world, this.random, x, z);
      }

      return var3;
   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(World world, Random random, int chunkX, int chunkZ) {
         super(chunkX, chunkZ);
         StrongholdPieces.resetWeights();
         StrongholdPieces.Start var5 = new StrongholdPieces.Start(0, random, (chunkX << 4) + 2, (chunkZ << 4) + 2);
         this.pieces.add(var5);
         var5.addChildren(var5, this.pieces, random);
         List var6 = var5.children;

         while(!var6.isEmpty()) {
            int var7 = random.nextInt(var6.size());
            StructurePiece var8 = (StructurePiece)var6.remove(var7);
            var8.addChildren(var5, this.pieces, random);
         }

         this.calculateBoundingBox();
         this.moveBelowSeaLevel(world, random, 10);
      }
   }
}
