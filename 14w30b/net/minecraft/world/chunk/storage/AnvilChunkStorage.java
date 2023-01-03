package net.minecraft.world.chunk.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entities;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.resource.Identifier;
import net.minecraft.server.world.ScheduledTick;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkNibbleStorage;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.WorldChunkSection;
import net.minecraft.world.chunk.storage.io.FileIoCallback;
import net.minecraft.world.chunk.storage.io.FileIoThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilChunkStorage implements ChunkStorage, FileIoCallback {
   private static final Logger LOGGER = LogManager.getLogger();
   private List chunkSaveQueue = Lists.newArrayList();
   private Set queuedChunks = Sets.newHashSet();
   private Object lock = new Object();
   private final File dir;

   public AnvilChunkStorage(File dir) {
      this.dir = dir;
   }

   @Override
   public WorldChunk loadChunk(World world, int chunkX, int chunkZ) {
      NbtCompound var4 = null;
      ChunkPos var5 = new ChunkPos(chunkX, chunkZ);
      synchronized(this.lock) {
         if (this.queuedChunks.contains(var5)) {
            for(int var7 = 0; var7 < this.chunkSaveQueue.size(); ++var7) {
               if (((AnvilChunkStorage.ChunkNbt)this.chunkSaveQueue.get(var7)).pos.equals(var5)) {
                  var4 = ((AnvilChunkStorage.ChunkNbt)this.chunkSaveQueue.get(var7)).data;
                  break;
               }
            }
         }
      }

      if (var4 == null) {
         DataInputStream var10 = RegionIo.getChunkInputStream(this.dir, chunkX, chunkZ);
         if (var10 == null) {
            return null;
         }

         var4 = NbtIo.read(var10);
      }

      return this.loadChunk(world, chunkX, chunkZ, var4);
   }

   protected WorldChunk loadChunk(World world, int chunkX, int chunkZ, NbtCompound nbt) {
      if (!nbt.isType("Level", 10)) {
         LOGGER.error("Chunk file at " + chunkX + "," + chunkZ + " is missing level data, skipping");
         return null;
      } else if (!nbt.getCompound("Level").isType("Sections", 9)) {
         LOGGER.error("Chunk file at " + chunkX + "," + chunkZ + " is missing block data, skipping");
         return null;
      } else {
         WorldChunk var5 = this.createChunkFromNbt(world, nbt.getCompound("Level"));
         if (!var5.isAt(chunkX, chunkZ)) {
            LOGGER.error(
               "Chunk file at "
                  + chunkX
                  + ","
                  + chunkZ
                  + " is in the wrong location; relocating. (Expected "
                  + chunkX
                  + ", "
                  + chunkZ
                  + ", got "
                  + var5.chunkX
                  + ", "
                  + var5.chunkZ
                  + ")"
            );
            nbt.putInt("xPos", chunkX);
            nbt.putInt("zPos", chunkZ);
            var5 = this.createChunkFromNbt(world, nbt.getCompound("Level"));
         }

         return var5;
      }
   }

   @Override
   public void saveChunk(World world, WorldChunk chunk) {
      world.checkSessionLock();

      try {
         NbtCompound var3 = new NbtCompound();
         NbtCompound var4 = new NbtCompound();
         var3.put("Level", var4);
         this.writeChunkToNbt(chunk, world, var4);
         this.queueChunkSave(chunk.getPos(), var3);
      } catch (Exception var5) {
         var5.printStackTrace();
      }
   }

   protected void queueChunkSave(ChunkPos pos, NbtCompound nbt) {
      synchronized(this.lock) {
         if (this.queuedChunks.contains(pos)) {
            for(int var4 = 0; var4 < this.chunkSaveQueue.size(); ++var4) {
               if (((AnvilChunkStorage.ChunkNbt)this.chunkSaveQueue.get(var4)).pos.equals(pos)) {
                  this.chunkSaveQueue.set(var4, new AnvilChunkStorage.ChunkNbt(pos, nbt));
                  return;
               }
            }
         }

         this.chunkSaveQueue.add(new AnvilChunkStorage.ChunkNbt(pos, nbt));
         this.queuedChunks.add(pos);
         FileIoThread.getInstance().registerCallback(this);
      }
   }

   @Override
   public boolean run() {
      Object var1 = null;
      AnvilChunkStorage.ChunkNbt var6;
      synchronized(this.lock) {
         if (this.chunkSaveQueue.isEmpty()) {
            return false;
         }

         var6 = (AnvilChunkStorage.ChunkNbt)this.chunkSaveQueue.remove(0);
         this.queuedChunks.remove(var6.pos);
      }

      if (var6 != null) {
         try {
            this.saveChunk(var6);
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }

      return true;
   }

   private void saveChunk(AnvilChunkStorage.ChunkNbt chunkNbt) {
      DataOutputStream var2 = RegionIo.getChunkOutputStream(this.dir, chunkNbt.pos.x, chunkNbt.pos.z);
      NbtIo.write(chunkNbt.data, (DataOutput)var2);
      var2.close();
   }

   @Override
   public void saveEntities(World world, WorldChunk chunk) {
   }

   @Override
   public void tick() {
   }

   @Override
   public void save() {
      while(this.run()) {
      }
   }

   private void writeChunkToNbt(WorldChunk chunk, World world, NbtCompound nbt) {
      nbt.putByte("V", (byte)1);
      nbt.putInt("xPos", chunk.chunkX);
      nbt.putInt("zPos", chunk.chunkZ);
      nbt.putLong("LastUpdate", world.getTime());
      nbt.putIntArray("HeightMap", chunk.getHeightMap());
      nbt.putBoolean("TerrainPopulated", chunk.isTerrainPopulated());
      nbt.putBoolean("LightPopulated", chunk.isLightPopulated());
      nbt.putLong("InhabitedTime", chunk.getInhabitedTime());
      WorldChunkSection[] var4 = chunk.getSections();
      NbtList var5 = new NbtList();
      boolean var6 = !world.dimension.isDark();

      for(WorldChunkSection var10 : var4) {
         if (var10 != null) {
            NbtCompound var11 = new NbtCompound();
            var11.putByte("Y", (byte)(var10.getOffsetY() >> 4 & 0xFF));
            byte[] var12 = new byte[var10.getBlockData().length];
            ChunkNibbleStorage var13 = new ChunkNibbleStorage(var10.getBlockData().length, 4);
            ChunkNibbleStorage var14 = null;

            for(int var15 = 0; var15 < var10.getBlockData().length; ++var15) {
               char var16 = var10.getBlockData()[var15];
               int var17 = var15 & 15;
               int var18 = var15 >> 8 & 15;
               int var19 = var15 >> 4 & 15;
               if (var16 >> '\f' != 0) {
                  if (var14 == null) {
                     var14 = new ChunkNibbleStorage(var10.getBlockData().length, 4);
                  }

                  var14.set(var17, var18, var19, var16 >> '\f');
               }

               var12[var15] = (byte)(var16 >> 4 & 0xFF);
               var13.set(var17, var18, var19, var16 & 15);
            }

            var11.putByteArray("Blocks", var12);
            var11.putByteArray("Data", var13.getData());
            if (var14 != null) {
               var11.putByteArray("Add", var14.getData());
            }

            var11.putByteArray("BlockLight", var10.getBlockLightStorage().getData());
            if (var6) {
               var11.putByteArray("SkyLight", var10.getSkyLightStorage().getData());
            } else {
               var11.putByteArray("SkyLight", new byte[var10.getBlockLightStorage().getData().length]);
            }

            var5.add(var11);
         }
      }

      nbt.put("Sections", var5);
      nbt.putByteArray("Biomes", chunk.getBiomes());
      chunk.setContainsEntities(false);
      NbtList var20 = new NbtList();

      for(int var21 = 0; var21 < chunk.getEntitiesBySection().length; ++var21) {
         for(Entity var26 : chunk.getEntitiesBySection()[var21]) {
            NbtCompound var29 = new NbtCompound();
            if (var26.writeNbtNoRider(var29)) {
               chunk.setContainsEntities(true);
               var20.add(var29);
            }
         }
      }

      nbt.put("Entities", var20);
      NbtList var22 = new NbtList();

      for(BlockEntity var27 : chunk.getBlockEntities().values()) {
         NbtCompound var30 = new NbtCompound();
         var27.writeNbt(var30);
         var22.add(var30);
      }

      nbt.put("TileEntities", var22);
      List var25 = world.getScheduledTicks(chunk, false);
      if (var25 != null) {
         long var28 = world.getTime();
         NbtList var31 = new NbtList();

         for(ScheduledTick var33 : var25) {
            NbtCompound var34 = new NbtCompound();
            Identifier var35 = (Identifier)Block.REGISTRY.getKey(var33.getBlock());
            var34.putString("i", var35 == null ? "" : var35.toString());
            var34.putInt("x", var33.pos.getX());
            var34.putInt("y", var33.pos.getY());
            var34.putInt("z", var33.pos.getZ());
            var34.putInt("t", (int)(var33.time - var28));
            var34.putInt("p", var33.priority);
            var31.add(var34);
         }

         nbt.put("TileTicks", var31);
      }
   }

   private WorldChunk createChunkFromNbt(World world, NbtCompound nbt) {
      int var3 = nbt.getInt("xPos");
      int var4 = nbt.getInt("zPos");
      WorldChunk var5 = new WorldChunk(world, var3, var4);
      var5.setHeightmap(nbt.getIntArray("HeightMap"));
      var5.setTerrainPopulated(nbt.getBoolean("TerrainPopulated"));
      var5.setLightPopulated(nbt.getBoolean("LightPopulated"));
      var5.setInhabitedTime(nbt.getLong("InhabitedTime"));
      NbtList var6 = nbt.getList("Sections", 10);
      byte var7 = 16;
      WorldChunkSection[] var8 = new WorldChunkSection[var7];
      boolean var9 = !world.dimension.isDark();

      for(int var10 = 0; var10 < var6.size(); ++var10) {
         NbtCompound var11 = var6.getCompound(var10);
         byte var12 = var11.getByte("Y");
         WorldChunkSection var13 = new WorldChunkSection(var12 << 4, var9);
         byte[] var14 = var11.getByteArray("Blocks");
         ChunkNibbleStorage var15 = new ChunkNibbleStorage(var11.getByteArray("Data"), 4);
         ChunkNibbleStorage var16 = var11.isType("Add", 7) ? new ChunkNibbleStorage(var11.getByteArray("Add"), 4) : null;
         char[] var17 = new char[var14.length];

         for(int var18 = 0; var18 < var17.length; ++var18) {
            int var19 = var18 & 15;
            int var20 = var18 >> 8 & 15;
            int var21 = var18 >> 4 & 15;
            int var22 = var16 != null ? var16.get(var19, var20, var21) : 0;
            var17[var18] = (char)(var22 << 12 | (var14[var18] & 255) << 4 | var15.get(var19, var20, var21));
         }

         var13.setBlockData(var17);
         var13.setBlockLightStorage(new ChunkNibbleStorage(var11.getByteArray("BlockLight"), 4));
         if (var9) {
            var13.setSkyLightStorage(new ChunkNibbleStorage(var11.getByteArray("SkyLight"), 4));
         }

         var13.validateBlockCounters();
         var8[var12] = var13;
      }

      var5.setSections(var8);
      if (nbt.isType("Biomes", 7)) {
         var5.setBiomes(nbt.getByteArray("Biomes"));
      }

      NbtList var23 = nbt.getList("Entities", 10);
      if (var23 != null) {
         for(int var24 = 0; var24 < var23.size(); ++var24) {
            NbtCompound var26 = var23.getCompound(var24);
            Entity var29 = Entities.create(var26, world);
            var5.setContainsEntities(true);
            if (var29 != null) {
               var5.addEntity(var29);
               Entity var32 = var29;

               for(NbtCompound var35 = var26; var35.isType("Riding", 10); var35 = var35.getCompound("Riding")) {
                  Entity var37 = Entities.create(var35.getCompound("Riding"), world);
                  if (var37 != null) {
                     var5.addEntity(var37);
                     var32.startRiding(var37);
                  }

                  var32 = var37;
               }
            }
         }
      }

      NbtList var25 = nbt.getList("TileEntities", 10);
      if (var25 != null) {
         for(int var27 = 0; var27 < var25.size(); ++var27) {
            NbtCompound var30 = var25.getCompound(var27);
            BlockEntity var33 = BlockEntity.fromNbt(var30);
            if (var33 != null) {
               var5.addBlockEntity(var33);
            }
         }
      }

      if (nbt.isType("TileTicks", 9)) {
         NbtList var28 = nbt.getList("TileTicks", 10);
         if (var28 != null) {
            for(int var31 = 0; var31 < var28.size(); ++var31) {
               NbtCompound var34 = var28.getCompound(var31);
               Block var36;
               if (var34.isType("i", 8)) {
                  var36 = Block.byId(var34.getString("i"));
               } else {
                  var36 = Block.byRawId(var34.getInt("i"));
               }

               world.loadScheduledTick(new BlockPos(var34.getInt("x"), var34.getInt("y"), var34.getInt("z")), var36, var34.getInt("t"), var34.getInt("p"));
            }
         }
      }

      return var5;
   }

   static class ChunkNbt {
      public final ChunkPos pos;
      public final NbtCompound data;

      public ChunkNbt(ChunkPos pos, NbtCompound data) {
         this.pos = pos;
         this.data = data;
      }
   }
}
