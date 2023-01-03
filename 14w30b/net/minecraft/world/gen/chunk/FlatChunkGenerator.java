package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.mob.MobSpawnGroup;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.BlockStateStorage;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.FlatWorldGenerator;
import net.minecraft.world.gen.FlatWorldLayer;
import net.minecraft.world.gen.Generator;
import net.minecraft.world.gen.feature.DungeonFeature;
import net.minecraft.world.gen.feature.LakeFeature;
import net.minecraft.world.gen.structure.MineshaftStructure;
import net.minecraft.world.gen.structure.OceanMonumentStructure;
import net.minecraft.world.gen.structure.StrongholdStructure;
import net.minecraft.world.gen.structure.StructureFeature;
import net.minecraft.world.gen.structure.TempleStructure;
import net.minecraft.world.gen.structure.VillageStructure;

public class FlatChunkGenerator implements ChunkSource {
   private World world;
   private Random random;
   private final BlockState[] blocks = new BlockState[256];
   private final FlatWorldGenerator generator;
   private final List structures = Lists.newArrayList();
   private final boolean hasDecoration;
   private final boolean hasDungeons;
   private LakeFeature waterLake;
   private LakeFeature lavaLake;

   public FlatChunkGenerator(World world, long seed, boolean structures, String preset) {
      this.world = world;
      this.random = new Random(seed);
      this.generator = FlatWorldGenerator.of(preset);
      if (structures) {
         Map var6 = this.generator.getFeatures();
         if (var6.containsKey("village")) {
            Map var7 = (Map)var6.get("village");
            if (!var7.containsKey("size")) {
               var7.put("size", "1");
            }

            this.structures.add(new VillageStructure(var7));
         }

         if (var6.containsKey("biome_1")) {
            this.structures.add(new TempleStructure((Map)var6.get("biome_1")));
         }

         if (var6.containsKey("mineshaft")) {
            this.structures.add(new MineshaftStructure((Map)var6.get("mineshaft")));
         }

         if (var6.containsKey("stronghold")) {
            this.structures.add(new StrongholdStructure((Map)var6.get("stronghold")));
         }

         if (var6.containsKey("oceanmonument")) {
            this.structures.add(new OceanMonumentStructure((Map)var6.get("oceanmonument")));
         }
      }

      this.hasDecoration = this.generator.getFeatures().containsKey("decoration");
      if (this.generator.getFeatures().containsKey("lake")) {
         this.waterLake = new LakeFeature(Blocks.WATER);
      }

      if (this.generator.getFeatures().containsKey("lava_lake")) {
         this.lavaLake = new LakeFeature(Blocks.LAVA);
      }

      this.hasDungeons = this.generator.getFeatures().containsKey("dungeon");

      for(FlatWorldLayer var11 : this.generator.getLayers()) {
         for(int var8 = var11.getY(); var8 < var11.getY() + var11.getSize(); ++var8) {
            Block var9 = var11.getBlock();
            if (var9 != null && var9 != Blocks.AIR) {
               this.blocks[var8] = var9.getStateFromMetadata(var11.getBlockMetadata());
            }
         }
      }
   }

   @Override
   public WorldChunk getChunk(int chunkX, int chunkZ) {
      BlockStateStorage var3 = new BlockStateStorage();

      for(int var4 = 0; var4 < this.blocks.length; ++var4) {
         BlockState var5 = this.blocks[var4];
         if (var5 != null) {
            for(int var6 = 0; var6 < 16; ++var6) {
               for(int var7 = 0; var7 < 16; ++var7) {
                  var3.set(var6, var4, var7, var5);
               }
            }
         }
      }

      for(Generator var10 : this.structures) {
         var10.place(this, this.world, chunkX, chunkZ, var3);
      }

      WorldChunk var9 = new WorldChunk(this.world, var3, chunkX, chunkZ);
      Biome[] var11 = this.world.getBiomeSource().getBiomes(null, chunkX * 16, chunkZ * 16, 16, 16);
      byte[] var12 = var9.getBiomes();

      for(int var13 = 0; var13 < var12.length; ++var13) {
         var12[var13] = (byte)var11[var13].id;
      }

      var9.populateSkylight();
      return var9;
   }

