package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class ConfirmMenuActionS2CPacket implements Packet {
   private int menuId;
   private short actionId;
   private boolean accepted;

   public ConfirmMenuActionS2CPacket() {
   }

   public ConfirmMenuActionS2CPacket(int menuId, short actionId, boolean accepted) {
      this.menuId = menuId;
      this.actionId = actionId;
      this.accepted = accepted;
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleConfirmMenuAction(this);
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.menuId = buffer.readUnsignedByte();
      this.actionId = buffer.readShort();
      this.accepted = buffer.readBoolean();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeByte(this.menuId);
      buffer.writeShort(this.actionId);
      buffer.writeBoolean(this.accepted);
   }

   @Environment(EnvType.CLIENT)
   public int getMenuId() {
      return this.menuId;
   }

   @Environment(EnvType.CLIENT)
   public short getActionId() {
      return this.actionId;
   }

   @Environment(EnvType.CLIENT)
   public boolean getAccepted() {
      return this.accepted;
   }
}
