package net.minecraft.network.packet.s2c.login;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.network.handler.ClientLoginPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class LoginSuccessS2CPacket implements Packet {
   private GameProfile profile;

   public LoginSuccessS2CPacket() {
   }

   public LoginSuccessS2CPacket(GameProfile profile) {
      this.profile = profile;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      String var2 = buffer.readString(36);
      String var3 = buffer.readString(16);
      UUID var4 = UUID.fromString(var2);
      this.profile = new GameProfile(var4, var3);
   }

   @Override
   public void read(PacketByteBuf buffer) {
      UUID var2 = this.profile.getId();
      buffer.writeString(var2 == null ? "" : var2.toString());
      buffer.writeString(this.profile.getName());
   }

   public void handle(ClientLoginPacketHandler c_67vhogdbn) {
      c_67vhogdbn.handleLoginSuccess(this);
   }

   @Environment(EnvType.CLIENT)
   public GameProfile getProfile() {
      return this.profile;
   }
}
