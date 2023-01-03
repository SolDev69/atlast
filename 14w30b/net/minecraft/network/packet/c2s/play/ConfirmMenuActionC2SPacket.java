package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class ConfirmMenuActionC2SPacket implements Packet {
   private int menuId;
   private short actionId;
   private boolean accepted;

   public ConfirmMenuActionC2SPacket() {
   }

   @Environment(EnvType.CLIENT)
   public ConfirmMenuActionC2SPacket(int menuId, short actionId, boolean accepted) {
      this.menuId = menuId;
      this.actionId = actionId;
      this.accepted = accepted;
   }

   public void handle(ServerPlayPacketHandler c_02lgcirvj) {
      c_02lgcirvj.handleConfirmMenuAction(this);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.menuId = buffer.readByte();
      this.actionId = buffer.readShort();
      this.accepted = buffer.readByte() != 0;
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeByte(this.menuId);
      buffer.writeShort(this.actionId);
      buffer.writeByte(this.accepted ? 1 : 0);
   }

   public int getMenuId() {
      return this.menuId;
   }

   public short getActionId() {
      return this.actionId;
   }
}
