package net.minecraft.server.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.world.ServerWorld;

public class TimeCommand extends Command {
   @Override
   public String getName() {
      return "time";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.time.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length > 1) {
         if (args[0].equals("set")) {
            int var6;
            if (args[1].equals("day")) {
               var6 = 1000;
            } else if (args[1].equals("night")) {
               var6 = 13000;
            } else {
               var6 = parseInt(args[1], 0);
            }

            this.setTimeOfDay(source, var6);
            sendSuccess(source, this, "commands.time.set", new Object[]{var6});
            return;
         }

         if (args[0].equals("add")) {
            int var5 = parseInt(args[1], 0);
            this.addToTimeOfDay(source, var5);
            sendSuccess(source, this, "commands.time.added", new Object[]{var5});
            return;
         }

         if (args[0].equals("query")) {
            if (args[1].equals("daytime")) {
               int var4 = (int)(source.getSourceWorld().getTimeOfDay() % 2147483647L);
               source.addResult(CommandResults.Type.QUERY_RESULT, var4);
               sendSuccess(source, this, "commands.time.query", new Object[]{var4});
               return;
            }

            if (args[1].equals("gametime")) {
               int var3 = (int)(source.getSourceWorld().getTime() % 2147483647L);
               source.addResult(CommandResults.Type.QUERY_RESULT, var3);
               sendSuccess(source, this, "commands.time.query", new Object[]{var3});
               return;
            }
         }
      }

      throw new IncorrectUsageException("commands.time.usage");
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      if (args.length == 1) {
         return suggestMatching(args, new String[]{"set", "add", "query"});
      } else if (args.length == 2 && args[0].equals("set")) {
         return suggestMatching(args, new String[]{"day", "night"});
      } else {
         return args.length == 2 && args[0].equals("query") ? suggestMatching(args, new String[]{"daytime", "gametime"}) : null;
      }
   }

   protected void setTimeOfDay(CommandSource source, int time) {
      for(int var3 = 0; var3 < MinecraftServer.getInstance().worlds.length; ++var3) {
         MinecraftServer.getInstance().worlds[var3].setTimeOfDay((long)time);
      }
   }

   protected void addToTimeOfDay(CommandSource source, int time) {
      for(int var3 = 0; var3 < MinecraftServer.getInstance().worlds.length; ++var3) {
         ServerWorld var4 = MinecraftServer.getInstance().worlds[var3];
         var4.setTimeOfDay(var4.getTimeOfDay() + (long)time);
      }
   }
}
