package net.minecraft.client.render;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextureStitchException extends RuntimeException {
   private final TextureStitcher.Holder stitchHolder;

   public TextureStitchException(TextureStitcher.Holder stitchHolder, String reason) {
      super(reason);
      this.stitchHolder = stitchHolder;
   }
}
