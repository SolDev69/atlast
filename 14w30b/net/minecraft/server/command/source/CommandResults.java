package net.minecraft.server.command.source;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.exception.EntityNotFoundException;

public class CommandResults {
   private static final int TYPE_COUNT = CommandResults.Type.values().length;
   private static final String[] EMPTY = new String[TYPE_COUNT];
   private String[] lastSourceByType = EMPTY;
   private String[] lastObjectiveByType = EMPTY;

   public void add(CommandSource source, CommandResults.Type type, int result) {
      String var4 = this.lastSourceByType[type.getIndex()];
      if (var4 != null) {
         String var5;
         try {
            var5 = Command.parseEntityName(source, var4);
         } catch (EntityNotFoundException var10) {
            return;
         }

         String var6 = this.lastObjectiveByType[type.getIndex()];
         if (var6 != null) {
            Scoreboard var7 = source.getSourceWorld().getScoreboard();
            ScoreboardObjective var8 = var7.getObjective(var6);
            if (var8 != null) {
               if (var7.hasScore(var5, var8)) {
                  ScoreboardScore var9 = var7.getScore(var5, var8);
                  var9.set(result);
               }
            }
         }
      }
   }

   public void readNbt(NbtCompound nbt) {
      if (nbt.isType("CommandStats", 10)) {
         NbtCompound var2 = nbt.getCompound("CommandStats");

         for(CommandResults.Type var6 : CommandResults.Type.values()) {
            String var7 = var6.getName() + "Name";
            String var8 = var6.getName() + "Objective";
            if (var2.isType(var7, 8) && var2.isType(var8, 8)) {
               String var9 = var2.getString(var7);
               String var10 = var2.getString(var8);
               updateSourceAndObjective(this, var6, var9, var10);
            }
         }
      }
   }

   public void writeNbt(NbtCompound nbt) {
      NbtCompound var2 = new NbtCompound();

      for(CommandResults.Type var6 : CommandResults.Type.values()) {
         String var7 = this.lastSourceByType[var6.getIndex()];
         String var8 = this.lastObjectiveByType[var6.getIndex()];
         if (var7 != null && var8 != null) {
            var2.putString(var6.getName() + "Name", var7);
            var2.putString(var6.getName() + "Objective", var8);
         }
      }

      if (!var2.isEmpty()) {
         nbt.put("CommandStats", var2);
      }
   }

   public static void updateSourceAndObjective(CommandResults results, CommandResults.Type type, String source, String objective) {
      if (source != null && source.length() != 0 && objective != null && objective.length() != 0) {
         if (results.lastSourceByType == EMPTY || results.lastObjectiveByType == EMPTY) {
            results.lastSourceByType = new String[TYPE_COUNT];
            results.lastObjectiveByType = new String[TYPE_COUNT];
         }

         results.lastSourceByType[type.getIndex()] = source;
         results.lastObjectiveByType[type.getIndex()] = objective;
      } else {
         clearSourceAndObjective(results, type);
      }
   }

   private static void clearSourceAndObjective(CommandResults results, CommandResults.Type type) {
      if (results.lastSourceByType != EMPTY && results.lastObjectiveByType != EMPTY) {
         results.lastSourceByType[type.getIndex()] = null;
         results.lastObjectiveByType[type.getIndex()] = null;
         boolean var2 = true;

         for(CommandResults.Type var6 : CommandResults.Type.values()) {
            if (results.lastSourceByType[var6.getIndex()] != null && results.lastObjectiveByType[var6.getIndex()] != null) {
               var2 = false;
               break;
            }
         }

         if (var2) {
            results.lastSourceByType = EMPTY;
            results.lastObjectiveByType = EMPTY;
         }
      }
   }

   public void copy(CommandResults results) {
      for(CommandResults.Type var5 : CommandResults.Type.values()) {
         updateSourceAndObjective(this, var5, results.lastSourceByType[var5.getIndex()], results.lastObjectiveByType[var5.getIndex()]);
      }
   }

   public static enum Type {
      SUCCESS_COUNT(0, "SuccessCount"),
      AFFECTED_BLOCKS(1, "AffectedBlocks"),
      AFFECTED_ENTITIES(2, "AffectedEntities"),
      AFFECTED_ITEMS(3, "AffectedItems"),
      QUERY_RESULT(4, "QueryResult");

      final int index;
      final String name;

      private Type(int index, String name) {
         this.index = index;
         this.name = name;
      }

      public int getIndex() {
         return this.index;
      }

      public String getName() {
         return this.name;
      }

      public static String[] getNames() {
         String[] var0 = new String[values().length];
         int var1 = 0;

         for(CommandResults.Type var5 : values()) {
            var0[var1++] = var5.getName();
         }

         return var0;
      }

      public static CommandResults.Type byName(String name) {
         for(CommandResults.Type var4 : values()) {
            if (var4.getName().equals(name)) {
               return var4;
            }
         }

         return null;
      }
   }
}
