package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class MenuDataS2CPacket implements Packet {
   private int menuId;
   private int dataId;
   private int value;

   public MenuDataS2CPacket() {
   }

   public MenuDataS2CPacket(int menuId, int dataId, int value) {
      this.menuId = menuId;
      this.dataId = dataId;
      this.value = value;
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleMenuData(this);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.menuId = buffer.readUnsignedByte();
      this.dataId = buffer.readShort();
      this.value = buffer.readShort();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeByte(this.menuId);
      buffer.writeShort(this.dataId);
      buffer.writeShort(this.value);
   }

   @Environment(EnvType.CLIENT)
   public int getMenuId() {
      return this.menuId;
   }

   @Environment(EnvType.CLIENT)
   public int getDataId() {
      return this.dataId;
   }

   @Environment(EnvType.CLIENT)
   public int getValue() {
      return this.value;
   }
}
