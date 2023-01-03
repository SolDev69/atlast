package net.minecraft.server.command;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugCommand extends Command {
   private static final Logger LOGGER = LogManager.getLogger();
   private long startMillis;
   private int startTicks;

   @Override
   public String getName() {
      return "debug";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.debug.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 1) {
         throw new IncorrectUsageException("commands.debug.usage");
      } else {
         if (args[0].equals("start")) {
            if (args.length != 1) {
               throw new IncorrectUsageException("commands.debug.usage");
            }

            sendSuccess(source, this, "commands.debug.start", new Object[0]);
            MinecraftServer.getInstance().enableProfiling();
            this.startMillis = MinecraftServer.getTimeMillis();
            this.startTicks = MinecraftServer.getInstance().getTicks();
         } else if (args[0].equals("stop")) {
            if (args.length != 1) {
               throw new IncorrectUsageException("commands.debug.usage");
            }

            if (!MinecraftServer.getInstance().profiler.isProfiling) {
               throw new CommandException("commands.debug.notStarted");
            }

            long var3 = MinecraftServer.getTimeMillis();
            int var5 = MinecraftServer.getInstance().getTicks();
            long var6 = var3 - this.startMillis;
            int var8 = var5 - this.startTicks;
            this.saveReport(var6, var8);
            MinecraftServer.getInstance().profiler.isProfiling = false;
            sendSuccess(source, this, "commands.debug.stop", new Object[]{(float)var6 / 1000.0F, var8});
         } else if (args[0].equals("chunk")) {
            if (args.length != 4) {
               throw new IncorrectUsageException("commands.debug.usage");
            }

            BlockPos var9 = parseBlockPos(source, args, 1, true);
         }
      }
   }

   private void saveReport(long durationMillis, int durationTicks) {
      File var4 = new File(
         MinecraftServer.getInstance().getFile("debug"), "profile-results-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt"
      );
      var4.getParentFile().mkdirs();

      try {
         FileWriter var5 = new FileWriter(var4);
         var5.write(this.buildReport(durationMillis, durationTicks));
         var5.close();
      } catch (Throwable var6) {
         LOGGER.error("Could not save profiler results to " + var4, var6);
      }
   }

   private String buildReport(long durationMillis, int durationTicks) {
      StringBuilder var4 = new StringBuilder();
      var4.append("---- Minecraft Profiler Results ----\n");
      var4.append("// ");
      var4.append(getWittyComment());
      var4.append("\n\n");
      var4.append("Time span: ").append(durationMillis).append(" ms\n");
      var4.append("Tick span: ").append(durationTicks).append(" ticks\n");
      var4.append("// This is approximately ")
         .append(String.format("%.2f", (float)durationTicks / ((float)durationMillis / 1000.0F)))
         .append(" ticks per second. It should be ")
         .append(20)
         .append(" ticks per second\n\n");
      var4.append("--- BEGIN PROFILE DUMP ---\n\n");
      this.addResults(0, "root", var4);
      var4.append("--- END PROFILE DUMP ---\n\n");
      return var4.toString();
   }

   private void addResults(int spacing, String location, StringBuilder report) {
      List var4 = MinecraftServer.getInstance().profiler.getResults(location);
      if (var4 != null && var4.size() >= 3) {
         for(int var5 = 1; var5 < var4.size(); ++var5) {
            Profiler.Result var6 = (Profiler.Result)var4.get(var5);
            report.append(String.format("[%02d] ", spacing));

            for(int var7 = 0; var7 < spacing; ++var7) {
               report.append(" ");
            }

            report.append(var6.location)
               .append(" - ")
               .append(String.format("%.2f", var6.percentageOfParent))
               .append("%/")
               .append(String.format("%.2f", var6.percentageOfTotal))
               .append("%\n");
            if (!var6.location.equals("unspecified")) {
               try {
                  this.addResults(spacing + 1, location + "." + var6.location, report);
               } catch (Exception var8) {
                  report.append("[[ EXCEPTION ").append(var8).append(" ]]");
               }
            }
         }
      }
   }

   private static String getWittyComment() {
      String[] var0 = new String[]{
         "Shiny numbers!",
         "Am I not running fast enough? :(",
         "I'm working as hard as I can!",
         "Will I ever be good enough for you? :(",
         "Speedy. Zoooooom!",
         "Hello world",
         "40% better than a crash report.",
         "Now with extra numbers",
         "Now with less numbers",
         "Now with the same numbers",
         "You should add flames to things, it makes them go faster!",
         "Do you feel the need for... optimization?",
         "*cracks redstone whip*",
         "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."
      };

      try {
         return var0[(int)(System.nanoTime() % (long)var0.length)];
      } catch (Throwable var2) {
         return "Witty comment unavailable :(";
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      return args.length == 1 ? suggestMatching(args, new String[]{"start", "stop"}) : null;
   }
}
