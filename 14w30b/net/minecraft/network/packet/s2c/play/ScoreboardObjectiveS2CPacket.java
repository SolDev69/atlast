package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.criterion.ScoreboardCriterion;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class ScoreboardObjectiveS2CPacket implements Packet {
   private String name;
   private String displayName;
   private ScoreboardCriterion.RenderType renderType;
   private int action;

   public ScoreboardObjectiveS2CPacket() {
   }

   public ScoreboardObjectiveS2CPacket(ScoreboardObjective objective, int action) {
      this.name = objective.getName();
      this.displayName = objective.getDisplayName();
      this.renderType = objective.getCriterion().getRenderType();
      this.action = action;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.name = buffer.readString(16);
      this.action = buffer.readByte();
      if (this.action == 0 || this.action == 2) {
         this.displayName = buffer.readString(32);
         this.renderType = ScoreboardCriterion.RenderType.byName(buffer.readString(16));
      }
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeString(this.name);
      buffer.writeByte(this.action);
      if (this.action == 0 || this.action == 2) {
         buffer.writeString(this.displayName);
         buffer.writeString(this.renderType.getName());
      }
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleScoreboardObjective(this);
   }

   @Environment(EnvType.CLIENT)
   public String getName() {
      return this.name;
   }

   @Environment(EnvType.CLIENT)
   public String getDisplayName() {
      return this.displayName;
   }

   @Environment(EnvType.CLIENT)
   public int getAction() {
      return this.action;
   }

   @Environment(EnvType.CLIENT)
   public ScoreboardCriterion.RenderType getRenderType() {
      return this.renderType;
   }
}
