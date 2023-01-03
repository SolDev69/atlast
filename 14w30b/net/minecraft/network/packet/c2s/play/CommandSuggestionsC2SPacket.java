package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;
import org.apache.commons.lang3.StringUtils;

public class CommandSuggestionsC2SPacket implements Packet {
   private String command;

   public CommandSuggestionsC2SPacket() {
   }

   public CommandSuggestionsC2SPacket(String command) {
      this.command = command;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.command = buffer.readString(32767);
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeString(StringUtils.substring(this.command, 0, 32767));
   }

   public void handle(ServerPlayPacketHandler c_02lgcirvj) {
      c_02lgcirvj.handleCommandSuggestions(this);
   }

   public String getCommand() {
      return this.command;
   }
}
