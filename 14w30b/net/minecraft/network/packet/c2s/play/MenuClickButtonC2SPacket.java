package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class MenuClickButtonC2SPacket implements Packet {
   private int menuId;
   private int buttonId;

   public MenuClickButtonC2SPacket() {
   }

   @Environment(EnvType.CLIENT)
   public MenuClickButtonC2SPacket(int menuId, int buttonId) {
      this.menuId = menuId;
      this.buttonId = buttonId;
   }

   public void handle(ServerPlayPacketHandler c_02lgcirvj) {
      c_02lgcirvj.handleMenuClickButton(this);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.menuId = buffer.readByte();
      this.buttonId = buffer.readByte();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeByte(this.menuId);
      buffer.writeByte(this.buttonId);
   }

   public int getMenuId() {
      return this.menuId;
   }

   public int getButtonId() {
      return this.buttonId;
   }
}
