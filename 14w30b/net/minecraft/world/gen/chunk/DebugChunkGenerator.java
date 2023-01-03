package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.mob.MobSpawnGroup;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.BlockStateStorage;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.chunk.WorldChunk;

public class DebugChunkGenerator implements ChunkSource {
   private static final List BLOCK_STATES = Lists.newArrayList();
   private static final int GRID_LENGTH;
   private final World world;

   public DebugChunkGenerator(World world) {
      this.world = world;
   }

   @Override
   public WorldChunk getChunk(int chunkX, int chunkZ) {
      BlockStateStorage var3 = new BlockStateStorage();

      for(int var4 = 0; var4 < 16; ++var4) {
         for(int var5 = 0; var5 < 16; ++var5) {
            int var6 = chunkX * 16 + var4;
            int var7 = chunkZ * 16 + var5;
            var3.set(var4, 60, var5, Blocks.BARRIER.defaultState());
            BlockState var8 = getBlockState(var6, var7);
            if (var8 != null) {
               var3.set(var4, 70, var5, var8);
            }
         }
      }

      WorldChunk var9 = new WorldChunk(this.world, var3, chunkX, chunkZ);
      var9.populateSkylight();
      Biome[] var10 = this.world.getBiomeSource().getBiomes(null, chunkX * 16, chunkZ * 16, 16, 16);
      byte[] var11 = var9.getBiomes();

      for(int var12 = 0; var12 < var11.length; ++var12) {
         var11[var12] = (byte)var10[var12].id;
      }

      var9.populateSkylight();
      return var9;
   }

   public static BlockState getBlockState(int x, int z) {
      BlockState var2 = null;
      if (x > 0 && z > 0 && x % 2 != 0 && z % 2 != 0) {
         x /= 2;
         z /= 2;
         if (x <= GRID_LENGTH && z <= GRID_LENGTH) {
            int var3 = MathHelper.abs(x * GRID_LENGTH + z);
            if (var3 < BLOCK_STATES.size()) {
               var2 = (BlockState)BLOCK_STATES.get(var3);
            }
         }
      }

      return var2;
   }

   @Override
   public boolean isLoaded(int chunkX, int chunkZ) {
      return true;
   }

   @Override
   public void populate(ChunkSource source, int chunkX, int chunkZ) {
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
      return "DebugLevelSource";
   }

   @Override
   public List getSpawnEntries(MobSpawnGroup spawnGroup, BlockPos pos) {
      Biome var3 = this.world.getBiome(pos);
      return var3.getSpawnEntries(spawnGroup);
   }

   @Override
   public BlockPos findNearestStructure(World world, String name, BlockPos pos) {
      return null;
   }

   @Override
   public int getLoadedCount() {
      return 0;
   }

   @Override
   public void placeStructures(WorldChunk chunk, int chunkX, int chunkZ) {
   }

   @Override
   public WorldChunk getChunk(BlockPos pos) {
      return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
   }

   static {
      for(Block var1 : Block.REGISTRY) {
         BLOCK_STATES.addAll(var1.stateDefinition().all());
      }

      GRID_LENGTH = MathHelper.ceil(MathHelper.sqrt((float)BLOCK_STATES.size()));
   }
}
