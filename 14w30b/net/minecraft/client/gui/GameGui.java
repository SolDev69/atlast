package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.overlay.DebugOverlay;
import net.minecraft.client.gui.overlay.PlayerTabOverlay;
import net.minecraft.client.gui.overlay.StreamOverlay;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.Window;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.attribute.IEntityAttributeInstance;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.mob.hostile.boss.BossBar;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Identifier;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.scoreboard.team.Team;
import net.minecraft.text.Formatting;
import net.minecraft.text.StringUtils;
import net.minecraft.text.Text;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.border.WorldBorder;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class GameGui extends GuiElement {
   private static final Identifier VIGNETTE = new Identifier("textures/misc/vignette.png");
   private static final Identifier WIDGETS = new Identifier("textures/gui/widgets.png");
   private static final Identifier PUMPKIN_BLUR = new Identifier("textures/misc/pumpkinblur.png");
   private final Random random = new Random();
   private final MinecraftClient client;
   private final ItemRenderer itemRenderer;
   private final ChatGui chat;
   private final StreamOverlay streamOverlay;
   private int ticks;
   private String overlayMessage = "";
   private int overlayMessageTimer;
   private boolean overlayMessageTinted;
   public float vignetteBrightness = 1.0F;
   private int mainHandMessageTimer;
   private ItemStack mainHandStack;
   private final DebugOverlay debugOverlay;
   private final SpectatorGui subtitleOverlay;
   private final PlayerTabOverlay playerTabOverlay;
   private int titleTime;
   private String title = "";
   private String subtitle = "";
   private int titleFadeInTime;
   private int titleDuration;
   private int titleFadeOutTime;

   public GameGui(MinecraftClient client) {
      this.client = client;
      this.itemRenderer = client.getItemRenderer();
      this.debugOverlay = new DebugOverlay(client);
      this.subtitleOverlay = new SpectatorGui(client);
      this.chat = new ChatGui(client);
      this.streamOverlay = new StreamOverlay(client);
      this.playerTabOverlay = new PlayerTabOverlay(client);
      this.resetTitleTimes();
   }

   public void resetTitleTimes() {
      this.titleFadeInTime = 10;
      this.titleDuration = 70;
      this.titleFadeOutTime = 20;
   }

   public void render(float tickDelta) {
      Window var2 = new Window(this.client, this.client.width, this.client.height);
      int var3 = var2.getWidth();
      int var4 = var2.getHeight();
      this.client.gameRenderer.setupHudMatrixMode();
      GlStateManager.disableBlend();
      if (MinecraftClient.isFancyGraphicsEnabled()) {
         this.renderVignette(this.client.player.getBrightness(tickDelta), var2);
      } else {
         GlStateManager.blendFuncSeparate(770, 771, 1, 0);
      }

      ItemStack var5 = this.client.player.inventory.getArmor(3);
      if (this.client.options.perspective == 0 && var5 != null && var5.getItem() == Item.byBlock(Blocks.PUMPKIN)) {
         this.renderPumpkinBlur(var2);
      }

      if (!this.client.player.hasStatusEffect(StatusEffect.NAUSEA)) {
         float var6 = this.client.player.oldNetherPortalDuration
            + (this.client.player.netherPortalDuration - this.client.player.oldNetherPortalDuration) * tickDelta;
         if (var6 > 0.0F) {
            this.renderNauseaOverlay(var6, var2);
         }
      }

      if (this.client.interactionManager.isInSpectatorMode()) {
         this.subtitleOverlay.renderHotbar(var2, tickDelta);
      } else {
         this.renderHotbar(var2, tickDelta);
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.client.getTextureManager().bind(ICONS);
      GlStateManager.disableBlend();
      if (this.hasCrossHair()) {
         GlStateManager.blendFuncSeparate(775, 769, 1, 0);
         GlStateManager.enableAlphaTest();
         this.drawTexture(var3 / 2 - 7, var4 / 2 - 7, 0, 0, 16, 16);
      }

      GlStateManager.blendFuncSeparate(770, 771, 1, 0);
      this.client.profiler.push("bossHealth");
      this.renderBossBars();
      this.client.profiler.pop();
      if (this.client.interactionManager.hasStatusBars()) {
         this.renderStatusBars(var2);
      }

      GlStateManager.enableBlend();
      if (this.client.player.getSleepTimer() > 0) {
         this.client.profiler.push("sleep");
         GlStateManager.enableDepth();
         GlStateManager.disableAlphaTest();
         int var11 = this.client.player.getSleepTimer();
         float var7 = (float)var11 / 100.0F;
         if (var7 > 1.0F) {
            var7 = 1.0F - (float)(var11 - 100) / 10.0F;
         }

         int var8 = (int)(220.0F * var7) << 24 | 1052704;
         fill(0, 0, var3, var4, var8);
         GlStateManager.enableAlphaTest();
         GlStateManager.disableDepth();
         this.client.profiler.pop();
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      int var12 = var3 / 2 - 91;
      if (this.client.player.isRidingRideableMob()) {
         this.renderJumpBar(var2, var12);
      } else if (this.client.interactionManager.hasXpBar()) {
         this.renderXpBar(var2, var12);
      }

      if (this.client.options.heldItemTooltips && !this.client.interactionManager.isInSpectatorMode()) {
         this.renderMainHandMessage(var2);
      } else if (this.client.player.isSpectator()) {
         this.subtitleOverlay.renderTooltip(var2);
      }

      if (this.client.isDemo()) {
         this.renderDemoMessage(var2);
      }

      if (this.client.options.debugEnabled) {
         this.debugOverlay.render(var2);
      }

      if (this.overlayMessageTimer > 0) {
         this.client.profiler.push("overlayMessage");
         float var13 = (float)this.overlayMessageTimer - tickDelta;
         int var16 = (int)(var13 * 255.0F / 20.0F);
         if (var16 > 255) {
            var16 = 255;
         }

         if (var16 > 8) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)(var3 / 2), (float)(var4 - 68), 0.0F);
            GlStateManager.disableBlend();
            GlStateManager.blendFuncSeparate(770, 771, 1, 0);
            int var9 = 16777215;
            if (this.overlayMessageTinted) {
               var9 = Color.HSBtoRGB(var13 / 50.0F, 0.7F, 0.6F) & 16777215;
            }

            this.getTextRenderer()
               .drawWithoutShadow(this.overlayMessage, -this.getTextRenderer().getStringWidth(this.overlayMessage) / 2, -4, var9 + (var16 << 24 & 0xFF000000));
            GlStateManager.enableBlend();
            GlStateManager.popMatrix();
         }

         this.client.profiler.pop();
      }

      if (this.titleTime > 0) {
         this.client.profiler.push("titleAndSubtitle");
         float var14 = (float)this.titleTime - tickDelta;
         int var17 = 255;
         if (this.titleTime > this.titleFadeOutTime + this.titleDuration) {
            float var20 = (float)(this.titleFadeInTime + this.titleDuration + this.titleFadeOutTime) - var14;
            var17 = (int)(var20 * 255.0F / (float)this.titleFadeInTime);
         }

         if (this.titleTime <= this.titleFadeOutTime) {
            var17 = (int)(var14 * 255.0F / (float)this.titleFadeOutTime);
         }

         var17 = MathHelper.clamp(var17, 0, 255);
         if (var17 > 8) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)(var3 / 2), (float)(var4 / 2), 0.0F);
            GlStateManager.disableBlend();
            GlStateManager.blendFuncSeparate(770, 771, 1, 0);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4.0F, 4.0F, 4.0F);
            int var21 = var17 << 24 & 0xFF000000;
            this.getTextRenderer().draw(this.title, (float)(-this.getTextRenderer().getStringWidth(this.title) / 2), -10.0F, 16777215 | var21, true);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(2.0F, 2.0F, 2.0F);
            this.getTextRenderer().draw(this.subtitle, (float)(-this.getTextRenderer().getStringWidth(this.subtitle) / 2), 5.0F, 16777215 | var21, true);
            GlStateManager.popMatrix();
            GlStateManager.enableBlend();
            GlStateManager.popMatrix();
         }

         this.client.profiler.pop();
      }

      Scoreboard var15 = this.client.world.getScoreboard();
      ScoreboardObjective var19 = null;
      Team var22 = var15.getTeamOfMember(this.client.player.getName());
      if (var22 != null) {
         int var10 = var22.getColor().getIndex();
         if (var10 >= 0) {
            var19 = var15.getDisplayObjective(3 + var10);
         }
      }

      ScoreboardObjective var23 = var19 != null ? var19 : var15.getDisplayObjective(1);
      if (var23 != null) {
         this.renderScoreboardObjective(var23, var2);
      }

      GlStateManager.disableBlend();
      GlStateManager.blendFuncSeparate(770, 771, 1, 0);
      GlStateManager.disableAlphaTest();
      GlStateManager.pushMatrix();
      GlStateManager.translatef(0.0F, (float)(var4 - 48), 0.0F);
      this.client.profiler.push("chat");
      this.chat.render(this.ticks);
      this.client.profiler.pop();
      GlStateManager.popMatrix();
      var23 = var15.getDisplayObjective(0);
      if (this.client.options.playerListKey.isPressed()
         && (!this.client.isIntegratedServerRunning() || this.client.player.networkHandler.getOnlinePlayers().size() > 1 || var23 != null)) {
         this.playerTabOverlay.render(var3, var15, var23);
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableLighting();
      GlStateManager.enableAlphaTest();
   }

   protected void renderHotbar(Window window, float tickDelta) {
      if (this.client.getCamera() instanceof PlayerEntity) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.client.getTextureManager().bind(WIDGETS);
         PlayerEntity var3 = (PlayerEntity)this.client.getCamera();
         int var4 = window.getWidth() / 2;
         float var5 = this.drawOffset;
         this.drawOffset = -90.0F;
         this.drawTexture(var4 - 91, window.getHeight() - 22, 0, 0, 182, 22);
         this.drawTexture(var4 - 91 - 1 + var3.inventory.selectedSlot * 20, window.getHeight() - 22 - 1, 0, 22, 24, 22);
         this.drawOffset = var5;
         GlStateManager.enableRescaleNormal();
         GlStateManager.disableBlend();
         GlStateManager.blendFuncSeparate(770, 771, 1, 0);
         Lighting.turnOnGui();

         for(int var6 = 0; var6 < 9; ++var6) {
            int var7 = window.getWidth() / 2 - 90 + var6 * 20 + 2;
            int var8 = window.getHeight() - 16 - 3;
            this.renderItemSlot(var6, var7, var8, tickDelta, var3);
         }

         Lighting.turnOff();
         GlStateManager.disableRescaleNormal();
         GlStateManager.enableBlend();
      }
   }

   public void renderJumpBar(Window window, int x) {
      this.client.profiler.push("jumpBar");
      this.client.getTextureManager().bind(GuiElement.ICONS);
      float var3 = this.client.player.getRidingJumpProgress();
      short var4 = 182;
      int var5 = (int)(var3 * (float)(var4 + 1));
      int var6 = window.getHeight() - 32 + 3;
      this.drawTexture(x, var6, 0, 84, var4, 5);
      if (var5 > 0) {
         this.drawTexture(x, var6, 0, 89, var5, 5);
      }

      this.client.profiler.pop();
   }

   public void renderXpBar(Window window, int x) {
      this.client.profiler.push("expBar");
      this.client.getTextureManager().bind(GuiElement.ICONS);
      int var3 = this.client.player.getXpAmount();
      if (var3 > 0) {
         short var4 = 182;
         int var5 = (int)(this.client.player.xpProgress * (float)(var4 + 1));
         int var6 = window.getHeight() - 32 + 3;
         this.drawTexture(x, var6, 0, 64, var4, 5);
         if (var5 > 0) {
            this.drawTexture(x, var6, 0, 69, var5, 5);
         }
      }

      this.client.profiler.pop();
      if (this.client.player.xpLevel > 0) {
         this.client.profiler.push("expLevel");
         int var9 = 8453920;
         String var10 = "" + this.client.player.xpLevel;
         int var11 = (window.getWidth() - this.getTextRenderer().getStringWidth(var10)) / 2;
         int var7 = window.getHeight() - 31 - 4;
         boolean var8 = false;
         this.getTextRenderer().drawWithoutShadow(var10, var11 + 1, var7, 0);
         this.getTextRenderer().drawWithoutShadow(var10, var11 - 1, var7, 0);
         this.getTextRenderer().drawWithoutShadow(var10, var11, var7 + 1, 0);
         this.getTextRenderer().drawWithoutShadow(var10, var11, var7 - 1, 0);
         this.getTextRenderer().drawWithoutShadow(var10, var11, var7, var9);
         this.client.profiler.pop();
      }
   }

   public void renderMainHandMessage(Window window) {
      this.client.profiler.push("toolHighlight");
      if (this.mainHandMessageTimer > 0 && this.mainHandStack != null) {
         String var2 = this.mainHandStack.getHoverName();
         int var3 = (window.getWidth() - this.getTextRenderer().getStringWidth(var2)) / 2;
         int var4 = window.getHeight() - 59;
         if (!this.client.interactionManager.hasStatusBars()) {
            var4 += 14;
         }

         int var5 = (int)((float)this.mainHandMessageTimer * 256.0F / 10.0F);
         if (var5 > 255) {
            var5 = 255;
         }

         if (var5 > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.disableBlend();
            GlStateManager.blendFuncSeparate(770, 771, 1, 0);
            this.getTextRenderer().drawWithShadow(var2, (float)var3, (float)var4, 16777215 + (var5 << 24));
            GlStateManager.enableBlend();
            GlStateManager.popMatrix();
         }
      }

      this.client.profiler.pop();
   }

   public void renderDemoMessage(Window window) {
      this.client.profiler.push("demo");
      String var2 = "";
      if (this.client.world.getTime() >= 120500L) {
         var2 = I18n.translate("demo.demoExpired");
      } else {
         var2 = I18n.translate("demo.remainingTime", StringUtils.getDurationString((int)(120500L - this.client.world.getTime())));
      }

      int var3 = this.getTextRenderer().getStringWidth(var2);
      this.getTextRenderer().drawWithShadow(var2, (float)(window.getWidth() - var3 - 10), 5.0F, 16777215);
      this.client.profiler.pop();
   }

   protected boolean hasCrossHair() {
      if (this.client.options.debugEnabled && !this.client.player.hasReducedDebugInfo() && !this.client.options.reducedDebugInfo) {
         return false;
      } else if (this.client.interactionManager.isInSpectatorMode()) {
         if (this.client.targetEntity != null) {
            return true;
         } else {
            if (this.client.crosshairTarget != null && this.client.crosshairTarget.type == HitResult.Type.BLOCK) {
               BlockPos var1 = this.client.crosshairTarget.getBlockPos();
               if (this.client.world.getBlockEntity(var1) instanceof Inventory) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return true;
      }
   }

   public void renderStreamOverlay(Window window) {
      this.streamOverlay.render(window.getWidth() - 10, 10);
   }

   private void renderScoreboardObjective(ScoreboardObjective objective, Window width) {
      Scoreboard var3 = objective.getScoreboard();
      Collection var4 = var3.getScores(objective);
      ArrayList var5 = Lists.newArrayList(Iterables.filter(var4, new Predicate() {
         public boolean apply(ScoreboardScore c_64uztyeff) {
            return c_64uztyeff.getOwner() != null && !c_64uztyeff.getOwner().startsWith("#");
         }
      }));
      ArrayList var21;
      if (var5.size() > 15) {
         var21 = Lists.newArrayList(Iterables.skip(var5, var4.size() - 15));
      } else {
         var21 = var5;
      }

      int var6 = this.getTextRenderer().getStringWidth(objective.getDisplayName());

      for(ScoreboardScore var8 : var21) {
         Team var9 = var3.getTeamOfMember(var8.getOwner());
         String var10 = Team.getMemberDisplayName(var9, var8.getOwner()) + ": " + Formatting.RED + var8.get();
         var6 = Math.max(var6, this.getTextRenderer().getStringWidth(var10));
      }

      int var22 = var21.size() * this.getTextRenderer().fontHeight;
      int var23 = width.getHeight() / 2 + var22 / 3;
      byte var24 = 3;
      int var25 = width.getWidth() - var6 - var24;
      int var11 = 0;

      for(ScoreboardScore var13 : var21) {
         ++var11;
         Team var14 = var3.getTeamOfMember(var13.getOwner());
         String var15 = Team.getMemberDisplayName(var14, var13.getOwner());
         String var16 = Formatting.RED + "" + var13.get();
         int var18 = var23 - var11 * this.getTextRenderer().fontHeight;
         int var19 = width.getWidth() - var24 + 2;
         fill(var25 - 2, var18, var19, var18 + this.getTextRenderer().fontHeight, 1342177280);
         this.getTextRenderer().drawWithoutShadow(var15, var25, var18, 553648127);
         this.getTextRenderer().drawWithoutShadow(var16, var19 - this.getTextRenderer().getStringWidth(var16), var18, 553648127);
         if (var11 == var21.size()) {
            String var20 = objective.getDisplayName();
            fill(var25 - 2, var18 - this.getTextRenderer().fontHeight - 1, var19, var18 - 1, 1610612736);
            fill(var25 - 2, var18 - 1, var19, var18, 1342177280);
            this.getTextRenderer()
               .drawWithoutShadow(
                  var20, var25 + var6 / 2 - this.getTextRenderer().getStringWidth(var20) / 2, var18 - this.getTextRenderer().fontHeight, 553648127
               );
         }
      }
   }

   private void renderStatusBars(Window width) {
      if (this.client.getCamera() instanceof PlayerEntity) {
         PlayerEntity var2 = (PlayerEntity)this.client.getCamera();
         boolean var3 = var2.maxHealth / 3 % 2 == 1;
         if (var2.maxHealth < 10) {
            var3 = false;
         }

         int var4 = MathHelper.ceil(var2.getHealth());
         int var5 = MathHelper.ceil(var2.lastHealth);
         this.random.setSeed((long)(this.ticks * 312871));
         boolean var6 = false;
         HungerManager var7 = var2.getHungerManager();
         int var8 = var7.getFoodLevel();
         int var9 = var7.getLastFoodLevel();
         IEntityAttributeInstance var10 = var2.initializeAttribute(EntityAttributes.MAX_HEALTH);
         int var11 = width.getWidth() / 2 - 91;
         int var12 = width.getWidth() / 2 + 91;
         int var13 = width.getHeight() - 39;
         float var14 = (float)var10.get();
         float var15 = var2.getAbsorption();
         int var16 = MathHelper.ceil((var14 + var15) / 2.0F / 10.0F);
         int var17 = Math.max(10 - (var16 - 2), 3);
         int var18 = var13 - (var16 - 1) * var17 - 10;
         float var19 = var15;
         int var20 = var2.getArmorProtection();
         int var21 = -1;
         if (var2.hasStatusEffect(StatusEffect.REGENERATION)) {
            var21 = this.ticks % MathHelper.ceil(var14 + 5.0F);
         }

         this.client.profiler.push("armor");

         for(int var22 = 0; var22 < 10; ++var22) {
            if (var20 > 0) {
               int var23 = var11 + var22 * 8;
               if (var22 * 2 + 1 < var20) {
                  this.drawTexture(var23, var18, 34, 9, 9, 9);
               }

               if (var22 * 2 + 1 == var20) {
                  this.drawTexture(var23, var18, 25, 9, 9, 9);
               }

               if (var22 * 2 + 1 > var20) {
                  this.drawTexture(var23, var18, 16, 9, 9, 9);
               }
            }
         }

         this.client.profiler.swap("health");

         for(int var34 = MathHelper.ceil((var14 + var15) / 2.0F) - 1; var34 >= 0; --var34) {
            int var36 = 16;
            if (var2.hasStatusEffect(StatusEffect.POISON)) {
               var36 += 36;
            } else if (var2.hasStatusEffect(StatusEffect.WITHER)) {
               var36 += 72;
            }

            byte var24 = 0;
            if (var3) {
               var24 = 1;
            }

            int var25 = MathHelper.ceil((float)(var34 + 1) / 10.0F) - 1;
            int var26 = var11 + var34 % 10 * 8;
            int var27 = var13 - var25 * var17;
            if (var4 <= 4) {
               var27 += this.random.nextInt(2);
            }

            if (var34 == var21) {
               var27 -= 2;
            }

            byte var28 = 0;
            if (var2.world.getData().isHardcore()) {
               var28 = 5;
            }

            this.drawTexture(var26, var27, 16 + var24 * 9, 9 * var28, 9, 9);
            if (var3) {
               if (var34 * 2 + 1 < var5) {
                  this.drawTexture(var26, var27, var36 + 54, 9 * var28, 9, 9);
               }

               if (var34 * 2 + 1 == var5) {
                  this.drawTexture(var26, var27, var36 + 63, 9 * var28, 9, 9);
               }
            }

            if (var19 > 0.0F) {
               if (var19 == var15 && var15 % 2.0F == 1.0F) {
                  this.drawTexture(var26, var27, var36 + 153, 9 * var28, 9, 9);
               } else {
                  this.drawTexture(var26, var27, var36 + 144, 9 * var28, 9, 9);
               }

               var19 -= 2.0F;
            } else {
               if (var34 * 2 + 1 < var4) {
                  this.drawTexture(var26, var27, var36 + 36, 9 * var28, 9, 9);
               }

               if (var34 * 2 + 1 == var4) {
                  this.drawTexture(var26, var27, var36 + 45, 9 * var28, 9, 9);
               }
            }
         }

         Entity var35 = var2.vehicle;
         if (var35 == null) {
            this.client.profiler.swap("food");

            for(int var37 = 0; var37 < 10; ++var37) {
               int var40 = var13;
               int var43 = 16;
               byte var46 = 0;
               if (var2.hasStatusEffect(StatusEffect.HUNGER)) {
                  var43 += 36;
                  var46 = 13;
               }

               if (var2.getHungerManager().getSaturationLevel() <= 0.0F && this.ticks % (var8 * 3 + 1) == 0) {
                  var40 = var13 + (this.random.nextInt(3) - 1);
               }

               if (var6) {
                  var46 = 1;
               }

               int var49 = var12 - var37 * 8 - 9;
               this.drawTexture(var49, var40, 16 + var46 * 9, 27, 9, 9);
               if (var6) {
                  if (var37 * 2 + 1 < var9) {
                     this.drawTexture(var49, var40, var43 + 54, 27, 9, 9);
                  }

                  if (var37 * 2 + 1 == var9) {
                     this.drawTexture(var49, var40, var43 + 63, 27, 9, 9);
                  }
               }

               if (var37 * 2 + 1 < var8) {
                  this.drawTexture(var49, var40, var43 + 36, 27, 9, 9);
               }

               if (var37 * 2 + 1 == var8) {
                  this.drawTexture(var49, var40, var43 + 45, 27, 9, 9);
               }
            }
         } else if (var35 instanceof LivingEntity) {
            this.client.profiler.swap("mountHealth");
            LivingEntity var38 = (LivingEntity)var35;
            int var41 = (int)Math.ceil((double)var38.getHealth());
            float var44 = var38.getMaxHealth();
            int var47 = (int)(var44 + 0.5F) / 2;
            if (var47 > 30) {
               var47 = 30;
            }

            int var50 = var13;

            for(int var51 = 0; var47 > 0; var51 += 20) {
               int var29 = Math.min(var47, 10);
               var47 -= var29;

               for(int var30 = 0; var30 < var29; ++var30) {
                  byte var31 = 52;
                  byte var32 = 0;
                  if (var6) {
                     var32 = 1;
                  }

                  int var33 = var12 - var30 * 8 - 9;
                  this.drawTexture(var33, var50, var31 + var32 * 9, 9, 9, 9);
                  if (var30 * 2 + 1 + var51 < var41) {
                     this.drawTexture(var33, var50, var31 + 36, 9, 9, 9);
                  }

                  if (var30 * 2 + 1 + var51 == var41) {
                     this.drawTexture(var33, var50, var31 + 45, 9, 9, 9);
                  }
               }

               var50 -= 10;
            }
         }

         this.client.profiler.swap("air");
         if (var2.isSubmergedIn(Material.WATER)) {
            int var39 = this.client.player.getBreath();
            int var42 = MathHelper.ceil((double)(var39 - 2) * 10.0 / 300.0);
            int var45 = MathHelper.ceil((double)var39 * 10.0 / 300.0) - var42;

            for(int var48 = 0; var48 < var42 + var45; ++var48) {
               if (var48 < var42) {
                  this.drawTexture(var12 - var48 * 8 - 9, var18, 16, 18, 9, 9);
               } else {
                  this.drawTexture(var12 - var48 * 8 - 9, var18, 25, 18, 9, 9);
               }
            }
         }

         this.client.profiler.pop();
      }
   }

   private void renderBossBars() {
      if (BossBar.name != null && BossBar.timer > 0) {
         --BossBar.timer;
         TextRenderer var1 = this.client.textRenderer;
         Window var2 = new Window(this.client, this.client.width, this.client.height);
         int var3 = var2.getWidth();
         short var4 = 182;
         int var5 = var3 / 2 - var4 / 2;
         int var6 = (int)(BossBar.health * (float)(var4 + 1));
         byte var7 = 12;
         this.drawTexture(var5, var7, 0, 74, var4, 5);
         this.drawTexture(var5, var7, 0, 74, var4, 5);
         if (var6 > 0) {
            this.drawTexture(var5, var7, 0, 79, var6, 5);
         }

         String var8 = BossBar.name;
         this.getTextRenderer().drawWithShadow(var8, (float)(var3 / 2 - this.getTextRenderer().getStringWidth(var8) / 2), (float)(var7 - 10), 16777215);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.client.getTextureManager().bind(ICONS);
      }
   }

   private void renderPumpkinBlur(Window width) {
      GlStateManager.enableDepth();
      GlStateManager.depthMask(false);
      GlStateManager.blendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableAlphaTest();
      this.client.getTextureManager().bind(PUMPKIN_BLUR);
      Tessellator var2 = Tessellator.getInstance();
      BufferBuilder var3 = var2.getBufferBuilder();
      var3.start();
      var3.vertex(0.0, (double)width.getHeight(), -90.0, 0.0, 1.0);
      var3.vertex((double)width.getWidth(), (double)width.getHeight(), -90.0, 1.0, 1.0);
      var3.vertex((double)width.getWidth(), 0.0, -90.0, 1.0, 0.0);
      var3.vertex(0.0, 0.0, -90.0, 0.0, 0.0);
      var2.end();
      GlStateManager.depthMask(true);
      GlStateManager.disableDepth();
      GlStateManager.enableAlphaTest();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void renderVignette(float brightnessAtEyes, Window width) {
      brightnessAtEyes = 1.0F - brightnessAtEyes;
      brightnessAtEyes = MathHelper.clamp(brightnessAtEyes, 0.0F, 1.0F);
      WorldBorder var3 = this.client.world.getWorldBorder();
      float var4 = (float)var3.getDistanceFrom(this.client.player);
      double var5 = Math.min(var3.getSizeChangeSpeed() * (double)var3.getWarningTime() * 1000.0, Math.abs(var3.getSizeLerpTarget() - var3.getLerpSize()));
      double var7 = Math.max((double)var3.getWarningBlocks(), var5);
      if ((double)var4 < var7) {
         var4 = 1.0F - (float)((double)var4 / var7);
      } else {
         var4 = 0.0F;
      }

      this.vignetteBrightness = (float)((double)this.vignetteBrightness + (double)(brightnessAtEyes - this.vignetteBrightness) * 0.01);
      GlStateManager.enableDepth();
      GlStateManager.depthMask(false);
      GlStateManager.blendFuncSeparate(0, 769, 1, 0);
      if (var4 > 0.0F) {
         GlStateManager.color4f(0.0F, var4, var4, 1.0F);
      } else {
         GlStateManager.color4f(this.vignetteBrightness, this.vignetteBrightness, this.vignetteBrightness, 1.0F);
      }

      this.client.getTextureManager().bind(VIGNETTE);
      Tessellator var9 = Tessellator.getInstance();
      BufferBuilder var10 = var9.getBufferBuilder();
      var10.start();
      var10.vertex(0.0, (double)width.getHeight(), -90.0, 0.0, 1.0);
      var10.vertex((double)width.getWidth(), (double)width.getHeight(), -90.0, 1.0, 1.0);
      var10.vertex((double)width.getWidth(), 0.0, -90.0, 1.0, 0.0);
      var10.vertex(0.0, 0.0, -90.0, 0.0, 0.0);
      var9.end();
      GlStateManager.depthMask(true);
      GlStateManager.disableDepth();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.blendFuncSeparate(770, 771, 1, 0);
   }

   private void renderNauseaOverlay(float portalTime, Window width) {
      if (portalTime < 1.0F) {
         portalTime *= portalTime;
         portalTime *= portalTime;
         portalTime = portalTime * 0.8F + 0.2F;
      }

      GlStateManager.disableAlphaTest();
      GlStateManager.enableDepth();
      GlStateManager.depthMask(false);
      GlStateManager.blendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, portalTime);
      this.client.getTextureManager().bind(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
      TextureAtlasSprite var3 = this.client.getBlockRenderDispatcher().getModelShaper().getParticleIcon(Blocks.NETHER_PORTAL.defaultState());
      float var4 = var3.getUMin();
      float var5 = var3.getVMin();
      float var6 = var3.getUMax();
      float var7 = var3.getVMax();
      Tessellator var8 = Tessellator.getInstance();
      BufferBuilder var9 = var8.getBufferBuilder();
      var9.start();
      var9.vertex(0.0, (double)width.getHeight(), -90.0, (double)var4, (double)var7);
      var9.vertex((double)width.getWidth(), (double)width.getHeight(), -90.0, (double)var6, (double)var7);
      var9.vertex((double)width.getWidth(), 0.0, -90.0, (double)var6, (double)var5);
      var9.vertex(0.0, 0.0, -90.0, (double)var4, (double)var5);
      var8.end();
      GlStateManager.depthMask(true);
      GlStateManager.disableDepth();
      GlStateManager.enableAlphaTest();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void renderItemSlot(int slot, int x, int z, float tickDelta, PlayerEntity c_84dqcqlog) {
      ItemStack var6 = c_84dqcqlog.inventory.inventorySlots[slot];
      if (var6 != null) {
         float var7 = (float)var6.popAnimationTime - tickDelta;
         if (var7 > 0.0F) {
            GlStateManager.pushMatrix();
            float var8 = 1.0F + var7 / 5.0F;
            GlStateManager.translatef((float)(x + 8), (float)(z + 12), 0.0F);
            GlStateManager.scalef(1.0F / var8, (var8 + 1.0F) / 2.0F, 1.0F);
            GlStateManager.translatef((float)(-(x + 8)), (float)(-(z + 12)), 0.0F);
         }

         this.itemRenderer.renderGuiItem(var6, x, z);
         if (var7 > 0.0F) {
            GlStateManager.popMatrix();
         }

         this.itemRenderer.renderGuiItemDecorations(this.client.textRenderer, var6, x, z);
      }
   }

   public void tick() {
      if (this.overlayMessageTimer > 0) {
         --this.overlayMessageTimer;
      }

      if (this.titleTime > 0) {
         --this.titleTime;
         if (this.titleTime <= 0) {
            this.title = "";
            this.subtitle = "";
         }
      }

      ++this.ticks;
      this.streamOverlay.m_10hksspcd();
      if (this.client.player != null) {
         ItemStack var1 = this.client.player.inventory.getMainHandStack();
         if (var1 == null) {
            this.mainHandMessageTimer = 0;
         } else if (this.mainHandStack != null
            && var1.getItem() == this.mainHandStack.getItem()
            && ItemStack.matchesNbt(var1, this.mainHandStack)
            && (var1.isDamageable() || var1.getMetadata() == this.mainHandStack.getMetadata())) {
            if (this.mainHandMessageTimer > 0) {
               --this.mainHandMessageTimer;
            }
         } else {
            this.mainHandMessageTimer = 40;
         }

         this.mainHandStack = var1;
      }
   }

   public void setRecordPlayingOverlay(String playerName) {
      this.setOverlayMessage(I18n.translate("record.nowPlaying", playerName), true);
   }

   public void setOverlayMessage(String overlayMessage, boolean tinted) {
      this.overlayMessage = overlayMessage;
      this.overlayMessageTimer = 60;
      this.overlayMessageTinted = tinted;
   }

   public void setTitles(String title, String subtitle, int titleFadeInTime, int titleDuration, int titleFadeOutTime) {
      if (title == null && subtitle == null && titleFadeInTime < 0 && titleDuration < 0 && titleFadeOutTime < 0) {
         this.title = "";
         this.subtitle = "";
         this.titleTime = 0;
      } else if (title != null) {
         this.title = title;
         this.titleTime = this.titleFadeInTime + this.titleDuration + this.titleFadeOutTime;
      } else if (subtitle != null) {
         this.subtitle = subtitle;
      } else {
         if (titleFadeInTime >= 0) {
            this.titleFadeInTime = titleFadeInTime;
         }

         if (titleDuration >= 0) {
            this.titleDuration = titleDuration;
         }

         if (titleFadeOutTime >= 0) {
            this.titleFadeOutTime = titleFadeOutTime;
         }

         if (this.titleTime > 0) {
            this.titleTime = this.titleFadeInTime + this.titleDuration + this.titleFadeOutTime;
         }
      }
   }

   public void setOverlayMessage(Text message, boolean tinted) {
      this.setOverlayMessage(message.buildString(), tinted);
   }

   public ChatGui getChat() {
      return this.chat;
   }

   public int getTicks() {
      return this.ticks;
   }

   public TextRenderer getTextRenderer() {
      return this.client.textRenderer;
   }

   public SpectatorGui getSpectatorGui() {
      return this.subtitleOverlay;
   }

   public PlayerTabOverlay getPlayerTabOverlay() {
      return this.playerTabOverlay;
   }
}
