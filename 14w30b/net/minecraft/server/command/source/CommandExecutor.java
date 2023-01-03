package net.minecraft.server.command.source;

import io.netty.buffer.ByteBuf;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.handler.CommandHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class CommandExecutor implements CommandSource {
   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
   private int successCount;
   private boolean trackOutput = true;
   private Text lastOutput = null;
   private String command = "";
   private String name = "@";
   private final CommandResults results = new CommandResults();

   public int getSuccessCount() {
      return this.successCount;
   }

   public Text getLastOutput() {
      return this.lastOutput;
   }

   public void writeNbt(NbtCompound nbt) {
      nbt.putString("Command", this.command);
      nbt.putInt("SuccessCount", this.successCount);
      nbt.putString("CustomName", this.name);
      nbt.putBoolean("TrackOutput", this.trackOutput);
      if (this.lastOutput != null && this.trackOutput) {
         nbt.putString("LastOutput", Text.Serializer.toJson(this.lastOutput));
      }

      this.results.writeNbt(nbt);
   }

   public void readNbt(NbtCompound nbt) {
      this.command = nbt.getString("Command");
      this.successCount = nbt.getInt("SuccessCount");
      if (nbt.isType("CustomName", 8)) {
         this.name = nbt.getString("CustomName");
      }

      if (nbt.isType("TrackOutput", 1)) {
         this.trackOutput = nbt.getBoolean("TrackOutput");
      }

      if (nbt.isType("LastOutput", 8) && this.trackOutput) {
         this.lastOutput = Text.Serializer.fromJson(nbt.getString("LastOutput"));
      }

      this.results.readNbt(nbt);
   }

   @Override
   public boolean canUseCommand(int permissionLevel, String command) {
      return permissionLevel <= 2;
   }

   public void setCommand(String command) {
      this.command = command;
      this.successCount = 0;
   }

   public String getCommand() {
      return this.command;
   }

   public void run(World world) {
      if (world.isClient) {
         this.successCount = 0;
      }

      MinecraftServer var2 = MinecraftServer.getInstance();
      if (var2 != null && var2.areCommandBlocksEnabled()) {
         CommandHandler var3 = var2.getCommandHandler();

         try {
            this.lastOutput = null;
            this.successCount = var3.run(this, this.command);
         } catch (Throwable var7) {
            CrashReport var5 = CrashReport.of(var7, "Executing command block");
            CashReportCategory var6 = var5.addCategory("Command to be executed");
            var6.add("Command", new Callable() {
               public String call() {
                  return CommandExecutor.this.getCommand();
               }
            });
            var6.add("Name", new Callable() {
               public String call() {
                  return CommandExecutor.this.getName();
               }
            });
            throw new CrashException(var5);
         }
      } else {
         this.successCount = 0;
      }
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public Text getDisplayName() {
      return new LiteralText(this.getName());
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public void sendMessage(Text message) {
      if (this.trackOutput && this.getSourceWorld() != null && !this.getSourceWorld().isClient) {
         this.lastOutput = new LiteralText("[" + DATE_FORMAT.format(new Date()) + "] ").append(message);
         this.markDirty();
      }
   }

   @Override
   public boolean sendCommandFeedback() {
      MinecraftServer var1 = MinecraftServer.getInstance();
      return var1 == null || var1.worlds[0].getGameRules().getBoolean("commandBlockOutput");
   }

   @Override
   public void addResult(CommandResults.Type type, int result) {
      this.results.add(this, type, result);
   }

   public abstract void markDirty();

   @Environment(EnvType.CLIENT)
   public abstract int getType();

   @Environment(EnvType.CLIENT)
   public abstract void writeEntityId(ByteBuf byteBuf);

   public void setLastOutput(Text lastOutput) {
      this.lastOutput = lastOutput;
   }

   public void setTrackOutput(boolean trackOutput) {
      this.trackOutput = trackOutput;
   }

   public boolean trackOutput() {
      return this.trackOutput;
   }

   public boolean openScreen(PlayerEntity player) {
      if (!player.abilities.creativeMode) {
         return false;
      } else {
         if (player.getSourceWorld().isClient) {
            player.openCommandBlockScreen(this);
         }

         return true;
      }
   }

   public CommandResults getResults() {
      return this.results;
   }
}
