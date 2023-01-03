package net.minecraft.network.packet.s2c.play;

import java.util.Collection;
import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.map.MapDecoration;
import net.minecraft.world.map.SavedMapData;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class MapDataS2CPacket implements Packet {
   private int id;
   private byte scale;
   private MapDecoration[] decorations;
   private int dirtyMinX;
   private int dirtyMinY;
   private int dirtyWidth;
   private int dirtyHeight;
   private byte[] colors;

   public MapDataS2CPacket() {
   }

   public MapDataS2CPacket(int id, byte scale, Collection decorations, byte[] colors, int dirtyMinX, int dirtyMinY, int dirtyWidth, int dirtyHeight) {
      this.id = id;
      this.scale = scale;
      this.decorations = decorations.toArray(new MapDecoration[decorations.size()]);
      this.dirtyMinX = dirtyMinX;
      this.dirtyMinY = dirtyMinY;
      this.dirtyWidth = dirtyWidth;
      this.dirtyHeight = dirtyHeight;
      this.colors = new byte[dirtyWidth * dirtyHeight];

      for(int var9 = 0; var9 < dirtyWidth; ++var9) {
         for(int var10 = 0; var10 < dirtyHeight; ++var10) {
            this.colors[var9 + var10 * dirtyWidth] = colors[dirtyMinX + var9 + (dirtyMinY + var10) * 128];
         }
      }
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readVarInt();
      this.scale = buffer.readByte();
      this.decorations = new MapDecoration[buffer.readVarInt()];

      for(int var2 = 0; var2 < this.decorations.length; ++var2) {
         short var3 = (short)buffer.readByte();
         this.decorations[var2] = new MapDecoration((byte)(var3 >> 4 & 15), buffer.readByte(), buffer.readByte(), (byte)(var3 & 15));
      }

      this.dirtyWidth = buffer.readUnsignedByte();
      if (this.dirtyWidth > 0) {
         this.dirtyHeight = buffer.readUnsignedByte();
         this.dirtyMinX = buffer.readUnsignedByte();
         this.dirtyMinY = buffer.readUnsignedByte();
         this.colors = buffer.readByteArray();
      }
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.id);
      buffer.writeByte(this.scale);
      buffer.writeVarInt(this.decorations.length);

      for(MapDecoration var5 : this.decorations) {
         buffer.writeByte((var5.getType() & 15) << 4 | var5.getRotation() & 15);
         buffer.writeByte(var5.getX());
         buffer.writeByte(var5.getY());
      }

      buffer.writeByte(this.dirtyWidth);
      if (this.dirtyWidth > 0) {
         buffer.writeByte(this.dirtyHeight);
         buffer.writeByte(this.dirtyMinX);
         buffer.writeByte(this.dirtyMinY);
         buffer.writeByteArray(this.colors);
      }
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleMapData(this);
   }

   @Environment(EnvType.CLIENT)
   public int getId() {
      return this.id;
   }

   @Environment(EnvType.CLIENT)
   public void apply(SavedMapData mapData) {
      mapData.scale = this.scale;
      mapData.decorations.clear();

      for(int var2 = 0; var2 < this.decorations.length; ++var2) {
         MapDecoration var3 = this.decorations[var2];
         mapData.decorations.put("icon-" + var2, var3);
      }

      for(int var4 = 0; var4 < this.dirtyWidth; ++var4) {
         for(int var5 = 0; var5 < this.dirtyHeight; ++var5) {
            mapData.colors[this.dirtyMinX + var4 + (this.dirtyMinY + var5) * 128] = this.colors[var4 + var5 * this.dirtyWidth];
         }
      }
   }
}
