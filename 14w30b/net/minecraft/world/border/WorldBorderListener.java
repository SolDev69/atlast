package net.minecraft.world.border;

public interface WorldBorderListener {
   void onSizeChanged(WorldBorder border, double size);

   void onSizeChanged(WorldBorder c_06ryzvjmf, double d, double e, int i);

   void onCenterChanged(WorldBorder border, double centerX, double centerZ);

   void onWarningTimeChanged(WorldBorder border, int warningTime);

   void onWarningBlocksChanged(WorldBorder border, int warningBlocks);

   void onDamagePerBlockChanged(WorldBorder border, double damagePerBlock);

   void onSafeZoneChanged(WorldBorder border, double safeZone);
}
