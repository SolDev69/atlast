package net.minecraft.stat.achievement;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface AchievementStatFormatter {
   String format(String value);
}
