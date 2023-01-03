package net.minecraft.server.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.BlockMiningProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.SoundEventS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEventListener;

public class ServerWorldEventListener implements WorldEventListener {
   private MinecraftServer server;
   private ServerWorld world;

   public ServerWorldEventListener(MinecraftServer server, ServerWorld world) {
      this.server = server;
      this.world = world;
   }

   @Override
   public void addParticle(
      int type, boolean ignoreDistance, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters
   ) {
   }

   @Override
   public void onEntityAdded(Entity entity) {
      this.world.getEntityTracker().onEntityAdded(entity);
   }

   @Override
   public void onEntityRemoved(Entity entity) {
      this.world.getEntityTracker().onEntityRemoved(entity);
   }

   @Override
   public void playSound(String name, double x, double y, double z, float pitch, float volume) {
      this.server
         .getPlayerManager()
         .sendToAround(
            x, y, z, pitch > 1.0F ? (double)(16.0F * pitch) : 16.0, this.world.dimension.getId(), new SoundEventS2CPacket(name, x, y, z, pitch, volume)
         );
   }

   @Override
   public void playSound(PlayerEntity source, String name, double x, double y, double z, float pitch, float volume) {
      this.server
         .getPlayerManager()
         .sendToAround(
            source, x, y, z, pitch > 1.0F ? (double)(16.0F * pitch) : 16.0, this.world.dimension.getId(), new SoundEventS2CPacket(name, x, y, z, pitch, volume)
         );
   }

   @Override
   public void onRegionChanged(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
   }

   @Override
   public void onBlockChanged(BlockPos x) {
      this.world.getChunkMap().onBlockChanged(x);
   }

   @Override
   public void onLightChanged(BlockPos pos) {
   }

   @Override
   public void onRecordRemoved(String record, BlockPos pos) {
   }

   @Override
   public void doEvent(PlayerEntity source, int type, BlockPos pos, int data) {
      this.server
         .getPlayerManager()
         .sendToAround(
            source,
            (double)pos.getX(),
            (double)pos.getY(),
            (double)pos.getZ(),
            64.0,
            this.world.dimension.getId(),
            new WorldEventS2CPacket(type, pos, data, false)
         );
   }

   @Override
   public void doGlobalEvent(int type, BlockPos pos, int data) {
      this.server.getPlayerManager().sendToAll(new WorldEventS2CPacket(type, pos, data, true));
   }

   @Override
   public void updateBlockMiningProgress(int id, BlockPos pos, int progress) {
      for(ServerPlayerEntity var5 : this.server.getPlayerManager().players) {
         if (var5 != null && var5.world == this.world && var5.getNetworkId() != id) {
            double var6 = (double)pos.getX() - var5.x;
            double var8 = (double)pos.getY() - var5.y;
            double var10 = (double)pos.getZ() - var5.z;
            if (var6 * var6 + var8 * var8 + var10 * var10 < 1024.0) {
               var5.networkHandler.sendPacket(new BlockMiningProgressS2CPacket(id, pos, progress));
            }
         }
      }
   }
}
