package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.resource.Identifier;
import net.minecraft.text.Formatting;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class CreditsScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Identifier MINECRAFT_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
   private static final Identifier VIGNETTE_TEXTURE = new Identifier("textures/misc/vignette.png");
   private int ticksOpen;
   private List creditTextLines;
   private int creditsHeight;
   private float speed = 0.5F;

   @Override
   public void tick() {
      ++this.ticksOpen;
      float var1 = (float)(this.creditsHeight + this.height + this.height + 24) / this.speed;
      if ((float)this.ticksOpen > var1) {
         this.close();
      }
   }

   @Override
   protected void keyPressed(char chr, int key) {
      if (key == 1) {
         this.close();
      }
   }

   private void close() {
      this.client.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Status.PERFORM_RESPAWN));
      this.client.openScreen(null);
   }

   @Override
   public boolean shouldPauseGame() {
      return true;
   }

   @Override
   public void init() {
      if (this.creditTextLines == null) {
         this.creditTextLines = Lists.newArrayList();

         try {
            String var1 = "";
            String var2 = "" + Formatting.WHITE + Formatting.OBFUSCATED + Formatting.GREEN + Formatting.AQUA;
            short var3 = 274;
            BufferedReader var4 = new BufferedReader(
               new InputStreamReader(this.client.getResourceManager().getResource(new Identifier("texts/end.txt")).asStream(), Charsets.UTF_8)
            );
            Random var5 = new Random(8124371L);

            while((var1 = var4.readLine()) != null) {
               String var7;
               String var8;
               for(var1 = var1.replaceAll("PLAYERNAME", this.client.getSession().getUsername());
                  var1.contains(var2);
                  var1 = var7 + Formatting.WHITE + Formatting.OBFUSCATED + "XXXXXXXX".substring(0, var5.nextInt(4) + 3) + var8
               ) {
                  int var6 = var1.indexOf(var2);
                  var7 = var1.substring(0, var6);
                  var8 = var1.substring(var6 + var2.length());
               }

               this.creditTextLines.addAll(this.client.textRenderer.wrapLines(var1, var3));
               this.creditTextLines.add("");
            }

            for(int var16 = 0; var16 < 8; ++var16) {
               this.creditTextLines.add("");
            }

            var4 = new BufferedReader(
               new InputStreamReader(this.client.getResourceManager().getResource(new Identifier("texts/credits.txt")).asStream(), Charsets.UTF_8)
            );

            while((var1 = var4.readLine()) != null) {
               var1 = var1.replaceAll("PLAYERNAME", this.client.getSession().getUsername());
               var1 = var1.replaceAll("\t", "    ");
               this.creditTextLines.addAll(this.client.textRenderer.wrapLines(var1, var3));
               this.creditTextLines.add("");
            }

            this.creditsHeight = this.creditTextLines.size() * 12;
         } catch (Exception var9) {
            LOGGER.error("Couldn't load credits", var9);
         }
      }
   }

   private void renderBackground(int mouseX, int mouseY, float tickDelta) {
      Tessellator var4 = Tessellator.getInstance();
      BufferBuilder var5 = var4.getBufferBuilder();
      this.client.getTextureManager().bind(GuiElement.OPTIONS_BACKGROUND);
      var5.start();
      var5.color(1.0F, 1.0F, 1.0F, 1.0F);
      int var6 = this.titleWidth;
      float var7 = 0.0F - ((float)this.ticksOpen + tickDelta) * 0.5F * this.speed;
      float var8 = (float)this.height - ((float)this.ticksOpen + tickDelta) * 0.5F * this.speed;
      float var9 = 0.015625F;
      float var10 = ((float)this.ticksOpen + tickDelta - 0.0F) * 0.02F;
      float var11 = (float)(this.creditsHeight + this.height + this.height + 24) / this.speed;
      float var12 = (var11 - 20.0F - ((float)this.ticksOpen + tickDelta)) * 0.005F;
      if (var12 < var10) {
         var10 = var12;
      }

      if (var10 > 1.0F) {
         var10 = 1.0F;
      }

      var10 *= var10;
      var10 = var10 * 96.0F / 255.0F;
      var5.color(var10, var10, var10);
      var5.vertex(0.0, (double)this.height, (double)this.drawOffset, 0.0, (double)(var7 * var9));
      var5.vertex((double)var6, (double)this.height, (double)this.drawOffset, (double)((float)var6 * var9), (double)(var7 * var9));
      var5.vertex((double)var6, 0.0, (double)this.drawOffset, (double)((float)var6 * var9), (double)(var8 * var9));
      var5.vertex(0.0, 0.0, (double)this.drawOffset, 0.0, (double)(var8 * var9));
      var4.end();
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground(mouseX, mouseY, tickDelta);
      Tessellator var4 = Tessellator.getInstance();
      BufferBuilder var5 = var4.getBufferBuilder();
      short var6 = 274;
      int var7 = this.titleWidth / 2 - var6 / 2;
      int var8 = this.height + 50;
      float var9 = -((float)this.ticksOpen + tickDelta) * this.speed;
      GlStateManager.pushMatrix();
      GlStateManager.translatef(0.0F, var9, 0.0F);
      this.client.getTextureManager().bind(MINECRAFT_TEXTURE);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.drawTexture(var7, var8, 0, 0, 155, 44);
      this.drawTexture(var7 + 155, var8, 0, 45, 155, 44);
      var5.color(16777215);
      int var10 = var8 + 200;

      for(int var11 = 0; var11 < this.creditTextLines.size(); ++var11) {
         if (var11 == this.creditTextLines.size() - 1) {
            float var12 = (float)var10 + var9 - (float)(this.height / 2 - 6);
            if (var12 < 0.0F) {
               GlStateManager.translatef(0.0F, -var12, 0.0F);
            }
         }

         if ((float)var10 + var9 + 12.0F + 8.0F > 0.0F && (float)var10 + var9 < (float)this.height) {
            String var14 = (String)this.creditTextLines.get(var11);
            if (var14.startsWith("[C]")) {
               this.textRenderer
                  .drawWithShadow(var14.substring(3), (float)(var7 + (var6 - this.textRenderer.getStringWidth(var14.substring(3))) / 2), (float)var10, 16777215);
            } else {
               this.textRenderer.random.setSeed((long)var11 * 4238972211L + (long)(this.ticksOpen / 4));
               this.textRenderer.drawWithShadow(var14, (float)var7, (float)var10, 16777215);
            }
         }

         var10 += 12;
      }

      GlStateManager.popMatrix();
      this.client.getTextureManager().bind(VIGNETTE_TEXTURE);
      GlStateManager.disableBlend();
      GlStateManager.blendFunc(0, 769);
      var5.start();
      var5.color(1.0F, 1.0F, 1.0F, 1.0F);
      int var13 = this.titleWidth;
      int var15 = this.height;
      var5.vertex(0.0, (double)var15, (double)this.drawOffset, 0.0, 1.0);
      var5.vertex((double)var13, (double)var15, (double)this.drawOffset, 1.0, 1.0);
      var5.vertex((double)var13, 0.0, (double)this.drawOffset, 1.0, 0.0);
      var5.vertex(0.0, 0.0, (double)this.drawOffset, 0.0, 0.0);
      var4.end();
      GlStateManager.enableBlend();
      super.render(mouseX, mouseY, tickDelta);
   }
}
