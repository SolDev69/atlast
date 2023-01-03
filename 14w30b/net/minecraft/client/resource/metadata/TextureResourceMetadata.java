package net.minecraft.client.resource.metadata;

import java.util.Collections;
import java.util.List;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextureResourceMetadata implements ResourceMetadataSection {
   private final boolean blur;
   private final boolean clamp;
   private final List mipmaps;

   public TextureResourceMetadata(boolean blur, boolean clamp, List mipmaps) {
      this.blur = blur;
      this.clamp = clamp;
      this.mipmaps = mipmaps;
   }

   public boolean hasBlur() {
      return this.blur;
   }

   public boolean isClamped() {
      return this.clamp;
   }

   public List getMipmaps() {
      return Collections.unmodifiableList(this.mipmaps);
   }
}
