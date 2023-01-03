package net.minecraft.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.handler.PacketHandler;

public interface Packet {
   void write(PacketByteBuf buffer);

   void read(PacketByteBuf buffer);

   void handle(PacketHandler handler);
}
