package net.minecraft.server.command;

import java.util.List;
import net.minecraft.network.packet.s2c.play.EntityEventS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.world.Gamerules;

public class GameRuleCommand extends Command {
   @Override
   public String getName() {
      return "gamerule";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.gamerule.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      Gamerules var3 = this.getGameRules();
      String var4 = args.length > 0 ? args[0] : "";
      String var5 = args.length > 1 ? parseString(args, 1) : "";
      switch(args.length) {
         case 0:
            source.sendMessage(new LiteralText(listArgs(var3.getAll())));
            break;
         case 1:
            if (!var3.contains(var4)) {
               throw new CommandException("commands.gamerule.norule", var4);
            }

            String var6 = var3.get(var4);
            source.sendMessage(new LiteralText(var4).append(" = ").append(var6));
            source.addResult(CommandResults.Type.QUERY_RESULT, var3.getInt(var4));
            break;
         default:
            var3.set(var4, var5);
            dispatchEntityEvent(var3, var4);
            sendSuccess(source, this, "commands.gamerule.success", new Object[0]);
      }
   }

   public static void dispatchEntityEvent(Gamerules gameRules, String ruleName) {
      if ("reducedDebugInfo".equals(ruleName)) {
         int var2 = gameRules.getBoolean(ruleName) ? 22 : 23;

         for(ServerPlayerEntity var4 : MinecraftServer.getInstance().getPlayerManager().players) {
            var4.networkHandler.sendPacket(new EntityEventS2CPacket(var4, (byte)var2));
         }
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      if (args.length == 1) {
         return suggestMatching(args, this.getGameRules().getAll());
      } else {
         return args.length == 2 ? suggestMatching(args, new String[]{"true", "false"}) : null;
      }
   }

   private Gamerules getGameRules() {
      return MinecraftServer.getInstance().getWorld(0).getGameRules();
   }
}
