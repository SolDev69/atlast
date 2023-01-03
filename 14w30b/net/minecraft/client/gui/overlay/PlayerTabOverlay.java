package net.minecraft.client.gui.overlay;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.network.PlayerInfo;
import net.minecraft.client.network.handler.ClientPlayNetworkHandler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.criterion.ScoreboardCriterion;
import net.minecraft.scoreboard.team.Team;
import net.minecraft.text.Formatting;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldSettings;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class PlayerTabOverlay extends GuiElement {
   private static final Ordering PLAYER_ORDERING = Ordering.from(new PlayerTabOverlay.PlayerInfoComparator());
   private final MinecraftClient client;
   private Text header;
   private Text footer;

   public PlayerTabOverlay(MinecraftClient c_13piauvdk) {
      this.client = c_13piauvdk;
   }

   public String getDisplayName(PlayerInfo player) {
      return player.getDisplayName() != null
         ? player.getDisplayName().buildFormattedString()
         : Team.getMemberDisplayName(player.getTeam(), player.getProfile().getName());
   }

   public void render(int width, Scoreboard scoreboard, ScoreboardObjective displayObjective) {
      ClientPlayNetworkHandler var4 = this.client.player.networkHandler;
      List var5 = PLAYER_ORDERING.sortedCopy(var4.getOnlinePlayers());
      int var6 = 0;
      int var7 = 0;

      for(PlayerInfo var9 : var5) {
         int var10 = this.client.textRenderer.getStringWidth(this.getDisplayName(var9));
         var6 = Math.max(var6, var10);
         if (displayObjective != null && displayObjective.getRenderType() != ScoreboardCriterion.RenderType.HEARTS) {
            var10 = this.client.textRenderer.getStringWidth(" " + scoreboard.getScore(var9.getProfile().getName(), displayObjective).get());
            var7 = Math.max(var7, var10);
         }
      }

      int var28 = var5.size();
      int var29 = var28;

      int var31;
      for(var31 = 1; var29 > 20; var29 = (var28 + var31 - 1) / var31) {
         ++var31;
      }

      boolean var11 = this.client.isIntegratedServerRunning() || this.client.getNetworkHandler().getConnection().isEncrypted();
      int var12;
      if (displayObjective != null) {
         if (displayObjective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
            var12 = 90;
         } else {
            var12 = var7;
         }
      } else {
         var12 = 0;
      }

      int var13 = Math.min(var31 * ((var11 ? 9 : 0) + var6 + var12 + 13), width - 50) / var31;
      int var14 = width / 2 - (var13 * var31 + (var31 - 1) * 5) / 2;
      int var15 = 10;
      int var16 = var13 * var31 + (var31 - 1) * 5;
      List var17 = null;
      List var18 = null;
      if (this.footer != null) {
         var17 = this.client.textRenderer.wrapLines(this.footer.buildFormattedString(), width - 50);

         for(String var20 : var17) {
            var16 = Math.max(var16, this.client.textRenderer.getStringWidth(var20));
         }
      }

      if (this.header != null) {
         var18 = this.client.textRenderer.wrapLines(this.header.buildFormattedString(), width - 50);

         for(String var37 : var18) {
            var16 = Math.max(var16, this.client.textRenderer.getStringWidth(var37));
         }
      }

      if (var17 != null) {
         fill(width / 2 - var16 / 2 - 1, var15 - 1, width / 2 + var16 / 2 + 1, var15 + var17.size() * this.client.textRenderer.fontHeight, Integer.MIN_VALUE);

         for(String var38 : var17) {
            int var21 = this.client.textRenderer.getStringWidth(var38);
            this.client.textRenderer.drawWithShadow(var38, (float)(width / 2 - var21 / 2), (float)var15, -1);
            var15 += this.client.textRenderer.fontHeight;
         }

         ++var15;
      }

      fill(width / 2 - var16 / 2 - 1, var15 - 1, width / 2 + var16 / 2 + 1, var15 + var29 * 9, Integer.MIN_VALUE);

      for(int var35 = 0; var35 < var28; ++var35) {
         int var39 = var35 / var29;
         int var41 = var35 % var29;
         int var22 = var14 + var39 * var13 + var39 * 5;
         int var23 = var15 + var41 * 9;
         fill(var22, var23, var22 + var13, var23 + 8, 553648127);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableAlphaTest();
         GlStateManager.disableBlend();
         GlStateManager.blendFuncSeparate(770, 771, 1, 0);
         if (var35 < var5.size()) {
            PlayerInfo var24 = (PlayerInfo)var5.get(var35);
            String var25 = this.getDisplayName(var24);
            if (var11) {
               this.client.getTextureManager().bind(var24.getSkinTexture());
               GuiElement.drawTexture(var22, var23, 8.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
               GuiElement.drawTexture(var22, var23, 40.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
               var22 += 9;
            }

            if (var24.getGameMode() == WorldSettings.GameMode.SPECTATOR) {
               var25 = Formatting.ITALIC + var25;
               this.client.textRenderer.drawWithShadow(var25, (float)var22, (float)var23, -1862270977);
            } else {
               this.client.textRenderer.drawWithShadow(var25, (float)var22, (float)var23, -1);
            }

            if (displayObjective != null && var24.getGameMode() != WorldSettings.GameMode.SPECTATOR) {
               int var26 = var22 + var6 + 1;
               int var27 = var26 + var12;
               if (var27 - var26 > 5) {
                  this.renderDisplayScore(displayObjective, var23, var24.getProfile().getName(), var26, var27);
               }
            }

            this.renderPing(var13, var22 - (var11 ? 9 : 0), var23, var24);
         }
      }

      if (var18 != null) {
         var15 += var29 * 9 + 1;
         fill(width / 2 - var16 / 2 - 1, var15 - 1, width / 2 + var16 / 2 + 1, var15 + var18.size() * this.client.textRenderer.fontHeight, Integer.MIN_VALUE);

         for(String var40 : var18) {
            int var42 = this.client.textRenderer.getStringWidth(var40);
            this.client.textRenderer.drawWithShadow(var40, (float)(width / 2 - var42 / 2), (float)var15, -1);
            var15 += this.client.textRenderer.fontHeight;
         }
      }
   }

   protected void renderPing(int width, int x, int y, PlayerInfo player) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.client.getTextureManager().bind(ICONS);
      byte var5 = 0;
      byte var6 = 0;
      if (player.getPing() < 0) {
         var6 = 5;
      } else if (player.getPing() < 150) {
         var6 = 0;
      } else if (player.getPing() < 300) {
         var6 = 1;
      } else if (player.getPing() < 600) {
         var6 = 2;
      } else if (player.getPing() < 1000) {
         var6 = 3;
      } else {
         var6 = 4;
      }

      this.drawOffset += 100.0F;
      this.drawTexture(x + width - 11, y, 0 + var5 * 10, 176 + var6 * 8, 10, 8);
      this.drawOffset -= 100.0F;
   }

   private void renderDisplayScore(ScoreboardObjective c_64nyqfgnj, int i, String string, int j, int k) {
      int var6 = c_64nyqfgnj.getScoreboard().getScore(string, c_64nyqfgnj).get();
      if (c_64nyqfgnj.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
         this.client.getTextureManager().bind(ICONS);
         int var7 = var6 / 2;
         int var8 = Math.max(MathHelper.ceil((float)(var6 / 2)), 10);
         boolean var9 = var6 % 2 != 0;
         if (var7 > 0 || var9) {
            float var10 = Math.min((float)(k - j - 4) / (float)var8, 9.0F);
            if (var10 > 3.0F) {
               for(int var11 = var7; var11 < var8; ++var11) {
                  this.drawTexture((float)j + (float)var11 * var10, (float)i, 16, 0, 9, 9);
               }

               for(int var15 = 0; var15 < var7; ++var15) {
                  this.drawTexture((float)j + (float)var15 * var10, (float)i, 16, 0, 9, 9);
                  this.drawTexture((float)j + (float)var15 * var10, (float)i, 52, 0, 9, 9);
               }

               if (var9) {
                  this.drawTexture((float)j + (float)var7 * var10, (float)i, 16, 0, 9, 9);
                  this.drawTexture((float)j + (float)var7 * var10, (float)i, 61, 0, 9, 9);
               }
            } else {
               float var16 = MathHelper.clamp((float)var6 / 20.0F, 0.0F, 1.0F);
               int var12 = (int)((1.0F - var16) * 255.0F) << 16 | (int)(var16 * 255.0F) << 8;
               String var13 = "" + (float)var6 / 2.0F;
               if (k - this.client.textRenderer.getStringWidth(var13 + "hp") >= j) {
                  var13 = var13 + "hp";
               }

               this.client.textRenderer.drawWithShadow(var13, (float)((k + j) / 2 - this.client.textRenderer.getStringWidth(var13) / 2), (float)i, var12);
            }
         }
      } else {
         String var14 = Formatting.YELLOW + "" + var6;
         this.client.textRenderer.drawWithShadow(var14, (float)(k - this.client.textRenderer.getStringWidth(var14)), (float)i, 16777215);
      }
   }

   public void setHeader(Text header) {
      this.header = header;
   }

   public void setFooter(Text footer) {
      this.footer = footer;
   }

   @Environment(EnvType.CLIENT)
   static class PlayerInfoComparator implements Comparator {
      private PlayerInfoComparator() {
      }

      public int compare(PlayerInfo c_38vawxpad, PlayerInfo c_38vawxpad2) {
         Team var3 = c_38vawxpad.getTeam();
         Team var4 = c_38vawxpad2.getTeam();
         return ComparisonChain.start()
            .compareTrueFirst(c_38vawxpad.getGameMode() != WorldSettings.GameMode.SPECTATOR, c_38vawxpad2.getGameMode() != WorldSettings.GameMode.SPECTATOR)
            .compare(var3 != null ? var3.getName() : "", var4 != null ? var4.getName() : "")
            .compare(c_38vawxpad.getProfile().getName(), c_38vawxpad2.getProfile().getName())
            .result();
      }
   }
}
