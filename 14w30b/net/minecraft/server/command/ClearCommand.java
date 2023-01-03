package net.minecraft.server.command;

import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class ClearCommand extends Command {
   @Override
   public String getName() {
      return "clear";
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.clear.usage";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void run(CommandSource source, String[] args) {
      ServerPlayerEntity var3 = args.length == 0 ? asPlayer(source) : parsePlayer(source, args[0]);
      Item var4 = args.length >= 2 ? parseItem(source, args[1]) : null;
      int var5 = args.length >= 3 ? parseInt(args[2], -1) : -1;
      int var6 = args.length >= 4 ? parseInt(args[3], -1) : -1;
      NbtCompound var7 = null;
      if (args.length >= 5) {
         try {
            var7 = StringNbtReader.parse(parseString(args, 4));
         } catch (NbtException var9) {
            throw new CommandException("commands.clear.tagError", var9.getMessage());
         }
      }

      if (args.length >= 2 && var4 == null) {
         throw new CommandException("commands.clear.failure", var3.getName());
      } else {
         int var8 = var3.inventory.getRemovedAmount(var4, var5, var6, var7);
         var3.playerMenu.updateListeners();
         if (!var3.abilities.creativeMode) {
            var3.use();
         }

         source.addResult(CommandResults.Type.AFFECTED_ITEMS, var8);
         if (var8 == 0) {
            throw new CommandException("commands.clear.failure", var3.getName());
         } else {
            if (var6 == 0) {
               source.sendMessage(new TranslatableText("commands.clear.testing", var3.getName(), var8));
            } else {
               sendSuccess(source, this, "commands.clear.success", new Object[]{var3.getName(), var8});
            }
         }
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      if (args.length == 1) {
         return suggestMatching(args, this.getPlayerNames());
      } else {
         return args.length == 2 ? suggestMatching(args, Item.REGISTRY.keySet()) : null;
      }
   }

   protected String[] getPlayerNames() {
      return MinecraftServer.getInstance().getPlayerNames();
   }

   @Override
   public boolean hasTargetSelectorAt(String[] args, int index) {
      return index == 0;
   }
}
