package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.TextRenderUtils;
import net.minecraft.client.render.Window;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ChatGui extends GuiElement {
   private static final Logger LOGGER = LogManager.getLogger();
   private final MinecraftClient client;
   private final List recentMessages = Lists.newArrayList();
   private final List messages = Lists.newArrayList();
   private final List trimmedMessages = Lists.newArrayList();
   private int scroll;
   private boolean hasNewMessagesSinceScroll;

   public ChatGui(MinecraftClient client) {
      this.client = client;
   }

   public void render(int ticks) {
      if (this.client.options.chatVisibility != PlayerEntity.ChatVisibility.HIDDEN) {
         int var2 = this.getVisibleLineCount();
         boolean var3 = false;
         int var4 = 0;
         int var5 = this.trimmedMessages.size();
         float var6 = this.client.options.chatOpacity * 0.9F + 0.1F;
         if (var5 > 0) {
            if (this.isChatFocused()) {
               var3 = true;
            }

            float var7 = this.getChatScale();
            int var8 = MathHelper.ceil((float)this.getWidth() / var7);
            GlStateManager.pushMatrix();
            GlStateManager.translatef(2.0F, 20.0F, 0.0F);
            GlStateManager.scalef(var7, var7, 1.0F);

            for(int var9 = 0; var9 + this.scroll < this.trimmedMessages.size() && var9 < var2; ++var9) {
               ChatMessage var10 = (ChatMessage)this.trimmedMessages.get(var9 + this.scroll);
               if (var10 != null) {
                  int var11 = ticks - var10.getTimeOfCreation();
                  if (var11 < 200 || var3) {
                     double var12 = (double)var11 / 200.0;
                     var12 = 1.0 - var12;
                     var12 *= 10.0;
                     var12 = MathHelper.clamp(var12, 0.0, 1.0);
                     var12 *= var12;
                     int var14 = (int)(255.0 * var12);
                     if (var3) {
                        var14 = 255;
                     }

                     var14 = (int)((float)var14 * var6);
                     ++var4;
                     if (var14 > 3) {
                        byte var15 = 0;
                        int var16 = -var9 * 9;
                        fill(var15, var16 - 9, var15 + var8 + 4, var16, var14 / 2 << 24);
                        String var17 = var10.getText().buildFormattedString();
                        this.client.textRenderer.drawWithShadow(var17, (float)var15, (float)(var16 - 8), 16777215 + (var14 << 24));
                        GlStateManager.disableAlphaTest();
                     }
                  }
               }
            }

            if (var3) {
               int var18 = this.client.textRenderer.fontHeight;
               GlStateManager.translatef(-3.0F, 0.0F, 0.0F);
               int var19 = var5 * var18 + var5;
               int var20 = var4 * var18 + var4;
               int var25 = this.scroll * var20 / var5;
               int var13 = var20 * var20 / var19;
               if (var19 != var20) {
                  int var27 = var25 > 0 ? 170 : 96;
                  int var28 = this.hasNewMessagesSinceScroll ? 13382451 : 3355562;
                  fill(0, -var25, 2, -var25 - var13, var28 + (var27 << 24));
                  fill(2, -var25, 1, -var25 - var13, 13421772 + (var27 << 24));
               }
            }

            GlStateManager.popMatrix();
         }
      }
   }

   public void clear() {
      this.trimmedMessages.clear();
      this.messages.clear();
      this.recentMessages.clear();
   }

   public void addMessage(Text message) {
      this.addMessage(message, 0);
   }

   public void addMessage(Text message, int id) {
      this.addMessage(message, id, this.client.gui.getTicks(), false);
      LOGGER.info("[CHAT] " + message.buildString());
   }

   private void addMessage(Text message, int id, int time, boolean deleted) {
      if (id != 0) {
         this.removeMessage(id);
      }

      int var5 = MathHelper.floor((float)this.getWidth() / this.getChatScale());
      List var6 = TextRenderUtils.wrapText(message, var5, this.client.textRenderer, false, false);
      boolean var7 = this.isChatFocused();

      for(Text var9 : var6) {
         if (var7 && this.scroll > 0) {
            this.hasNewMessagesSinceScroll = true;
            this.scroll(1);
         }

         this.trimmedMessages.add(0, new ChatMessage(time, var9, id));
      }

      while(this.trimmedMessages.size() > 100) {
         this.trimmedMessages.remove(this.trimmedMessages.size() - 1);
      }

      if (!deleted) {
         this.messages.add(0, new ChatMessage(time, message, id));

         while(this.messages.size() > 100) {
            this.messages.remove(this.messages.size() - 1);
         }
      }
   }

   public void reset() {
      this.trimmedMessages.clear();
      this.resetScroll();

      for(int var1 = this.messages.size() - 1; var1 >= 0; --var1) {
         ChatMessage var2 = (ChatMessage)this.messages.get(var1);
         this.addMessage(var2.getText(), var2.getId(), var2.getTimeOfCreation(), true);
      }
   }

   public List getRecentMessages() {
      return this.recentMessages;
   }

   public void addRecentMessage(String message) {
      if (this.recentMessages.isEmpty() || !((String)this.recentMessages.get(this.recentMessages.size() - 1)).equals(message)) {
         this.recentMessages.add(message);
      }
   }

   public void resetScroll() {
      this.scroll = 0;
      this.hasNewMessagesSinceScroll = false;
   }

   public void scroll(int lines) {
      this.scroll += lines;
      int var2 = this.trimmedMessages.size();
      if (this.scroll > var2 - this.getVisibleLineCount()) {
         this.scroll = var2 - this.getVisibleLineCount();
      }

      if (this.scroll <= 0) {
         this.scroll = 0;
         this.hasNewMessagesSinceScroll = false;
      }
   }

   public Text getMessageAt(int x, int y) {
      if (!this.isChatFocused()) {
         return null;
      } else {
         Window var3 = new Window(this.client, this.client.width, this.client.height);
         int var4 = var3.getScale();
         float var5 = this.getChatScale();
         int var6 = x / var4 - 3;
         int var7 = y / var4 - 27;
         var6 = MathHelper.floor((float)var6 / var5);
         var7 = MathHelper.floor((float)var7 / var5);
         if (var6 >= 0 && var7 >= 0) {
            int var8 = Math.min(this.getVisibleLineCount(), this.trimmedMessages.size());
            if (var6 <= MathHelper.floor((float)this.getWidth() / this.getChatScale()) && var7 < this.client.textRenderer.fontHeight * var8 + var8) {
               int var9 = var7 / this.client.textRenderer.fontHeight + this.scroll;
               if (var9 >= 0 && var9 < this.trimmedMessages.size()) {
                  ChatMessage var10 = (ChatMessage)this.trimmedMessages.get(var9);
                  int var11 = 0;

                  for(Text var13 : var10.getText()) {
                     if (var13 instanceof LiteralText) {
                        var11 += this.client.textRenderer.getStringWidth(TextRenderUtils.prepareText(((LiteralText)var13).getRawString(), false));
                        if (var11 > var6) {
                           return var13;
                        }
                     }
                  }
               }

               return null;
            } else {
               return null;
            }
         } else {
            return null;
         }
      }
   }

   public boolean isChatFocused() {
      return this.client.currentScreen instanceof ChatScreen;
   }

   public void removeMessage(int id) {
      Iterator var2 = this.trimmedMessages.iterator();

      while(var2.hasNext()) {
         ChatMessage var3 = (ChatMessage)var2.next();
         if (var3.getId() == id) {
            var2.remove();
         }
      }

      var2 = this.messages.iterator();

      while(var2.hasNext()) {
         ChatMessage var5 = (ChatMessage)var2.next();
         if (var5.getId() == id) {
            var2.remove();
            break;
         }
      }
   }

   public int getWidth() {
      return getWidth(this.client.options.chatWidth);
   }

   public int getHeight() {
      return getHeight(this.isChatFocused() ? this.client.options.focusedChatHeight : this.client.options.unfocusedChatHeight);
   }

   public float getChatScale() {
      return this.client.options.chatScale;
   }

   public static int getWidth(float chatWidth) {
      short var1 = 320;
      byte var2 = 40;
      return MathHelper.floor(chatWidth * (float)(var1 - var2) + (float)var2);
   }

   public static int getHeight(float chatHeight) {
      short var1 = 180;
      byte var2 = 20;
      return MathHelper.floor(chatHeight * (float)(var1 - var2) + (float)var2);
   }

   public int getVisibleLineCount() {
      return this.getHeight() / 9;
   }
}
