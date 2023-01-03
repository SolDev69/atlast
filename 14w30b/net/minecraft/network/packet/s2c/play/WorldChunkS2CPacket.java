package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.chunk.ChunkNibbleStorage;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.WorldChunkSection;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class WorldChunkS2CPacket implements Packet {
   private int chunkX;
   private int chunkZ;
   private WorldChunkS2CPacket.ChunkData data;
   private boolean full;

   public WorldChunkS2CPacket() {
   }

   public WorldChunkS2CPacket(WorldChunk chunk, boolean full, int dirtySections) {
      this.chunkX = chunk.chunkX;
      this.chunkZ = chunk.chunkZ;
      this.full = full;
      this.data = packChunkData(chunk, full, !chunk.getWorld().dimension.isDark(), dirtySections);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.chunkX = buffer.readInt();
      this.chunkZ = buffer.readInt();
      this.full = buffer.readBoolean();
      this.data = new WorldChunkS2CPacket.ChunkData();
      this.data.sectionsWithData = buffer.readShort();
      this.data.rawChunkData = buffer.readByteArray();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeInt(this.chunkX);
      buffer.writeInt(this.chunkZ);
      buffer.writeBoolean(this.full);
      buffer.writeShort((short)(this.data.sectionsWithData & 65535));
      buffer.writeByteArray(this.data.rawChunkData);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleWorldChunk(this);
   }

   @Environment(EnvType.CLIENT)
   public byte[] getRawChunkData() {
      return this.data.rawChunkData;
   }

   protected static int getChunkDataSize(int sectionsWithData, boolean doubleHeight, boolean full) {
      int var3 = sectionsWithData * 8192;
      int var4 = sectionsWithData * 4096 / 2;
      int var5 = doubleHeight ? sectionsWithData * 4096 / 2 : 0;
      int var6 = full ? 256 : 0;
      return var3 + var4 + var5 + var6;
   }

   public static WorldChunkS2CPacket.ChunkData packChunkData(WorldChunk chunk, boolean full, boolean doubleHeight, int dirtySections) {
      WorldChunkSection[] var4 = chunk.getSections();
      WorldChunkS2CPacket.ChunkData var5 = new WorldChunkS2CPacket.ChunkData();

      for(int var6 = 0; var6 < var4.length; ++var6) {
         if (var4[var6] != null && (!full || !var4[var6].isEmpty()) && (dirtySections & 1 << var6) != 0) {
            var5.sectionsWithData |= 1 << var6;
         }
      }

      var5.rawChunkData = new byte[getChunkDataSize(Integer.bitCount(var5.sectionsWithData), doubleHeight, full)];
      int var13 = 0;

      for(int var7 = 0; var7 < var4.length; ++var7) {
         if (var4[var7] != null && (!full || !var4[var7].isEmpty()) && (dirtySections & 1 << var7) != 0) {
            char[] var8 = var4[var7].getBlockData();

            for(char var12 : var8) {
               var5.rawChunkData[var13++] = (byte)(var12 & 255);
               var5.rawChunkData[var13++] = (byte)(var12 >> '\b' & 0xFF);
            }
         }
      }

      for(int var16 = 0; var16 < var4.length; ++var16) {
         if (var4[var16] != null && (!full || !var4[var16].isEmpty()) && (dirtySections & 1 << var16) != 0) {
            ChunkNibbleStorage var19 = var4[var16].getBlockLightStorage();
            System.arraycopy(var19.getData(), 0, var5.rawChunkData, var13, var19.getData().length);
            var13 += var19.getData().length;
         }
      }

      if (doubleHeight) {
         for(int var17 = 0; var17 < var4.length; ++var17) {
            if (var4[var17] != null && (!full || !var4[var17].isEmpty()) && (dirtySections & 1 << var17) != 0) {
               ChunkNibbleStorage var20 = var4[var17].getSkyLightStorage();
               System.arraycopy(var20.getData(), 0, var5.rawChunkData, var13, var20.getData().length);
               var13 += var20.getData().length;
            }
         }
      }

      if (full) {
         byte[] var18 = chunk.getBiomes();
         System.arraycopy(var18, 0, var5.rawChunkData, var13, var18.length);
         var13 += var18.length;
      }

      return var5;
   }

   @Environment(EnvType.CLIENT)
   public int getChunkX() {
      return this.chunkX;
   }

   @Environment(EnvType.CLIENT)
   public int getChunkZ() {
      return this.chunkZ;
   }

   @Environment(EnvType.CLIENT)
   public int getSectionsWithData() {
      return this.data.sectionsWithData;
   }

   @Environment(EnvType.CLIENT)
   public boolean isFull() {
      return this.full;
   }

   public static class ChunkData {
      public byte[] rawChunkData;
      public int sectionsWithData;
   }
}
