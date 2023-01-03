package net.minecraft.realms;

import net.minecraft.C_59atfncjq;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class RealmsButton {
   private C_59atfncjq proxy;

   public RealmsButton(int i, int j, int k, String string) {
      this.proxy = new C_59atfncjq(this, i, j, k, string);
   }

   public RealmsButton(int i, int j, int k, int l, int m, String string) {
      this.proxy = new C_59atfncjq(this, i, j, k, string, l, m);
   }

   public ButtonWidget getProxy() {
      return this.proxy;
   }

   public int m_19exxarmw() {
      return this.proxy.m_40wdwlcfk();
   }

   public boolean active() {
      return this.proxy.m_50iueafxx();
   }

   public void active(boolean bl) {
      this.proxy.m_60vhogjah(bl);
   }

   public void msg(String string) {
      this.proxy.m_50vzogisx(string);
   }

   public int getWidth() {
      return this.proxy.getWidth();
   }

   public int getHeight() {
      return this.proxy.m_01oomqfto();
   }

   public int m_33dwvwsij() {
      return this.proxy.m_36zfkgtwn();
   }

   public void render(int i, int j) {
      this.proxy.render(MinecraftClient.getInstance(), i, j);
   }

   public void clicked(int i, int j) {
   }

   public void released(int i, int j) {
   }

   public void blit(int i, int j, int k, int l, int m, int n) {
      this.proxy.drawTexture(i, j, k, l, m, n);
   }

   public void renderBg(int i, int j) {
   }

   public int getYImage(boolean bl) {
      return this.proxy.m_78chdznve(bl);
   }
}
