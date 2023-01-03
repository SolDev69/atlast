package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class TabListS2CPacket implements Packet {
   private Text header;
   private Text footer;

   public TabListS2CPacket() {
   }

   public TabListS2CPacket(Text header) {
      this.header = header;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.header = buffer.readText();
      this.footer = buffer.readText();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeText(this.header);
      buffer.writeText(this.footer);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleTabList(this);
   }

   @Environment(EnvType.CLIENT)
   public Text getHeader() {
      return this.header;
   }

   @Environment(EnvType.CLIENT)
   public Text getFooter() {
      return this.footer;
   }
}
