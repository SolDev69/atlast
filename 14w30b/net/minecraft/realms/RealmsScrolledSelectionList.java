package net.minecraft.realms;

import net.minecraft.C_43mafeaza;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class RealmsScrolledSelectionList {
   private final C_43mafeaza proxy;

   public RealmsScrolledSelectionList(int i, int j, int k, int l, int m) {
      this.proxy = new C_43mafeaza(this, i, j, k, l, m);
   }

   public void render(int i, int j, float f) {
      this.proxy.render(i, j, f);
   }

   public int width() {
      return this.proxy.m_86vsryogl();
   }

   public int m_52ddngidq() {
      return this.proxy.m_13icozequ();
   }

   public int m_88fgjhyce() {
      return this.proxy.m_58mgdvadv();
   }

   protected void renderItem(int i, int j, int k, int l, Tezzelator tezzelator, int m, int n) {
   }

   public void renderItem(int i, int j, int k, int l, int m, int n) {
      this.renderItem(i, j, k, l, Tezzelator.instance, m, n);
   }

   public int getItemCount() {
      return 0;
   }

   public void selectItem(int i, boolean bl, int j, int k) {
   }

   public boolean isSelectedItem(int i) {
      return false;
   }

   public void renderBackground() {
   }

   public int getMaxPosition() {
      return 0;
   }

   public int getScrollbarPosition() {
      return this.proxy.m_86vsryogl() / 2 + 124;
   }
}
