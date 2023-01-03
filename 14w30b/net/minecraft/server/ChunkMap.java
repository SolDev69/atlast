package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlocksUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldChunkS2CPacket;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Long2ObjectHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.Dimension;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkMap {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ServerWorld world;
   private final List players = Lists.newArrayList();
   private final Long2ObjectHashMap chunks = new Long2ObjectHashMap();
   private final List dirty = Lists.newArrayList();
   private final List ticking = Lists.newArrayList();
   private int chunkViewDistance;
   private long lastUpdateTime;
   private final int[][] adjacentChunkCoords = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

   public ChunkMap(ServerWorld world) {
      this.world = world;
      this.updateViewDistance(world.getServer().getPlayerManager().getChunkViewDistance());
   }

   public ServerWorld getWorld() {
      return this.world;
   }

   public void tick() {
      long var1 = this.world.getTime();
      if (var1 - this.lastUpdateTime > 8000L) {
         this.lastUpdateTime = var1;

         for(int var3 = 0; var3 < this.ticking.size(); ++var3) {
            ChunkMap.ChunkHolder var4 = (ChunkMap.ChunkHolder)this.ticking.get(var3);
            var4.sendChanges();
            var4.updateInhabitedTime();
         }
      } else {
         for(int var5 = 0; var5 < this.dirty.size(); ++var5) {
            ChunkMap.ChunkHolder var7 = (ChunkMap.ChunkHolder)this.dirty.get(var5);
            var7.sendChanges();
         }
      }

      this.dirty.clear();
      if (this.players.isEmpty()) {
         Dimension var6 = this.world.dimension;
         if (!var6.hasWorldSpawn()) {
            this.world.chunkCache.scheduleUnloadAll();
         }
      }
   }

   public boolean isLoaded(int chunkX, int chunkZ) {
      long var3 = (long)chunkX + 2147483647L | (long)chunkZ + 2147483647L << 32;
      return this.chunks.get(var3) != null;
   }

   private ChunkMap.ChunkHolder getChunk(int chunkX, int chunkZ, boolean orCreate) {
      long var4 = (long)chunkX + 2147483647L | (long)chunkZ + 2147483647L << 32;
      ChunkMap.ChunkHolder var6 = (ChunkMap.ChunkHolder)this.chunks.get(var4);
      if (var6 == null && orCreate) {
         var6 = new ChunkMap.ChunkHolder(chunkX, chunkZ);
         this.chunks.put(var4, var6);
         this.ticking.add(var6);
      }

      return var6;
   }

   public void onBlockChanged(BlockPos pos) {
      int var2 = pos.getX() >> 4;
      int var3 = pos.getZ() >> 4;
      ChunkMap.ChunkHolder var4 = this.getChunk(var2, var3, false);
      if (var4 != null) {
         var4.onBlockChanged(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
      }
   }

   public void onPlayerAdded(ServerPlayerEntity player) {
      int var2 = (int)player.x >> 4;
      int var3 = (int)player.z >> 4;
      player.trackedX = player.x;
      player.trackedZ = player.z;

      for(int var4 = var2 - this.chunkViewDistance; var4 <= var2 + this.chunkViewDistance; ++var4) {
         for(int var5 = var3 - this.chunkViewDistance; var5 <= var3 + this.chunkViewDistance; ++var5) {
            this.getChunk(var4, var5, true).addPlayer(player);
         }
      }

      this.players.add(player);
      this.updateChunkTracking(player);
   }

   public void updateChunkTracking(ServerPlayerEntity player) {
      ArrayList var2 = Lists.newArrayList(player.newLoadedChunks);
      int var3 = 0;
      int var4 = this.chunkViewDistance;
      int var5 = (int)player.x >> 4;
      int var6 = (int)player.z >> 4;
      int var7 = 0;
      int var8 = 0;
      ChunkPos var9 = this.getChunk(var5, var6, true).pos;
      player.newLoadedChunks.clear();
      if (var2.contains(var9)) {
         player.newLoadedChunks.add(var9);
      }

      for(int var10 = 1; var10 <= var4 * 2; ++var10) {
         for(int var11 = 0; var11 < 2; ++var11) {
            int[] var12 = this.adjacentChunkCoords[var3++ % 4];

            for(int var13 = 0; var13 < var10; ++var13) {
               var7 += var12[0];
               var8 += var12[1];
               var9 = this.getChunk(var5 + var7, var6 + var8, true).pos;
               if (var2.contains(var9)) {
                  player.newLoadedChunks.add(var9);
               }
            }
         }
      }

      var3 %= 4;

      for(int var17 = 0; var17 < var4 * 2; ++var17) {
         var7 += this.adjacentChunkCoords[var3][0];
         var8 += this.adjacentChunkCoords[var3][1];
         var9 = this.getChunk(var5 + var7, var6 + var8, true).pos;
         if (var2.contains(var9)) {
            player.newLoadedChunks.add(var9);
         }
      }
   }

   public void onPlayerRemoved(ServerPlayerEntity player) {
      int var2 = (int)player.trackedX >> 4;
      int var3 = (int)player.trackedZ >> 4;

      for(int var4 = var2 - this.chunkViewDistance; var4 <= var2 + this.chunkViewDistance; ++var4) {
         for(int var5 = var3 - this.chunkViewDistance; var5 <= var3 + this.chunkViewDistance; ++var5) {
            ChunkMap.ChunkHolder var6 = this.getChunk(var4, var5, false);
            if (var6 != null) {
               var6.removePlayer(player);
            }
         }
      }

      this.players.remove(player);
   }

   private boolean isChunkWithinView(int chunkX, int chunkZ, int playerChunkX, int playerChunkZ, int chunkViewDistance) {
      int var6 = chunkX - playerChunkX;
      int var7 = chunkZ - playerChunkZ;
      if (var6 < -chunkViewDistance || var6 > chunkViewDistance) {
         return false;
      } else {
         return var7 >= -chunkViewDistance && var7 <= chunkViewDistance;
      }
   }

   public void onPlayerMoved(ServerPlayerEntity player) {
      int var2 = (int)player.x >> 4;
      int var3 = (int)player.z >> 4;
      double var4 = player.trackedX - player.x;
      double var6 = player.trackedZ - player.z;
      double var8 = var4 * var4 + var6 * var6;
      if (!(var8 < 64.0)) {
         int var10 = (int)player.trackedX >> 4;
         int var11 = (int)player.trackedZ >> 4;
         int var12 = this.chunkViewDistance;
         int var13 = var2 - var10;
         int var14 = var3 - var11;
         if (var13 != 0 || var14 != 0) {
            for(int var15 = var2 - var12; var15 <= var2 + var12; ++var15) {
               for(int var16 = var3 - var12; var16 <= var3 + var12; ++var16) {
                  if (!this.isChunkWithinView(var15, var16, var10, var11, var12)) {
                     this.getChunk(var15, var16, true).addPlayer(player);
                  }

                  if (!this.isChunkWithinView(var15 - var13, var16 - var14, var2, var3, var12)) {
                     ChunkMap.ChunkHolder var17 = this.getChunk(var15 - var13, var16 - var14, false);
                     if (var17 != null) {
                        var17.removePlayer(player);
                     }
                  }
               }
            }

            this.updateChunkTracking(player);
            player.trackedX = player.x;
            player.trackedZ = player.z;
         }
      }
   }

   public boolean isChunkWithinView(ServerPlayerEntity player, int chunkX, int chunkZ) {
      ChunkMap.ChunkHolder var4 = this.getChunk(chunkX, chunkZ, false);
      return var4 != null && var4.players.contains(player) && !player.newLoadedChunks.contains(var4.pos);
   }

   public void updateViewDistance(int chunkViewDistance) {
      chunkViewDistance = MathHelper.clamp(chunkViewDistance, 3, 32);
      if (chunkViewDistance != this.chunkViewDistance) {
         int var2 = chunkViewDistance - this.chunkViewDistance;

         for(ServerPlayerEntity var5 : Lists.newArrayList(this.players)) {
            int var6 = (int)var5.x >> 4;
            int var7 = (int)var5.z >> 4;
            if (var2 > 0) {
               for(int var12 = var6 - chunkViewDistance; var12 <= var6 + chunkViewDistance; ++var12) {
                  for(int var13 = var7 - chunkViewDistance; var13 <= var7 + chunkViewDistance; ++var13) {
                     ChunkMap.ChunkHolder var10 = this.getChunk(var12, var13, true);
                     if (!var10.players.contains(var5)) {
                        var10.addPlayer(var5);
                     }
                  }
               }
            } else {
               for(int var8 = var6 - this.chunkViewDistance; var8 <= var6 + this.chunkViewDistance; ++var8) {
                  for(int var9 = var7 - this.chunkViewDistance; var9 <= var7 + this.chunkViewDistance; ++var9) {
                     if (!this.isChunkWithinView(var8, var9, var6, var7, chunkViewDistance)) {
                        this.getChunk(var8, var9, true).removePlayer(var5);
                     }
                  }
               }
            }
         }

         this.chunkViewDistance = chunkViewDistance;
      }
   }

   public static int getViewDistance(int chunkViewDistance) {
      return chunkViewDistance * 16 - 16;
   }

   class ChunkHolder {
      private final List players = Lists.newArrayList();
      private final ChunkPos pos;
      private short[] dirtyBlocks = new short[64];
      private int blocksChanged;
      private int dirtySections;
      private long lastUpdateTime;

      public ChunkHolder(int chunkX, int chunkZ) {
         this.pos = new ChunkPos(chunkX, chunkZ);
         ChunkMap.this.getWorld().chunkCache.loadChunk(chunkX, chunkZ);
      }

      public void addPlayer(ServerPlayerEntity player) {
         if (this.players.contains(player)) {
            ChunkMap.LOGGER.debug("Failed to add player. {} already is in chunk {}, {}", new Object[]{player, this.pos.x, this.pos.z});
         } else {
            if (this.players.isEmpty()) {
               this.lastUpdateTime = ChunkMap.this.world.getTime();
            }

            this.players.add(player);
            player.newLoadedChunks.add(this.pos);
         }
      }

      public void removePlayer(ServerPlayerEntity player) {
         if (this.players.contains(player)) {
            WorldChunk var2 = ChunkMap.this.world.getChunkAt(this.pos.x, this.pos.z);
            if (var2.isPopulated()) {
               player.networkHandler.sendPacket(new WorldChunkS2CPacket(var2, true, 0));
            }

            this.players.remove(player);
            player.newLoadedChunks.remove(this.pos);
            if (this.players.isEmpty()) {
               long var3 = (long)this.pos.x + 2147483647L | (long)this.pos.z + 2147483647L << 32;
               this.updateInhabitedTime(var2);
               ChunkMap.this.chunks.remove(var3);
               ChunkMap.this.ticking.remove(this);
               if (this.blocksChanged > 0) {
                  ChunkMap.this.dirty.remove(this);
               }

               ChunkMap.this.getWorld().chunkCache.scheduleUnload(this.pos.x, this.pos.z);
            }
         }
      }

      public void updateInhabitedTime() {
         this.updateInhabitedTime(ChunkMap.this.world.getChunkAt(this.pos.x, this.pos.z));
      }

      private void updateInhabitedTime(WorldChunk chunk) {
         chunk.setInhabitedTime(chunk.getInhabitedTime() + ChunkMap.this.world.getTime() - this.lastUpdateTime);
         this.lastUpdateTime = ChunkMap.this.world.getTime();
      }

      public void onBlockChanged(int sectionX, int y, int sectionZ) {
         if (this.blocksChanged == 0) {
            ChunkMap.this.dirty.add(this);
         }

         this.dirtySections |= 1 << (y >> 4);
         if (this.blocksChanged < 64) {
            short var4 = (short)(sectionX << 12 | sectionZ << 8 | y);

            for(int var5 = 0; var5 < this.blocksChanged; ++var5) {
               if (this.dirtyBlocks[var5] == var4) {
                  return;
               }
            }

            this.dirtyBlocks[this.blocksChanged++] = var4;
         }
      }

      public void sendPacket(Packet packet) {
         for(int var2 = 0; var2 < this.players.size(); ++var2) {
            ServerPlayerEntity var3 = (ServerPlayerEntity)this.players.get(var2);
            if (!var3.newLoadedChunks.contains(this.pos)) {
               var3.networkHandler.sendPacket(packet);
            }
         }
      }

      public void sendChanges() {
         if (this.blocksChanged != 0) {
            if (this.blocksChanged == 1) {
               int var1 = (this.dirtyBlocks[0] >> 12 & 15) + this.pos.x * 16;
               int var2 = this.dirtyBlocks[0] & 255;
               int var3 = (this.dirtyBlocks[0] >> 8 & 15) + this.pos.z * 16;
               BlockPos var4 = new BlockPos(var1, var2, var3);
               this.sendPacket(new BlockUpdateS2CPacket(ChunkMap.this.world, var4));
               if (ChunkMap.this.world.getBlockState(var4).getBlock().hasBlockEntity()) {
                  this.sendBlockEntityUpdate(ChunkMap.this.world.getBlockEntity(var4));
               }
            } else if (this.blocksChanged == 64) {
               int var7 = this.pos.x * 16;
               int var9 = this.pos.z * 16;
               this.sendPacket(new WorldChunkS2CPacket(ChunkMap.this.world.getChunkAt(this.pos.x, this.pos.z), false, this.dirtySections));

               for(int var11 = 0; var11 < 16; ++var11) {
                  if ((this.dirtySections & 1 << var11) != 0) {
                     int var13 = var11 << 4;
                     List var5 = ChunkMap.this.world.getBlockEntities(var7, var13, var9, var7 + 16, var13 + 16, var9 + 16);

                     for(int var6 = 0; var6 < var5.size(); ++var6) {
                        this.sendBlockEntityUpdate((BlockEntity)var5.get(var6));
                     }
                  }
               }
            } else {
               this.sendPacket(new BlocksUpdateS2CPacket(this.blocksChanged, this.dirtyBlocks, ChunkMap.this.world.getChunkAt(this.pos.x, this.pos.z)));

               for(int var8 = 0; var8 < this.blocksChanged; ++var8) {
                  int var10 = (this.dirtyBlocks[var8] >> 12 & 15) + this.pos.x * 16;
                  int var12 = this.dirtyBlocks[var8] & 255;
                  int var14 = (this.dirtyBlocks[var8] >> 8 & 15) + this.pos.z * 16;
                  BlockPos var15 = new BlockPos(var10, var12, var14);
                  if (ChunkMap.this.world.getBlockState(var15).getBlock().hasBlockEntity()) {
                     this.sendBlockEntityUpdate(ChunkMap.this.world.getBlockEntity(var15));
                  }
               }
            }

            this.blocksChanged = 0;
            this.dirtySections = 0;
         }
      }

      private void sendBlockEntityUpdate(BlockEntity blockEntity) {
         if (blockEntity != null) {
            Packet var2 = blockEntity.createUpdatePacket();
            if (var2 != null) {
               this.sendPacket(var2);
            }
         }
      }
   }
}
