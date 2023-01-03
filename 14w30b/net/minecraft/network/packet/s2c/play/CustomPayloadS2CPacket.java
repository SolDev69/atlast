package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class CustomPayloadS2CPacket implements Packet {
   private String channel;
   private byte[] data;

   public CustomPayloadS2CPacket() {
   }

   public CustomPayloadS2CPacket(String channel, PacketByteBuf data) {
      this(channel, data.getBytes());
   }

   public CustomPayloadS2CPacket(String channel, byte[] data) {
      this.channel = channel;
      this.data = data;
      if (data.length > 1048576) {
         throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
      }
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.channel = buffer.readString(20);
      int var2 = buffer.readVarInt();
      if (var2 >= 0 && var2 <= 1048576) {
         this.data = new byte[var2];
         buffer.readBytes(this.data);
      } else {
         throw new IOException("Payload may not be larger than 1048576 bytes");
      }
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeString(this.channel);
      buffer.writeVarInt(this.data.length);
      buffer.writeBytes(this.data);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleCustomPayload(this);
   }

   @Environment(EnvType.CLIENT)
   public String getChannel() {
      return this.channel;
   }

   @Environment(EnvType.CLIENT)
   public byte[] getData() {
      return this.data;
   }
}
