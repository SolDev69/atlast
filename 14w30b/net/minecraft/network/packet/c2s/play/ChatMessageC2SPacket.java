package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;

public class ChatMessageC2SPacket implements Packet {
   private String message;

   public ChatMessageC2SPacket() {
   }

   public ChatMessageC2SPacket(String message) {
      if (message.length() > 100) {
         message = message.substring(0, 100);
      }

      this.message = message;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.message = buffer.readString(100);
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeString(this.message);
   }

   public void handle(ServerPlayPacketHandler c_02lgcirvj) {
      c_02lgcirvj.handleChatMessage(this);
   }

   public String getMessage() {
      return this.message;
   }
}
