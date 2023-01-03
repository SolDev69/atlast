package net.minecraft.client.gui.screen;

import java.util.List;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class DisconnectedScreen extends Screen {
   private String name;
   private Text reason;
   private List textLines;
   private final Screen parent;

   public DisconnectedScreen(Screen parent, String name, Text reason) {
      this.parent = parent;
      this.name = I18n.translate(name);
      this.reason = reason;
   }

   @Override
   protected void keyPressed(char chr, int key) {
   }

   @Override
   public void init() {
      this.buttons.clear();
      this.buttons.add(new ButtonWidget(0, this.titleWidth / 2 - 100, this.height / 4 + 120 + 12, I18n.translate("gui.toMenu")));
      this.textLines = this.textRenderer.wrapLines(this.reason.buildFormattedString(), this.titleWidth - 50);
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.id == 0) {
         this.client.openScreen(this.parent);
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.drawCenteredString(this.textRenderer, this.name, this.titleWidth / 2, this.height / 2 - 50, 11184810);
      int var4 = this.height / 2 - 30;
      if (this.textLines != null) {
         for(String var6 : this.textLines) {
            this.drawCenteredString(this.textRenderer, var6, this.titleWidth / 2, var4, 16777215);
            var4 += this.textRenderer.fontHeight;
         }
      }

      super.render(mouseX, mouseY, tickDelta);
   }
}
