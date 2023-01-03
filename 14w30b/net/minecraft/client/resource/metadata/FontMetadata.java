package net.minecraft.client.resource.metadata;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class FontMetadata implements ResourceMetadataSection {
   private final float[] widths;
   private final float[] lefts;
   private final float[] spacings;

   public FontMetadata(float[] widths, float[] lefts, float[] spacings) {
      this.widths = widths;
      this.lefts = lefts;
      this.spacings = spacings;
   }
}
