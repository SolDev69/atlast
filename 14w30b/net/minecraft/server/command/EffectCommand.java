package net.minecraft.server.command;

import java.util.List;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.exception.InvalidNumberException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.TranslatableText;

public class EffectCommand extends Command {
   @Override
   public String getName() {
      return "effect";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.effect.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 2) {
         throw new IncorrectUsageException("commands.effect.usage");
      } else {
         LivingEntity var3 = (LivingEntity)parseEntity(source, args[0], LivingEntity.class);
         if (args[1].equals("clear")) {
            if (var3.getStatusEffects().isEmpty()) {
               throw new CommandException("commands.effect.failure.notActive.all", var3.getName());
            } else {
               var3.clearStatusEffects();
               sendSuccess(source, this, "commands.effect.success.removed.all", new Object[]{var3.getName()});
            }
         } else {
            int var4;
            try {
               var4 = parseInt(args[1], 1);
            } catch (InvalidNumberException var11) {
               StatusEffect var6 = StatusEffect.get(args[1]);
               if (var6 == null) {
                  throw var11;
               }

               var4 = var6.id;
            }

            int var5 = 600;
            int var12 = 30;
            int var7 = 0;
            if (var4 >= 0 && var4 < StatusEffect.BY_ID.length && StatusEffect.BY_ID[var4] != null) {
               StatusEffect var8 = StatusEffect.BY_ID[var4];
               if (args.length >= 3) {
                  var12 = parseInt(args[2], 0, 1000000);
                  if (var8.isInstant()) {
                     var5 = var12;
                  } else {
                     var5 = var12 * 20;
                  }
               } else if (var8.isInstant()) {
                  var5 = 1;
               }

               if (args.length >= 4) {
                  var7 = parseInt(args[3], 0, 255);
               }

               boolean var9 = true;
               if (args.length >= 5 && "true".equalsIgnoreCase(args[4])) {
                  var9 = false;
               }

               if (var12 > 0) {
                  StatusEffectInstance var10 = new StatusEffectInstance(var4, var5, var7, false, var9);
                  var3.addStatusEffect(var10);
                  sendSuccess(source, this, "commands.effect.success", new Object[]{new TranslatableText(var10.getName()), var4, var7, var3.getName(), var12});
               } else if (var3.hasStatusEffect(var4)) {
                  var3.removeStatusEffect(var4);
                  sendSuccess(source, this, "commands.effect.success.removed", new Object[]{new TranslatableText(var8.getName()), var3.getName()});
               } else {
                  throw new CommandException("commands.effect.failure.notActive", new TranslatableText(var8.getName()), var3.getName());
               }
            } else {
               throw new InvalidNumberException("commands.effect.notFound", var4);
            }
         }
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      if (args.length == 1) {
         return suggestMatching(args, this.getPlayerNames());
      } else if (args.length == 2) {
         return suggestMatching(args, StatusEffect.m_01todocay());
      } else {
         return args.length == 5 ? suggestMatching(args, new String[]{"true", "false"}) : null;
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
