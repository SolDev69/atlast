package net.minecraft.network.packet.c2s.play;

import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.handler.ServerPlayPacketHandler;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class ClientSettingsC2SPacket implements Packet {
   private String language;
   private int viewDistance;
   private PlayerEntity.ChatVisibility chatVisibility;
   private boolean chatColors;
   private int difficulty;

   public ClientSettingsC2SPacket() {
   }

   @Environment(EnvType.CLIENT)
   public ClientSettingsC2SPacket(String language, int viewDistance, PlayerEntity.ChatVisibility chatVisibility, boolean chatColors, int difficulty) {
      this.language = language;
      this.viewDistance = viewDistance;
      this.chatVisibility = chatVisibility;
      this.chatColors = chatColors;
      this.difficulty = difficulty;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.language = buffer.readString(7);
      this.viewDistance = buffer.readByte();
      this.chatVisibility = PlayerEntity.ChatVisibility.byIndex(buffer.readByte());
      this.chatColors = buffer.readBoolean();
      this.difficulty = buffer.readUnsignedByte();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeString(this.language);
      buffer.writeByte(this.viewDistance);
      buffer.writeByte(this.chatVisibility.getIndex());
      buffer.writeBoolean(this.chatColors);
      buffer.writeByte(this.difficulty);
   }

   public void handle(ServerPlayPacketHandler c_02lgcirvj) {
      c_02lgcirvj.handleClientSettings(this);
   }

   public String getLanguage() {
      return this.language;
   }

   public PlayerEntity.ChatVisibility getChatVisibility() {
      return this.chatVisibility;
   }

   public boolean getChatColors() {
      return this.chatColors;
   }

   public int getViewDistance() {
      return this.difficulty;
   }
}
