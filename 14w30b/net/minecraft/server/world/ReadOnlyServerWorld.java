package net.minecraft.server.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.storage.WorldStorage;
import net.minecraft.world.village.SavedVillageData;

public class ReadOnlyServerWorld extends ServerWorld {
   private ServerWorld delegate;

   public ReadOnlyServerWorld(MinecraftServer server, WorldStorage storage, int dimensionId, ServerWorld delegate, Profiler profiler) {
      super(server, storage, new ReadOnlyWorldData(delegate.getData()), dimensionId, profiler);
      this.delegate = delegate;
      delegate.getWorldBorder().addListener(new WorldBorderListener() {
         @Override
         public void onSizeChanged(WorldBorder border, double size) {
            ReadOnlyServerWorld.this.getWorldBorder().setSize(size);
         }

         @Override
         public void onSizeChanged(WorldBorder c_06ryzvjmf, double d, double e, int i) {
            ReadOnlyServerWorld.this.getWorldBorder().setSize(d, e, i);
         }

         @Override
         public void onCenterChanged(WorldBorder border, double centerX, double centerZ) {
            ReadOnlyServerWorld.this.getWorldBorder().setCenter(centerX, centerZ);
         }

         @Override
         public void onWarningTimeChanged(WorldBorder border, int warningTime) {
            ReadOnlyServerWorld.this.getWorldBorder().setWarningTime(warningTime);
         }

         @Override
         public void onWarningBlocksChanged(WorldBorder border, int warningBlocks) {
            ReadOnlyServerWorld.this.getWorldBorder().setWarningBlocks(warningBlocks);
         }

         @Override
         public void onDamagePerBlockChanged(WorldBorder border, double damagePerBlock) {
            ReadOnlyServerWorld.this.getWorldBorder().setDamagePerBlock(damagePerBlock);
         }

         @Override
         public void onSafeZoneChanged(WorldBorder border, double safeZone) {
            ReadOnlyServerWorld.this.getWorldBorder().setSafeZone(safeZone);
         }
      });
   }

   @Override
   protected void saveData() {
   }

   @Override
   public World init() {
      this.savedDataStorage = this.delegate.getSavedDataStorage();
      this.scoreboard = this.delegate.getScoreboard();
      String var1 = SavedVillageData.getId(this.dimension);
      SavedVillageData var2 = (SavedVillageData)this.savedDataStorage.loadData(SavedVillageData.class, var1);
      if (var2 == null) {
         this.villageData = new SavedVillageData(this);
         this.savedDataStorage.setData(var1, this.villageData);
      } else {
         this.villageData = var2;
         this.villageData.setWorld(this);
      }

      return this;
   }
}
