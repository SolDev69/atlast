package net.minecraft.server.dedicated.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.storage.exception.WorldStorageException;

public class SaveAllCommand extends Command {
   @Override
   public String getName() {
      return "save-all";
   }

   @Override
   public String getUsage(CommandSource source) {
      return "commands.save.usage";
   }

   @Override
   public void run(CommandSource source, String[] args) {
      MinecraftServer var3 = MinecraftServer.getInstance();
      source.sendMessage(new TranslatableText("commands.save.start"));
      if (var3.getPlayerManager() != null) {
         var3.getPlayerManager().saveData();
      }

      try {
         for(int var4 = 0; var4 < var3.worlds.length; ++var4) {
            if (var3.worlds[var4] != null) {
               ServerWorld var5 = var3.worlds[var4];
               boolean var6 = var5.isSaving;
               var5.isSaving = false;
               var5.save(true, null);
               var5.isSaving = var6;
            }
         }

         if (args.length > 0 && "flush".equals(args[0])) {
            source.sendMessage(new TranslatableText("commands.save.flushStart"));

            for(int var8 = 0; var8 < var3.worlds.length; ++var8) {
               if (var3.worlds[var8] != null) {
                  ServerWorld var9 = var3.worlds[var8];
                  boolean var10 = var9.isSaving;
                  var9.isSaving = false;
                  var9.saveChunks();
                  var9.isSaving = var10;
               }
            }

            source.sendMessage(new TranslatableText("commands.save.flushEnd"));
         }
      } catch (WorldStorageException var7) {
         sendSuccess(source, this, "commands.save.failed", new Object[]{var7.getMessage()});
         return;
      }

      sendSuccess(source, this, "commands.save.success", new Object[0]);
   }
}
