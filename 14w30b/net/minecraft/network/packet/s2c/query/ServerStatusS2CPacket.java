package net.minecraft.network.packet.s2c.query;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.network.handler.ClientQueryPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.ServerStatus;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class ServerStatusS2CPacket implements Packet {
   private static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(ServerStatus.Version.class, new ServerStatus.Version.Serializer())
      .registerTypeAdapter(ServerStatus.Players.class, new ServerStatus.Players.Serializer())
      .registerTypeAdapter(ServerStatus.class, new ServerStatus.Serializer())
      .registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
      .registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
      .registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory())
      .create();
   private ServerStatus status;

   public ServerStatusS2CPacket() {
   }

   public ServerStatusS2CPacket(ServerStatus status) {
      this.status = status;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.status = (ServerStatus)GSON.fromJson(buffer.readString(32767), ServerStatus.class);
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeString(GSON.toJson(this.status));
   }

   public void handle(ClientQueryPacketHandler c_74cpriuuv) {
      c_74cpriuuv.handleServerStatus(this);
   }

   @Environment(EnvType.CLIENT)
   public ServerStatus getServerStatus() {
      return this.status;
   }
}
