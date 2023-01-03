package net.minecraft.world.gen.structure;

import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.math.MathHelper;

public class MineshaftStructure extends StructureFeature {
   private double spawnChance = 0.004;

   public MineshaftStructure() {
   }

   @Override
   public String getName() {
      return "Mineshaft";
   }

   public MineshaftStructure(Map options) {
      for(Entry var3 : options.entrySet()) {
         if (((String)var3.getKey()).equals("chance")) {
            this.spawnChance = MathHelper.parseDouble((String)var3.getValue(), this.spawnChance);
         }
      }
   }

   @Override
   protected boolean isFeatureChunk(int chunkX, int chunkZ) {
      return this.random.nextDouble() < this.spawnChance && this.random.nextInt(80) < Math.max(Math.abs(chunkX), Math.abs(chunkZ));
   }

   @Override
   protected StructureStart createStart(int x, int z) {
      return new MineshaftStart(this.world, this.random, x, z);
   }
}
