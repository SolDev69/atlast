package net.minecraft;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.realms.RealmsButton;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class C_59atfncjq extends ButtonWidget {
   private RealmsButton f_82wthkwvk;

   public C_59atfncjq(RealmsButton realmsButton, int i, int j, int k, String string) {
      super(i, j, k, string);
      this.f_82wthkwvk = realmsButton;
   }

   public C_59atfncjq(RealmsButton realmsButton, int i, int j, int k, String string, int l, int m) {
      super(i, j, k, l, m, string);
      this.f_82wthkwvk = realmsButton;
   }

   public int m_40wdwlcfk() {
      return super.id;
   }

   public boolean m_50iueafxx() {
      return super.active;
   }

   public void m_60vhogjah(boolean bl) {
      super.active = bl;
   }

   public void m_50vzogisx(String string) {
      super.message = string;
   }

   @Override
   public int getWidth() {
      return super.getWidth();
   }

   public int m_36zfkgtwn() {
      return super.y;
   }

   @Override
   public boolean isMouseOver(MinecraftClient client, int mouseX, int mouseY) {
      if (super.isMouseOver(client, mouseX, mouseY)) {
         this.f_82wthkwvk.clicked(mouseX, mouseY);
      }

      return super.isMouseOver(client, mouseX, mouseY);
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY) {
      this.f_82wthkwvk.released(mouseX, mouseY);
   }

   @Override
   public void renderBg(MinecraftClient client, int mouseX, int mouseY) {
      this.f_82wthkwvk.renderBg(mouseX, mouseY);
   }

   public RealmsButton m_97qvqxoua() {
      return this.f_82wthkwvk;
   }

   @Override
   public int getYImage(boolean isHovered) {
      return this.f_82wthkwvk.getYImage(isHovered);
   }

   public int m_78chdznve(boolean bl) {
      return super.getYImage(bl);
   }

   public int m_01oomqfto() {
      return this.height;
   }
}
