package net.minecraft.world.storage;

import net.minecraft.world.WorldSettings;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class WorldSaveInfo implements Comparable {
   private final String fileName;
   private final String worldName;
   private final long lastPlayed;
   private final long size;
   private final boolean sameVersion;
   private final WorldSettings.GameMode gameMode;
   private final boolean hardcore;
   private final boolean cheats;

   public WorldSaveInfo(
      String fileName, String worldName, long lastPlayed, long size, WorldSettings.GameMode gameMode, boolean sameVersion, boolean hardcore, boolean cheats
   ) {
      this.fileName = fileName;
      this.worldName = worldName;
      this.lastPlayed = lastPlayed;
      this.size = size;
      this.gameMode = gameMode;
      this.sameVersion = sameVersion;
      this.hardcore = hardcore;
      this.cheats = cheats;
   }

   public String getFileName() {
      return this.fileName;
   }

   public String getWorldName() {
      return this.worldName;
   }

   public long getSize() {
      return this.size;
   }

   public boolean isSameVersion() {
      return this.sameVersion;
   }

   public long getLastPlayed() {
      return this.lastPlayed;
   }

   public int compareTo(WorldSaveInfo c_21lhpwfar) {
      if (this.lastPlayed < c_21lhpwfar.lastPlayed) {
         return 1;
      } else {
         return this.lastPlayed > c_21lhpwfar.lastPlayed ? -1 : this.fileName.compareTo(c_21lhpwfar.fileName);
      }
   }

   public WorldSettings.GameMode getGameMode() {
      return this.gameMode;
   }

   public boolean isHardcore() {
      return this.hardcore;
   }

   public boolean areCheatsEnabled() {
      return this.cheats;
   }
}
