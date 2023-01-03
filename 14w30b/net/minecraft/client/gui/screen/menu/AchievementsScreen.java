package net.minecraft.client.gui.screen.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.resource.Identifier;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.achievement.AchievementStat;
import net.minecraft.stat.achievement.Achievements;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.input.Mouse;

@Environment(EnvType.CLIENT)
public class AchievementsScreen extends Screen implements StatsListener {
   private static final int MIN_COLUMN = Achievements.minColumn * 24 - 112;
   private static final int MIN_ROW = Achievements.minRow * 24 - 112;
   private static final int MAX_COLUMN = Achievements.maxColumn * 24 - 77;
   private static final int MAX_ROW = Achievements.maxRow * 24 - 77;
   private static final Identifier ACHIEVEMENT_BACKGROUND = new Identifier("textures/gui/achievement/achievement_background.png");
   protected Screen parent;
   protected int iconWidth = 256;
   protected int iconHeight = 202;
   protected int prevMouseX;
   protected int prevMouseY;
   protected float scale = 1.0F;
   protected double mouseX;
   protected double mouseY;
   protected double scaledMouseDx;
   protected double scaledMouseDy;
   protected double scrollX;
   protected double scrollY;
   private int scroll;
   private StatHandler statHandler;
   private boolean downloading = true;

   public AchievementsScreen(Screen parent, StatHandler statHandler) {
      this.parent = parent;
      this.statHandler = statHandler;
      short var3 = 141;
      short var4 = 141;
      this.mouseX = this.scaledMouseDx = this.scrollX = (double)(Achievements.OPEN_INVENTORY.column * 24 - var3 / 2 - 12);
      this.mouseY = this.scaledMouseDy = this.scrollY = (double)(Achievements.OPEN_INVENTORY.row * 24 - var4 / 2);
   }

