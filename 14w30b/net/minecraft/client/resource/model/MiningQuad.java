package net.minecraft.client.resource.model;

import java.util.Arrays;
import net.minecraft.client.render.model.block.FaceBakery;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class MiningQuad extends BakedQuad {
   private final TextureAtlasSprite miningSprite;

   public MiningQuad(BakedQuad delegate, TextureAtlasSprite miningSprite) {
      super(Arrays.copyOf(delegate.getVertices(), delegate.getVertices().length), delegate.tintIndex, FaceBakery.getFacing(delegate.getVertices()));
      this.miningSprite = miningSprite;
      this.calculateTextureCoords();
   }

   private void calculateTextureCoords() {
      for(int var1 = 0; var1 < 4; ++var1) {
         this.calculateTextureCoords(var1);
      }
   }

   private void calculateTextureCoords(int vertex) {
      int var2 = 7 * vertex;
      float var3 = Float.intBitsToFloat(this.vertices[var2]);
      float var4 = Float.intBitsToFloat(this.vertices[var2 + 1]);
      float var5 = Float.intBitsToFloat(this.vertices[var2 + 2]);
      float var6 = 0.0F;
      float var7 = 0.0F;
      switch(this.face) {
         case DOWN:
            var6 = var3 * 16.0F;
            var7 = (1.0F - var5) * 16.0F;
            break;
         case UP:
            var6 = var3 * 16.0F;
            var7 = var5 * 16.0F;
            break;
         case NORTH:
            var6 = (1.0F - var3) * 16.0F;
            var7 = (1.0F - var4) * 16.0F;
            break;
         case SOUTH:
            var6 = var3 * 16.0F;
            var7 = (1.0F - var4) * 16.0F;
            break;
         case WEST:
            var6 = var5 * 16.0F;
            var7 = (1.0F - var4) * 16.0F;
            break;
         case EAST:
            var6 = (1.0F - var5) * 16.0F;
            var7 = (1.0F - var4) * 16.0F;
      }

      this.vertices[var2 + 4] = Float.floatToRawIntBits(this.miningSprite.getU((double)var6));
      this.vertices[var2 + 4 + 1] = Float.floatToRawIntBits(this.miningSprite.getV((double)var7));
   }
}
