package net.minecraft.world.biome.layer;

public class FuzzyZoomLayer extends ZoomLayer {
   public FuzzyZoomLayer(long l, Layer c_48ualqmqj) {
      super(l, c_48ualqmqj);
   }

   @Override
   protected int getModeOrRandom(int x, int z, int width, int length) {
      return this.getRandomInt(new int[]{x, z, width, length});
   }
}
