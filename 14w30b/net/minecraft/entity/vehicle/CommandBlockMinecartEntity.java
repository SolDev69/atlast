package net.minecraft.entity.vehicle;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.source.CommandExecutor;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class CommandBlockMinecartEntity extends MinecartEntity {
   private final CommandExecutor executor = new CommandExecutor() {
      @Override
      public void markDirty() {
         CommandBlockMinecartEntity.this.getDataTracker().update(23, this.getCommand());
         CommandBlockMinecartEntity.this.getDataTracker().update(24, Text.Serializer.toJson(this.getLastOutput()));
      }

      @Environment(EnvType.CLIENT)
      @Override
      public int getType() {
         return 1;
      }

      @Environment(EnvType.CLIENT)
      @Override
      public void writeEntityId(ByteBuf byteBuf) {
         byteBuf.writeInt(CommandBlockMinecartEntity.this.getNetworkId());
      }

      @Override
      public BlockPos getSourceBlockPos() {
         return new BlockPos(CommandBlockMinecartEntity.this.x, CommandBlockMinecartEntity.this.y + 0.5, CommandBlockMinecartEntity.this.z);
      }

      @Override
      public World getSourceWorld() {
         return CommandBlockMinecartEntity.this.world;
      }

      @Override
      public Entity asEntity() {
         return CommandBlockMinecartEntity.this;
      }
   };
   private int lastExecuted = 0;

   public CommandBlockMinecartEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   public CommandBlockMinecartEntity(World c_54ruxjwzt, double d, double e, double f) {
      super(c_54ruxjwzt, d, e, f);
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.getDataTracker().put(23, "");
      this.getDataTracker().put(24, "");
   }

   @Override
   protected void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.executor.readNbt(nbt);
      this.getDataTracker().update(23, this.getCommandExecutor().getCommand());
      this.getDataTracker().update(24, Text.Serializer.toJson(this.getCommandExecutor().getLastOutput()));
   }

   @Override
   protected void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      this.executor.writeNbt(nbt);
   }

   @Override
   public MinecartEntity.Type getMinecartType() {
      return MinecartEntity.Type.COMMAND_BLOCK;
   }

   @Override
   public Block getDefaultDisplayBlock() {
      return Blocks.COMMAND_BLOCK;
   }

   public CommandExecutor getCommandExecutor() {
      return this.executor;
   }

   @Override
   public void onActivatorRail(int x, int y, int z, boolean powered) {
      if (powered && this.time - this.lastExecuted >= 4) {
         this.getCommandExecutor().run(this.world);
         this.lastExecuted = this.time;
      }
   }

   @Override
   public boolean interact(PlayerEntity player) {
      this.executor.openScreen(player);
      return false;
   }

   @Override
   public void onDataValueChanged(int id) {
      super.onDataValueChanged(id);
      if (id == 24) {
         try {
            this.executor.setLastOutput(Text.Serializer.fromJson(this.getDataTracker().getString(24)));
         } catch (Throwable var3) {
         }
      } else if (id == 23) {
         this.executor.setCommand(this.getDataTracker().getString(23));
      }
   }
}
