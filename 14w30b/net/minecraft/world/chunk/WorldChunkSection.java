package net.minecraft.world.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;

public class WorldChunkSection {
   private int offsetY;
   private int nonAirBlockCount;
   private int randomTickingBlockCount;
   private char[] blockData;
   private ChunkNibbleStorage blockLight;
   private ChunkNibbleStorage skyLight;

   public WorldChunkSection(int offsetY, boolean hasLight) {
      this.offsetY = offsetY;
      this.blockData = new char[4096];
      this.blockLight = new ChunkNibbleStorage(this.blockData.length, 4);
      if (hasLight) {
         this.skyLight = new ChunkNibbleStorage(this.blockData.length, 4);
      }
   }

   public BlockState getBlockState(int x, int y, int z) {
      BlockState var4 = (BlockState)Block.STATE_REGISTRY.get(this.blockData[y << 8 | z << 4 | x]);
      return var4 != null ? var4 : Blocks.AIR.defaultState();
   }

   public void setBlockState(int x, int y, int z, BlockState state) {
      BlockState var5 = this.getBlockState(x, y, z);
      Block var6 = var5.getBlock();
      Block var7 = state.getBlock();
      if (var6 != Blocks.AIR) {
         --this.nonAirBlockCount;
         if (var6.ticksRandomly()) {
            --this.randomTickingBlockCount;
         }
      }

      if (var7 != Blocks.AIR) {
         ++this.nonAirBlockCount;
         if (var7.ticksRandomly()) {
            ++this.randomTickingBlockCount;
         }
      }

      this.blockData[y << 8 | z << 4 | x] = (char)Block.STATE_REGISTRY.getId(state);
   }

   public Block getBlock(int x, int y, int z) {
      return this.getBlockState(x, y, z).getBlock();
   }

   public int getBlockMetadata(int x, int y, int z) {
      BlockState var4 = this.getBlockState(x, y, z);
      return var4.getBlock().getMetadataFromState(var4);
   }

   public boolean isEmpty() {
      return this.nonAirBlockCount == 0;
   }

   public boolean hasRandomTickingBlocks() {
      return this.randomTickingBlockCount > 0;
   }

   public int getOffsetY() {
      return this.offsetY;
   }

   public void setSkyLight(int x, int y, int z, int light) {
      this.skyLight.set(x, y, z, light);
   }

   public int getSkyLight(int x, int y, int z) {
      return this.skyLight.get(x, y, z);
   }

   public void setBlockLight(int x, int y, int z, int light) {
      this.blockLight.set(x, y, z, light);
   }

   public int getBlockLight(int x, int y, int z) {
      return this.blockLight.get(x, y, z);
   }

   public void validateBlockCounters() {
      this.nonAirBlockCount = 0;
      this.randomTickingBlockCount = 0;

      for(int var1 = 0; var1 < 16; ++var1) {
         for(int var2 = 0; var2 < 16; ++var2) {
            for(int var3 = 0; var3 < 16; ++var3) {
               Block var4 = this.getBlock(var1, var2, var3);
               if (var4 != Blocks.AIR) {
                  ++this.nonAirBlockCount;
                  if (var4.ticksRandomly()) {
                     ++this.randomTickingBlockCount;
                  }
               }
            }
         }
      }
   }

   public char[] getBlockData() {
      return this.blockData;
   }

   public void setBlockData(char[] blockData) {
      this.blockData = blockData;
   }

   public ChunkNibbleStorage getBlockLightStorage() {
      return this.blockLight;
   }

   public ChunkNibbleStorage getSkyLightStorage() {
      return this.skyLight;
   }

   public void setBlockLightStorage(ChunkNibbleStorage blockLight) {
      this.blockLight = blockLight;
   }

   public void setSkyLightStorage(ChunkNibbleStorage skyLight) {
      this.skyLight = skyLight;
   }
}
