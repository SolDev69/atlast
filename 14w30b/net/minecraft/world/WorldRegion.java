package net.minecraft.world;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.WorldGeneratorType;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class WorldRegion implements IWorld {
   protected int chunkX;
   protected int chunkZ;
   protected WorldChunk[][] chunks;
   protected boolean saved;
   protected World world;

   public WorldRegion(World world, BlockPos minPos, BlockPos maxPos, int offset) {
      this.world = world;
      this.chunkX = minPos.getX() - offset >> 4;
      this.chunkZ = minPos.getZ() - offset >> 4;
      int var5 = maxPos.getX() + offset >> 4;
      int var6 = maxPos.getZ() + offset >> 4;
      this.chunks = new WorldChunk[var5 - this.chunkX + 1][var6 - this.chunkZ + 1];
      this.saved = true;

      for(int var7 = this.chunkX; var7 <= var5; ++var7) {
         for(int var8 = this.chunkZ; var8 <= var6; ++var8) {
            this.chunks[var7 - this.chunkX][var8 - this.chunkZ] = world.getChunkAt(var7, var8);
         }
      }

      for(int var10 = minPos.getX() >> 4; var10 <= maxPos.getX() >> 4; ++var10) {
         for(int var11 = minPos.getZ() >> 4; var11 <= maxPos.getZ() >> 4; ++var11) {
            WorldChunk var9 = this.chunks[var10 - this.chunkX][var11 - this.chunkZ];
            if (var9 != null && !var9.isEmpty(minPos.getY(), maxPos.getY())) {
               this.saved = false;
            }
         }
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean isSaved() {
      return this.saved;
   }

   @Override
   public BlockEntity getBlockEntity(BlockPos pos) {
      int var2 = (pos.getX() >> 4) - this.chunkX;
      int var3 = (pos.getZ() >> 4) - this.chunkZ;
      return this.chunks[var2][var3].getBlockEntity(pos);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getLightColor(BlockPos pos, int blockLight) {
      int var3 = this.getBlockLightLevel(LightType.SKY, pos);
      int var4 = this.getBlockLightLevel(LightType.BLOCK, pos);
      if (var4 < blockLight) {
         var4 = blockLight;
      }

      return var3 << 20 | var4 << 4;
   }

   @Override
   public BlockState getBlockState(BlockPos pos) {
      if (pos.getY() >= 0 && pos.getY() < 256) {
         int var2 = (pos.getX() >> 4) - this.chunkX;
         int var3 = (pos.getZ() >> 4) - this.chunkZ;
         if (var2 >= 0 && var2 < this.chunks.length && var3 >= 0 && var3 < this.chunks[var2].length) {
            WorldChunk var4 = this.chunks[var2][var3];
            if (var4 != null) {
               return var4.getBlockState(pos);
            }
         }
      }

      return Blocks.AIR.defaultState();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Biome getBiome(BlockPos pos) {
      return this.world.getBiome(pos);
   }

   @Environment(EnvType.CLIENT)
   private int getBlockLightLevel(LightType type, BlockPos x) {
      if (type == LightType.SKY && this.world.dimension.isDark()) {
         return 0;
      } else if (x.getY() >= 0 && x.getY() < 256) {
         if (this.getBlockState(x).getBlock().usesNeighborLight()) {
            int var9 = 0;

            for(Direction var7 : Direction.values()) {
               int var8 = this.getLightLevel(type, x.offset(var7));
               if (var8 > var9) {
                  var9 = var8;
               }

               if (var9 >= 15) {
                  return var9;
               }
            }

            return var9;
         } else {
            int var3 = (x.getX() >> 4) - this.chunkX;
            int var4 = (x.getZ() >> 4) - this.chunkZ;
            return this.chunks[var3][var4].getLight(type, x);
         }
      } else {
         return type.defaultValue;
      }
   }

   @Override
   public boolean isAir(BlockPos pos) {
      return this.getBlockState(pos).getBlock().getMaterial() == Material.AIR;
   }

   @Environment(EnvType.CLIENT)
   public int getLightLevel(LightType type, BlockPos x) {
      if (x.getY() >= 0 && x.getY() < 256) {
         int var3 = (x.getX() >> 4) - this.chunkX;
         int var4 = (x.getZ() >> 4) - this.chunkZ;
         return this.chunks[var3][var4].getLight(type, x);
      } else {
         return type.defaultValue;
      }
   }

   @Override
   public int getEmittedStrongPower(BlockPos pos, Direction dir) {
      BlockState var3 = this.getBlockState(pos);
      return var3.getBlock().getEmittedStrongPower(this, pos, var3, dir);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public WorldGeneratorType getGeneratorType() {
      return this.world.getGeneratorType();
   }
}
