package net.minecraft.block.entity;

import com.google.gson.JsonParseException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.SignBlockEntityUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class SignBlockEntity extends BlockEntity {
   public final Text[] lines = new Text[]{new LiteralText(""), new LiteralText(""), new LiteralText(""), new LiteralText("")};
   public int currentRow = -1;
   private boolean editable = true;
   private PlayerEntity player;
   private final CommandResults commandResults = new CommandResults();

   @Override
   public void writeNbt(NbtCompound nbt) {
      super.writeNbt(nbt);

      for(int var2 = 0; var2 < 4; ++var2) {
         String var3 = Text.Serializer.toJson(this.lines[var2]);
         nbt.putString("Text" + (var2 + 1), var3);
      }

      this.commandResults.writeNbt(nbt);
   }

   @Override
   public void readNbt(NbtCompound nbt) {
      this.editable = false;
      super.readNbt(nbt);
      CommandSource var2 = new CommandSource() {
         @Override
         public String getName() {
            return "Sign";
         }

         @Override
         public Text getDisplayName() {
            return new LiteralText(this.getName());
         }

         @Override
         public void sendMessage(Text message) {
         }

         @Override
         public boolean canUseCommand(int permissionLevel, String command) {
            return true;
         }

         @Override
         public BlockPos getSourceBlockPos() {
            return SignBlockEntity.this.pos;
         }

         @Override
         public World getSourceWorld() {
            return SignBlockEntity.this.world;
         }

         @Override
         public Entity asEntity() {
            return null;
         }

         @Override
         public boolean sendCommandFeedback() {
            return false;
         }

         @Override
         public void addResult(CommandResults.Type type, int result) {
         }
      };

      for(int var3 = 0; var3 < 4; ++var3) {
         String var4 = nbt.getString("Text" + (var3 + 1));

         try {
            Text var5 = Text.Serializer.fromJson(var4);

            try {
               this.lines[var3] = TextUtils.updateForEntity(var2, var5, null);
            } catch (CommandException var7) {
               this.lines[var3] = var5;
            }
         } catch (JsonParseException var8) {
            this.lines[var3] = new LiteralText(var4);
         }
      }

      this.commandResults.readNbt(nbt);
   }

   @Override
   public Packet createUpdatePacket() {
      Text[] var1 = new Text[4];
      System.arraycopy(this.lines, 0, var1, 0, 4);
      return new SignBlockEntityUpdateS2CPacket(this.world, this.pos, var1);
   }

   public boolean isEditable() {
      return this.editable;
   }

   @Environment(EnvType.CLIENT)
   public void setEditable(boolean editable) {
      this.editable = editable;
      if (!editable) {
         this.player = null;
      }
   }

   public void setPlayer(PlayerEntity player) {
      this.player = player;
   }

   public PlayerEntity getPlayer() {
      return this.player;
   }

   public boolean onUse(PlayerEntity player) {
      CommandSource var2 = new CommandSource() {
         @Override
         public String getName() {
            return player.getName();
         }

         @Override
         public Text getDisplayName() {
            return player.getDisplayName();
         }

         @Override
         public void sendMessage(Text message) {
         }

         @Override
         public boolean canUseCommand(int permissionLevel, String command) {
            return true;
         }

         @Override
         public BlockPos getSourceBlockPos() {
            return SignBlockEntity.this.pos;
         }

         @Override
         public World getSourceWorld() {
            return player.getSourceWorld();
         }

         @Override
         public Entity asEntity() {
            return player;
         }

         @Override
         public boolean sendCommandFeedback() {
            return false;
         }

         @Override
         public void addResult(CommandResults.Type type, int result) {
            SignBlockEntity.this.commandResults.add(this, type, result);
         }
      };

      for(int var3 = 0; var3 < this.lines.length; ++var3) {
         Style var4 = this.lines[var3] == null ? null : this.lines[var3].getStyle();
         if (var4 != null && var4.getClickEvent() != null) {
            ClickEvent var5 = var4.getClickEvent();
            if (var5.getAction() == ClickEvent.Action.RUN_COMMAND) {
               MinecraftServer.getInstance().getCommandHandler().run(var2, var5.getValue());
            }
         }
      }

      return true;
   }

   public CommandResults getCommandResults() {
      return this.commandResults;
   }
}
