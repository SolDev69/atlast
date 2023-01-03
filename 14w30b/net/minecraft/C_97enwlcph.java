package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class C_97enwlcph extends ButtonWidget {
   private float f_63itnapys = 1.0F;
   public boolean f_24rirepmk;
   private String f_03swsdgdx;
   private final float f_10rcmenkn;
   private final float f_53kyfgmhd;
   private final C_37rnsjynt.C_59lpdyoky f_48kudxrbm;
   private C_97enwlcph.C_56tbhdubs f_17wzeqqcz;

   public C_97enwlcph(C_37rnsjynt.C_59lpdyoky c_59lpdyoky, int i, int j, int k, String string, float f, float g, float h, C_97enwlcph.C_56tbhdubs c_56tbhdubs) {
      super(i, j, k, 150, 20, "");
      this.f_03swsdgdx = string;
      this.f_10rcmenkn = f;
      this.f_53kyfgmhd = g;
      this.f_63itnapys = (h - f) / (g - f);
      this.f_17wzeqqcz = c_56tbhdubs;
      this.f_48kudxrbm = c_59lpdyoky;
      this.message = this.m_32hppektm();
   }

   public float m_24bzxqlsa() {
      return this.f_10rcmenkn + (this.f_53kyfgmhd - this.f_10rcmenkn) * this.f_63itnapys;
   }

   public void m_18ssfzdfp(float f, boolean bl) {
      this.f_63itnapys = (f - this.f_10rcmenkn) / (this.f_53kyfgmhd - this.f_10rcmenkn);
      this.message = this.m_32hppektm();
      if (bl) {
         this.f_48kudxrbm.m_03bvoeuzb(this.id, this.m_24bzxqlsa());
      }
   }

   public float m_64efbxezm() {
      return this.f_63itnapys;
   }

   private String m_32hppektm() {
      return this.f_17wzeqqcz == null
         ? I18n.translate(this.f_03swsdgdx) + ": " + this.m_24bzxqlsa()
         : this.f_17wzeqqcz.m_77mlhcero(this.id, I18n.translate(this.f_03swsdgdx), this.m_24bzxqlsa());
   }

   @Override
   protected int getYImage(boolean isHovered) {
      return 0;
   }

   @Override
   protected void renderBg(MinecraftClient client, int mouseX, int mouseY) {
      if (this.visible) {
         if (this.f_24rirepmk) {
            this.f_63itnapys = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
            if (this.f_63itnapys < 0.0F) {
               this.f_63itnapys = 0.0F;
            }

            if (this.f_63itnapys > 1.0F) {
               this.f_63itnapys = 1.0F;
            }

            this.message = this.m_32hppektm();
            this.f_48kudxrbm.m_03bvoeuzb(this.id, this.m_24bzxqlsa());
         }

         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.drawTexture(this.x + (int)(this.f_63itnapys * (float)(this.width - 8)), this.y, 0, 66, 4, 20);
         this.drawTexture(this.x + (int)(this.f_63itnapys * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
      }
   }

   public void m_00hqugfaj(float f) {
      this.f_63itnapys = f;
      this.message = this.m_32hppektm();
      this.f_48kudxrbm.m_03bvoeuzb(this.id, this.m_24bzxqlsa());
   }

   @Override
   public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
      if (super.isMouseOver(client, mouseX, mouseY)) {
         this.f_63itnapys = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
         if (this.f_63itnapys < 0.0F) {
            this.f_63itnapys = 0.0F;
         }

         if (this.f_63itnapys > 1.0F) {
            this.f_63itnapys = 1.0F;
         }

         this.message = this.m_32hppektm();
         this.f_48kudxrbm.m_03bvoeuzb(this.id, this.m_24bzxqlsa());
         this.f_24rirepmk = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY) {
      this.f_24rirepmk = false;
   }

   @Environment(EnvType.CLIENT)
   public interface C_56tbhdubs {
      String m_77mlhcero(int i, String string, float f);
   }
}
