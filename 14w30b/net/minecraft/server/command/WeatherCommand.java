package net.minecraft.server.command;

import java.util.List;
import java.util.Random;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WorldData;

public class WeatherCommand extends Command {
   @Override
   public String getName() {
      return "weather";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.weather.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length >= 1 && args.length <= 2) {
         int var3 = (300 + new Random().nextInt(600)) * 20;
         if (args.length >= 2) {
            var3 = parseInt(args[1], 1, 1000000) * 20;
         }

         ServerWorld var4 = MinecraftServer.getInstance().worlds[0];
         WorldData var5 = var4.getData();
         if ("clear".equalsIgnoreCase(args[0])) {
            var5.setClearWeatherTime(var3);
            var5.setRainTime(0);
            var5.setThunderTime(0);
            var5.setRaining(false);
            var5.setThundering(false);
            sendSuccess(source, this, "commands.weather.clear", new Object[0]);
         } else if ("rain".equalsIgnoreCase(args[0])) {
            var5.setClearWeatherTime(0);
            var5.setRainTime(var3);
            var5.setThunderTime(var3);
            var5.setRaining(true);
            var5.setThundering(false);
            sendSuccess(source, this, "commands.weather.rain", new Object[0]);
         } else {
            if (!"thunder".equalsIgnoreCase(args[0])) {
               throw new IncorrectUsageException("commands.weather.usage");
            }

            var5.setClearWeatherTime(0);
            var5.setRainTime(var3);
            var5.setThunderTime(var3);
            var5.setRaining(true);
            var5.setThundering(true);
            sendSuccess(source, this, "commands.weather.thunder", new Object[0]);
         }
      } else {
         throw new IncorrectUsageException("commands.weather.usage");
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      return args.length == 1 ? suggestMatching(args, new String[]{"clear", "rain", "thunder"}) : null;
   }
}
