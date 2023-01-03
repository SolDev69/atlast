package net.minecraft.client.gui.screen;

import net.minecraft.util.ProgressListener;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ProgressScreen extends Screen implements ProgressListener {
   private String title = "";
   private String task = "";
   private int progress;
   private boolean done;

   @Override
   public void updateProgress(String title) {
      this.updateTitle(title);
   }

   @Override
   public void updateTitle(String title) {
      this.title = title;
      this.setTask("Working...");
   }

   @Override
   public void setTask(String task) {
      this.task = task;
      this.progressStagePercentage(0);
   }

   @Override
   public void progressStagePercentage(int percentage) {
      this.progress = percentage;
   }

   @Override
   public void setDone() {
      this.done = true;
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      if (this.done) {
         this.client.openScreen(null);
      } else {
         this.renderBackground();
         this.drawCenteredString(this.textRenderer, this.title, this.titleWidth / 2, 70, 16777215);
         this.drawCenteredString(this.textRenderer, this.task + " " + this.progress + "%", this.titleWidth / 2, 90, 16777215);
         super.render(mouseX, mouseY, tickDelta);
      }
   }
}
