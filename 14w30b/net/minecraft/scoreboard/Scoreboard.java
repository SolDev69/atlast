package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.scoreboard.criterion.ScoreboardCriterion;
import net.minecraft.scoreboard.team.Team;
import net.minecraft.text.Formatting;

public class Scoreboard {
   private final Map objectivesByName = Maps.newHashMap();
   private final Map objectivesByCreaterion = Maps.newHashMap();
   private final Map scores = Maps.newHashMap();
   private final ScoreboardObjective[] displayObjectives = new ScoreboardObjective[19];
   private final Map teamsByName = Maps.newHashMap();
   private final Map teamsByMember = Maps.newHashMap();
   private static String[] displayLocations = null;

   public ScoreboardObjective getObjective(String name) {
      return (ScoreboardObjective)this.objectivesByName.get(name);
   }

   public ScoreboardObjective createObjective(String name, ScoreboardCriterion criterion) {
      ScoreboardObjective var3 = this.getObjective(name);
      if (var3 != null) {
         throw new IllegalArgumentException("An objective with the name '" + name + "' already exists!");
      } else {
         var3 = new ScoreboardObjective(this, name, criterion);
         Object var4 = (List)this.objectivesByCreaterion.get(criterion);
         if (var4 == null) {
            var4 = Lists.newArrayList();
            this.objectivesByCreaterion.put(criterion, var4);
         }

         var4.add(var3);
         this.objectivesByName.put(name, var3);
         this.onObjectiveCreated(var3);
         return var3;
      }
   }

   public Collection getObjectives(ScoreboardCriterion criterion) {
      Collection var2 = (Collection)this.objectivesByCreaterion.get(criterion);
      return var2 == null ? Lists.newArrayList() : Lists.newArrayList(var2);
   }

   public boolean hasScore(String owner, ScoreboardObjective objective) {
      Map var3 = (Map)this.scores.get(owner);
      if (var3 == null) {
         return false;
      } else {
         ScoreboardScore var4 = (ScoreboardScore)var3.get(objective);
         return var4 != null;
      }
   }

   public ScoreboardScore getScore(String owner, ScoreboardObjective objective) {
      Object var3 = (Map)this.scores.get(owner);
      if (var3 == null) {
         var3 = Maps.newHashMap();
         this.scores.put(owner, var3);
      }

      ScoreboardScore var4 = (ScoreboardScore)var3.get(objective);
      if (var4 == null) {
         var4 = new ScoreboardScore(this, objective, owner);
         var3.put(objective, var4);
      }

      return var4;
   }

   public Collection getScores(ScoreboardObjective objective) {
      ArrayList var2 = Lists.newArrayList();

      for(Map var4 : this.scores.values()) {
         ScoreboardScore var5 = (ScoreboardScore)var4.get(objective);
         if (var5 != null) {
            var2.add(var5);
         }
      }

      Collections.sort(var2, ScoreboardScore.COMPARATOR);
      return var2;
   }

   public Collection getObjectives() {
      return this.objectivesByName.values();
   }

   public Collection getScoreOwners() {
      return this.scores.keySet();
   }

   public void removeScore(String owner, ScoreboardObjective objective) {
      if (objective == null) {
         Map var3 = (Map)this.scores.remove(owner);
         if (var3 != null) {
            this.onScoresRemoved(owner);
         }
      } else {
         Map var6 = (Map)this.scores.get(owner);
         if (var6 != null) {
            ScoreboardScore var4 = (ScoreboardScore)var6.remove(objective);
            if (var6.size() < 1) {
               Map var5 = (Map)this.scores.remove(owner);
               if (var5 != null) {
                  this.onScoresRemoved(owner);
               }
            } else if (var4 != null) {
               this.onScoreRemoved(owner, objective);
            }
         }
      }
   }

   public Collection getScores() {
      Collection var1 = this.scores.values();
      ArrayList var2 = Lists.newArrayList();

      for(Map var4 : var1) {
         var2.addAll(var4.values());
      }

      return var2;
   }

   public Map getScores(String owner) {
      Object var2 = (Map)this.scores.get(owner);
      if (var2 == null) {
         var2 = Maps.newHashMap();
      }

      return (Map)var2;
   }

