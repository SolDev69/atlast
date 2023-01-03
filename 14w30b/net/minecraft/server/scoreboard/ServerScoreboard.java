package net.minecraft.server.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.scoreboard.SavedScoreboardData;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.scoreboard.team.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

public class ServerScoreboard extends Scoreboard {
   private final MinecraftServer server;
   private final Set displayedObjectives = Sets.newHashSet();
   private SavedScoreboardData savedData;

   public ServerScoreboard(MinecraftServer server) {
      this.server = server;
   }

   @Override
   public void onScoreUpdated(ScoreboardScore score) {
      super.onScoreUpdated(score);
      if (this.displayedObjectives.contains(score.getObjective())) {
         this.server.getPlayerManager().sendToAll(new ScoreboardScoreS2CPacket(score));
      }

      this.markDirty();
   }

   @Override
   public void onScoresRemoved(String owner) {
      super.onScoresRemoved(owner);
      this.server.getPlayerManager().sendToAll(new ScoreboardScoreS2CPacket(owner));
      this.markDirty();
   }

   @Override
   public void onScoreRemoved(String owner, ScoreboardObjective objective) {
      super.onScoreRemoved(owner, objective);
      this.server.getPlayerManager().sendToAll(new ScoreboardScoreS2CPacket(owner, objective));
      this.markDirty();
   }

   @Override
   public void setDisplayObjective(int slot, ScoreboardObjective objective) {
      ScoreboardObjective var3 = this.getDisplayObjective(slot);
      super.setDisplayObjective(slot, objective);
      if (var3 != objective && var3 != null) {
         if (this.getDisplaySlot(var3) > 0) {
            this.server.getPlayerManager().sendToAll(new ScoreboardDisplayS2CPacket(slot, objective));
         } else {
            this.stopDisplayingObjective(var3);
         }
      }

      if (objective != null) {
         if (this.displayedObjectives.contains(objective)) {
            this.server.getPlayerManager().sendToAll(new ScoreboardDisplayS2CPacket(slot, objective));
         } else {
            this.startDisplayingObjective(objective);
         }
      }

      this.markDirty();
   }

   @Override
   public boolean addMemberToTeam(String member, String teamName) {
      if (super.addMemberToTeam(member, teamName)) {
         Team var3 = this.getTeam(teamName);
         this.server.getPlayerManager().sendToAll(new TeamS2CPacket(var3, Arrays.asList(member), 3));
         this.markDirty();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void removeMemberFromTeam(String member, Team team) {
      super.removeMemberFromTeam(member, team);
      this.server.getPlayerManager().sendToAll(new TeamS2CPacket(team, Arrays.asList(member), 4));
      this.markDirty();
   }

   @Override
   public void onObjectiveCreated(ScoreboardObjective objective) {
      super.onObjectiveCreated(objective);
      this.markDirty();
   }

   @Override
   public void onObjectiveUpdated(ScoreboardObjective objective) {
      super.onObjectiveUpdated(objective);
      if (this.displayedObjectives.contains(objective)) {
         this.server.getPlayerManager().sendToAll(new ScoreboardObjectiveS2CPacket(objective, 2));
      }

      this.markDirty();
   }

   @Override
   public void onObjectiveRemoved(ScoreboardObjective objective) {
      super.onObjectiveRemoved(objective);
      if (this.displayedObjectives.contains(objective)) {
         this.stopDisplayingObjective(objective);
      }

      this.markDirty();
   }

   @Override
   public void onTeamAdded(Team team) {
      super.onTeamAdded(team);
      this.server.getPlayerManager().sendToAll(new TeamS2CPacket(team, 0));
      this.markDirty();
   }

   @Override
   public void onTeamUpdated(Team team) {
      super.onTeamUpdated(team);
      this.server.getPlayerManager().sendToAll(new TeamS2CPacket(team, 2));
      this.markDirty();
   }

   @Override
   public void onTeamRemoved(Team team) {
      super.onTeamRemoved(team);
      this.server.getPlayerManager().sendToAll(new TeamS2CPacket(team, 1));
      this.markDirty();
   }

   public void setSavedData(SavedScoreboardData savedData) {
      this.savedData = savedData;
   }

   protected void markDirty() {
      if (this.savedData != null) {
         this.savedData.markDirty();
      }
   }

   public List createStartDisplayingObjectivePackets(ScoreboardObjective objective) {
      ArrayList var2 = Lists.newArrayList();
      var2.add(new ScoreboardObjectiveS2CPacket(objective, 0));

      for(int var3 = 0; var3 < 19; ++var3) {
         if (this.getDisplayObjective(var3) == objective) {
            var2.add(new ScoreboardDisplayS2CPacket(var3, objective));
         }
      }

      for(ScoreboardScore var4 : this.getScores(objective)) {
         var2.add(new ScoreboardScoreS2CPacket(var4));
      }

      return var2;
   }

   public void startDisplayingObjective(ScoreboardObjective objective) {
      List var2 = this.createStartDisplayingObjectivePackets(objective);

      for(ServerPlayerEntity var4 : this.server.getPlayerManager().players) {
         for(Packet var6 : var2) {
            var4.networkHandler.sendPacket(var6);
         }
      }

      this.displayedObjectives.add(objective);
   }

   public List createStopDisplayingObjectivePackets(ScoreboardObjective objective) {
      ArrayList var2 = Lists.newArrayList();
      var2.add(new ScoreboardObjectiveS2CPacket(objective, 1));

      for(int var3 = 0; var3 < 19; ++var3) {
         if (this.getDisplayObjective(var3) == objective) {
            var2.add(new ScoreboardDisplayS2CPacket(var3, objective));
         }
      }

      return var2;
   }

   public void stopDisplayingObjective(ScoreboardObjective objective) {
      List var2 = this.createStopDisplayingObjectivePackets(objective);

      for(ServerPlayerEntity var4 : this.server.getPlayerManager().players) {
         for(Packet var6 : var2) {
            var4.networkHandler.sendPacket(var6);
         }
      }

      this.displayedObjectives.remove(objective);
   }

   public int getDisplaySlot(ScoreboardObjective objective) {
      int var2 = 0;

      for(int var3 = 0; var3 < 19; ++var3) {
         if (this.getDisplayObjective(var3) == objective) {
            ++var2;
         }
      }

      return var2;
   }
}
