package net.minecraft.client.render.model;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class TexturePos {
   public final int u;
   public final int v;

   public TexturePos(int u, int v) {
      this.u = u;
      this.v = v;
   }
}
