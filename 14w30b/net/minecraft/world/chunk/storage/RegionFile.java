package net.minecraft.world.chunk.storage;

import com.google.common.collect.Lists;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import net.minecraft.server.MinecraftServer;

public class RegionFile {
   private static final byte[] BLOCK_BUFFER = new byte[4096];
   private final File file;
   private RandomAccessFile randomAccessFile;
   private final int[] chunkBlockInfo = new int[1024];
   private final int[] chunkSaveTimes = new int[1024];
   private List blockEmptyFlags;
   private int bytesWritten;
   private long lastModifiedTime;

   public RegionFile(File file) {
      this.file = file;
      this.bytesWritten = 0;

      try {
         if (file.exists()) {
            this.lastModifiedTime = file.lastModified();
         }

         this.randomAccessFile = new RandomAccessFile(file, "rw");
         if (this.randomAccessFile.length() < 4096L) {
            for(int var2 = 0; var2 < 1024; ++var2) {
               this.randomAccessFile.writeInt(0);
            }

            for(int var7 = 0; var7 < 1024; ++var7) {
               this.randomAccessFile.writeInt(0);
            }

            this.bytesWritten += 8192;
         }

         if ((this.randomAccessFile.length() & 4095L) != 0L) {
            for(int var8 = 0; (long)var8 < (this.randomAccessFile.length() & 4095L); ++var8) {
               this.randomAccessFile.write(0);
            }
         }

         int var9 = (int)this.randomAccessFile.length() / 4096;
         this.blockEmptyFlags = Lists.newArrayListWithCapacity(var9);

         for(int var3 = 0; var3 < var9; ++var3) {
            this.blockEmptyFlags.add(true);
         }

         this.blockEmptyFlags.set(0, false);
         this.blockEmptyFlags.set(1, false);
         this.randomAccessFile.seek(0L);

         for(int var10 = 0; var10 < 1024; ++var10) {
            int var4 = this.randomAccessFile.readInt();
            this.chunkBlockInfo[var10] = var4;
            if (var4 != 0 && (var4 >> 8) + (var4 & 0xFF) <= this.blockEmptyFlags.size()) {
               for(int var5 = 0; var5 < (var4 & 0xFF); ++var5) {
                  this.blockEmptyFlags.set((var4 >> 8) + var5, false);
               }
            }
         }

         for(int var11 = 0; var11 < 1024; ++var11) {
            int var12 = this.randomAccessFile.readInt();
            this.chunkSaveTimes[var11] = var12;
         }
      } catch (IOException var6) {
         var6.printStackTrace();
      }
   }

