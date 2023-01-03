package net.minecraft.server.command;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

public class GiveCommand extends Command {
   @Override
   public String getName() {
      return "give";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.give.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 2) {
         throw new IncorrectUsageException("commands.give.usage");
      } else {
         ServerPlayerEntity var3 = parsePlayer(source, args[0]);
         Item var4 = parseItem(source, args[1]);
         int var5 = args.length >= 3 ? parseInt(args[2], 1, 64) : 1;
         int var6 = args.length >= 4 ? parseInt(args[3]) : 0;
         ItemStack var7 = new ItemStack(var4, var5, var6);
         if (args.length >= 5) {
            String var8 = parseText(source, args, 4).buildString();

            try {
               var7.setNbt(StringNbtReader.parse(var8));
            } catch (NbtException var10) {
               throw new CommandException("commands.give.tagError", var10.getMessage());
            }
         }

         boolean var11 = var3.inventory.insertStack(var7);
         if (var11) {
            var3.world.playSound((Entity)var3, "random.pop", 0.2F, ((var3.getRandom().nextFloat() - var3.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            var3.playerMenu.updateListeners();
         }

         if (var11 && var7.size <= 0) {
            var7.size = 1;
            source.addResult(CommandResults.Type.AFFECTED_ITEMS, var5);
            ItemEntity var12 = var3.dropItem(var7, false);
            if (var12 != null) {
               var12.m_67zgljrbu();
            }
         } else {
            source.addResult(CommandResults.Type.AFFECTED_ITEMS, var5 - var7.size);
            ItemEntity var9 = var3.dropItem(var7, false);
            if (var9 != null) {
               var9.getPickupCooldown();
               var9.setOwner(var3.getName());
            }
         }

         sendSuccess(source, this, "commands.give.success", new Object[]{var7.getDisplayName(), var5, var3.getName()});
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
