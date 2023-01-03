package net.minecraft.client.render;

import net.minecraft.util.math.Box;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Culler {
   boolean isVisible(Box box);

   void set(double x, double y, double z);
}