   public synchronized DataInputStream getChunkInputStream(int chunkX, int chunkZ) {
      if (this.isOutsideRegion(chunkX, chunkZ)) {
         return null;
      } else {
         try {
            int var3 = this.getChunkBlockInfo(chunkX, chunkZ);
            if (var3 == 0) {
               return null;
            } else {
               int var4 = var3 >> 8;
               int var5 = var3 & 0xFF;
               if (var4 + var5 > this.blockEmptyFlags.size()) {
                  return null;
               } else {
                  this.randomAccessFile.seek((long)(var4 * 4096));
                  int var6 = this.randomAccessFile.readInt();
                  if (var6 > 4096 * var5) {
                     return null;
                  } else if (var6 <= 0) {
                     return null;
                  } else {
                     byte var7 = this.randomAccessFile.readByte();
                     if (var7 == 1) {
                        byte[] var10 = new byte[var6 - 1];
                        this.randomAccessFile.read(var10);
                        return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(var10))));
                     } else if (var7 == 2) {
                        byte[] var8 = new byte[var6 - 1];
                        this.randomAccessFile.read(var8);
                        return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(var8))));
                     } else {
                        return null;
                     }
                  }
               }
            }
         } catch (IOException var9) {
            return null;
         }
      }
   }

   public DataOutputStream getChunkOutputStream(int chunkX, int chunkZ) {
      return this.isOutsideRegion(chunkX, chunkZ) ? null : new DataOutputStream(new DeflaterOutputStream(new RegionFile.ChunkOutputStream(chunkX, chunkZ)));
   }

   protected synchronized void writeChunkData(int chunkX, int chunkZ, byte[] data, int size) {
      try {
         int var5 = this.getChunkBlockInfo(chunkX, chunkZ);
         int var6 = var5 >> 8;
         int var7 = var5 & 0xFF;
         int var8 = (size + 5) / 4096 + 1;
         if (var8 >= 256) {
            return;
         }

         if (var6 != 0 && var7 == var8) {
            this.writeChunkData(var6, data, size);
         } else {
            for(int var9 = 0; var9 < var7; ++var9) {
               this.blockEmptyFlags.set(var6 + var9, true);
            }

            int var15 = this.blockEmptyFlags.indexOf(true);
            int var10 = 0;
            if (var15 != -1) {
               for(int var11 = var15; var11 < this.blockEmptyFlags.size(); ++var11) {
                  if (var10 != 0) {
                     if (this.blockEmptyFlags.get(var11)) {
                        ++var10;
                     } else {
                        var10 = 0;
                     }
                  } else if (this.blockEmptyFlags.get(var11)) {
                     var15 = var11;
                     var10 = 1;
                  }

                  if (var10 >= var8) {
                     break;
                  }
               }
            }

            if (var10 >= var8) {
               var6 = var15;
               this.writeChunkBlockInfo(chunkX, chunkZ, var15 << 8 | var8);

               for(int var17 = 0; var17 < var8; ++var17) {
                  this.blockEmptyFlags.set(var6 + var17, false);
               }

               this.writeChunkData(var6, data, size);
            } else {
               this.randomAccessFile.seek(this.randomAccessFile.length());
               var6 = this.blockEmptyFlags.size();

               for(int var16 = 0; var16 < var8; ++var16) {
                  this.randomAccessFile.write(BLOCK_BUFFER);
                  this.blockEmptyFlags.add(false);
               }

               this.bytesWritten += 4096 * var8;
               this.writeChunkData(var6, data, size);
               this.writeChunkBlockInfo(chunkX, chunkZ, var6 << 8 | var8);
            }
         }

         this.writeChunkSaveTime(chunkX, chunkZ, (int)(MinecraftServer.getTimeMillis() / 1000L));
      } catch (IOException var12) {
         var12.printStackTrace();
      }
   }

   private void writeChunkData(int blockOffset, byte[] data, int size) {
      this.randomAccessFile.seek((long)(blockOffset * 4096));
      this.randomAccessFile.writeInt(size + 1);
      this.randomAccessFile.writeByte(2);
      this.randomAccessFile.write(data, 0, size);
   }

   private boolean isOutsideRegion(int chunkX, int chunkZ) {
      return chunkX < 0 || chunkX >= 32 || chunkZ < 0 || chunkZ >= 32;
   }

   private int getChunkBlockInfo(int chunkX, int chunkZ) {
      return this.chunkBlockInfo[chunkX + chunkZ * 32];
   }

   public boolean hasChunkData(int chunkX, int chunkZ) {
      return this.getChunkBlockInfo(chunkX, chunkZ) != 0;
   }

   private void writeChunkBlockInfo(int chunkX, int chunkZ, int blockInfo) {
      this.chunkBlockInfo[chunkX + chunkZ * 32] = blockInfo;
      this.randomAccessFile.seek((long)((chunkX + chunkZ * 32) * 4));
      this.randomAccessFile.writeInt(blockInfo);
   }

   private void writeChunkSaveTime(int chunkX, int chunkZ, int timeSeconds) {
      this.chunkSaveTimes[chunkX + chunkZ * 32] = timeSeconds;
      this.randomAccessFile.seek((long)(4096 + (chunkX + chunkZ * 32) * 4));
      this.randomAccessFile.writeInt(timeSeconds);
   }

   public void close() {
      if (this.randomAccessFile != null) {
         this.randomAccessFile.close();
      }
   }

   class ChunkOutputStream extends ByteArrayOutputStream {
      private int chunkX;
      private int chunkZ;

      public ChunkOutputStream(int chunkX, int chunkZ) {
         super(8096);
         this.chunkX = chunkX;
         this.chunkZ = chunkZ;
      }

      @Override
      public void close() {
         RegionFile.this.writeChunkData(this.chunkX, this.chunkZ, this.buf, this.count);
      }
   }
}
