package net.minecraft.entity.living.mob.hostile.boss;

import net.minecraft.text.Text;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Boss {
   float getMaxHealth();

   float getHealth();

   Text getDisplayName();
}
