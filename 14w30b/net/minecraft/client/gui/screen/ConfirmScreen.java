package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ConfirmScreen extends Screen {
   protected ConfirmationListener parent;
   protected String title;
   private String description;
   private final List f_33ladhitg = Lists.newArrayList();
   protected String confirmText;
   protected String abortText;
   protected int id;
   private int buttonEnableTimer;

   public ConfirmScreen(ConfirmationListener parent, String title, String description, int id) {
      this.parent = parent;
      this.title = title;
      this.description = description;
      this.id = id;
      this.confirmText = I18n.translate("gui.yes");
      this.abortText = I18n.translate("gui.no");
   }

   public ConfirmScreen(ConfirmationListener parent, String title, String description, String confirmText, String abortText, int id) {
      this.parent = parent;
      this.title = title;
      this.description = description;
      this.confirmText = confirmText;
      this.abortText = abortText;
      this.id = id;
   }

   @Override
   public void init() {
      this.buttons.add(new OptionButtonWidget(0, this.titleWidth / 2 - 155, this.height / 6 + 96, this.confirmText));
      this.buttons.add(new OptionButtonWidget(1, this.titleWidth / 2 - 155 + 160, this.height / 6 + 96, this.abortText));
      this.f_33ladhitg.clear();
      this.f_33ladhitg.addAll(this.textRenderer.wrapLines(this.description, this.titleWidth - 50));
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      this.parent.confirmResult(buttonWidget.id == 0, this.id);
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.drawCenteredString(this.textRenderer, this.title, this.titleWidth / 2, 70, 16777215);
      int var4 = 90;

      for(String var6 : this.f_33ladhitg) {
         this.drawCenteredString(this.textRenderer, var6, this.titleWidth / 2, var4, 16777215);
         var4 += this.textRenderer.fontHeight;
      }

      super.render(mouseX, mouseY, tickDelta);
   }

   public void disableButtons(int duration) {
      this.buttonEnableTimer = duration;

      for(ButtonWidget var3 : this.buttons) {
         var3.active = false;
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (--this.buttonEnableTimer == 0) {
         for(ButtonWidget var2 : this.buttons) {
            var2.active = true;
         }
      }
   }
}
