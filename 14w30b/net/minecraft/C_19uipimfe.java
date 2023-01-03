package net.minecraft;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.model.PlayerModelPart;
import net.minecraft.client.resource.language.I18n;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class C_19uipimfe extends Screen {
   private final Screen f_05qjfrzpw;
   private String f_61simtgfo;

   public C_19uipimfe(Screen c_31rdgoemj) {
      this.f_05qjfrzpw = c_31rdgoemj;
   }

   @Override
   public void init() {
      int var1 = 0;
      this.f_61simtgfo = I18n.translate("options.skinCustomisation.title");

      for(PlayerModelPart var5 : PlayerModelPart.values()) {
         this.buttons
            .add(new C_19uipimfe.C_68yrxrfbl(var5.getIndex(), this.titleWidth / 2 - 155 + var1 % 2 * 160, this.height / 6 + 24 * (var1 >> 1), 150, 20, var5));
         ++var1;
      }

      if (var1 % 2 == 1) {
         ++var1;
      }

      this.buttons.add(new ButtonWidget(200, this.titleWidth / 2 - 100, this.height / 6 + 24 * (var1 >> 1), I18n.translate("gui.done")));
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         if (buttonWidget.id == 200) {
            this.client.options.save();
            this.client.openScreen(this.f_05qjfrzpw);
         } else if (buttonWidget instanceof C_19uipimfe.C_68yrxrfbl) {
            PlayerModelPart var2 = ((C_19uipimfe.C_68yrxrfbl)buttonWidget).f_26aflelda;
            this.client.options.togglePlayerModelPart(var2);
            buttonWidget.message = this.m_89rcyjxnz(var2);
         }
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.drawCenteredString(this.textRenderer, this.f_61simtgfo, this.titleWidth / 2, 20, 16777215);
      super.render(mouseX, mouseY, tickDelta);
   }

   private String m_89rcyjxnz(PlayerModelPart c_79gsdrgoz) {
      String var2;
      if (this.client.options.getPlayerModelParts().contains(c_79gsdrgoz)) {
         var2 = I18n.translate("options.on");
      } else {
         var2 = I18n.translate("options.off");
      }

      return c_79gsdrgoz.getName().buildFormattedString() + ": " + var2;
   }

   @Environment(EnvType.CLIENT)
   class C_68yrxrfbl extends ButtonWidget {
      private final PlayerModelPart f_26aflelda;

      private C_68yrxrfbl(int i, int j, int k, int l, int m, PlayerModelPart c_79gsdrgoz) {
         super(i, j, k, l, m, C_19uipimfe.this.m_89rcyjxnz(c_79gsdrgoz));
         this.f_26aflelda = c_79gsdrgoz;
      }
   }
}
