package net.minecraft.server.command;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.exception.InvalidNumberException;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReplaceItemCommand extends Command {
   private static final Map SLOTS_BY_ID = Maps.newHashMap();

   @Override
   public String getName() {
      return "replaceitem";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.replaceitem.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      if (args.length < 1) {
         throw new IncorrectUsageException("commands.replaceitem.usage");
      } else {
         boolean var3;
         if (args[0].equals("entity")) {
            var3 = false;
         } else {
            if (!args[0].equals("block")) {
               throw new IncorrectUsageException("commands.replaceitem.usage");
            }

            var3 = true;
         }

         int var4;
         if (var3) {
            if (args.length < 6) {
               throw new IncorrectUsageException("commands.replaceitem.block.usage");
            }

            var4 = 4;
         } else {
            if (args.length < 4) {
               throw new IncorrectUsageException("commands.replaceitem.entity.usage");
            }

            var4 = 2;
         }

         int var5 = this.getSlot(args[var4++]);

         Item var6;
         try {
            var6 = parseItem(source, args[var4]);
         } catch (InvalidNumberException var15) {
            if (Block.byId(args[var4]) != Blocks.AIR) {
               throw var15;
            }

            var6 = null;
         }

         ++var4;
         int var7 = args.length > var4 ? parseInt(args[var4++], 1, 64) : 1;
         int var8 = args.length > var4 ? parseInt(args[var4++]) : 0;
         ItemStack var9 = new ItemStack(var6, var7, var8);
         if (args.length > var4) {
            String var10 = parseText(source, args, var4).buildString();

            try {
               var9.setNbt(StringNbtReader.parse(var10));
            } catch (NbtException var14) {
               throw new CommandException("commands.replaceitem.tagError", var14.getMessage());
            }
         }

         if (var9.getItem() == null) {
            var9 = null;
         }

         if (var3) {
            source.addResult(CommandResults.Type.AFFECTED_ITEMS, 0);
            BlockPos var18 = parseBlockPos(source, args, 1, false);
            World var11 = source.getSourceWorld();
            BlockEntity var12 = var11.getBlockEntity(var18);
            if (var12 == null || !(var12 instanceof Inventory)) {
               throw new CommandException("commands.replaceitem.noContainer", var18.getX(), var18.getY(), var18.getZ());
            }

            Inventory var13 = (Inventory)var12;
            if (var5 >= 0 && var5 < var13.getSize()) {
               var13.setStack(var5, var9);
            }
         } else {
            Entity var19 = parseEntity(source, args[1]);
            source.addResult(CommandResults.Type.AFFECTED_ITEMS, 0);
            if (!var19.m_81zmldzmm(var5, var9)) {
               throw new CommandException("commands.replaceitem.failed", var5, var7, var9 == null ? "Air" : var9.getDisplayName());
            }

            if (var19 instanceof PlayerEntity) {
               ((PlayerEntity)var19).playerMenu.updateListeners();
            }
         }

         source.addResult(CommandResults.Type.AFFECTED_ITEMS, var7);
         sendSuccess(source, this, "commands.replaceitem.success", new Object[]{var5, var7, var9 == null ? "Air" : var9.getDisplayName()});
      }
   }

   private int getSlot(String slotId) {
      if (!SLOTS_BY_ID.containsKey(slotId)) {
         throw new CommandException("commands.generic.parameter.invalid", slotId);
      } else {
         return SLOTS_BY_ID.get(slotId);
      }
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      if (args.length == 1) {
         return suggestMatching(args, new String[]{"entity", "block"});
      } else if (args.length == 2 && args[0].equals("entity")) {
         return suggestMatching(args, this.getPlayerNames());
      } else if ((args.length != 3 || !args[0].equals("entity")) && (args.length != 5 || !args[0].equals("block"))) {
         return (args.length != 4 || !args[0].equals("entity")) && (args.length != 6 || !args[0].equals("block"))
            ? null
            : suggestMatching(args, Item.REGISTRY.keySet());
      } else {
         return suggestMatching(args, SLOTS_BY_ID.keySet());
      }
   }

   protected String[] getPlayerNames() {
      return MinecraftServer.getInstance().getPlayerNames();
   }

   @Override
   public boolean hasTargetSelectorAt(String[] args, int index) {
      return args.length > 0 && args[0].equals("entity") && index == 1;
   }

   static {
      for(int var0 = 0; var0 < 54; ++var0) {
         SLOTS_BY_ID.put("slot.container." + var0, var0);
      }

      for(int var1 = 0; var1 < 9; ++var1) {
         SLOTS_BY_ID.put("slot.hotbar." + var1, var1);
      }

      for(int var2 = 0; var2 < 27; ++var2) {
         SLOTS_BY_ID.put("slot.inventory." + var2, 9 + var2);
      }

      for(int var3 = 0; var3 < 27; ++var3) {
         SLOTS_BY_ID.put("slot.enderchest." + var3, 200 + var3);
      }

      for(int var4 = 0; var4 < 8; ++var4) {
         SLOTS_BY_ID.put("slot.villager." + var4, 300 + var4);
      }

      for(int var5 = 0; var5 < 15; ++var5) {
         SLOTS_BY_ID.put("slot.horse." + var5, 500 + var5);
      }

      SLOTS_BY_ID.put("slot.weapon", 99);
      SLOTS_BY_ID.put("slot.armor.head", 103);
      SLOTS_BY_ID.put("slot.armor.chest", 102);
      SLOTS_BY_ID.put("slot.armor.legs", 101);
      SLOTS_BY_ID.put("slot.armor.feet", 100);
      SLOTS_BY_ID.put("slot.horse.saddle", 400);
      SLOTS_BY_ID.put("slot.horse.armor", 401);
      SLOTS_BY_ID.put("slot.horse.chest", 499);
   }
}
