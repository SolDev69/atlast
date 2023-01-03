package net.minecraft.server.command;

import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.exception.InvalidNumberException;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;

public class EnchantCommand extends Command {
   @Override
   public String getName() {
      return "enchant";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.enchant.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 2) {
         throw new IncorrectUsageException("commands.enchant.usage");
      } else {
         ServerPlayerEntity var3 = parsePlayer(source, args[0]);
         source.addResult(CommandResults.Type.AFFECTED_ITEMS, 0);

         int var4;
         try {
            var4 = parseInt(args[1], 0);
         } catch (InvalidNumberException var12) {
            Enchantment var6 = Enchantment.byId(args[1]);
            if (var6 == null) {
               throw var12;
            }

            var4 = var6.id;
         }

         int var5 = 1;
         ItemStack var13 = var3.getMainHandStack();
         if (var13 == null) {
            throw new CommandException("commands.enchant.noItem");
         } else {
            Enchantment var7 = Enchantment.byRawId(var4);
            if (var7 == null) {
               throw new InvalidNumberException("commands.enchant.notFound", var4);
            } else if (!var7.isValidTarget(var13)) {
               throw new CommandException("commands.enchant.cantEnchant");
            } else {
               if (args.length >= 3) {
                  var5 = parseInt(args[2], var7.getMinLevel(), var7.getMaxLevel());
               }

               if (var13.hasNbt()) {
                  NbtList var8 = var13.getEnchantments();
                  if (var8 != null) {
                     for(int var9 = 0; var9 < var8.size(); ++var9) {
                        short var10 = var8.getCompound(var9).getShort("id");
                        if (Enchantment.byRawId(var10) != null) {
                           Enchantment var11 = Enchantment.byRawId(var10);
                           if (!var11.checkCompatibility(var7)) {
                              throw new CommandException(
                                 "commands.enchant.cantCombine", var7.getDisplayName(var5), var11.getDisplayName(var8.getCompound(var9).getShort("lvl"))
                              );
                           }
                        }
                     }
                  }
               }

               var13.addEnchantment(var7, var5);
               sendSuccess(source, this, "commands.enchant.success", new Object[0]);
               source.addResult(CommandResults.Type.AFFECTED_ITEMS, 1);
            }
         }
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      if (args.length == 1) {
         return suggestMatching(args, this.getPlayerNames());
      } else {
         return args.length == 2 ? suggestMatching(args, Enchantment.m_04ntpdulz()) : null;
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
