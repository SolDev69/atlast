package net.minecraft.entity.vehicle;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.spawner.MobSpawner;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class SpawnerMinecartEntity extends MinecartEntity {
   private final MobSpawner spawner = new MobSpawner() {
      @Override
      public void broadcastEntityEvent(int event) {
         SpawnerMinecartEntity.this.world.doEntityEvent(SpawnerMinecartEntity.this, (byte)event);
      }

      @Override
      public World getWorld() {
         return SpawnerMinecartEntity.this.world;
      }

      @Override
      public BlockPos getPos() {
         return new BlockPos(SpawnerMinecartEntity.this);
      }
   };

   public SpawnerMinecartEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   public SpawnerMinecartEntity(World c_54ruxjwzt, double d, double e, double f) {
      super(c_54ruxjwzt, d, e, f);
   }

   @Override
   public MinecartEntity.Type getMinecartType() {
      return MinecartEntity.Type.SPAWNER;
   }

   @Override
   public Block getDefaultDisplayBlock() {
      return Blocks.MOB_SPAWNER;
   }

   @Override
   protected void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.spawner.readNbt(nbt);
   }

   @Override
   protected void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      this.spawner.writeNbt(nbt);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void doEvent(byte event) {
      this.spawner.doEntityEvent(event);
   }

   @Override
   public void tick() {
      super.tick();
      this.spawner.tick();
   }

   @Environment(EnvType.CLIENT)
   public MobSpawner getSpawnerBehavior() {
      return this.spawner;
   }
}
