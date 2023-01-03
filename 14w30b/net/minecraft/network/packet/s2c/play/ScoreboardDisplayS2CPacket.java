package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class ScoreboardDisplayS2CPacket implements Packet {
   private int slot;
   private String objective;

   public ScoreboardDisplayS2CPacket() {
   }

   public ScoreboardDisplayS2CPacket(int slot, ScoreboardObjective objective) {
      this.slot = slot;
      if (objective == null) {
         this.objective = "";
      } else {
         this.objective = objective.getName();
      }
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.slot = buffer.readByte();
      this.objective = buffer.readString(16);
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeByte(this.slot);
      buffer.writeString(this.objective);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleScoreboardDisplay(this);
   }

   @Environment(EnvType.CLIENT)
   public int getSlot() {
      return this.slot;
   }

   @Environment(EnvType.CLIENT)
   public String getObjective() {
      return this.objective;
   }
}
