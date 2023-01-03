package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.IcePatchFeature;
import net.minecraft.world.gen.feature.IceSpikeFeature;
import net.minecraft.world.gen.feature.SpruceTreeFeature;

public class IceBiome extends Biome {
   private boolean spikes;
   private IceSpikeFeature iceSpike = new IceSpikeFeature();
   private IcePatchFeature icePatch = new IcePatchFeature(4);

   public IceBiome(int id, boolean spikes) {
      super(id);
      this.spikes = spikes;
      if (spikes) {
         this.surfaceBlock = Blocks.SNOW.defaultState();
      }

      this.passiveEntries.clear();
   }

   @Override
   public void decorate(World world, Random random, BlockPos pos) {
      if (this.spikes) {
         for(int var4 = 0; var4 < 3; ++var4) {
            int var5 = random.nextInt(16) + 8;
            int var6 = random.nextInt(16) + 8;
            this.iceSpike.place(world, random, world.getHeight(pos.add(var5, 0, var6)));
         }

         for(int var7 = 0; var7 < 2; ++var7) {
            int var8 = random.nextInt(16) + 8;
            int var9 = random.nextInt(16) + 8;
            this.icePatch.place(world, random, world.getHeight(pos.add(var8, 0, var9)));
         }
      }

      super.decorate(world, random, pos);
   }

   @Override
   public AbstractTreeFeature getRandomTree(Random random) {
      return new SpruceTreeFeature(false);
   }

   @Override
   protected Biome mutate(int id) {
      Biome var2 = new IceBiome(id, true)
         .setColor(13828095, true)
         .setName(this.name + " Spikes")
         .setSnowy()
         .setTemperatureAndDownfall(0.0F, 0.5F)
         .setHeight(new Biome.Height(this.baseHeight + 0.1F, this.heightVariation + 0.1F));
      var2.baseHeight = this.baseHeight + 0.3F;
      var2.heightVariation = this.heightVariation + 0.4F;
      return var2;
   }
}
