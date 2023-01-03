package net.minecraft.scoreboard;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.scoreboard.criterion.ScoreboardCriterion;
import net.minecraft.scoreboard.team.AbstractTeam;
import net.minecraft.scoreboard.team.Team;
import net.minecraft.text.Formatting;
import net.minecraft.world.saved.SavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SavedScoreboardData extends SavedData {
   private static final Logger LOGGER = LogManager.getLogger();
   private Scoreboard scoreboard;
   private NbtCompound nbt;

   public SavedScoreboardData() {
      this("scoreboard");
   }

   public SavedScoreboardData(String string) {
      super(string);
   }

   public void setScoreboard(Scoreboard scoreboard) {
      this.scoreboard = scoreboard;
      if (this.nbt != null) {
         this.readNbt(this.nbt);
      }
   }

   @Override
   public void readNbt(NbtCompound nbt) {
      if (this.scoreboard == null) {
         this.nbt = nbt;
      } else {
         this.readObjectivesFromNbt(nbt.getList("Objectives", 10));
         this.readScoresFromNbt(nbt.getList("PlayerScores", 10));
         if (nbt.isType("DisplaySlots", 10)) {
            this.readDisplaySlotsFromNbt(nbt.getCompound("DisplaySlots"));
         }

         if (nbt.isType("Teams", 9)) {
            this.readTeamsFromNbt(nbt.getList("Teams", 10));
         }
      }
   }

   protected void readTeamsFromNbt(NbtList nbt) {
      for(int var2 = 0; var2 < nbt.size(); ++var2) {
         NbtCompound var3 = nbt.getCompound(var2);
         Team var4 = this.scoreboard.addTeam(var3.getString("Name"));
         var4.setDisplayName(var3.getString("DisplayName"));
         if (var3.isType("TeamColor", 8)) {
            var4.setColor(Formatting.byName(var3.getString("TeamColor")));
         }

         var4.setPrefix(var3.getString("Prefix"));
         var4.setSuffix(var3.getString("Suffix"));
         if (var3.isType("AllowFriendlyFire", 99)) {
            var4.setAllowFriendlyFire(var3.getBoolean("AllowFriendlyFire"));
         }

         if (var3.isType("SeeFriendlyInvisibles", 99)) {
            var4.setShowFriendlyInvisibles(var3.getBoolean("SeeFriendlyInvisibles"));
         }

         if (var3.isType("NameTagVisibility", 8)) {
            AbstractTeam.Visibility var5 = AbstractTeam.Visibility.byName(var3.getString("NameTagVisibility"));
            if (var5 != null) {
               var4.setNameTagVisibility(var5);
            }
         }

         if (var3.isType("DeathMessageVisibility", 8)) {
            AbstractTeam.Visibility var6 = AbstractTeam.Visibility.byName(var3.getString("DeathMessageVisibility"));
            if (var6 != null) {
               var4.setDeathMessageVisibility(var6);
            }
         }

         this.readTeamMembersFromNbt(var4, var3.getList("Players", 8));
      }
   }

   protected void readTeamMembersFromNbt(Team team, NbtList nbt) {
      for(int var3 = 0; var3 < nbt.size(); ++var3) {
         this.scoreboard.addMemberToTeam(nbt.getString(var3), team.getName());
      }
   }

   protected void readDisplaySlotsFromNbt(NbtCompound nbt) {
      for(int var2 = 0; var2 < 19; ++var2) {
         if (nbt.isType("slot_" + var2, 8)) {
            String var3 = nbt.getString("slot_" + var2);
            ScoreboardObjective var4 = this.scoreboard.getObjective(var3);
            this.scoreboard.setDisplayObjective(var2, var4);
         }
      }
   }

   protected void readObjectivesFromNbt(NbtList nbt) {
      for(int var2 = 0; var2 < nbt.size(); ++var2) {
         NbtCompound var3 = nbt.getCompound(var2);
         ScoreboardCriterion var4 = (ScoreboardCriterion)ScoreboardCriterion.BY_NAME.get(var3.getString("CriteriaName"));
         if (var4 != null) {
            ScoreboardObjective var5 = this.scoreboard.createObjective(var3.getString("Name"), var4);
            var5.setDisplayName(var3.getString("DisplayName"));
            var5.setRenderType(ScoreboardCriterion.RenderType.byName(var3.getString("RenderType")));
         }
      }
   }

   protected void readScoresFromNbt(NbtList nbt) {
      for(int var2 = 0; var2 < nbt.size(); ++var2) {
         NbtCompound var3 = nbt.getCompound(var2);
         ScoreboardObjective var4 = this.scoreboard.getObjective(var3.getString("Objective"));
         ScoreboardScore var5 = this.scoreboard.getScore(var3.getString("Name"), var4);
         var5.set(var3.getInt("Score"));
         if (var3.contains("Locked")) {
            var5.setLocked(var3.getBoolean("Locked"));
         }
      }
   }

   @Override
   public void writeNbt(NbtCompound nbt) {
      if (this.scoreboard == null) {
         LOGGER.warn("Tried to save scoreboard without having a scoreboard...");
      } else {
         nbt.put("Objectives", this.objectivesToNbt());
         nbt.put("PlayerScores", this.scoresToNbt());
         nbt.put("Teams", this.teamsToNbt());
         this.writeDisplaySlots(nbt);
      }
   }

   protected NbtList teamsToNbt() {
      NbtList var1 = new NbtList();

      for(Team var4 : this.scoreboard.getTeams()) {
         NbtCompound var5 = new NbtCompound();
         var5.putString("Name", var4.getName());
         var5.putString("DisplayName", var4.getDisplayName());
         if (var4.getColor().getIndex() >= 0) {
            var5.putString("TeamColor", var4.getColor().getName());
         }

         var5.putString("Prefix", var4.getPrefix());
         var5.putString("Suffix", var4.getSuffix());
         var5.putBoolean("AllowFriendlyFire", var4.allowFriendlyFire());
         var5.putBoolean("SeeFriendlyInvisibles", var4.showFriendlyInvisibles());
         var5.putString("NameTagVisibility", var4.getNameTagVisibility().name);
         var5.putString("DeathMessageVisibility", var4.getDeathMessageVisibility().name);
         NbtList var6 = new NbtList();

         for(String var8 : var4.getMembers()) {
            var6.add(new NbtString(var8));
         }

         var5.put("Players", var6);
         var1.add(var5);
      }

      return var1;
   }

   protected void writeDisplaySlots(NbtCompound nbt) {
      NbtCompound var2 = new NbtCompound();
      boolean var3 = false;

      for(int var4 = 0; var4 < 19; ++var4) {
         ScoreboardObjective var5 = this.scoreboard.getDisplayObjective(var4);
         if (var5 != null) {
            var2.putString("slot_" + var4, var5.getName());
            var3 = true;
         }
      }

      if (var3) {
         nbt.put("DisplaySlots", var2);
      }
   }

   protected NbtList objectivesToNbt() {
      NbtList var1 = new NbtList();

      for(ScoreboardObjective var4 : this.scoreboard.getObjectives()) {
         if (var4.getCriterion() != null) {
            NbtCompound var5 = new NbtCompound();
            var5.putString("Name", var4.getName());
            var5.putString("CriteriaName", var4.getCriterion().getName());
            var5.putString("DisplayName", var4.getDisplayName());
            var5.putString("RenderType", var4.getRenderType().getName());
            var1.add(var5);
         }
      }

      return var1;
   }

   protected NbtList scoresToNbt() {
      NbtList var1 = new NbtList();

      for(ScoreboardScore var4 : this.scoreboard.getScores()) {
         if (var4.getObjective() != null) {
            NbtCompound var5 = new NbtCompound();
            var5.putString("Name", var4.getOwner());
            var5.putString("Objective", var4.getObjective().getName());
            var5.putInt("Score", var4.get());
            var5.putBoolean("Locked", var4.isLocked());
            var1.add(var5);
         }
      }

      return var1;
   }
}
