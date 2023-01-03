package net.minecraft.server.command;

import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.network.packet.s2c.play.TitlesS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandSyntaxException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TitleCommand extends Command {
   private static final Logger LOGGER = LogManager.getLogger();

   @Override
   public String getName() {
      return "title";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.title.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 2) {
         throw new IncorrectUsageException("commands.title.usage");
      } else {
         if (args.length < 3) {
            if ("title".equals(args[1]) || "subtitle".equals(args[1])) {
               throw new IncorrectUsageException("commands.title.usage.title");
            }

            if ("times".equals(args[1])) {
               throw new IncorrectUsageException("commands.title.usage.times");
            }
         }

         ServerPlayerEntity var3 = parsePlayer(source, args[0]);
         TitlesS2CPacket.Type var4 = TitlesS2CPacket.Type.byName(args[1]);
         if (var4 != TitlesS2CPacket.Type.CLEAR && var4 != TitlesS2CPacket.Type.RESET) {
            if (var4 == TitlesS2CPacket.Type.TIMES) {
               if (args.length != 5) {
                  throw new IncorrectUsageException("commands.title.usage");
               } else {
                  int var11 = parseInt(args[2]);
                  int var12 = parseInt(args[3]);
                  int var13 = parseInt(args[4]);
                  TitlesS2CPacket var14 = new TitlesS2CPacket(var11, var12, var13);
                  var3.networkHandler.sendPacket(var14);
                  sendSuccess(source, this, "commands.title.success", new Object[0]);
               }
            } else if (args.length < 3) {
               throw new IncorrectUsageException("commands.title.usage");
            } else {
               String var10 = parseString(args, 2);

               Text var6;
               try {
                  var6 = Text.Serializer.fromJson(var10);
               } catch (JsonParseException var9) {
                  Throwable var8 = ExceptionUtils.getRootCause(var9);
                  throw new CommandSyntaxException("commands.tellraw.jsonException", var8 == null ? "" : var8.getMessage());
               }

               TitlesS2CPacket var7 = new TitlesS2CPacket(var4, TextUtils.updateForEntity(source, var6, var3));
               var3.networkHandler.sendPacket(var7);
               sendSuccess(source, this, "commands.title.success", new Object[0]);
            }
         } else if (args.length != 2) {
            throw new IncorrectUsageException("commands.title.usage");
         } else {
            TitlesS2CPacket var5 = new TitlesS2CPacket(var4, null);
            var3.networkHandler.sendPacket(var5);
            sendSuccess(source, this, "commands.title.success", new Object[0]);
         }
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      if (args.length == 1) {
         return suggestMatching(args, MinecraftServer.getInstance().getPlayerNames());
      } else {
         return args.length == 2 ? suggestMatching(args, TitlesS2CPacket.Type.getNames()) : null;
      }
   }

   @Override
   public boolean hasTargetSelectorAt(String[] args, int index) {
      return index == 0;
   }
}
