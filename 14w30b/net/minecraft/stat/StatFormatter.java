package net.minecraft.stat;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public interface StatFormatter {
   @Environment(EnvType.CLIENT)
   String format(int value);
}
