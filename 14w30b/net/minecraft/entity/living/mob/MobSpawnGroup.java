package net.minecraft.entity.living.mob;

import net.minecraft.block.material.Material;
import net.minecraft.entity.living.mob.ambient.AmbientEntity;
import net.minecraft.entity.living.mob.passive.animal.AnimalEntity;
import net.minecraft.entity.living.mob.water.WaterMobEntity;

public enum MobSpawnGroup {
   MONSTER(Monster.class, 70, Material.AIR, false, false),
   CREATURE(AnimalEntity.class, 10, Material.AIR, true, true),
   AMBIENT(AmbientEntity.class, 15, Material.AIR, true, false),
   WATER_CREATURE(WaterMobEntity.class, 5, Material.WATER, true, false);

   private final Class type;
   private final int capacity;
   private final Material spawnableMaterial;
   private final boolean peaceful;
   private final boolean rare;

   private MobSpawnGroup(Class type, int capacity, Material spawnableMaterial, boolean peaceful, boolean rare) {
      this.type = type;
      this.capacity = capacity;
      this.spawnableMaterial = spawnableMaterial;
      this.peaceful = peaceful;
      this.rare = rare;
   }

   public Class getType() {
      return this.type;
   }

   public int getCapacity() {
      return this.capacity;
   }

   public boolean isPeaceful() {
      return this.peaceful;
   }

   public boolean isRare() {
      return this.rare;
   }
}
