package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateStorage;
import net.minecraft.world.chunk.ChunkSource;

public class Generator {
   protected int range = 8;
   protected Random random = new Random();
   protected World world;

   public void place(ChunkSource source, World world, int chunkX, int chunkZ, BlockStateStorage chunk) {
      int var6 = this.range;
      this.world = world;
      this.random.setSeed(world.getSeed());
      long var7 = this.random.nextLong();
      long var9 = this.random.nextLong();

      for(int var11 = chunkX - var6; var11 <= chunkX + var6; ++var11) {
         for(int var12 = chunkZ - var6; var12 <= chunkZ + var6; ++var12) {
            long var13 = (long)var11 * var7;
            long var15 = (long)var12 * var9;
            this.random.setSeed(var13 ^ var15 ^ world.getSeed());
            this.place(world, var11, var12, chunkX, chunkZ, chunk);
         }
      }
   }

   protected void place(World world, int chunkX, int chunkZ, int centerChunkX, int centerChunkZ, BlockStateStorage blocks) {
   }
}
