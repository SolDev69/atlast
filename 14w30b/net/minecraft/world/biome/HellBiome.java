package net.minecraft.world.biome;

import net.minecraft.entity.living.mob.GhastEntity;
import net.minecraft.entity.living.mob.MagmaCubeEntity;
import net.minecraft.entity.living.mob.hostile.ZombiePigmanEntity;

public class HellBiome extends Biome {
   public HellBiome(int i) {
      super(i);
      this.monsterEntries.clear();
      this.passiveEntries.clear();
      this.waterEntries.clear();
      this.caveEntries.clear();
      this.monsterEntries.add(new Biome.SpawnEntry(GhastEntity.class, 50, 4, 4));
      this.monsterEntries.add(new Biome.SpawnEntry(ZombiePigmanEntity.class, 100, 4, 4));
      this.monsterEntries.add(new Biome.SpawnEntry(MagmaCubeEntity.class, 1, 4, 4));
   }
}
