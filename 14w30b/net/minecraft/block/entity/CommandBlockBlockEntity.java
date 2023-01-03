package net.minecraft.block.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.command.source.CommandExecutor;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class CommandBlockBlockEntity extends BlockEntity {
   private final CommandExecutor commandExecutor = new CommandExecutor() {
      @Override
      public BlockPos getSourceBlockPos() {
         return CommandBlockBlockEntity.this.pos;
      }

      @Override
      public World getSourceWorld() {
         return CommandBlockBlockEntity.this.getWorld();
      }

      @Override
      public void setCommand(String command) {
         super.setCommand(command);
         CommandBlockBlockEntity.this.markDirty();
      }

      @Override
      public void markDirty() {
         CommandBlockBlockEntity.this.getWorld().onBlockChanged(CommandBlockBlockEntity.this.pos);
      }

      @Environment(EnvType.CLIENT)
      @Override
      public int getType() {
         return 0;
      }

      @Environment(EnvType.CLIENT)
      @Override
      public void writeEntityId(ByteBuf byteBuf) {
         byteBuf.writeInt(CommandBlockBlockEntity.this.pos.getX());
         byteBuf.writeInt(CommandBlockBlockEntity.this.pos.getY());
         byteBuf.writeInt(CommandBlockBlockEntity.this.pos.getZ());
      }

      @Override
      public Entity asEntity() {
         return null;
      }
   };

   @Override
   public void writeNbt(NbtCompound nbt) {
      super.writeNbt(nbt);
      this.commandExecutor.writeNbt(nbt);
   }

   @Override
   public void readNbt(NbtCompound nbt) {
      super.readNbt(nbt);
      this.commandExecutor.readNbt(nbt);
   }

   @Override
   public Packet createUpdatePacket() {
      NbtCompound var1 = new NbtCompound();
      this.writeNbt(var1);
      return new BlockEntityUpdateS2CPacket(this.pos, 2, var1);
   }

   public CommandExecutor getCommandExecutor() {
      return this.commandExecutor;
   }

   public CommandResults getCommandResults() {
      return this.commandExecutor.getResults();
   }
}
