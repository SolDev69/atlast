package net.minecraft.client.gui.screen.menu;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface StatsListener {
   String[] PROGRESS_BAR_STAGES = new String[]{"oooooo", "Oooooo", "oOoooo", "ooOooo", "oooOoo", "ooooOo", "oooooO"};

   void onStatsReady();
}
