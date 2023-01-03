package net.minecraft.server.command;

import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandSyntaxException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class TellRawCommand extends Command {
   @Override
   public String getName() {
      return "tellraw";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.tellraw.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 2) {
         throw new IncorrectUsageException("commands.tellraw.usage");
      } else {
         ServerPlayerEntity var3 = parsePlayer(source, args[0]);
         String var4 = parseString(args, 1);

         try {
            Text var5 = Text.Serializer.fromJson(var4);
            var3.sendMessage(TextUtils.updateForEntity(source, var5, var3));
         } catch (JsonParseException var7) {
            Throwable var6 = ExceptionUtils.getRootCause(var7);
            throw new CommandSyntaxException("commands.tellraw.jsonException", var6 == null ? "" : var6.getMessage());
         }
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      return args.length == 1 ? suggestMatching(args, MinecraftServer.getInstance().getPlayerNames()) : null;
   }

   @Override
   public boolean hasTargetSelectorAt(String[] args, int index) {
      return index == 0;
   }
}