   public void removeObjective(ScoreboardObjective objective) {
      this.objectivesByName.remove(objective.getName());

      for(int var2 = 0; var2 < 19; ++var2) {
         if (this.getDisplayObjective(var2) == objective) {
            this.setDisplayObjective(var2, null);
         }
      }

      List var5 = (List)this.objectivesByCreaterion.get(objective.getCriterion());
      if (var5 != null) {
         var5.remove(objective);
      }

      for(Map var4 : this.scores.values()) {
         var4.remove(objective);
      }

      this.onObjectiveRemoved(objective);
   }

   public void setDisplayObjective(int slot, ScoreboardObjective objective) {
      this.displayObjectives[slot] = objective;
   }

   public ScoreboardObjective getDisplayObjective(int slot) {
      return this.displayObjectives[slot];
   }

   public Team getTeam(String name) {
      return (Team)this.teamsByName.get(name);
   }

   public Team addTeam(String name) {
      Team var2 = this.getTeam(name);
      if (var2 != null) {
         throw new IllegalArgumentException("A team with the name '" + name + "' already exists!");
      } else {
         var2 = new Team(this, name);
         this.teamsByName.put(name, var2);
         this.onTeamAdded(var2);
         return var2;
      }
   }

   public void removeTeam(Team team) {
      this.teamsByName.remove(team.getName());

      for(String var3 : team.getMembers()) {
         this.teamsByMember.remove(var3);
      }

      this.onTeamRemoved(team);
   }

   public boolean addMemberToTeam(String member, String teamName) {
      if (!this.teamsByName.containsKey(teamName)) {
         return false;
      } else {
         Team var3 = this.getTeam(teamName);
         if (this.getTeamOfMember(member) != null) {
            this.removeMemberFromTeam(member);
         }

         this.teamsByMember.put(member, var3);
         var3.getMembers().add(member);
         return true;
      }
   }

   public boolean removeMemberFromTeam(String member) {
      Team var2 = this.getTeamOfMember(member);
      if (var2 != null) {
         this.removeMemberFromTeam(member, var2);
         return true;
      } else {
         return false;
      }
   }

   public void removeMemberFromTeam(String member, Team team) {
      if (this.getTeamOfMember(member) != team) {
         throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + team.getName() + "'.");
      } else {
         this.teamsByMember.remove(member);
         team.getMembers().remove(member);
      }
   }

   public Collection getTeamNames() {
      return this.teamsByName.keySet();
   }

   public Collection getTeams() {
      return this.teamsByName.values();
   }

   public Team getTeamOfMember(String playerName) {
      return (Team)this.teamsByMember.get(playerName);
   }

   public void onObjectiveCreated(ScoreboardObjective objective) {
   }

   public void onObjectiveUpdated(ScoreboardObjective objective) {
   }

   public void onObjectiveRemoved(ScoreboardObjective objective) {
   }

   public void onScoreUpdated(ScoreboardScore score) {
   }

   public void onScoresRemoved(String owner) {
   }

   public void onScoreRemoved(String owner, ScoreboardObjective objective) {
   }

   public void onTeamAdded(Team team) {
   }

   public void onTeamUpdated(Team team) {
   }

   public void onTeamRemoved(Team team) {
   }

   public static String getDisplayLocation(int slot) {
      switch(slot) {
         case 0:
            return "list";
         case 1:
            return "sidebar";
         case 2:
            return "belowName";
         default:
            if (slot >= 3 && slot <= 18) {
               Formatting var1 = Formatting.byIndex(slot - 3);
               if (var1 != null && var1 != Formatting.RESET) {
                  return "sidebar.team." + var1.getName();
               }
            }

            return null;
      }
   }

   public static int getDisplaySlot(String location) {
      if (location.equalsIgnoreCase("list")) {
         return 0;
      } else if (location.equalsIgnoreCase("sidebar")) {
         return 1;
      } else if (location.equalsIgnoreCase("belowName")) {
         return 2;
      } else {
         if (location.startsWith("sidebar.team.")) {
            String var1 = location.substring("sidebar.team.".length());
            Formatting var2 = Formatting.byName(var1);
            if (var2 != null && var2.getIndex() >= 0) {
               return var2.getIndex() + 3;
            }
         }

         return -1;
      }
   }

   public static String[] getDisplayLocations() {
      if (displayLocations == null) {
         displayLocations = new String[19];

         for(int var0 = 0; var0 < 19; ++var0) {
            displayLocations[var0] = getDisplayLocation(var0);
         }
      }

      return displayLocations;
   }
}
