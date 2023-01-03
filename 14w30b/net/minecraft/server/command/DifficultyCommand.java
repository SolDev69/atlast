package net.minecraft.server.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.Difficulty;

public class DifficultyCommand extends Command {
   @Override
   public String getName() {
      return "difficulty";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.difficulty.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length <= 0) {
         throw new IncorrectUsageException("commands.difficulty.usage");
      } else {
         Difficulty var3 = this.parseDifficulty(args[0]);
         MinecraftServer.getInstance().setDifficulty(var3);
         sendSuccess(source, this, "commands.difficulty.success", new Object[]{new TranslatableText(var3.getName())});
      }
   }

   protected Difficulty parseDifficulty(String s) {
      if (s.equalsIgnoreCase("peaceful") || s.equalsIgnoreCase("p")) {
         return Difficulty.PEACEFUL;
      } else if (s.equalsIgnoreCase("easy") || s.equalsIgnoreCase("e")) {
         return Difficulty.EASY;
      } else if (s.equalsIgnoreCase("normal") || s.equalsIgnoreCase("n")) {
         return Difficulty.NORMAL;
      } else {
         return !s.equalsIgnoreCase("hard") && !s.equalsIgnoreCase("h") ? Difficulty.byIndex(parseInt(s, 0, 3)) : Difficulty.HARD;
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      return args.length == 1 ? suggestMatching(args, new String[]{"peaceful", "easy", "normal", "hard"}) : null;
   }
}
