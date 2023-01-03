package net.minecraft.server.dedicated.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class ListCommand extends Command {
   @Override
   public String getName() {
      return "list";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.players.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      int var3 = MinecraftServer.getInstance().getPlayerCount();
      source.sendMessage(new TranslatableText("commands.players.list", var3, MinecraftServer.getInstance().getMaxPlayerCount()));
      source.sendMessage(new LiteralText(MinecraftServer.getInstance().getPlayerManager().getNamesAsString()));
      source.addResult(CommandResults.Type.QUERY_RESULT, var3);
   }
}
