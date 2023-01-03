package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.options.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LanguageButton;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.resource.Identifier;
import net.minecraft.server.world.DemoServerWorld;
import net.minecraft.text.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldData;
import net.minecraft.world.storage.WorldStorageSource;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

@Environment(EnvType.CLIENT)
public class TitleScreen extends Screen implements ConfirmationListener {
   private static final AtomicInteger mcoAvailabilityCheckerCount = new AtomicInteger(0);
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Random RANDOM = new Random();
   private float randomFloat;
   private String splashText;
   private ButtonWidget buttonResetDemo;
   private int time;
   private NativeImageBackedTexture defualtBackgroundImage;
   private boolean realmsEnabled = true;
   private final Object threadedLock = new Object();
   private String outdatedGpuWarning;
   private String warningInfo;
   private String warningInfoLink;
   private static final Identifier SPLASHES = new Identifier("texts/splashes.txt");
   private static final Identifier MINECRAFT_TITLE_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
   private static final Identifier[] PANORAMA_ARRAY = new Identifier[]{
      new Identifier("textures/gui/title/background/panorama_0.png"),
      new Identifier("textures/gui/title/background/panorama_1.png"),
      new Identifier("textures/gui/title/background/panorama_2.png"),
      new Identifier("textures/gui/title/background/panorama_3.png"),
      new Identifier("textures/gui/title/background/panorama_4.png"),
      new Identifier("textures/gui/title/background/panorama_5.png")
   };
   public static final String MORE_INFO = "Please click " + Formatting.UNDERLINE + "here" + Formatting.RESET + " for more information.";
   private int warningTextWidth;
   private int outdatedGpuTextWidth;
   private int x1;
   private int y1;
   private int x2;
   private int y2;
   private Identifier backgroundTexture;
   private ButtonWidget realmsButton;

   public TitleScreen() {
      this.warningInfo = MORE_INFO;
      this.splashText = "missingno";
      BufferedReader var1 = null;

      try {
         ArrayList var2 = Lists.newArrayList();
         var1 = new BufferedReader(new InputStreamReader(MinecraftClient.getInstance().getResourceManager().getResource(SPLASHES).asStream(), Charsets.UTF_8));

         String var3;
         while((var3 = var1.readLine()) != null) {
            var3 = var3.trim();
            if (!var3.isEmpty()) {
               var2.add(var3);
            }
         }

         if (!var2.isEmpty()) {
            do {
               this.splashText = (String)var2.get(RANDOM.nextInt(var2.size()));
            } while(this.splashText.hashCode() == 125780783);
         }
      } catch (IOException var12) {
      } finally {
         if (var1 != null) {
            try {
               var1.close();
            } catch (IOException var11) {
            }
         }
      }

      this.randomFloat = RANDOM.nextFloat();
      this.outdatedGpuWarning = "";
      if (!GLContext.getCapabilities().OpenGL20 && !GLX.isNextGen()) {
         this.outdatedGpuWarning = I18n.translate("title.oldgl1");
         this.warningInfo = I18n.translate("title.oldgl2");
         this.warningInfoLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
      }
   }

   @Override
   public void tick() {
      ++this.time;
   }

   @Override
   public boolean shouldPauseGame() {
      return false;
   }

   @Override
   protected void keyPressed(char chr, int key) {
   }

   @Override
   public void init() {
      this.defualtBackgroundImage = new NativeImageBackedTexture(256, 256);
      this.backgroundTexture = this.client.getTextureManager().register("background", this.defualtBackgroundImage);
      Calendar var1 = Calendar.getInstance();
      var1.setTime(new Date());
      if (var1.get(2) + 1 == 11 && var1.get(5) == 9) {
         this.splashText = "Happy birthday, ez!";
      } else if (var1.get(2) + 1 == 6 && var1.get(5) == 1) {
         this.splashText = "Happy birthday, Notch!";
      } else if (var1.get(2) + 1 == 12 && var1.get(5) == 24) {
         this.splashText = "Merry X-mas!";
      } else if (var1.get(2) + 1 == 1 && var1.get(5) == 1) {
         this.splashText = "Happy new year!";
      } else if (var1.get(2) + 1 == 10 && var1.get(5) == 31) {
         this.splashText = "OOoooOOOoooo! Spooky!";
      }

      boolean var2 = true;
      int var3 = this.height / 4 + 48;
      if (this.client.isDemo()) {
         this.initWidgetsDemo(var3, 24);
      } else {
         this.initWidgetsNormal(var3, 24);
      }

      this.buttons.add(new ButtonWidget(0, this.titleWidth / 2 - 100, var3 + 72 + 12, 98, 20, I18n.translate("menu.options")));
      this.buttons.add(new ButtonWidget(4, this.titleWidth / 2 + 2, var3 + 72 + 12, 98, 20, I18n.translate("menu.quit")));
      this.buttons.add(new LanguageButton(5, this.titleWidth / 2 - 124, var3 + 72 + 12));
      synchronized(this.threadedLock) {
         this.outdatedGpuTextWidth = this.textRenderer.getStringWidth(this.outdatedGpuWarning);
         this.warningTextWidth = this.textRenderer.getStringWidth(this.warningInfo);
         int var5 = Math.max(this.outdatedGpuTextWidth, this.warningTextWidth);
         this.x1 = (this.titleWidth - var5) / 2;
         this.y1 = ((ButtonWidget)this.buttons.get(0)).y - 24;
         this.x2 = this.x1 + var5;
         this.y2 = this.y1 + 24;
      }
   }

