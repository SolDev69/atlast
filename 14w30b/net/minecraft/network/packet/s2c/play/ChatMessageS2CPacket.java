package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class ChatMessageS2CPacket implements Packet {
   private Text message;
   private byte type;

   public ChatMessageS2CPacket() {
   }

   public ChatMessageS2CPacket(Text mesage) {
      this(mesage, (byte)1);
   }

   public ChatMessageS2CPacket(Text message, byte type) {
      this.message = message;
      this.type = type;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.message = buffer.readText();
      this.type = buffer.readByte();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeText(this.message);
      buffer.writeByte(this.type);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleChatMessage(this);
   }

   @Environment(EnvType.CLIENT)
   public Text getMessage() {
      return this.message;
   }

   public boolean isSystemMessage() {
      return this.type == 1 || this.type == 2;
   }

   @Environment(EnvType.CLIENT)
   public byte getType() {
      return this.type;
   }
}
