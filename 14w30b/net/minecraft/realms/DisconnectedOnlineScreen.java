package net.minecraft.realms;

import java.util.List;
import net.minecraft.text.Text;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class DisconnectedOnlineScreen extends RealmsScreen {
   private String title;
   private Text reason;
   private List lines;
   private final RealmsScreen parent;

   public DisconnectedOnlineScreen(RealmsScreen realmsScreen, String string, Text c_21uoltggz) {
      this.parent = realmsScreen;
      this.title = getLocalizedString(string);
      this.reason = c_21uoltggz;
   }

   @Override
   public void init() {
      this.buttonsClear();
      this.buttonsAdd(newButton(0, this.width() / 2 - 100, this.height() / 4 + 120 + 12, getLocalizedString("gui.back")));
      this.lines = this.fontSplit(this.reason.buildFormattedString(), this.width() - 50);
   }

   @Override
   public void keyPressed(char c, int i) {
      if (i == 1) {
         Realms.setScreen(this.parent);
      }
   }

   @Override
   public void buttonClicked(RealmsButton realmsButton) {
      if (realmsButton.m_19exxarmw() == 0) {
         Realms.setScreen(this.parent);
      }
   }

   @Override
   public void render(int i, int j, float f) {
      this.renderBackground();
      this.drawCenteredString(this.title, this.width() / 2, this.height() / 2 - 50, 11184810);
      int var4 = this.height() / 2 - 30;
      if (this.lines != null) {
         for(String var6 : this.lines) {
            this.drawCenteredString(var6, this.width() / 2, var4, 16777215);
            var4 += this.fontLineHeight();
         }
      }

      super.render(i, j, f);
   }
}