   @Override
   public boolean isLoaded(int chunkX, int chunkZ) {
      return true;
   }

   @Override
   public void populate(ChunkSource source, int chunkX, int chunkZ) {
      int var4 = chunkX * 16;
      int var5 = chunkZ * 16;
      BlockPos var6 = new BlockPos(var4, 0, var5);
      Biome var7 = this.world.getBiome(new BlockPos(var4 + 16, 0, var5 + 16));
      boolean var8 = false;
      this.random.setSeed(this.world.getSeed());
      long var9 = this.random.nextLong() / 2L * 2L + 1L;
      long var11 = this.random.nextLong() / 2L * 2L + 1L;
      this.random.setSeed((long)chunkX * var9 + (long)chunkZ * var11 ^ this.world.getSeed());
      ChunkPos var13 = new ChunkPos(chunkX, chunkZ);

      for(StructureFeature var15 : this.structures) {
         boolean var16 = var15.place(this.world, this.random, var13);
         if (var15 instanceof VillageStructure) {
            var8 |= var16;
         }
      }

      if (this.waterLake != null && !var8 && this.random.nextInt(4) == 0) {
         this.waterLake.place(this.world, this.random, var6.add(this.random.nextInt(16) + 8, this.random.nextInt(256), this.random.nextInt(16) + 8));
      }

      if (this.lavaLake != null && !var8 && this.random.nextInt(8) == 0) {
         BlockPos var17 = var6.add(this.random.nextInt(16) + 8, this.random.nextInt(this.random.nextInt(248) + 8), this.random.nextInt(16) + 8);
         if (var17.getY() < 63 || this.random.nextInt(10) == 0) {
            this.lavaLake.place(this.world, this.random, var17);
         }
      }

      if (this.hasDungeons) {
         for(int var18 = 0; var18 < 8; ++var18) {
            new DungeonFeature().place(this.world, this.random, var6.add(this.random.nextInt(16) + 8, this.random.nextInt(256), this.random.nextInt(16) + 8));
         }
      }

      if (this.hasDecoration) {
         var7.decorate(this.world, this.random, new BlockPos(var4, 0, var5));
      }
   }

   @Override
   public boolean populateSpecial(ChunkSource source, WorldChunk chunk, int chunkX, int chunkZ) {
      return false;
   }

   @Override
   public boolean save(boolean saveEntities, ProgressListener listener) {
      return true;
   }

   @Override
   public void save() {
   }

   @Override
   public boolean tick() {
      return false;
   }

   @Override
   public boolean canSave() {
      return true;
   }

   @Override
   public String getName() {
      return "FlatLevelSource";
   }

   @Override
   public List getSpawnEntries(MobSpawnGroup spawnGroup, BlockPos pos) {
      Biome var3 = this.world.getBiome(pos);
      return var3.getSpawnEntries(spawnGroup);
   }

   @Override
   public BlockPos findNearestStructure(World world, String name, BlockPos pos) {
      if ("Stronghold".equals(name)) {
         for(StructureFeature var5 : this.structures) {
            if (var5 instanceof StrongholdStructure) {
               return var5.findNearestStructure(world, pos);
            }
         }
      }

      return null;
   }

   @Override
   public int getLoadedCount() {
      return 0;
   }

   @Override
   public void placeStructures(WorldChunk chunk, int chunkX, int chunkZ) {
      for(StructureFeature var5 : this.structures) {
         var5.place(this, this.world, chunkX, chunkZ, null);
      }
   }

   @Override
   public WorldChunk getChunk(BlockPos pos) {
      return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
   }
}
