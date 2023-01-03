package net.minecraft.server;

import net.minecraft.entity.Entity;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.SERVER)
public class Console implements CommandSource {
   private static final Console INSTANCE = new Console();
   private StringBuffer text = new StringBuffer();

   public static Console getInstance() {
      return INSTANCE;
   }

   public void destroy() {
      this.text.setLength(0);
   }

   public String getTextAsString() {
      return this.text.toString();
   }

   @Override
   public String getName() {
      return "Rcon";
   }

   @Override
   public Text getDisplayName() {
      return new LiteralText(this.getName());
   }

   @Override
   public void sendMessage(Text message) {
      this.text.append(message.buildString());
   }

   @Override
   public boolean canUseCommand(int permissionLevel, String command) {
      return true;
   }

   @Override
   public BlockPos getSourceBlockPos() {
      return new BlockPos(0, 0, 0);
   }

   @Override
   public World getSourceWorld() {
      return MinecraftServer.getInstance().getSourceWorld();
   }

   @Override
   public Entity asEntity() {
      return null;
   }

   @Override
   public boolean sendCommandFeedback() {
      return true;
   }

   @Override
   public void addResult(CommandResults.Type type, int result) {
   }
}
