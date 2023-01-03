package net.minecraft.network.packet.s2c.play;

import java.util.List;
import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.chunk.WorldChunk;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class WorldChunksS2CPacket implements Packet {
   private int[] chunkX;
   private int[] chunkZ;
   private WorldChunkS2CPacket.ChunkData[] data;
   private boolean doubleHeight;

   public WorldChunksS2CPacket() {
   }

   public WorldChunksS2CPacket(List chunks) {
      int var2 = chunks.size();
      this.chunkX = new int[var2];
      this.chunkZ = new int[var2];
      this.data = new WorldChunkS2CPacket.ChunkData[var2];
      this.doubleHeight = !((WorldChunk)chunks.get(0)).getWorld().dimension.isDark();

      for(int var3 = 0; var3 < var2; ++var3) {
         WorldChunk var4 = (WorldChunk)chunks.get(var3);
         WorldChunkS2CPacket.ChunkData var5 = WorldChunkS2CPacket.packChunkData(var4, true, this.doubleHeight, 65535);
         this.chunkX[var3] = var4.chunkX;
         this.chunkZ[var3] = var4.chunkZ;
         this.data[var3] = var5;
      }
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.doubleHeight = buffer.readBoolean();
      int var2 = buffer.readVarInt();
      this.chunkX = new int[var2];
      this.chunkZ = new int[var2];
      this.data = new WorldChunkS2CPacket.ChunkData[var2];

      for(int var3 = 0; var3 < var2; ++var3) {
         this.chunkX[var3] = buffer.readInt();
         this.chunkZ[var3] = buffer.readInt();
         this.data[var3] = new WorldChunkS2CPacket.ChunkData();
         this.data[var3].sectionsWithData = buffer.readShort() & '\uffff';
         this.data[var3].rawChunkData = new byte[WorldChunkS2CPacket.getChunkDataSize(
            Integer.bitCount(this.data[var3].sectionsWithData), this.doubleHeight, true
         )];
      }

      for(int var4 = 0; var4 < var2; ++var4) {
         buffer.readBytes(this.data[var4].rawChunkData);
      }
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeBoolean(this.doubleHeight);
      buffer.writeVarInt(this.data.length);

      for(int var2 = 0; var2 < this.chunkX.length; ++var2) {
         buffer.writeInt(this.chunkX[var2]);
         buffer.writeInt(this.chunkZ[var2]);
         buffer.writeShort((short)(this.data[var2].sectionsWithData & 65535));
      }

      for(int var3 = 0; var3 < this.chunkX.length; ++var3) {
         buffer.writeBytes(this.data[var3].rawChunkData);
      }
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleWorldChunks(this);
   }

   @Environment(EnvType.CLIENT)
   public int getChunkX(int index) {
      return this.chunkX[index];
   }

   @Environment(EnvType.CLIENT)
   public int getChunkZ(int index) {
      return this.chunkZ[index];
   }

   @Environment(EnvType.CLIENT)
   public int getChunkCount() {
      return this.chunkX.length;
   }

   @Environment(EnvType.CLIENT)
   public byte[] getRawChunkData(int index) {
      return this.data[index].rawChunkData;
   }

   @Environment(EnvType.CLIENT)
   public int getSectionsWithData(int index) {
      return this.data[index].sectionsWithData;
   }
}