   private void initWidgetsNormal(int height, int offset) {
      this.buttons.add(new ButtonWidget(1, this.titleWidth / 2 - 100, height, I18n.translate("menu.singleplayer")));
      this.buttons.add(new ButtonWidget(2, this.titleWidth / 2 - 100, height + offset * 1, I18n.translate("menu.multiplayer")));
      this.buttons.add(this.realmsButton = new ButtonWidget(14, this.titleWidth / 2 - 100, height + offset * 2, I18n.translate("menu.online")));
   }

   private void initWidgetsDemo(int y, int spacingY) {
      this.buttons.add(new ButtonWidget(11, this.titleWidth / 2 - 100, y, I18n.translate("menu.playdemo")));
      this.buttons.add(this.buttonResetDemo = new ButtonWidget(12, this.titleWidth / 2 - 100, y + spacingY * 1, I18n.translate("menu.resetdemo")));
      WorldStorageSource var3 = this.client.getWorldStorageSource();
      WorldData var4 = var3.getData("Demo_World");
      if (var4 == null) {
         this.buttonResetDemo.active = false;
      }
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.id == 0) {
         this.client.openScreen(new OptionsScreen(this, this.client.options));
      }

      if (buttonWidget.id == 5) {
         this.client.openScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager()));
      }

      if (buttonWidget.id == 1) {
         this.client.openScreen(new SelectWorldScreen(this));
      }

      if (buttonWidget.id == 2) {
         this.client.openScreen(new MultiplayerScreen(this));
      }

      if (buttonWidget.id == 14 && this.realmsButton.visible) {
         this.m_67qdjqdtk();
      }

      if (buttonWidget.id == 4) {
         this.client.scheduleStop();
      }

      if (buttonWidget.id == 11) {
         this.client.startGame("Demo_World", "Demo_World", DemoServerWorld.SETTINGS);
      }

      if (buttonWidget.id == 12) {
         WorldStorageSource var2 = this.client.getWorldStorageSource();
         WorldData var3 = var2.getData("Demo_World");
         if (var3 != null) {
            ConfirmScreen var4 = SelectWorldScreen.getDeleteWarningPrompt(this, var3.getName(), 12);
            this.client.openScreen(var4);
         }
      }
   }

   private void m_67qdjqdtk() {
      RealmsBridge var1 = new RealmsBridge();
      var1.switchToRealms(this);
   }

   @Override
   public void confirmResult(boolean result, int id) {
      if (result && id == 12) {
         WorldStorageSource var6 = this.client.getWorldStorageSource();
         var6.clearRegionIo();
         var6.delete("Demo_World");
         this.client.openScreen(this);
      } else if (id == 13) {
         if (result) {
            try {
               Class var3 = Class.forName("java.awt.Desktop");
               Object var4 = var3.getMethod("getDesktop").invoke(null);
               var3.getMethod("browse", URI.class).invoke(var4, new URI(this.warningInfoLink));
            } catch (Throwable var5) {
               LOGGER.error("Couldn't open link", var5);
            }
         }

         this.client.openScreen(this);
      }
   }

   private void drawBackgroundBase(int mouseX, int mouseY, float tickdelta) {
      Tessellator var4 = Tessellator.getInstance();
      BufferBuilder var5 = var4.getBufferBuilder();
      GlStateManager.matrixMode(5889);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.disableAlphaTest();
      GlStateManager.disableCull();
      GlStateManager.depthMask(false);
      GlStateManager.blendFuncSeparate(770, 771, 1, 0);
      byte var6 = 8;

      for(int var7 = 0; var7 < var6 * var6; ++var7) {
         GlStateManager.pushMatrix();
         float var8 = ((float)(var7 % var6) / (float)var6 - 0.5F) / 64.0F;
         float var9 = ((float)(var7 / var6) / (float)var6 - 0.5F) / 64.0F;
         float var10 = 0.0F;
         GlStateManager.translatef(var8, var9, var10);
         GlStateManager.rotatef(MathHelper.sin(((float)this.time + tickdelta) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(-((float)this.time + tickdelta) * 0.1F, 0.0F, 1.0F, 0.0F);

         for(int var11 = 0; var11 < 6; ++var11) {
            GlStateManager.pushMatrix();
            if (var11 == 1) {
               GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (var11 == 2) {
               GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }

            if (var11 == 3) {
               GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (var11 == 4) {
               GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (var11 == 5) {
               GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            this.client.getTextureManager().bind(PANORAMA_ARRAY[var11]);
            var5.start();
            var5.color(16777215, 255 / (var7 + 1));
            float var12 = 0.0F;
            var5.vertex(-1.0, -1.0, 1.0, (double)(0.0F + var12), (double)(0.0F + var12));
            var5.vertex(1.0, -1.0, 1.0, (double)(1.0F - var12), (double)(0.0F + var12));
            var5.vertex(1.0, 1.0, 1.0, (double)(1.0F - var12), (double)(1.0F - var12));
            var5.vertex(-1.0, 1.0, 1.0, (double)(0.0F + var12), (double)(1.0F - var12));
            var4.end();
            GlStateManager.popMatrix();
         }

         GlStateManager.popMatrix();
         GlStateManager.colorMask(true, true, true, false);
      }

      var5.offset(0.0, 0.0, 0.0);
      GlStateManager.colorMask(true, true, true, true);
      GlStateManager.matrixMode(5889);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.disableDepth();
   }

   private void drawBackgroundImage(float tickDelta) {
      this.client.getTextureManager().bind(this.backgroundTexture);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, 256, 256);
      GlStateManager.disableBlend();
      GlStateManager.blendFuncSeparate(770, 771, 1, 0);
      GlStateManager.colorMask(true, true, true, false);
      Tessellator var2 = Tessellator.getInstance();
      BufferBuilder var3 = var2.getBufferBuilder();
      var3.start();
      GlStateManager.disableAlphaTest();
      byte var4 = 3;

      for(int var5 = 0; var5 < var4; ++var5) {
         var3.color(1.0F, 1.0F, 1.0F, 1.0F / (float)(var5 + 1));
         int var6 = this.titleWidth;
         int var7 = this.height;
         float var8 = (float)(var5 - var4 / 2) / 256.0F;
         var3.vertex((double)var6, (double)var7, (double)this.drawOffset, (double)(0.0F + var8), 1.0);
         var3.vertex((double)var6, 0.0, (double)this.drawOffset, (double)(1.0F + var8), 1.0);
         var3.vertex(0.0, 0.0, (double)this.drawOffset, (double)(1.0F + var8), 0.0);
         var3.vertex(0.0, (double)var7, (double)this.drawOffset, (double)(0.0F + var8), 0.0);
      }

      var2.end();
      GlStateManager.enableAlphaTest();
      GlStateManager.colorMask(true, true, true, true);
   }

   private void drawBackground(int mouseX, int mouseY, float tickDelta) {
      this.client.getRenderTarget().unbindWrite();
      GlStateManager.viewport(0, 0, 256, 256);
      this.drawBackgroundBase(mouseX, mouseY, tickDelta);
      this.drawBackgroundImage(tickDelta);
      this.drawBackgroundImage(tickDelta);
      this.drawBackgroundImage(tickDelta);
      this.drawBackgroundImage(tickDelta);
      this.drawBackgroundImage(tickDelta);
      this.drawBackgroundImage(tickDelta);
      this.drawBackgroundImage(tickDelta);
      this.client.getRenderTarget().bindWrite(true);
      GlStateManager.viewport(0, 0, this.client.width, this.client.height);
      Tessellator var4 = Tessellator.getInstance();
      BufferBuilder var5 = var4.getBufferBuilder();
      var5.start();
      float var6 = this.titleWidth > this.height ? 120.0F / (float)this.titleWidth : 120.0F / (float)this.height;
      float var7 = (float)this.height * var6 / 256.0F;
      float var8 = (float)this.titleWidth * var6 / 256.0F;
      var5.color(1.0F, 1.0F, 1.0F, 1.0F);
      int var9 = this.titleWidth;
      int var10 = this.height;
      var5.vertex(0.0, (double)var10, (double)this.drawOffset, (double)(0.5F - var7), (double)(0.5F + var8));
      var5.vertex((double)var9, (double)var10, (double)this.drawOffset, (double)(0.5F - var7), (double)(0.5F - var8));
      var5.vertex((double)var9, 0.0, (double)this.drawOffset, (double)(0.5F + var7), (double)(0.5F - var8));
      var5.vertex(0.0, 0.0, (double)this.drawOffset, (double)(0.5F + var7), (double)(0.5F + var8));
      var4.end();
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      GlStateManager.disableAlphaTest();
      this.drawBackground(mouseX, mouseY, tickDelta);
      GlStateManager.enableAlphaTest();
      Tessellator var4 = Tessellator.getInstance();
      BufferBuilder var5 = var4.getBufferBuilder();
      short var6 = 274;
      int var7 = this.titleWidth / 2 - var6 / 2;
      byte var8 = 30;
      this.fillGradient(0, 0, this.titleWidth, this.height, -2130706433, 16777215);
      this.fillGradient(0, 0, this.titleWidth, this.height, 0, Integer.MIN_VALUE);
      this.client.getTextureManager().bind(MINECRAFT_TITLE_TEXTURE);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      if ((double)this.randomFloat < 1.0E-4) {
         this.drawTexture(var7 + 0, var8 + 0, 0, 0, 99, 44);
         this.drawTexture(var7 + 99, var8 + 0, 129, 0, 27, 44);
         this.drawTexture(var7 + 99 + 26, var8 + 0, 126, 0, 3, 44);
         this.drawTexture(var7 + 99 + 26 + 3, var8 + 0, 99, 0, 26, 44);
         this.drawTexture(var7 + 155, var8 + 0, 0, 45, 155, 44);
      } else {
         this.drawTexture(var7 + 0, var8 + 0, 0, 0, 155, 44);
         this.drawTexture(var7 + 155, var8 + 0, 0, 45, 155, 44);
      }

      var5.color(-1);
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)(this.titleWidth / 2 + 90), 70.0F, 0.0F);
      GlStateManager.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
      float var9 = 1.8F - MathHelper.abs(MathHelper.sin((float)(MinecraftClient.getTime() % 1000L) / 1000.0F * (float) Math.PI * 2.0F) * 0.1F);
      var9 = var9 * 100.0F / (float)(this.textRenderer.getStringWidth(this.splashText) + 32);
      GlStateManager.scalef(var9, var9, var9);
      this.drawCenteredString(this.textRenderer, this.splashText, 0, -8, -256);
      GlStateManager.popMatrix();
      String var10 = "Minecraft 14w30b";
      if (this.client.isDemo()) {
         var10 = var10 + " Demo";
      }

      this.drawString(this.textRenderer, var10, 2, this.height - 10, -1);
      String var11 = "Copyright Mojang AB. Do not distribute!";
      this.drawString(this.textRenderer, var11, this.titleWidth - this.textRenderer.getStringWidth(var11) - 2, this.height - 10, -1);
      if (this.outdatedGpuWarning != null && this.outdatedGpuWarning.length() > 0) {
         fill(this.x1 - 2, this.y1 - 2, this.x2 + 2, this.y2 - 1, 1428160512);
         this.drawString(this.textRenderer, this.outdatedGpuWarning, this.x1, this.y1, -1);
         this.drawString(this.textRenderer, this.warningInfo, (this.titleWidth - this.warningTextWidth) / 2, ((ButtonWidget)this.buttons.get(0)).y - 12, -1);
      }

      super.render(mouseX, mouseY, tickDelta);
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      synchronized(this.threadedLock) {
         if (this.outdatedGpuWarning.length() > 0 && mouseX >= this.x1 && mouseX <= this.x2 && mouseY >= this.y1 && mouseY <= this.y2) {
            ConfirmChatLinkScreen var5 = new ConfirmChatLinkScreen(this, this.warningInfoLink, 13, true);
            var5.noWarning();
            this.client.openScreen(var5);
         }
      }
   }
}
