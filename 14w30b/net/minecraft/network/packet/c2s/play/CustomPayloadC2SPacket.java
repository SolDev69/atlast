package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class CustomPayloadC2SPacket implements Packet {
   private String channel;
   private byte[] data;

   public CustomPayloadC2SPacket() {
   }

   @Environment(EnvType.CLIENT)
   public CustomPayloadC2SPacket(String channel, PacketByteBuf data) {
      this(channel, data.getBytes());
   }

   @Environment(EnvType.CLIENT)
   public CustomPayloadC2SPacket(String channel, byte[] data) {
      this.channel = channel;
      this.data = data;
      if (data.length > 32767) {
         throw new IllegalArgumentException("Payload may not be larger than 32767 bytes");
      }
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.channel = buffer.readString(20);
      int var2 = buffer.readVarInt();
      if (var2 >= 0 && var2 <= 32767) {
         this.data = new byte[var2];
         buffer.readBytes(this.data);
      } else {
         throw new IOException("Payload may not be larger than 32767 bytes");
      }
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeString(this.channel);
      buffer.writeVarInt(this.data.length);
      buffer.writeBytes(this.data);
   }

   public void handle(ServerPlayPacketHandler c_02lgcirvj) {
      c_02lgcirvj.handleCustomPayload(this);
   }

   public String getChannel() {
      return this.channel;
   }

   public byte[] getData() {
      return this.data;
   }
}
