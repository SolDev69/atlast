package net.minecraft.world.biome;

import net.minecraft.block.Blocks;
import net.minecraft.entity.living.mob.hostile.boss.EnderDragonEntity;
import net.minecraft.world.gen.feature.EndPillarFeature;
import net.minecraft.world.gen.feature.Feature;

public class TheEndBiomeDecorator extends BiomeDecorator {
   protected Feature pillar = new EndPillarFeature(Blocks.END_STONE);

   @Override
   protected void decorate(Biome biome) {
      this.placeVeins();
      if (this.random.nextInt(5) == 0) {
         int var2 = this.random.nextInt(16) + 8;
         int var3 = this.random.nextInt(16) + 8;
         this.pillar.place(this.world, this.random, this.world.getSurfaceHeight(this.pos.add(var2, 0, var3)));
      }

      if (this.pos.getX() == 0 && this.pos.getZ() == 0) {
         EnderDragonEntity var4 = new EnderDragonEntity(this.world);
         var4.refreshPositionAndAngles(0.0, 128.0, 0.0, this.random.nextFloat() * 360.0F, 0.0F);
         this.world.addEntity(var4);
      }
   }
}
