package net.minecraft.server.command;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;

public class TestForCommand extends Command {
   @Override
   public String getName() {
      return "testfor";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.testfor.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 1) {
         throw new IncorrectUsageException("commands.testfor.usage");
      } else {
         Entity var3 = parseEntity(source, args[0]);
         NbtCompound var4 = null;
         if (args.length >= 2) {
            try {
               var4 = StringNbtReader.parse(parseString(args, 1));
            } catch (NbtException var6) {
               throw new CommandException("commands.testfor.tagError", var6.getMessage());
            }
         }

         if (var4 != null) {
            NbtCompound var5 = new NbtCompound();
            var3.writeEntityNbt(var5);
            if (!TestForBlockCommand.matchesNbt(var4, var5, true)) {
               throw new CommandException("commands.testfor.failure", var3.getName());
            }
         }

         sendSuccess(source, this, "commands.testfor.success", new Object[]{var3.getName()});
      }
   }

   @Override
   public boolean hasTargetSelectorAt(String[] args, int index) {
      return index == 0;
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      return args.length == 1 ? suggestMatching(args, MinecraftServer.getInstance().getPlayerNames()) : null;
   }
}
