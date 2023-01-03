package net.minecraft.network.packet.c2s.login;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerLoginPacketHandler;

public class HelloC2SPacket implements Packet {
   private GameProfile profile;

   public HelloC2SPacket() {
   }

   public HelloC2SPacket(GameProfile profile) {
      this.profile = profile;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.profile = new GameProfile(null, buffer.readString(16));
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeString(this.profile.getName());
   }

   public void handle(ServerLoginPacketHandler c_33nfqyvka) {
      c_33nfqyvka.handleHello(this);
   }

   public GameProfile getProfile() {
      return this.profile;
   }
}
