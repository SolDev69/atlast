package net.minecraft.util;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public interface ProgressListener {
   void updateProgress(String title);

   @Environment(EnvType.CLIENT)
   void updateTitle(String title);

   void setTask(String task);

   void progressStagePercentage(int percentage);

   @Environment(EnvType.CLIENT)
   void setDone();
}
