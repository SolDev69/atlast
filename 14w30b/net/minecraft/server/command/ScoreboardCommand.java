package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.scoreboard.criterion.ScoreboardCriterion;
import net.minecraft.scoreboard.team.AbstractTeam;
import net.minecraft.scoreboard.team.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.CommandSyntaxException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.Formatting;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class ScoreboardCommand extends Command {
   @Override
   public String getName() {
      return "scoreboard";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.scoreboard.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (!this.runWildcard(source, args)) {
         if (args.length < 1) {
            throw new IncorrectUsageException("commands.scoreboard.usage");
         } else {
            if (args[0].equalsIgnoreCase("objectives")) {
               if (args.length == 1) {
                  throw new IncorrectUsageException("commands.scoreboard.objectives.usage");
               }

               if (args[1].equalsIgnoreCase("list")) {
                  this.listObjectives(source);
               } else if (args[1].equalsIgnoreCase("add")) {
                  if (args.length < 4) {
                     throw new IncorrectUsageException("commands.scoreboard.objectives.add.usage");
                  }

                  this.addObjective(source, args, 2);
               } else if (args[1].equalsIgnoreCase("remove")) {
                  if (args.length != 3) {
                     throw new IncorrectUsageException("commands.scoreboard.objectives.remove.usage");
                  }

                  this.removeObjective(source, args[2]);
               } else {
                  if (!args[1].equalsIgnoreCase("setdisplay")) {
                     throw new IncorrectUsageException("commands.scoreboard.objectives.usage");
                  }

                  if (args.length != 3 && args.length != 4) {
                     throw new IncorrectUsageException("commands.scoreboard.objectives.setdisplay.usage");
                  }

                  this.setDisplayObjective(source, args, 2);
               }
            } else if (args[0].equalsIgnoreCase("players")) {
               if (args.length == 1) {
                  throw new IncorrectUsageException("commands.scoreboard.players.usage");
               }

               if (args[1].equalsIgnoreCase("list")) {
                  if (args.length > 3) {
                     throw new IncorrectUsageException("commands.scoreboard.players.list.usage");
                  }

                  this.listPlayers(source, args, 2);
               } else if (args[1].equalsIgnoreCase("add")) {
                  if (args.length < 5) {
                     throw new IncorrectUsageException("commands.scoreboard.players.add.usage");
                  }

                  this.setScore(source, args, 2);
               } else if (args[1].equalsIgnoreCase("remove")) {
                  if (args.length < 5) {
                     throw new IncorrectUsageException("commands.scoreboard.players.remove.usage");
                  }

                  this.setScore(source, args, 2);
               } else if (args[1].equalsIgnoreCase("set")) {
                  if (args.length < 5) {
                     throw new IncorrectUsageException("commands.scoreboard.players.set.usage");
                  }

                  this.setScore(source, args, 2);
               } else if (args[1].equalsIgnoreCase("reset")) {
                  if (args.length != 3 && args.length != 4) {
                     throw new IncorrectUsageException("commands.scoreboard.players.reset.usage");
                  }

                  this.resetScore(source, args, 2);
               } else if (args[1].equalsIgnoreCase("enable")) {
                  if (args.length != 4) {
                     throw new IncorrectUsageException("commands.scoreboard.players.enable.usage");
                  }

                  this.enableTrigger(source, args, 2);
               } else if (args[1].equalsIgnoreCase("test")) {
                  if (args.length != 5 && args.length != 6) {
                     throw new IncorrectUsageException("commands.scoreboard.players.test.usage");
                  }

                  this.testScore(source, args, 2);
               } else {
                  if (!args[1].equalsIgnoreCase("operation")) {
                     throw new IncorrectUsageException("commands.scoreboard.players.usage");
                  }

                  if (args.length != 7) {
                     throw new IncorrectUsageException("commands.scoreboard.players.operation.usage");
                  }

                  this.modifyScore(source, args, 2);
               }
            } else if (args[0].equalsIgnoreCase("teams")) {
               if (args.length == 1) {
                  throw new IncorrectUsageException("commands.scoreboard.teams.usage");
               }

               if (args[1].equalsIgnoreCase("list")) {
                  if (args.length > 3) {
                     throw new IncorrectUsageException("commands.scoreboard.teams.list.usage");
                  }

                  this.listTeams(source, args, 2);
               } else if (args[1].equalsIgnoreCase("add")) {
                  if (args.length < 3) {
                     throw new IncorrectUsageException("commands.scoreboard.teams.add.usage");
                  }

                  this.addTeam(source, args, 2);
               } else if (args[1].equalsIgnoreCase("remove")) {
                  if (args.length != 3) {
                     throw new IncorrectUsageException("commands.scoreboard.teams.remove.usage");
                  }

                  this.removeTeam(source, args, 2);
               } else if (args[1].equalsIgnoreCase("empty")) {
                  if (args.length != 3) {
                     throw new IncorrectUsageException("commands.scoreboard.teams.empty.usage");
                  }

                  this.emptyTeam(source, args, 2);
               } else if (args[1].equalsIgnoreCase("join")) {
                  if (args.length < 4 && (args.length != 3 || !(source instanceof PlayerEntity))) {
                     throw new IncorrectUsageException("commands.scoreboard.teams.join.usage");
                  }

                  this.joinTeam(source, args, 2);
               } else if (args[1].equalsIgnoreCase("leave")) {
                  if (args.length < 3 && !(source instanceof PlayerEntity)) {
                     throw new IncorrectUsageException("commands.scoreboard.teams.leave.usage");
                  }

                  this.leaveTeam(source, args, 2);
               } else {
                  if (!args[1].equalsIgnoreCase("option")) {
                     throw new IncorrectUsageException("commands.scoreboard.teams.usage");
                  }

                  if (args.length != 4 && args.length != 5) {
                     throw new IncorrectUsageException("commands.scoreboard.teams.option.usage");
                  }

                  this.updateTeam(source, args, 2);
               }
            }
         }
      }
   }

   private boolean runWildcard(CommandSource source, String[] args) {
      int var3 = -1;

      for(int var4 = 0; var4 < args.length; ++var4) {
         if (this.hasTargetSelectorAt(args, var4) && "*".equals(args[var4])) {
            if (var3 >= 0) {
               throw new CommandException("commands.scoreboard.noMultiWildcard");
            }

            var3 = var4;
         }
      }

      if (var3 < 0) {
         return false;
      } else {
         ArrayList var12 = Lists.newArrayList(this.getScoreboard().getScoreOwners());
         String var5 = args[var3];
         ArrayList var6 = Lists.newArrayList();

         for(String var8 : var12) {
            args[var3] = var8;

            try {
               this.run(source, args);
               var6.add(var8);
            } catch (CommandException var11) {
               TranslatableText var10 = new TranslatableText(var11.getMessage(), var11.getArgs());
               var10.getStyle().setColor(Formatting.RED);
               source.sendMessage(var10);
            }
         }

         args[var3] = var5;
         source.addResult(CommandResults.Type.AFFECTED_ENTITIES, var6.size());
         if (var6.size() == 0) {
            throw new IncorrectUsageException("commands.scoreboard.allMatchesFailed");
         } else {
            return true;
         }
      }
   }

   protected Scoreboard getScoreboard() {
      return MinecraftServer.getInstance().getWorld(0).getScoreboard();
   }

   protected ScoreboardObjective getObjective(String name, boolean write) {
      Scoreboard var3 = this.getScoreboard();
      ScoreboardObjective var4 = var3.getObjective(name);
      if (var4 == null) {
         throw new CommandException("commands.scoreboard.objectiveNotFound", name);
      } else if (write && var4.getCriterion().isReadOnly()) {
         throw new CommandException("commands.scoreboard.objectiveReadOnly", name);
      } else {
         return var4;
      }
   }

   protected Team getTeam(String name) {
      Scoreboard var2 = this.getScoreboard();
      Team var3 = var2.getTeam(name);
      if (var3 == null) {
         throw new CommandException("commands.scoreboard.teamNotFound", name);
      } else {
         return var3;
      }
   }

   protected void addObjective(CommandSource source, String[] args, int index) {
      String var4 = args[index++];
      String var5 = args[index++];
      Scoreboard var6 = this.getScoreboard();
      ScoreboardCriterion var7 = (ScoreboardCriterion)ScoreboardCriterion.BY_NAME.get(var5);
      if (var7 == null) {
         throw new IncorrectUsageException("commands.scoreboard.objectives.add.wrongType", var5);
      } else if (var6.getObjective(var4) != null) {
         throw new CommandException("commands.scoreboard.objectives.add.alreadyExists", var4);
      } else if (var4.length() > 16) {
         throw new CommandSyntaxException("commands.scoreboard.objectives.add.tooLong", var4, 16);
      } else if (var4.length() == 0) {
         throw new IncorrectUsageException("commands.scoreboard.objectives.add.usage");
      } else {
         if (args.length > index) {
            String var8 = parseText(source, args, index).buildString();
            if (var8.length() > 32) {
               throw new CommandSyntaxException("commands.scoreboard.objectives.add.displayTooLong", var8, 32);
            }

            if (var8.length() > 0) {
               var6.createObjective(var4, var7).setDisplayName(var8);
            } else {
               var6.createObjective(var4, var7);
            }
         } else {
            var6.createObjective(var4, var7);
         }

         sendSuccess(source, this, "commands.scoreboard.objectives.add.success", new Object[]{var4});
      }
   }

   protected void addTeam(CommandSource source, String[] args, int index) {
      String var4 = args[index++];
      Scoreboard var5 = this.getScoreboard();
      if (var5.getTeam(var4) != null) {
         throw new CommandException("commands.scoreboard.teams.add.alreadyExists", var4);
      } else if (var4.length() > 16) {
         throw new CommandSyntaxException("commands.scoreboard.teams.add.tooLong", var4, 16);
      } else if (var4.length() == 0) {
         throw new IncorrectUsageException("commands.scoreboard.teams.add.usage");
      } else {
         if (args.length > index) {
            String var6 = parseText(source, args, index).buildString();
            if (var6.length() > 32) {
               throw new CommandSyntaxException("commands.scoreboard.teams.add.displayTooLong", var6, 32);
            }

            if (var6.length() > 0) {
               var5.addTeam(var4).setDisplayName(var6);
            } else {
               var5.addTeam(var4);
            }
         } else {
            var5.addTeam(var4);
         }

         sendSuccess(source, this, "commands.scoreboard.teams.add.success", new Object[]{var4});
      }
   }

   protected void updateTeam(CommandSource source, String[] args, int index) {
      Team var4 = this.getTeam(args[index++]);
      if (var4 != null) {
         String var5 = args[index++].toLowerCase();
         if (!var5.equalsIgnoreCase("color")
            && !var5.equalsIgnoreCase("friendlyfire")
            && !var5.equalsIgnoreCase("seeFriendlyInvisibles")
            && !var5.equalsIgnoreCase("nametagVisibility")
            && !var5.equalsIgnoreCase("deathMessageVisibility")) {
            throw new IncorrectUsageException("commands.scoreboard.teams.option.usage");
         } else if (args.length == 4) {
            if (var5.equalsIgnoreCase("color")) {
               throw new IncorrectUsageException("commands.scoreboard.teams.option.noValue", var5, listArgs(Formatting.getNames(true, false)));
            } else if (var5.equalsIgnoreCase("friendlyfire") || var5.equalsIgnoreCase("seeFriendlyInvisibles")) {
               throw new IncorrectUsageException("commands.scoreboard.teams.option.noValue", var5, listArgs(Arrays.asList("true", "false")));
            } else if (!var5.equalsIgnoreCase("nametagVisibility") && !var5.equalsIgnoreCase("deathMessageVisibility")) {
               throw new IncorrectUsageException("commands.scoreboard.teams.option.usage");
            } else {
               throw new IncorrectUsageException("commands.scoreboard.teams.option.noValue", var5, listArgs(AbstractTeam.Visibility.getNames()));
            }
         } else {
            String var6 = args[index];
            if (var5.equalsIgnoreCase("color")) {
               Formatting var7 = Formatting.byName(var6);
               if (var7 == null || var7.isModifier()) {
                  throw new IncorrectUsageException("commands.scoreboard.teams.option.noValue", var5, listArgs(Formatting.getNames(true, false)));
               }

               var4.setColor(var7);
               var4.setPrefix(var7.toString());
               var4.setSuffix(Formatting.RESET.toString());
            } else if (var5.equalsIgnoreCase("friendlyfire")) {
               if (!var6.equalsIgnoreCase("true") && !var6.equalsIgnoreCase("false")) {
                  throw new IncorrectUsageException("commands.scoreboard.teams.option.noValue", var5, listArgs(Arrays.asList("true", "false")));
               }

               var4.setAllowFriendlyFire(var6.equalsIgnoreCase("true"));
            } else if (var5.equalsIgnoreCase("seeFriendlyInvisibles")) {
               if (!var6.equalsIgnoreCase("true") && !var6.equalsIgnoreCase("false")) {
                  throw new IncorrectUsageException("commands.scoreboard.teams.option.noValue", var5, listArgs(Arrays.asList("true", "false")));
               }

               var4.setShowFriendlyInvisibles(var6.equalsIgnoreCase("true"));
            } else if (var5.equalsIgnoreCase("nametagVisibility")) {
               AbstractTeam.Visibility var10 = AbstractTeam.Visibility.byName(var6);
               if (var10 == null) {
                  throw new IncorrectUsageException("commands.scoreboard.teams.option.noValue", var5, listArgs(AbstractTeam.Visibility.getNames()));
               }

               var4.setNameTagVisibility(var10);
            } else if (var5.equalsIgnoreCase("deathMessageVisibility")) {
               AbstractTeam.Visibility var11 = AbstractTeam.Visibility.byName(var6);
               if (var11 == null) {
                  throw new IncorrectUsageException("commands.scoreboard.teams.option.noValue", var5, listArgs(AbstractTeam.Visibility.getNames()));
               }

               var4.setDeathMessageVisibility(var11);
            }

            sendSuccess(source, this, "commands.scoreboard.teams.option.success", new Object[]{var5, var4.getName(), var6});
         }
      }
   }

   protected void removeTeam(CommandSource source, String[] args, int index) {
      Scoreboard var4 = this.getScoreboard();
      Team var5 = this.getTeam(args[index]);
      if (var5 != null) {
         var4.removeTeam(var5);
         sendSuccess(source, this, "commands.scoreboard.teams.remove.success", new Object[]{var5.getName()});
      }
   }

   protected void listTeams(CommandSource source, String[] args, int index) {
      Scoreboard var4 = this.getScoreboard();
      if (args.length > index) {
         Team var5 = this.getTeam(args[index]);
         if (var5 == null) {
            return;
         }

         Collection var6 = var5.getMembers();
         source.addResult(CommandResults.Type.QUERY_RESULT, var6.size());
         if (var6.size() <= 0) {
            throw new CommandException("commands.scoreboard.teams.list.player.empty", var5.getName());
         }

         TranslatableText var7 = new TranslatableText("commands.scoreboard.teams.list.player.count", var6.size(), var5.getName());
         var7.getStyle().setColor(Formatting.DARK_GREEN);
         source.sendMessage(var7);
         source.sendMessage(new LiteralText(listArgs(var6.toArray())));
      } else {
         Collection var9 = var4.getTeams();
         source.addResult(CommandResults.Type.QUERY_RESULT, var9.size());
         if (var9.size() <= 0) {
            throw new CommandException("commands.scoreboard.teams.list.empty");
         }

         TranslatableText var10 = new TranslatableText("commands.scoreboard.teams.list.count", var9.size());
         var10.getStyle().setColor(Formatting.DARK_GREEN);
         source.sendMessage(var10);

         for(Team var8 : var9) {
            source.sendMessage(new TranslatableText("commands.scoreboard.teams.list.entry", var8.getName(), var8.getDisplayName(), var8.getMembers().size()));
         }
      }
   }

   protected void joinTeam(CommandSource source, String[] args, int index) {
      Scoreboard var4 = this.getScoreboard();
      String var5 = args[index++];
      HashSet var6 = Sets.newHashSet();
      HashSet var7 = Sets.newHashSet();
      if (source instanceof PlayerEntity && index == args.length) {
         String var14 = asPlayer(source).getName();
         if (var4.addMemberToTeam(var14, var5)) {
            var6.add(var14);
         } else {
            var7.add(var14);
         }
      } else {
         while(index < args.length) {
            String var8 = args[index++];
            if (var8.startsWith("@")) {
               for(Entity var11 : parseEntities(source, var8)) {
                  String var12 = parseEntityName(source, var11.getUuid().toString());
                  if (var4.addMemberToTeam(var12, var5)) {
                     var6.add(var12);
                  } else {
                     var7.add(var12);
                  }
               }
            } else {
               String var9 = parseEntityName(source, var8);
               if (var4.addMemberToTeam(var9, var5)) {
                  var6.add(var9);
               } else {
                  var7.add(var9);
               }
            }
         }
      }

      if (!var6.isEmpty()) {
         source.addResult(CommandResults.Type.AFFECTED_ENTITIES, var6.size());
         sendSuccess(source, this, "commands.scoreboard.teams.join.success", new Object[]{var6.size(), var5, listArgs(var6.toArray(new String[0]))});
      }

      if (!var7.isEmpty()) {
         throw new CommandException("commands.scoreboard.teams.join.failure", var7.size(), var5, listArgs(var7.toArray(new String[0])));
      }
   }

   protected void leaveTeam(CommandSource source, String[] args, int index) {
      Scoreboard var4 = this.getScoreboard();
      HashSet var5 = Sets.newHashSet();
      HashSet var6 = Sets.newHashSet();
      if (source instanceof PlayerEntity && index == args.length) {
         String var12 = asPlayer(source).getName();
         if (var4.removeMemberFromTeam(var12)) {
            var5.add(var12);
         } else {
            var6.add(var12);
         }
      } else {
         while(index < args.length) {
            String var7 = args[index++];
            if (var7.startsWith("@")) {
               for(Entity var10 : parseEntities(source, var7)) {
                  String var11 = parseEntityName(source, var10.getUuid().toString());
                  if (var4.removeMemberFromTeam(var11)) {
                     var5.add(var11);
                  } else {
                     var6.add(var11);
                  }
               }
            } else {
               String var8 = parseEntityName(source, var7);
               if (var4.removeMemberFromTeam(var8)) {
                  var5.add(var8);
               } else {
                  var6.add(var8);
               }
            }
         }
      }

      if (!var5.isEmpty()) {
         source.addResult(CommandResults.Type.AFFECTED_ENTITIES, var5.size());
         sendSuccess(source, this, "commands.scoreboard.teams.leave.success", new Object[]{var5.size(), listArgs(var5.toArray(new String[0]))});
      }

      if (!var6.isEmpty()) {
         throw new CommandException("commands.scoreboard.teams.leave.failure", var6.size(), listArgs(var6.toArray(new String[0])));
      }
   }

   protected void emptyTeam(CommandSource source, String[] args, int index) {
      Scoreboard var4 = this.getScoreboard();
      Team var5 = this.getTeam(args[index]);
      if (var5 != null) {
         ArrayList var6 = Lists.newArrayList(var5.getMembers());
         source.addResult(CommandResults.Type.AFFECTED_ENTITIES, var6.size());
         if (var6.isEmpty()) {
            throw new CommandException("commands.scoreboard.teams.empty.alreadyEmpty", var5.getName());
         } else {
            for(String var8 : var6) {
               var4.removeMemberFromTeam(var8, var5);
            }

            sendSuccess(source, this, "commands.scoreboard.teams.empty.success", new Object[]{var6.size(), var5.getName()});
         }
      }
   }

   protected void removeObjective(CommandSource source, String name) {
      Scoreboard var3 = this.getScoreboard();
      ScoreboardObjective var4 = this.getObjective(name, false);
      var3.removeObjective(var4);
      sendSuccess(source, this, "commands.scoreboard.objectives.remove.success", new Object[]{name});
   }

   protected void listObjectives(CommandSource source) {
      Scoreboard var2 = this.getScoreboard();
      Collection var3 = var2.getObjectives();
      if (var3.size() <= 0) {
         throw new CommandException("commands.scoreboard.objectives.list.empty");
      } else {
         TranslatableText var4 = new TranslatableText("commands.scoreboard.objectives.list.count", var3.size());
         var4.getStyle().setColor(Formatting.DARK_GREEN);
         source.sendMessage(var4);

         for(ScoreboardObjective var6 : var3) {
            source.sendMessage(
               new TranslatableText("commands.scoreboard.objectives.list.entry", var6.getName(), var6.getDisplayName(), var6.getCriterion().getName())
            );
         }
      }
   }

   protected void setDisplayObjective(CommandSource source, String[] args, int index) {
      Scoreboard var4 = this.getScoreboard();
      String var5 = args[index++];
      int var6 = Scoreboard.getDisplaySlot(var5);
      ScoreboardObjective var7 = null;
      if (args.length == 4) {
         var7 = this.getObjective(args[index], false);
      }

      if (var6 < 0) {
         throw new CommandException("commands.scoreboard.objectives.setdisplay.invalidSlot", var5);
      } else {
         var4.setDisplayObjective(var6, var7);
         if (var7 != null) {
            sendSuccess(source, this, "commands.scoreboard.objectives.setdisplay.successSet", new Object[]{Scoreboard.getDisplayLocation(var6), var7.getName()});
         } else {
            sendSuccess(source, this, "commands.scoreboard.objectives.setdisplay.successCleared", new Object[]{Scoreboard.getDisplayLocation(var6)});
         }
      }
   }

   protected void listPlayers(CommandSource source, String[] args, int index) {
      Scoreboard var4 = this.getScoreboard();
      if (args.length > index) {
         String var5 = parseEntityName(source, args[index]);
         Map var6 = var4.getScores(var5);
         source.addResult(CommandResults.Type.QUERY_RESULT, var6.size());
         if (var6.size() <= 0) {
            throw new CommandException("commands.scoreboard.players.list.player.empty", var5);
         }

         TranslatableText var7 = new TranslatableText("commands.scoreboard.players.list.player.count", var6.size(), var5);
         var7.getStyle().setColor(Formatting.DARK_GREEN);
         source.sendMessage(var7);

         for(ScoreboardScore var9 : var6.values()) {
            source.sendMessage(
               new TranslatableText(
                  "commands.scoreboard.players.list.player.entry", var9.get(), var9.getObjective().getDisplayName(), var9.getObjective().getName()
               )
            );
         }
      } else {
         Collection var10 = var4.getScoreOwners();
         source.addResult(CommandResults.Type.QUERY_RESULT, var10.size());
         if (var10.size() <= 0) {
            throw new CommandException("commands.scoreboard.players.list.empty");
         }

         TranslatableText var11 = new TranslatableText("commands.scoreboard.players.list.count", var10.size());
         var11.getStyle().setColor(Formatting.DARK_GREEN);
         source.sendMessage(var11);
         source.sendMessage(new LiteralText(listArgs(var10.toArray())));
      }
   }

   protected void setScore(CommandSource source, String[] args, int index) {
      String var4 = args[index - 1];
      int var5 = index;
      String var6 = parseEntityName(source, args[index++]);
      ScoreboardObjective var7 = this.getObjective(args[index++], true);
      int var8 = var4.equalsIgnoreCase("set") ? parseInt(args[index++]) : parseInt(args[index++], 0);
      if (args.length > index) {
         Entity var9 = parseEntity(source, args[var5]);

         try {
            NbtCompound var10 = StringNbtReader.parse(parseString(args, index));
            NbtCompound var11 = new NbtCompound();
            var9.writeEntityNbt(var11);
            if (!TestForBlockCommand.matchesNbt(var10, var11, true)) {
               throw new CommandException("commands.scoreboard.players.set.tagMismatch", var6);
            }
         } catch (NbtException var12) {
            throw new CommandException("commands.scoreboard.players.set.tagError", var12.getMessage());
         }
      }

      Scoreboard var16 = this.getScoreboard();
      ScoreboardScore var17 = var16.getScore(var6, var7);
      if (var4.equalsIgnoreCase("set")) {
         var17.set(var8);
      } else if (var4.equalsIgnoreCase("add")) {
         var17.increase(var8);
      } else {
         var17.decrease(var8);
      }

      sendSuccess(source, this, "commands.scoreboard.players.set.success", new Object[]{var7.getName(), var6, var17.get()});
   }

   protected void resetScore(CommandSource source, String[] args, int index) {
      Scoreboard var4 = this.getScoreboard();
      String var5 = parseEntityName(source, args[index++]);
      if (args.length > index) {
         ScoreboardObjective var6 = this.getObjective(args[index++], false);
         var4.removeScore(var5, var6);
         sendSuccess(source, this, "commands.scoreboard.players.resetscore.success", new Object[]{var6.getName(), var5});
      } else {
         var4.removeScore(var5, null);
         sendSuccess(source, this, "commands.scoreboard.players.reset.success", new Object[]{var5});
      }
   }

   protected void enableTrigger(CommandSource source, String[] args, int index) {
      Scoreboard var4 = this.getScoreboard();
      String var5 = parsePlayerName(source, args[index++]);
      ScoreboardObjective var6 = this.getObjective(args[index], false);
      if (var6.getCriterion() != ScoreboardCriterion.TRIGGER) {
         throw new CommandException("commands.scoreboard.players.enable.noTrigger", var6.getName());
      } else {
         ScoreboardScore var7 = var4.getScore(var5, var6);
         var7.setLocked(false);
         sendSuccess(source, this, "commands.scoreboard.players.enable.success", new Object[]{var6.getName(), var5});
      }
   }

   protected void testScore(CommandSource source, String[] args, int index) {
      Scoreboard var4 = this.getScoreboard();
      String var5 = parseEntityName(source, args[index++]);
      ScoreboardObjective var6 = this.getObjective(args[index++], false);
      if (!var4.hasScore(var5, var6)) {
         throw new CommandException("commands.scoreboard.players.test.notFound", var6.getName(), var5);
      } else {
         int var7 = args[index].equals("*") ? Integer.MIN_VALUE : parseInt(args[index]);
         ++index;
         int var8 = index < args.length && !args[index].equals("*") ? parseInt(args[index], var7) : Integer.MAX_VALUE;
         ScoreboardScore var9 = var4.getScore(var5, var6);
         if (var9.get() >= var7 && var9.get() <= var8) {
            sendSuccess(source, this, "commands.scoreboard.players.test.success", new Object[]{var9.get(), var7, var8});
         } else {
            throw new CommandException("commands.scoreboard.players.test.failed", var9.get(), var7, var8);
         }
      }
   }

   protected void modifyScore(CommandSource source, String[] args, int index) {
      Scoreboard var4 = this.getScoreboard();
      String var5 = parseEntityName(source, args[index++]);
      ScoreboardObjective var6 = this.getObjective(args[index++], true);
      String var7 = args[index++];
      String var8 = parseEntityName(source, args[index++]);
      ScoreboardObjective var9 = this.getObjective(args[index], false);
      ScoreboardScore var10 = var4.getScore(var5, var6);
      if (!var4.hasScore(var8, var9)) {
         throw new CommandException("commands.scoreboard.players.operation.notFound", var9.getName(), var8);
      } else {
         ScoreboardScore var11 = var4.getScore(var8, var9);
         if (var7.equals("+=")) {
            var10.set(var10.get() + var11.get());
         } else if (var7.equals("-=")) {
            var10.set(var10.get() - var11.get());
         } else if (var7.equals("*=")) {
            var10.set(var10.get() * var11.get());
         } else if (var7.equals("/=")) {
            if (var11.get() != 0) {
               var10.set(var10.get() / var11.get());
            }
         } else if (var7.equals("%=")) {
            if (var11.get() != 0) {
               var10.set(var10.get() % var11.get());
            }
         } else if (var7.equals("=")) {
            var10.set(var11.get());
         } else if (var7.equals("<")) {
            var10.set(Math.min(var10.get(), var11.get()));
         } else if (var7.equals(">")) {
            var10.set(Math.max(var10.get(), var11.get()));
         } else {
            if (!var7.equals("><")) {
               throw new CommandException("commands.scoreboard.players.operation.invalidOperation", var7);
            }

            int var12 = var10.get();
            var10.set(var11.get());
            var11.set(var12);
         }

         sendSuccess(source, this, "commands.scoreboard.players.operation.success", new Object[0]);
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      if (args.length == 1) {
         return suggestMatching(args, new String[]{"objectives", "players", "teams"});
      } else {
         if (args[0].equalsIgnoreCase("objectives")) {
            if (args.length == 2) {
               return suggestMatching(args, new String[]{"list", "add", "remove", "setdisplay"});
            }

            if (args[1].equalsIgnoreCase("add")) {
               if (args.length == 4) {
                  Set var3 = ScoreboardCriterion.BY_NAME.keySet();
                  return suggestMatching(args, var3);
               }
            } else if (args[1].equalsIgnoreCase("remove")) {
               if (args.length == 3) {
                  return suggestMatching(args, this.getObjectives(false));
               }
            } else if (args[1].equalsIgnoreCase("setdisplay")) {
               if (args.length == 3) {
                  return suggestMatching(args, Scoreboard.getDisplayLocations());
               }

               if (args.length == 4) {
                  return suggestMatching(args, this.getObjectives(false));
               }
            }
         } else if (args[0].equalsIgnoreCase("players")) {
            if (args.length == 2) {
               return suggestMatching(args, new String[]{"set", "add", "remove", "reset", "list", "enable", "test", "operation"});
            }

            if (!args[1].equalsIgnoreCase("set")
               && !args[1].equalsIgnoreCase("add")
               && !args[1].equalsIgnoreCase("remove")
               && !args[1].equalsIgnoreCase("reset")) {
               if (args[1].equalsIgnoreCase("enable")) {
                  if (args.length == 3) {
                     return suggestMatching(args, MinecraftServer.getInstance().getPlayerNames());
                  }

                  if (args.length == 4) {
                     return suggestMatching(args, this.getTriggers());
                  }
               } else if (!args[1].equalsIgnoreCase("list") && !args[1].equalsIgnoreCase("test")) {
                  if (args[1].equalsIgnoreCase("operation")) {
                     if (args.length == 3) {
                        return suggestMatching(args, this.getScoreboard().getScoreOwners());
                     }

                     if (args.length == 4) {
                        return suggestMatching(args, this.getObjectives(true));
                     }

                     if (args.length == 5) {
                        return suggestMatching(args, new String[]{"+=", "-=", "*=", "/=", "%=", "=", "<", ">", "><"});
                     }

                     if (args.length == 6) {
                        return suggestMatching(args, MinecraftServer.getInstance().getPlayerNames());
                     }

                     if (args.length == 7) {
                        return suggestMatching(args, this.getObjectives(false));
                     }
                  }
               } else {
                  if (args.length == 3) {
                     return suggestMatching(args, this.getScoreboard().getScoreOwners());
                  }

                  if (args.length == 4 && args[1].equalsIgnoreCase("test")) {
                     return suggestMatching(args, this.getObjectives(false));
                  }
               }
            } else {
               if (args.length == 3) {
                  return suggestMatching(args, MinecraftServer.getInstance().getPlayerNames());
               }

               if (args.length == 4) {
                  return suggestMatching(args, this.getObjectives(true));
               }
            }
         } else if (args[0].equalsIgnoreCase("teams")) {
            if (args.length == 2) {
               return suggestMatching(args, new String[]{"add", "remove", "join", "leave", "empty", "list", "option"});
            }

            if (args[1].equalsIgnoreCase("join")) {
               if (args.length == 3) {
                  return suggestMatching(args, this.getScoreboard().getTeamNames());
               }

               if (args.length >= 4) {
                  return suggestMatching(args, MinecraftServer.getInstance().getPlayerNames());
               }
            } else {
               if (args[1].equalsIgnoreCase("leave")) {
                  return suggestMatching(args, MinecraftServer.getInstance().getPlayerNames());
               }

               if (!args[1].equalsIgnoreCase("empty") && !args[1].equalsIgnoreCase("list") && !args[1].equalsIgnoreCase("remove")) {
                  if (args[1].equalsIgnoreCase("option")) {
                     if (args.length == 3) {
                        return suggestMatching(args, this.getScoreboard().getTeamNames());
                     }

                     if (args.length == 4) {
                        return suggestMatching(
                           args, new String[]{"color", "friendlyfire", "seeFriendlyInvisibles", "nametagVisibility", "deathMessageVisibility"}
                        );
                     }

                     if (args.length == 5) {
                        if (args[3].equalsIgnoreCase("color")) {
                           return suggestMatching(args, Formatting.getNames(true, false));
                        }

                        if (args[3].equalsIgnoreCase("nametagVisibility") || args[3].equalsIgnoreCase("deathMessageVisibility")) {
                           return suggestMatching(args, AbstractTeam.Visibility.getNames());
                        }

                        if (args[3].equalsIgnoreCase("friendlyfire") || args[3].equalsIgnoreCase("seeFriendlyInvisibles")) {
                           return suggestMatching(args, new String[]{"true", "false"});
                        }
                     }
                  }
               } else if (args.length == 3) {
                  return suggestMatching(args, this.getScoreboard().getTeamNames());
               }
            }
         }

         return null;
      }
   }

   protected List getObjectives(boolean write) {
      Collection var2 = this.getScoreboard().getObjectives();
      ArrayList var3 = Lists.newArrayList();

      for(ScoreboardObjective var5 : var2) {
         if (!write || !var5.getCriterion().isReadOnly()) {
            var3.add(var5.getName());
         }
      }

      return var3;
   }

   protected List getTriggers() {
      Collection var1 = this.getScoreboard().getObjectives();
      ArrayList var2 = Lists.newArrayList();

      for(ScoreboardObjective var4 : var1) {
         if (var4.getCriterion() == ScoreboardCriterion.TRIGGER) {
            var2.add(var4.getName());
         }
      }

      return var2;
   }

   @Override
   public boolean hasTargetSelectorAt(String[] args, int index) {
      if (!args[0].equalsIgnoreCase("players")) {
         if (args[0].equalsIgnoreCase("teams")) {
            return index == 2;
         } else {
            return false;
         }
      } else if (args.length > 1 && args[1].equalsIgnoreCase("operation")) {
         return index == 2 || index == 5;
      } else {
         return index == 2;
      }
   }
}
