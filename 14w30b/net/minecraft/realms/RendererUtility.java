package net.minecraft.realms;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class RendererUtility {
   public static void render(RealmsButton realmsButton, int i, int j) {
      realmsButton.render(i, j);
   }
}
