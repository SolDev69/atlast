package net.minecraft.realms;

import java.util.List;
import net.minecraft.C_84obvpdwb;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class RealmsScreen {
   public static final int SKIN_HEAD_U = 8;
   public static final int SKIN_HEAD_V = 8;
   public static final int SKIN_HEAD_WIDTH = 8;
   public static final int SKIN_HEAD_HEIGHT = 8;
   public static final int SKIN_HAT_U = 40;
   public static final int SKIN_HAT_V = 8;
   public static final int SKIN_HAT_WIDTH = 8;
   public static final int SKIN_HAT_HEIGHT = 8;
   public static final int SKIN_TEX_WIDTH = 64;
   public static final int SKIN_TEX_HEIGHT = 64;
   protected MinecraftClient minecraft;
   public int width;
   public int height;
   private C_84obvpdwb proxy = new C_84obvpdwb(this);

   public C_84obvpdwb getProxy() {
      return this.proxy;
   }

   public void init() {
   }

   public void init(MinecraftClient c_13piauvdk, int i, int j) {
   }

   public void drawCenteredString(String string, int i, int j, int k) {
      this.proxy.m_22opaheap(string, i, j, k);
   }

   public void drawString(String string, int i, int j, int k) {
      this.proxy.m_22ygridsn(string, i, j, k);
   }

   public void blit(int i, int j, int k, int l, int m, int n) {
      this.proxy.drawTexture(i, j, k, l, m, n);
   }

   public static void blit(int i, int j, float f, float g, int k, int l, int m, int n, float h, float o) {
      GuiElement.drawTexture(i, j, f, g, k, l, m, n, h, o);
   }

   public static void blit(int i, int j, float f, float g, int k, int l, float h, float m) {
      GuiElement.drawTexture(i, j, f, g, k, l, h, m);
   }

   public void fillGradient(int i, int j, int k, int l, int m, int n) {
      this.proxy.fillGradient(i, j, k, l, m, n);
   }

   public void renderBackground() {
      this.proxy.renderBackground();
   }

   public boolean isPauseScreen() {
      return this.proxy.shouldPauseGame();
   }

   public void renderBackground(int i) {
      this.proxy.renderBackground(i);
   }

   public void render(int i, int j, float f) {
      for(int var4 = 0; var4 < this.proxy.m_76byhkwst().size(); ++var4) {
         ((RealmsButton)this.proxy.m_76byhkwst().get(var4)).render(i, j);
      }
   }

   public void renderTooltip(ItemStack c_72owraavl, int i, int j) {
      this.proxy.renderTooltip(c_72owraavl, i, j);
   }

   public void renderTooltip(String string, int i, int j) {
      this.proxy.renderTooltip(string, i, j);
   }

   public void renderTooltip(List list, int i, int j) {
      this.proxy.renderTooltip(list, i, j);
   }

   public static void bindFace(String string) {
      Identifier var1 = ClientPlayerEntity.getSkinTextureId(string);
      if (var1 == null) {
         var1 = ClientPlayerEntity.getSkinTextureId("default");
      }

      ClientPlayerEntity.registerSkinTexture(var1, string);
      MinecraftClient.getInstance().getTextureManager().bind(var1);
   }

   public static void bind(String string) {
      Identifier var1 = new Identifier(string);
      MinecraftClient.getInstance().getTextureManager().bind(var1);
   }

   public void tick() {
   }

   public int width() {
      return this.proxy.titleWidth;
   }

   public int height() {
      return this.proxy.height;
   }

   public int fontLineHeight() {
      return this.proxy.m_60sormira();
   }

   public int fontWidth(String string) {
      return this.proxy.m_48cwbrijg(string);
   }

   public void fontDrawShadow(String string, int i, int j, int k) {
      this.proxy.m_99zzwknxk(string, i, j, k);
   }

   public List fontSplit(String string, int i) {
      return this.proxy.m_48kxyfzhd(string, i);
   }

   public void buttonClicked(RealmsButton realmsButton) {
   }

   public static RealmsButton newButton(int i, int j, int k, String string) {
      return new RealmsButton(i, j, k, string);
   }

   public static RealmsButton newButton(int i, int j, int k, int l, int m, String string) {
      return new RealmsButton(i, j, k, l, m, string);
   }

   public void buttonsClear() {
      this.proxy.m_02oyttahp();
   }

   public void buttonsAdd(RealmsButton realmsButton) {
      this.proxy.m_66tptftwm(realmsButton);
   }

   public List buttons() {
      return this.proxy.m_76byhkwst();
   }

   public void buttonsRemove(RealmsButton realmsButton) {
      this.proxy.m_15tvfewmg(realmsButton);
   }

   public RealmsEditBox newEditBox(int i, int j, int k, int l) {
      return new RealmsEditBox(i, j, k, l);
   }

   public void mouseClicked(int i, int j, int k) {
   }

   public void mouseEvent() {
   }

   public void keyboardEvent() {
   }

   public void mouseReleased(int i, int j, int k) {
   }

   public void mouseDragged(int i, int j, int k, long l) {
   }

   public void keyPressed(char c, int i) {
   }

   public void confirmResult(boolean bl, int i) {
   }

   public static String getLocalizedString(String string) {
      return I18n.translate(string);
   }

   public static String getLocalizedString(String string, Object... objects) {
      return I18n.translate(string, objects);
   }

   public RealmsAnvilLevelStorageSource getLevelStorageSource() {
      return new RealmsAnvilLevelStorageSource(MinecraftClient.getInstance().getWorldStorageSource());
   }

   public void removed() {
   }
}
