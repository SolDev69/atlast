package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Window;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.resource.Identifier;
import net.minecraft.stat.achievement.AchievementStat;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ToastGui extends GuiElement {
   private static final Identifier ACHIEVEMENT_BACKGROUND = new Identifier("textures/gui/achievement/achievement_background.png");
   private MinecraftClient client;
   private int width;
   private int height;
   private String title;
   private String description;
   private AchievementStat achievement;
   private long startTime;
   private ItemRenderer itemRenderer;
   private boolean tutorial;

   public ToastGui(MinecraftClient client) {
      this.client = client;
      this.itemRenderer = client.getItemRenderer();
   }

   public void set(AchievementStat achieved) {
      this.title = I18n.translate("achievement.get");
      this.description = achieved.getDecoratedName().buildString();
      this.startTime = MinecraftClient.getTime();
      this.achievement = achieved;
      this.tutorial = false;
   }

   public void setTutorial(AchievementStat achieved) {
      this.title = achieved.getDecoratedName().buildString();
      this.description = achieved.getDescription();
      this.startTime = MinecraftClient.getTime() + 2500L;
      this.achievement = achieved;
      this.tutorial = true;
   }

   private void render() {
      GlStateManager.viewport(0, 0, this.client.width, this.client.height);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      this.width = this.client.width;
      this.height = this.client.height;
      Window var1 = new Window(this.client, this.client.width, this.client.height);
      this.width = var1.getWidth();
      this.height = var1.getHeight();
      GlStateManager.clear(256);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.ortho(0.0, (double)this.width, (double)this.height, 0.0, 1000.0, 3000.0);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
   }

   public void tick() {
      if (this.achievement != null && this.startTime != 0L && MinecraftClient.getInstance().player != null) {
         double var1 = (double)(MinecraftClient.getTime() - this.startTime) / 3000.0;
         if (!this.tutorial) {
            if (var1 < 0.0 || var1 > 1.0) {
               this.startTime = 0L;
               return;
            }
         } else if (var1 > 0.5) {
            var1 = 0.5;
         }

         this.render();
         GlStateManager.enableDepth();
         GlStateManager.depthMask(false);
         double var3 = var1 * 2.0;
         if (var3 > 1.0) {
            var3 = 2.0 - var3;
         }

         var3 *= 4.0;
         var3 = 1.0 - var3;
         if (var3 < 0.0) {
            var3 = 0.0;
         }

         var3 *= var3;
         var3 *= var3;
         int var5 = this.width - 160;
         int var6 = 0 - (int)(var3 * 36.0);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableTexture();
         this.client.getTextureManager().bind(ACHIEVEMENT_BACKGROUND);
         GlStateManager.disableLighting();
         this.drawTexture(var5, var6, 96, 202, 160, 32);
         if (this.tutorial) {
            this.client.textRenderer.drawTrimmed(this.description, var5 + 30, var6 + 7, 120, -1);
         } else {
            this.client.textRenderer.drawWithoutShadow(this.title, var5 + 30, var6 + 7, -256);
            this.client.textRenderer.drawWithoutShadow(this.description, var5 + 30, var6 + 18, -1);
         }

         Lighting.turnOnGui();
         GlStateManager.disableLighting();
         GlStateManager.enableRescaleNormal();
         GlStateManager.enableColorMaterial();
         GlStateManager.enableLighting();
         this.itemRenderer.renderGuiItem(this.achievement.icon, var5 + 8, var6 + 8);
         GlStateManager.disableLighting();
         GlStateManager.depthMask(true);
         GlStateManager.disableDepth();
      }
   }

   public void clear() {
      this.achievement = null;
      this.startTime = 0L;
   }
}
