package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class ScoreboardScoreS2CPacket implements Packet {
   private String owner = "";
   private String objective = "";
   private int score;
   private ScoreboardScoreS2CPacket.Action action;

   public ScoreboardScoreS2CPacket() {
   }

   public ScoreboardScoreS2CPacket(ScoreboardScore score) {
      this.owner = score.getOwner();
      this.objective = score.getObjective().getName();
      this.score = score.get();
      this.action = ScoreboardScoreS2CPacket.Action.CHANGE;
   }

   public ScoreboardScoreS2CPacket(String owner) {
      this.owner = owner;
      this.objective = "";
      this.score = 0;
      this.action = ScoreboardScoreS2CPacket.Action.REMOVE;
   }

   public ScoreboardScoreS2CPacket(String owner, ScoreboardObjective objective) {
      this.owner = owner;
      this.objective = objective.getName();
      this.score = 0;
      this.action = ScoreboardScoreS2CPacket.Action.REMOVE;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.owner = buffer.readString(40);
      this.action = (ScoreboardScoreS2CPacket.Action)buffer.readEnum(ScoreboardScoreS2CPacket.Action.class);
      this.objective = buffer.readString(16);
      if (this.action != ScoreboardScoreS2CPacket.Action.REMOVE) {
         this.score = buffer.readVarInt();
      }
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeString(this.owner);
      buffer.writeEnum(this.action);
      buffer.writeString(this.objective);
      if (this.action != ScoreboardScoreS2CPacket.Action.REMOVE) {
         buffer.writeVarInt(this.score);
      }
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleScoreboardScore(this);
   }

   @Environment(EnvType.CLIENT)
   public String getOwner() {
      return this.owner;
   }

   @Environment(EnvType.CLIENT)
   public String getObjective() {
      return this.objective;
   }

   @Environment(EnvType.CLIENT)
   public int getScore() {
      return this.score;
   }

   @Environment(EnvType.CLIENT)
   public ScoreboardScoreS2CPacket.Action getAction() {
      return this.action;
   }

   public static enum Action {
      CHANGE,
      REMOVE;
   }
}