   @Override
   public void init() {
      this.client.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Status.REQUEST_STATS));
      this.buttons.clear();
      this.buttons.add(new OptionButtonWidget(1, this.titleWidth / 2 + 24, this.height / 2 + 74, 80, 20, I18n.translate("gui.done")));
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (!this.downloading) {
         if (buttonWidget.id == 1) {
            this.client.openScreen(this.parent);
         }
      }
   }

   @Override
   protected void keyPressed(char chr, int key) {
      if (key == this.client.options.inventoryKey.getKeyCode()) {
         this.client.openScreen(null);
         this.client.closeScreen();
      } else {
         super.keyPressed(chr, key);
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      if (this.downloading) {
         this.renderBackground();
         this.drawCenteredString(this.textRenderer, I18n.translate("multiplayer.downloadingStats"), this.titleWidth / 2, this.height / 2, 16777215);
         this.drawCenteredString(
            this.textRenderer,
            PROGRESS_BAR_STAGES[(int)(MinecraftClient.getTime() / 150L % (long)PROGRESS_BAR_STAGES.length)],
            this.titleWidth / 2,
            this.height / 2 + this.textRenderer.fontHeight * 2,
            16777215
         );
      } else {
         if (Mouse.isButtonDown(0)) {
            int var4 = (this.titleWidth - this.iconWidth) / 2;
            int var5 = (this.height - this.iconHeight) / 2;
            int var6 = var4 + 8;
            int var7 = var5 + 17;
            if ((this.scroll == 0 || this.scroll == 1) && mouseX >= var6 && mouseX < var6 + 224 && mouseY >= var7 && mouseY < var7 + 155) {
               if (this.scroll == 0) {
                  this.scroll = 1;
               } else {
                  this.scaledMouseDx -= (double)((float)(mouseX - this.prevMouseX) * this.scale);
                  this.scaledMouseDy -= (double)((float)(mouseY - this.prevMouseY) * this.scale);
                  this.scrollX = this.mouseX = this.scaledMouseDx;
                  this.scrollY = this.mouseY = this.scaledMouseDy;
               }

               this.prevMouseX = mouseX;
               this.prevMouseY = mouseY;
            }
         } else {
            this.scroll = 0;
         }

         int var11 = Mouse.getDWheel();
         float var12 = this.scale;
         if (var11 < 0) {
            this.scale += 0.25F;
         } else if (var11 > 0) {
            this.scale -= 0.25F;
         }

         this.scale = MathHelper.clamp(this.scale, 1.0F, 2.0F);
         if (this.scale != var12) {
            float var13 = var12 - this.scale;
            float var14 = var12 * (float)this.iconWidth;
            float var8 = var12 * (float)this.iconHeight;
            float var9 = this.scale * (float)this.iconWidth;
            float var10 = this.scale * (float)this.iconHeight;
            this.scaledMouseDx -= (double)((var9 - var14) * 0.5F);
            this.scaledMouseDy -= (double)((var10 - var8) * 0.5F);
            this.scrollX = this.mouseX = this.scaledMouseDx;
            this.scrollY = this.mouseY = this.scaledMouseDy;
         }

         if (this.scrollX < (double)MIN_COLUMN) {
            this.scrollX = (double)MIN_COLUMN;
         }

         if (this.scrollY < (double)MIN_ROW) {
            this.scrollY = (double)MIN_ROW;
         }

         if (this.scrollX >= (double)MAX_COLUMN) {
            this.scrollX = (double)(MAX_COLUMN - 1);
         }

         if (this.scrollY >= (double)MAX_ROW) {
            this.scrollY = (double)(MAX_ROW - 1);
         }

         this.renderBackground();
         this.renderIcons(mouseX, mouseY, tickDelta);
         GlStateManager.disableLighting();
         GlStateManager.enableDepth();
         this.setTitle();
         GlStateManager.enableLighting();
         GlStateManager.disableDepth();
      }
   }

   @Override
   public void onStatsReady() {
      if (this.downloading) {
         this.downloading = false;
      }
   }

   @Override
   public void tick() {
      if (!this.downloading) {
         this.mouseX = this.scaledMouseDx;
         this.mouseY = this.scaledMouseDy;
         double var1 = this.scrollX - this.scaledMouseDx;
         double var3 = this.scrollY - this.scaledMouseDy;
         if (var1 * var1 + var3 * var3 < 4.0) {
            this.scaledMouseDx += var1;
            this.scaledMouseDy += var3;
         } else {
            this.scaledMouseDx += var1 * 0.85;
            this.scaledMouseDy += var3 * 0.85;
         }
      }
   }

   protected void setTitle() {
      int var1 = (this.titleWidth - this.iconWidth) / 2;
      int var2 = (this.height - this.iconHeight) / 2;
      this.textRenderer.drawWithoutShadow(I18n.translate("gui.achievements"), var1 + 15, var2 + 5, 4210752);
   }

   protected void renderIcons(int mouseX, int mouseY, float tickdelta) {
      int var4 = MathHelper.floor(this.mouseX + (this.scaledMouseDx - this.mouseX) * (double)tickdelta);
      int var5 = MathHelper.floor(this.mouseY + (this.scaledMouseDy - this.mouseY) * (double)tickdelta);
      if (var4 < MIN_COLUMN) {
         var4 = MIN_COLUMN;
      }

      if (var5 < MIN_ROW) {
         var5 = MIN_ROW;
      }

      if (var4 >= MAX_COLUMN) {
         var4 = MAX_COLUMN - 1;
      }

      if (var5 >= MAX_ROW) {
         var5 = MAX_ROW - 1;
      }

      int var6 = (this.titleWidth - this.iconWidth) / 2;
      int var7 = (this.height - this.iconHeight) / 2;
      int var8 = var6 + 16;
      int var9 = var7 + 17;
      this.drawOffset = 0.0F;
      GlStateManager.depthFunc(518);
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)var8, (float)var9, -200.0F);
      GlStateManager.scalef(1.0F / this.scale, 1.0F / this.scale, 0.0F);
      GlStateManager.enableTexture();
      GlStateManager.disableLighting();
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableColorMaterial();
      int var10 = var4 + 288 >> 4;
      int var11 = var5 + 288 >> 4;
      int var12 = (var4 + 288) % 16;
      int var13 = (var5 + 288) % 16;
      boolean var14 = true;
      boolean var15 = true;
      boolean var16 = true;
      boolean var17 = true;
      boolean var18 = true;
      Random var19 = new Random();
      float var20 = 16.0F / this.scale;
      float var21 = 16.0F / this.scale;

      for(int var22 = 0; (float)var22 * var20 - (float)var13 < 155.0F; ++var22) {
         float var23 = 0.6F - (float)(var11 + var22) / 25.0F * 0.3F;
         GlStateManager.color4f(var23, var23, var23, 1.0F);

         for(int var24 = 0; (float)var24 * var21 - (float)var12 < 224.0F; ++var24) {
            var19.setSeed((long)(this.client.getSession().getUuid().hashCode() + var10 + var24 + (var11 + var22) * 16));
            int var25 = var19.nextInt(1 + var11 + var22) + (var11 + var22) / 2;
            TextureAtlasSprite var26 = this.m_07idzywlt(Blocks.SAND);
            if (var25 > 37 || var11 + var22 == 35) {
               Block var27 = Blocks.BEDROCK;
               var26 = this.m_07idzywlt(var27);
            } else if (var25 == 22) {
               if (var19.nextInt(2) == 0) {
                  var26 = this.m_07idzywlt(Blocks.DIAMOND_ORE);
               } else {
                  var26 = this.m_07idzywlt(Blocks.REDSTONE_ORE);
               }
            } else if (var25 == 10) {
               var26 = this.m_07idzywlt(Blocks.IRON_ORE);
            } else if (var25 == 8) {
               var26 = this.m_07idzywlt(Blocks.COAL_ORE);
            } else if (var25 > 4) {
               var26 = this.m_07idzywlt(Blocks.STONE);
            } else if (var25 > 0) {
               var26 = this.m_07idzywlt(Blocks.DIRT);
            }

            this.client.getTextureManager().bind(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
            this.drawSprite(var24 * 16 - var12, var22 * 16 - var13, var26, 16, 16);
         }
      }

      GlStateManager.disableDepth();
      GlStateManager.depthFunc(515);
      this.client.getTextureManager().bind(ACHIEVEMENT_BACKGROUND);

      for(int var33 = 0; var33 < Achievements.ALL.size(); ++var33) {
         AchievementStat var35 = (AchievementStat)Achievements.ALL.get(var33);
         if (var35.parent != null) {
            int var37 = var35.column * 24 - var4 + 11;
            int var39 = var35.row * 24 - var5 + 11;
            int var42 = var35.parent.column * 24 - var4 + 11;
            int var45 = var35.parent.row * 24 - var5 + 11;
            boolean var28 = this.statHandler.hasAchievement(var35);
            boolean var29 = this.statHandler.hasParentAchievement(var35);
            int var30 = this.statHandler.getValue(var35);
            if (var30 <= 4) {
               int var31 = -16777216;
               if (var28) {
                  var31 = -6250336;
               } else if (var29) {
                  var31 = -16711936;
               }

               this.drawHorizontalLine(var37, var42, var39, var31);
               this.drawVerticalLine(var42, var39, var45, var31);
               if (var37 > var42) {
                  this.drawTexture(var37 - 11 - 7, var39 - 5, 114, 234, 7, 11);
               } else if (var37 < var42) {
                  this.drawTexture(var37 + 11, var39 - 5, 107, 234, 7, 11);
               } else if (var39 > var45) {
                  this.drawTexture(var37 - 5, var39 - 11 - 7, 96, 234, 11, 7);
               } else if (var39 < var45) {
                  this.drawTexture(var37 - 5, var39 + 11, 96, 241, 11, 7);
               }
            }
         }
      }

      AchievementStat var34 = null;
      float var36 = (float)(mouseX - var8) * this.scale;
      float var38 = (float)(mouseY - var9) * this.scale;
      Lighting.turnOnGui();
      GlStateManager.disableLighting();
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableColorMaterial();

      for(int var40 = 0; var40 < Achievements.ALL.size(); ++var40) {
         AchievementStat var43 = (AchievementStat)Achievements.ALL.get(var40);
         int var46 = var43.column * 24 - var4;
         int var48 = var43.row * 24 - var5;
         if (var46 >= -24 && var48 >= -24 && (float)var46 <= 224.0F * this.scale && (float)var48 <= 155.0F * this.scale) {
            int var50 = this.statHandler.getValue(var43);
            if (this.statHandler.hasAchievement(var43)) {
               float var52 = 0.75F;
               GlStateManager.color4f(var52, var52, var52, 1.0F);
            } else if (this.statHandler.hasParentAchievement(var43)) {
               float var53 = 1.0F;
               GlStateManager.color4f(var53, var53, var53, 1.0F);
            } else if (var50 < 3) {
               float var54 = 0.3F;
               GlStateManager.color4f(var54, var54, var54, 1.0F);
            } else if (var50 == 3) {
               float var55 = 0.2F;
               GlStateManager.color4f(var55, var55, var55, 1.0F);
            } else {
               if (var50 != 4) {
                  continue;
               }

               float var56 = 0.1F;
               GlStateManager.color4f(var56, var56, var56, 1.0F);
            }

            this.client.getTextureManager().bind(ACHIEVEMENT_BACKGROUND);
            if (var43.isChallenge()) {
               this.drawTexture(var46 - 2, var48 - 2, 26, 202, 26, 26);
            } else {
               this.drawTexture(var46 - 2, var48 - 2, 0, 202, 26, 26);
            }

            if (!this.statHandler.hasParentAchievement(var43)) {
               float var57 = 0.1F;
               GlStateManager.color4f(var57, var57, var57, 1.0F);
               this.itemRenderer.setUseCustomDisplayColor(false);
            }

            GlStateManager.enableLighting();
            GlStateManager.enableCull();
            this.itemRenderer.renderGuiItem(var43.icon, var46 + 3, var48 + 3);
            GlStateManager.blendFunc(770, 771);
            GlStateManager.disableLighting();
            if (!this.statHandler.hasParentAchievement(var43)) {
               this.itemRenderer.setUseCustomDisplayColor(true);
            }

            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (var36 >= (float)var46 && var36 <= (float)(var46 + 22) && var38 >= (float)var48 && var38 <= (float)(var48 + 22)) {
               var34 = var43;
            }
         }
      }

      GlStateManager.enableDepth();
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.client.getTextureManager().bind(ACHIEVEMENT_BACKGROUND);
      this.drawTexture(var6, var7, 0, 0, this.iconWidth, this.iconHeight);
      this.drawOffset = 0.0F;
      GlStateManager.depthFunc(515);
      GlStateManager.enableDepth();
      GlStateManager.enableTexture();
      super.render(mouseX, mouseY, tickdelta);
      if (var34 != null) {
         String var41 = var34.getDecoratedName().buildString();
         String var44 = var34.getDescription();
         int var47 = mouseX + 12;
         int var49 = mouseY - 4;
         int var51 = this.statHandler.getValue(var34);
         if (this.statHandler.hasParentAchievement(var34)) {
            int var58 = Math.max(this.textRenderer.getStringWidth(var41), 120);
            int var61 = this.textRenderer.getTextBoxHeight(var44, var58);
            if (this.statHandler.hasAchievement(var34)) {
               var61 += 12;
            }

            this.fillGradient(var47 - 3, var49 - 3, var47 + var58 + 3, var49 + var61 + 3 + 12, -1073741824, -1073741824);
            this.textRenderer.drawTrimmed(var44, var47, var49 + 12, var58, -6250336);
            if (this.statHandler.hasAchievement(var34)) {
               this.textRenderer.drawWithShadow(I18n.translate("achievement.taken"), (float)var47, (float)(var49 + var61 + 4), -7302913);
            }
         } else if (var51 == 3) {
            var41 = I18n.translate("achievement.unknown");
            int var59 = Math.max(this.textRenderer.getStringWidth(var41), 120);
            String var62 = new TranslatableText("achievement.requires", var34.parent.getDecoratedName()).buildString();
            int var32 = this.textRenderer.getTextBoxHeight(var62, var59);
            this.fillGradient(var47 - 3, var49 - 3, var47 + var59 + 3, var49 + var32 + 12 + 3, -1073741824, -1073741824);
            this.textRenderer.drawTrimmed(var62, var47, var49 + 12, var59, -9416624);
         } else if (var51 < 3) {
            int var60 = Math.max(this.textRenderer.getStringWidth(var41), 120);
            String var63 = new TranslatableText("achievement.requires", var34.parent.getDecoratedName()).buildString();
            int var64 = this.textRenderer.getTextBoxHeight(var63, var60);
            this.fillGradient(var47 - 3, var49 - 3, var47 + var60 + 3, var49 + var64 + 12 + 3, -1073741824, -1073741824);
            this.textRenderer.drawTrimmed(var63, var47, var49 + 12, var60, -9416624);
         } else {
            var41 = null;
         }

         if (var41 != null) {
            this.textRenderer
               .drawWithShadow(
                  var41,
                  (float)var47,
                  (float)var49,
                  this.statHandler.hasParentAchievement(var34) ? (var34.isChallenge() ? -128 : -1) : (var34.isChallenge() ? -8355776 : -8355712)
               );
         }
      }

      GlStateManager.disableDepth();
      GlStateManager.enableLighting();
      Lighting.turnOff();
   }

   private TextureAtlasSprite m_07idzywlt(Block c_68zcrzyxg) {
      return MinecraftClient.getInstance().getBlockRenderDispatcher().getModelShaper().getParticleIcon(c_68zcrzyxg.defaultState());
   }

   @Override
   public boolean shouldPauseGame() {
      return !this.downloading;
   }
}
