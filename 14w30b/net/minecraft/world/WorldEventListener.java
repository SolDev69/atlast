package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public interface WorldEventListener {
   void onBlockChanged(BlockPos x);

   void onLightChanged(BlockPos pos);

   void onRegionChanged(int minX, int minY, int minZ, int maxX, int maxY, int maxZ);

   void playSound(String name, double x, double y, double z, float pitch, float volume);

   void playSound(PlayerEntity source, String name, double x, double y, double z, float pitch, float volume);

   void addParticle(int type, boolean ignoreDistance, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters);

   void onEntityAdded(Entity entity);

   void onEntityRemoved(Entity entity);

   void onRecordRemoved(String record, BlockPos pos);

   void doGlobalEvent(int type, BlockPos pos, int data);

   void doEvent(PlayerEntity source, int type, BlockPos pos, int data);

   void updateBlockMiningProgress(int id, BlockPos pos, int progress);
}
