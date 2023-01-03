package net.minecraft.realms;

import net.minecraft.world.storage.WorldSaveInfo;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class RealmsLevelSummary implements Comparable {
   private WorldSaveInfo levelSummary;

   public RealmsLevelSummary(WorldSaveInfo c_21lhpwfar) {
      this.levelSummary = c_21lhpwfar;
   }

   public int getGameMode() {
      return this.levelSummary.getGameMode().getIndex();
   }

   public String getLevelId() {
      return this.levelSummary.getFileName();
   }

   public boolean hasCheats() {
      return this.levelSummary.areCheatsEnabled();
   }

   public boolean isHardcore() {
      return this.levelSummary.isHardcore();
   }

   public boolean isRequiresConversion() {
      return this.levelSummary.isSameVersion();
   }

   public String getLevelName() {
      return this.levelSummary.getWorldName();
   }

   public long getLastPlayed() {
      return this.levelSummary.getLastPlayed();
   }

   public int compareTo(WorldSaveInfo c_21lhpwfar) {
      return this.levelSummary.compareTo(c_21lhpwfar);
   }

   public long getSizeOnDisk() {
      return this.levelSummary.getSize();
   }

   public int compareTo(RealmsLevelSummary realmsLevelSummary) {
      if (this.levelSummary.getLastPlayed() < realmsLevelSummary.getLastPlayed()) {
         return 1;
      } else {
         return this.levelSummary.getLastPlayed() > realmsLevelSummary.getLastPlayed()
            ? -1
            : this.levelSummary.getFileName().compareTo(realmsLevelSummary.getLevelId());
      }
   }
}
