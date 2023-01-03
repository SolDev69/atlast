package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class OutOfMemoryScreen extends Screen {
   @Override
   public void init() {
      this.buttons.clear();
      this.buttons.add(new OptionButtonWidget(0, this.titleWidth / 2 - 155, this.height / 4 + 120 + 12, I18n.translate("gui.toMenu")));
      this.buttons.add(new OptionButtonWidget(1, this.titleWidth / 2 - 155 + 160, this.height / 4 + 120 + 12, I18n.translate("menu.quit")));
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.id == 0) {
         this.client.openScreen(new TitleScreen());
      } else if (buttonWidget.id == 1) {
         this.client.scheduleStop();
      }
   }

   @Override
   protected void keyPressed(char chr, int key) {
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.drawCenteredString(this.textRenderer, "Out of memory!", this.titleWidth / 2, this.height / 4 - 60 + 20, 16777215);
      this.drawString(this.textRenderer, "Minecraft has run out of memory.", this.titleWidth / 2 - 140, this.height / 4 - 60 + 60 + 0, 10526880);
      this.drawString(
         this.textRenderer, "This could be caused by a bug in the game or by the", this.titleWidth / 2 - 140, this.height / 4 - 60 + 60 + 18, 10526880
      );
      this.drawString(this.textRenderer, "Java Virtual Machine not being allocated enough", this.titleWidth / 2 - 140, this.height / 4 - 60 + 60 + 27, 10526880);
      this.drawString(this.textRenderer, "memory.", this.titleWidth / 2 - 140, this.height / 4 - 60 + 60 + 36, 10526880);
      this.drawString(
         this.textRenderer, "To prevent level corruption, the current game has quit.", this.titleWidth / 2 - 140, this.height / 4 - 60 + 60 + 54, 10526880
      );
      this.drawString(
         this.textRenderer, "We've tried to free up enough memory to let you go back to", this.titleWidth / 2 - 140, this.height / 4 - 60 + 60 + 63, 10526880
      );
      this.drawString(
         this.textRenderer,
         "the main menu and back to playing, but this may not have worked.",
         this.titleWidth / 2 - 140,
         this.height / 4 - 60 + 60 + 72,
         10526880
      );
      this.drawString(
         this.textRenderer, "Please restart the game if you see this message again.", this.titleWidth / 2 - 140, this.height / 4 - 60 + 60 + 81, 10526880
      );
      super.render(mouseX, mouseY, tickDelta);
   }
}
