package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PlayerXpS2CPacket implements Packet {
   private float levelProgress;
   private int xp;
   private int level;

   public PlayerXpS2CPacket() {
   }

   public PlayerXpS2CPacket(float levelProgress, int xp, int level) {
      this.levelProgress = levelProgress;
      this.xp = xp;
      this.level = level;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.levelProgress = buffer.readFloat();
      this.level = buffer.readVarInt();
      this.xp = buffer.readVarInt();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeFloat(this.levelProgress);
      buffer.writeVarInt(this.level);
      buffer.writeVarInt(this.xp);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handlePlayerXp(this);
   }

   @Environment(EnvType.CLIENT)
   public float getLevelProgress() {
      return this.levelProgress;
   }

   @Environment(EnvType.CLIENT)
   public int getXp() {
      return this.xp;
   }

   @Environment(EnvType.CLIENT)
   public int getLevel() {
      return this.level;
   }
}
