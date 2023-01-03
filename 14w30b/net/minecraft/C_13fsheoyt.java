package net.minecraft;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class C_13fsheoyt extends ButtonWidget {
   private boolean f_97jljkhhb;
   private String f_63zcvvdlz;
   private final C_37rnsjynt.C_59lpdyoky f_65fxtqnsd;

   public C_13fsheoyt(C_37rnsjynt.C_59lpdyoky c_59lpdyoky, int i, int j, int k, String string, boolean bl) {
      super(i, j, k, 150, 20, "");
      this.f_63zcvvdlz = string;
      this.f_97jljkhhb = bl;
      this.message = this.m_09tmgsqhi();
      this.f_65fxtqnsd = c_59lpdyoky;
   }

   private String m_09tmgsqhi() {
      return I18n.translate(this.f_63zcvvdlz) + ": " + (this.f_97jljkhhb ? I18n.translate("gui.yes") : I18n.translate("gui.no"));
   }

   public void m_05dnxwzsu(boolean bl) {
      this.f_97jljkhhb = bl;
      this.message = this.m_09tmgsqhi();
      this.f_65fxtqnsd.m_10bjktzqq(this.id, bl);
   }

   @Override
   public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
      if (super.isMouseOver(client, mouseX, mouseY)) {
         this.f_97jljkhhb = !this.f_97jljkhhb;
         this.message = this.m_09tmgsqhi();
         this.f_65fxtqnsd.m_10bjktzqq(this.id, this.f_97jljkhhb);
         return true;
      } else {
         return false;
      }
   }
}
