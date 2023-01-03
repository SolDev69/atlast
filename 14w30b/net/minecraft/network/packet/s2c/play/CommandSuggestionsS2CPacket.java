package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class CommandSuggestionsS2CPacket implements Packet {
   private String[] suggestions;

   public CommandSuggestionsS2CPacket() {
   }

   public CommandSuggestionsS2CPacket(String[] suggestions) {
      this.suggestions = suggestions;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.suggestions = new String[buffer.readVarInt()];

      for(int var2 = 0; var2 < this.suggestions.length; ++var2) {
         this.suggestions[var2] = buffer.readString(32767);
      }
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.suggestions.length);

      for(String var5 : this.suggestions) {
         buffer.writeString(var5);
      }
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleCommandSuggestions(this);
   }

   @Environment(EnvType.CLIENT)
   public String[] getSuggestions() {
      return this.suggestions;
   }
}
