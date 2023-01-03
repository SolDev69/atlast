package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class FatalErrorScreen extends Screen {
   private String title;
   private String description;

   public FatalErrorScreen(String title, String description) {
      this.title = title;
      this.description = description;
   }

   @Override
   public void init() {
      super.init();
      this.buttons.add(new ButtonWidget(0, this.titleWidth / 2 - 100, 140, I18n.translate("gui.cancel")));
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.fillGradient(0, 0, this.titleWidth, this.height, -12574688, -11530224);
      this.drawCenteredString(this.textRenderer, this.title, this.titleWidth / 2, 90, 16777215);
      this.drawCenteredString(this.textRenderer, this.description, this.titleWidth / 2, 110, 16777215);
      super.render(mouseX, mouseY, tickDelta);
   }

   @Override
   protected void keyPressed(char chr, int key) {
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      this.client.openScreen(null);
   }
}
