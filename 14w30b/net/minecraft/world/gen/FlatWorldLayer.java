package net.minecraft.world.gen;

import net.minecraft.block.Block;
import net.minecraft.resource.Identifier;

public class FlatWorldLayer {
   private final int biomeId;
   private Block block;
   private int size = 1;
   private int metadata;
   private int y;

   public FlatWorldLayer(int size, Block block) {
      this(3, size, block);
   }

   public FlatWorldLayer(int biomeId, int size, Block block) {
      this.biomeId = biomeId;
      this.size = size;
      this.block = block;
   }

   public FlatWorldLayer(int biomeId, int size, Block block, int metadata) {
      this(biomeId, size, block);
      this.metadata = metadata;
   }

   public int getSize() {
      return this.size;
   }

   public Block getBlock() {
      return this.block;
   }

   public int getBlockMetadata() {
      return this.metadata;
   }

   public int getY() {
      return this.y;
   }

   public void setY(int y) {
      this.y = y;
   }

   @Override
   public String toString() {
      String var1;
      if (this.biomeId >= 3) {
         Identifier var2 = (Identifier)Block.REGISTRY.getKey(this.block);
         var1 = var2 == null ? "null" : var2.toString();
         if (this.size > 1) {
            var1 = this.size + "*" + var1;
         }
      } else {
         var1 = Integer.toString(Block.getRawId(this.block));
         if (this.size > 1) {
            var1 = this.size + "x" + var1;
         }
      }

      if (this.metadata > 0) {
         var1 = var1 + ":" + this.metadata;
      }

      return var1;
   }
}
