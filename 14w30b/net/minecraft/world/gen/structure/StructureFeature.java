package net.minecraft.world.gen.structure;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateStorage;
import net.minecraft.world.gen.Generator;
import net.minecraft.world.gen.feature.SavedFeatureData;

public abstract class StructureFeature extends Generator {
   private SavedFeatureData savedData;
   protected Map structures = Maps.newHashMap();

   public abstract String getName();

   @Override
   protected final void place(World world, int chunkX, int chunkZ, int centerChunkX, int centerChunkZ, BlockStateStorage blocks) {
      this.loadSavedData(world);
      if (!this.structures.containsKey(ChunkPos.toLong(chunkX, chunkZ))) {
         this.random.nextInt();

         try {
            if (this.isFeatureChunk(chunkX, chunkZ)) {
               StructureStart var7 = this.createStart(chunkX, chunkZ);
               this.structures.put(ChunkPos.toLong(chunkX, chunkZ), var7);
               this.save(chunkX, chunkZ, var7);
            }
         } catch (Throwable var10) {
            CrashReport var8 = CrashReport.of(var10, "Exception preparing structure feature");
            CashReportCategory var9 = var8.addCategory("Feature being prepared");
            var9.add("Is feature chunk", new Callable() {
               public String call() {
                  return StructureFeature.this.isFeatureChunk(chunkX, chunkZ) ? "True" : "False";
               }
            });
            var9.add("Chunk location", String.format("%d,%d", chunkX, chunkZ));
            var9.add("Chunk pos hash", new Callable() {
               public String call() {
                  return String.valueOf(ChunkPos.toLong(chunkX, chunkZ));
               }
            });
            var9.add("Structure type", new Callable() {
               public String call() {
                  return StructureFeature.this.getClass().getCanonicalName();
               }
            });
            throw new CrashException(var8);
         }
      }
   }

   public boolean place(World world, Random random, ChunkPos chunkPos) {
      this.loadSavedData(world);
      int var4 = (chunkPos.x << 4) + 8;
      int var5 = (chunkPos.z << 4) + 8;
      boolean var6 = false;

      for(StructureStart var8 : this.structures.values()) {
         if (var8.isValid() && var8.isValid(chunkPos) && var8.getBoundingBox().intersects(var4, var5, var4 + 15, var5 + 15)) {
            var8.postProcess(world, random, new StructureBox(var4, var5, var4 + 15, var5 + 15));
            var8.postPlacement(chunkPos);
            var6 = true;
            this.save(var8.getChunkX(), var8.getChunkZ(), var8);
         }
      }

      return var6;
   }

   public boolean isInsideStructure(BlockPos pos) {
      this.loadSavedData(this.world);
      return this.getStructure(pos) != null;
   }

   protected StructureStart getStructure(BlockPos pos) {
      for(StructureStart var3 : this.structures.values()) {
         if (var3.isValid() && var3.getBoundingBox().contains(pos)) {
            for(StructurePiece var5 : var3.getPieces()) {
               if (var5.getBoundingBox().contains(pos)) {
                  return var3;
               }
            }
         }
      }

      return null;
   }

   public boolean isInsideStructureBox(World world, BlockPos pos) {
      this.loadSavedData(world);

      for(StructureStart var4 : this.structures.values()) {
         if (var4.isValid() && var4.getBoundingBox().contains(pos)) {
            return true;
         }
      }

      return false;
   }

   public BlockPos findNearestStructure(World world, BlockPos pos) {
      this.world = world;
      this.loadSavedData(world);
      this.random.setSeed(world.getSeed());
      long var3 = this.random.nextLong();
      long var5 = this.random.nextLong();
      long var7 = (long)(pos.getX() >> 4) * var3;
      long var9 = (long)(pos.getZ() >> 4) * var5;
      this.random.setSeed(var7 ^ var9 ^ world.getSeed());
      this.place(world, pos.getX() >> 4, pos.getZ() >> 4, 0, 0, null);
      double var11 = Double.MAX_VALUE;
      BlockPos var13 = null;

      for(StructureStart var15 : this.structures.values()) {
         if (var15.isValid()) {
            StructurePiece var16 = (StructurePiece)var15.getPieces().get(0);
            BlockPos var17 = var16.getCenterPos();
            double var18 = var17.squaredDistanceTo(pos);
            if (var18 < var11) {
               var11 = var18;
               var13 = var17;
            }
         }
      }

      if (var13 != null) {
         return var13;
      } else {
         List var20 = this.getPotentialStructureChunkPos();
         if (var20 != null) {
            BlockPos var21 = null;

            for(BlockPos var23 : var20) {
               double var24 = var23.squaredDistanceTo(pos);
               if (var24 < var11) {
                  var11 = var24;
                  var21 = var23;
               }
            }

            return var21;
         } else {
            return null;
         }
      }
   }

   protected List getPotentialStructureChunkPos() {
      return null;
   }

   private void loadSavedData(World world) {
      if (this.savedData == null) {
         this.savedData = (SavedFeatureData)world.loadSavedData(SavedFeatureData.class, this.getName());
         if (this.savedData == null) {
            this.savedData = new SavedFeatureData(this.getName());
            world.setSavedData(this.getName(), this.savedData);
         } else {
            NbtCompound var2 = this.savedData.getFeatures();

            for(String var4 : var2.getKeys()) {
               NbtElement var5 = var2.get(var4);
               if (var5.getType() == 10) {
                  NbtCompound var6 = (NbtCompound)var5;
                  if (var6.contains("ChunkX") && var6.contains("ChunkZ")) {
                     int var7 = var6.getInt("ChunkX");
                     int var8 = var6.getInt("ChunkZ");
                     StructureStart var9 = StructureManager.getStartFromNbt(var6, world);
                     if (var9 != null) {
                        this.structures.put(ChunkPos.toLong(var7, var8), var9);
                     }
                  }
               }
            }
         }
      }
   }

   private void save(int chunkX, int chunkZ, StructureStart start) {
      this.savedData.put(start.toNbt(chunkX, chunkZ), chunkX, chunkZ);
      this.savedData.markDirty();
   }

   protected abstract boolean isFeatureChunk(int chunkX, int chunkZ);

   protected abstract StructureStart createStart(int x, int z);
}
