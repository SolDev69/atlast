package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.world.World;

public class MineshaftStart extends StructureStart {
   public MineshaftStart() {
   }

   public MineshaftStart(World world, Random random, int chunkX, int chunkZ) {
      super(chunkX, chunkZ);
      MineshaftPieces.Room var5 = new MineshaftPieces.Room(0, random, (chunkX << 4) + 2, (chunkZ << 4) + 2);
      this.pieces.add(var5);
      var5.addChildren(var5, this.pieces, random);
      this.calculateBoundingBox();
      this.moveBelowSeaLevel(world, random, 10);
   }
}
