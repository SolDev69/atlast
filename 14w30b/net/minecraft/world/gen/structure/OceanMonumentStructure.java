package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.living.mob.hostile.GuardianEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class OceanMonumentStructure extends StructureFeature {
   private int spacing = 32;
   private int separation = 5;
   public static final List VALID_BIOMES = Arrays.asList(Biome.OCEAN, Biome.DEEP_OCEAN, Biome.RIVER, Biome.FROZEN_OCEAN, Biome.FROZEN_RIVER);
   private static final List SPAWN_ENTRIES = Lists.newArrayList();

   public OceanMonumentStructure() {
   }

   public OceanMonumentStructure(Map options) {
      this();

      for(Entry var3 : options.entrySet()) {
         if (((String)var3.getKey()).equals("spacing")) {
            this.spacing = MathHelper.parseInt((String)var3.getValue(), this.spacing, 1);
         } else if (((String)var3.getKey()).equals("separation")) {
            this.separation = MathHelper.parseInt((String)var3.getValue(), this.separation, 1);
         }
      }
   }

   @Override
   public String getName() {
      return "Monument";
   }

   @Override
   protected boolean isFeatureChunk(int chunkX, int chunkZ) {
      int var3 = chunkX;
      int var4 = chunkZ;
      if (chunkX < 0) {
         chunkX -= this.spacing - 1;
      }

      if (chunkZ < 0) {
         chunkZ -= this.spacing - 1;
      }

      int var5 = chunkX / this.spacing;
      int var6 = chunkZ / this.spacing;
      Random var7 = this.world.setRandomSeed(var5, var6, 10387313);
      var5 *= this.spacing;
      var6 *= this.spacing;
      var5 += (var7.nextInt(this.spacing - this.separation) + var7.nextInt(this.spacing - this.separation)) / 2;
      var6 += (var7.nextInt(this.spacing - this.separation) + var7.nextInt(this.spacing - this.separation)) / 2;
      if (var3 == var5 && var4 == var6) {
         if (this.world.getBiomeSource().getBiomeOrDefault(new BlockPos(var3 * 16 + 8, 64, var4 * 16 + 8), null) != Biome.DEEP_OCEAN) {
            return false;
         }

         boolean var8 = this.world.getBiomeSource().areBiomesValid(var3 * 16 + 8, var4 * 16 + 8, 29, VALID_BIOMES);
         if (var8) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected StructureStart createStart(int x, int z) {
      return new OceanMonumentStructure.Start(this.world, this.random, x, z);
   }

   public List getSpawnEntries() {
      return SPAWN_ENTRIES;
   }

   static {
      SPAWN_ENTRIES.add(new Biome.SpawnEntry(GuardianEntity.class, 1, 2, 4));
   }

   public static class Start extends StructureStart {
      private Set placedMonuments = Sets.newHashSet();
      private boolean initialized;

      public Start() {
      }

      public Start(World world, Random random, int chunkX, int chunkZ) {
         super(chunkX, chunkZ);
         this.init(world, random, chunkX, chunkZ);
      }

      private void init(World world, Random random, int chunkX, int chunkZ) {
         random.setSeed(world.getSeed());
         long var5 = random.nextLong();
         long var7 = random.nextLong();
         long var9 = (long)chunkX * var5;
         long var11 = (long)chunkZ * var7;
         random.setSeed(var9 ^ var11 ^ world.getSeed());
         int var13 = chunkX * 16 + 8 - 29;
         int var14 = chunkZ * 16 + 8 - 29;
         Direction var15 = Direction.Plane.HORIZONTAL.pick(random);
         this.pieces.add(new OceanMonumentPieces.OceanMonument(random, var13, var14, var15));
         this.calculateBoundingBox();
         this.initialized = true;
      }

      @Override
      public void postProcess(World world, Random random, StructureBox box) {
         if (!this.initialized) {
            this.pieces.clear();
            this.init(world, random, this.getChunkX(), this.getChunkZ());
         }

         super.postProcess(world, random, box);
      }

      @Override
      public boolean isValid(ChunkPos chunkPos) {
         return this.placedMonuments.contains(chunkPos) ? false : super.isValid(chunkPos);
      }

      @Override
      public void postPlacement(ChunkPos chunkPos) {
         super.postPlacement(chunkPos);
         this.placedMonuments.add(chunkPos);
      }

      @Override
      public void writeValidityNbt(NbtCompound nbt) {
         super.writeValidityNbt(nbt);
         NbtList var2 = new NbtList();

         for(ChunkPos var4 : this.placedMonuments) {
            NbtCompound var5 = new NbtCompound();
            var5.putInt("X", var4.x);
            var5.putInt("Z", var4.z);
            var2.add(var5);
         }

         nbt.put("Processed", var2);
      }

      @Override
      public void readValidityNbt(NbtCompound nbt) {
         super.readValidityNbt(nbt);
         if (nbt.isType("Processed", 9)) {
            NbtList var2 = nbt.getList("Processed", 10);

            for(int var3 = 0; var3 < var2.size(); ++var3) {
               NbtCompound var4 = var2.getCompound(var3);
               this.placedMonuments.add(new ChunkPos(var4.getInt("X"), var4.getInt("Z")));
            }
         }
      }
   }
}
