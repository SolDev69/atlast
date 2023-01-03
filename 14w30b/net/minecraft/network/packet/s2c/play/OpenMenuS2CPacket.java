package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class OpenMenuS2CPacket implements Packet {
   private int menuId;
   private String menuType;
   private Text displayName;
   private int size;
   private int ownerId;

   public OpenMenuS2CPacket() {
   }

   public OpenMenuS2CPacket(int menuId, String menuType, Text displayName) {
      this(menuId, menuType, displayName, 0);
   }

   public OpenMenuS2CPacket(int menuId, String menuType, Text displayName, int size) {
      this.menuId = menuId;
      this.menuType = menuType;
      this.displayName = displayName;
      this.size = size;
   }

   public OpenMenuS2CPacket(int menuId, String menuType, Text displayName, int size, int ownerId) {
      this(menuId, menuType, displayName, size);
      this.ownerId = ownerId;
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleOpenMenu(this);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.menuId = buffer.readUnsignedByte();
      this.menuType = buffer.readString(32);
      this.displayName = buffer.readText();
      this.size = buffer.readUnsignedByte();
      if (this.menuType.equals("EntityHorse")) {
         this.ownerId = buffer.readInt();
      }
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeByte(this.menuId);
      buffer.writeString(this.menuType);
      buffer.writeText(this.displayName);
      buffer.writeByte(this.size);
      if (this.menuType.equals("EntityHorse")) {
         buffer.writeInt(this.ownerId);
      }
   }

   @Environment(EnvType.CLIENT)
   public int getMenuId() {
      return this.menuId;
   }

   @Environment(EnvType.CLIENT)
   public String getMenuType() {
      return this.menuType;
   }

   @Environment(EnvType.CLIENT)
   public Text getDisplayName() {
      return this.displayName;
   }

   @Environment(EnvType.CLIENT)
   public int getSize() {
      return this.size;
   }

   @Environment(EnvType.CLIENT)
   public int getOwnerId() {
      return this.ownerId;
   }

   @Environment(EnvType.CLIENT)
   public boolean hasSize() {
      return this.size > 0;
   }
}
