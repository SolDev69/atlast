package net.minecraft.client.gui.screen;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.C_16fhxekln;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LabelWidget;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.Entities;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.stat.achievement.AchievementStat;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Formatting;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tv.twitch.chat.ChatUserInfo;

@Environment(EnvType.CLIENT)
public abstract class Screen extends GuiElement implements ConfirmationListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Set HYPERTEXT_TRANSFER_PROTOCOLS = Sets.newHashSet(new String[]{"http", "https"});
   private static final Splitter LINE_SPLITTER = Splitter.on('\n');
   protected MinecraftClient client;
   protected ItemRenderer itemRenderer;
   public int titleWidth;
   public int height;
   protected List buttons = Lists.newArrayList();
   protected List labels = Lists.newArrayList();
   public boolean passEvents;
   protected TextRenderer textRenderer;
   private ButtonWidget lastClickedButton;
   private int lastButton;
   private long lastUpdateTime;
   private int multiTouch;
   private URI link;

   public void resize(MinecraftClient client, int width, int height) {
      this.init(client, width, height);
   }

   public void render(int mouseX, int mouseY, float tickDelta) {
      for(int var4 = 0; var4 < this.buttons.size(); ++var4) {
         ((ButtonWidget)this.buttons.get(var4)).render(this.client, mouseX, mouseY);
      }

      for(int var5 = 0; var5 < this.labels.size(); ++var5) {
         ((LabelWidget)this.labels.get(var5)).render(this.client, mouseX, mouseY);
      }
   }

   protected void keyPressed(char chr, int key) {
      if (key == 1) {
         this.client.openScreen(null);
         if (this.client.currentScreen == null) {
            this.client.closeScreen();
         }
      }
   }

   public static String getClipboard() {
      try {
         Transferable var0 = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
         if (var0 != null && var0.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return (String)var0.getTransferData(DataFlavor.stringFlavor);
         }
      } catch (Exception var1) {
      }

      return "";
   }

   public static void setClipboard(String text) {
      try {
         StringSelection var1 = new StringSelection(text);
         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(var1, null);
      } catch (Exception var2) {
      }
   }

   protected void renderTooltip(ItemStack stack, int x, int y) {
      List var4 = stack.getTooltip(this.client.player, this.client.options.advancedItemTooltips);

      for(int var5 = 0; var5 < var4.size(); ++var5) {
         if (var5 == 0) {
            var4.set(var5, stack.getRarity().formatting + (String)var4.get(var5));
         } else {
            var4.set(var5, Formatting.GRAY + (String)var4.get(var5));
         }
      }

      this.renderTooltip(var4, x, y);
   }

   protected void renderTooltip(String text, int x, int y) {
      this.renderTooltip(Arrays.asList(text), x, y);
   }

   protected void renderTooltip(List text, int x, int y) {
      if (!text.isEmpty()) {
         GlStateManager.disableRescaleNormal();
         Lighting.turnOff();
         GlStateManager.disableLighting();
         GlStateManager.enableDepth();
         int var4 = 0;

         for(String var6 : text) {
            int var7 = this.textRenderer.getStringWidth(var6);
            if (var7 > var4) {
               var4 = var7;
            }
         }

         int var14 = x + 12;
         int var15 = y - 12;
         int var8 = 8;
         if (text.size() > 1) {
            var8 += 2 + (text.size() - 1) * 10;
         }

         if (var14 + var4 > this.titleWidth) {
            var14 -= 28 + var4;
         }

         if (var15 + var8 + 6 > this.height) {
            var15 = this.height - var8 - 6;
         }

         this.drawOffset = 300.0F;
         this.itemRenderer.zOffset = 300.0F;
         int var9 = -267386864;
         this.fillGradient(var14 - 3, var15 - 4, var14 + var4 + 3, var15 - 3, var9, var9);
         this.fillGradient(var14 - 3, var15 + var8 + 3, var14 + var4 + 3, var15 + var8 + 4, var9, var9);
         this.fillGradient(var14 - 3, var15 - 3, var14 + var4 + 3, var15 + var8 + 3, var9, var9);
         this.fillGradient(var14 - 4, var15 - 3, var14 - 3, var15 + var8 + 3, var9, var9);
         this.fillGradient(var14 + var4 + 3, var15 - 3, var14 + var4 + 4, var15 + var8 + 3, var9, var9);
         int var10 = 1347420415;
         int var11 = (var10 & 16711422) >> 1 | var10 & 0xFF000000;
         this.fillGradient(var14 - 3, var15 - 3 + 1, var14 - 3 + 1, var15 + var8 + 3 - 1, var10, var11);
         this.fillGradient(var14 + var4 + 2, var15 - 3 + 1, var14 + var4 + 3, var15 + var8 + 3 - 1, var10, var11);
         this.fillGradient(var14 - 3, var15 - 3, var14 + var4 + 3, var15 - 3 + 1, var10, var10);
         this.fillGradient(var14 - 3, var15 + var8 + 2, var14 + var4 + 3, var15 + var8 + 3, var11, var11);

         for(int var12 = 0; var12 < text.size(); ++var12) {
            String var13 = (String)text.get(var12);
            this.textRenderer.drawWithShadow(var13, (float)var14, (float)var15, -1);
            if (var12 == 0) {
               var15 += 2;
            }

            var15 += 10;
         }

         this.drawOffset = 0.0F;
         this.itemRenderer.zOffset = 0.0F;
         GlStateManager.enableLighting();
         GlStateManager.disableDepth();
         Lighting.turnOn();
         GlStateManager.enableRescaleNormal();
      }
   }

   protected void m_44svopzkj(Text c_21uoltggz, int i, int j) {
      if (c_21uoltggz != null && c_21uoltggz.getStyle().getHoverEvent() != null) {
         HoverEvent var4 = c_21uoltggz.getStyle().getHoverEvent();
         if (var4.getAction() == HoverEvent.Action.SHOW_ITEM) {
            ItemStack var5 = null;

            try {
               NbtCompound var6 = StringNbtReader.parse(var4.getValue().buildString());
               if (var6 instanceof NbtCompound) {
                  var5 = ItemStack.fromNbt(var6);
               }
            } catch (NbtException var11) {
            }

            if (var5 != null) {
               this.renderTooltip(var5, i, j);
            } else {
               this.renderTooltip(Formatting.RED + "Invalid Item!", i, j);
            }
         } else if (var4.getAction() == HoverEvent.Action.SHOW_ENTITY) {
            if (this.client.options.advancedItemTooltips) {
               try {
                  NbtCompound var12 = StringNbtReader.parse(var4.getValue().buildString());
                  if (var12 instanceof NbtCompound) {
                     ArrayList var14 = Lists.newArrayList();
                     NbtCompound var7 = var12;
                     var14.add(var7.getString("name"));
                     if (var7.isType("type", 8)) {
                        String var8 = var7.getString("type");
                        var14.add("Type: " + var8 + " (" + Entities.getRawId(var8) + ")");
                     }

                     var14.add(var7.getString("id"));
                     this.renderTooltip(var14, i, j);
                  } else {
                     this.renderTooltip(Formatting.RED + "Invalid Entity!", i, j);
                  }
               } catch (NbtException var10) {
                  this.renderTooltip(Formatting.RED + "Invalid Entity!", i, j);
               }
            }
         } else if (var4.getAction() == HoverEvent.Action.SHOW_TEXT) {
            this.renderTooltip(LINE_SPLITTER.splitToList(var4.getValue().buildFormattedString()), i, j);
         } else if (var4.getAction() == HoverEvent.Action.SHOW_ACHIEVEMENT) {
            Stat var13 = Stats.get(var4.getValue().buildString());
            if (var13 != null) {
               Text var15 = var13.getDecoratedName();
               TranslatableText var16 = new TranslatableText("stats.tooltip.type." + (var13.isAchievement() ? "achievement" : "statistic"));
               var16.getStyle().setItalic(true);
               String var17 = var13 instanceof AchievementStat ? ((AchievementStat)var13).getDescription() : null;
               ArrayList var9 = Lists.newArrayList(new String[]{var15.buildFormattedString(), var16.buildFormattedString()});
               if (var17 != null) {
                  var9.addAll(this.textRenderer.wrapLines(var17, 150));
               }

               this.renderTooltip(var9, i, j);
            } else {
               this.renderTooltip(Formatting.RED + "Invalid statistic/achievement!", i, j);
            }
         }

         GlStateManager.disableLighting();
      }
   }

   protected void m_01hkthhvu(String string, boolean bl) {
   }

   protected boolean m_35nuugpna(Text c_21uoltggz) {
      if (c_21uoltggz == null) {
         return false;
      } else {
         ClickEvent var2 = c_21uoltggz.getStyle().getClickEvent();
         if (isShiftDown()) {
            if (c_21uoltggz.getStyle().getInsertion() != null) {
               this.m_01hkthhvu(c_21uoltggz.getStyle().getInsertion(), false);
            }
         } else if (var2 != null) {
            if (var2.getAction() == ClickEvent.Action.OPEN_URL) {
               if (!this.client.options.chatLinks) {
                  return false;
               }

               try {
                  URI var3 = new URI(var2.getValue());
                  if (!HYPERTEXT_TRANSFER_PROTOCOLS.contains(var3.getScheme().toLowerCase())) {
                     throw new URISyntaxException(var2.getValue(), "Unsupported protocol: " + var3.getScheme().toLowerCase());
                  }

                  if (this.client.options.promptChatLinks) {
                     this.link = var3;
                     this.client.openScreen(new ConfirmChatLinkScreen(this, var2.getValue(), 31102009, false));
                  } else {
                     this.openLink(var3);
                  }
               } catch (URISyntaxException var4) {
                  LOGGER.error("Can't open url for " + var2, var4);
               }
            } else if (var2.getAction() == ClickEvent.Action.OPEN_FILE) {
               URI var5 = new File(var2.getValue()).toURI();
               this.openLink(var5);
            } else if (var2.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
               this.m_01hkthhvu(var2.getValue(), true);
            } else if (var2.getAction() == ClickEvent.Action.RUN_COMMAND) {
               this.m_99vjzrgce(var2.getValue(), false);
            } else if (var2.getAction() == ClickEvent.Action.TWITCH_USER_INFO) {
               ChatUserInfo var6 = this.client.getTwitchStream().m_11dhevjwi(var2.getValue());
               if (var6 != null) {
                  this.client.openScreen(new C_16fhxekln(this.client.getTwitchStream(), var6));
               } else {
                  LOGGER.error("Tried to handle twitch user but couldn't find them!");
               }
            } else {
               LOGGER.error("Don't know how to handle " + var2);
            }

            return true;
         }

         return false;
      }
   }

   public void m_03juvdrce(String string) {
      this.m_99vjzrgce(string, true);
   }

   public void m_99vjzrgce(String string, boolean bl) {
      if (bl) {
         this.client.gui.getChat().addRecentMessage(string);
      }

      this.client.player.sendChat(string);
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 0) {
         for(int var4 = 0; var4 < this.buttons.size(); ++var4) {
            ButtonWidget var5 = (ButtonWidget)this.buttons.get(var4);
            if (var5.isMouseOver(this.client, mouseX, mouseY)) {
               this.lastClickedButton = var5;
               var5.playDownSound(this.client.getSoundManager());
               this.buttonClicked(var5);
            }
         }
      }
   }

   protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
      if (this.lastClickedButton != null && mouseButton == 0) {
         this.lastClickedButton.mouseReleased(mouseX, mouseY);
         this.lastClickedButton = null;
      }
   }

   protected void mouseDragged(int mouseX, int mouseY, int mouseButton, long duration) {
   }

   protected void buttonClicked(ButtonWidget buttonWidget) {
   }

   public void init(MinecraftClient client, int width, int height) {
      this.client = client;
      this.itemRenderer = client.getItemRenderer();
      this.textRenderer = client.textRenderer;
      this.titleWidth = width;
      this.height = height;
      this.buttons.clear();
      this.init();
   }

   public void init() {
   }

   public void handleInputs() {
      if (Mouse.isCreated()) {
         while(Mouse.next()) {
            this.handleMouse();
         }
      }

      if (Keyboard.isCreated()) {
         while(Keyboard.next()) {
            this.handleKeyboard();
         }
      }
   }

   public void handleMouse() {
      int var1 = Mouse.getEventX() * this.titleWidth / this.client.width;
      int var2 = this.height - Mouse.getEventY() * this.height / this.client.height - 1;
      int var3 = Mouse.getEventButton();
      if (Mouse.getEventButtonState()) {
         if (this.client.options.touchscreen && this.multiTouch++ > 0) {
            return;
         }

         this.lastButton = var3;
         this.lastUpdateTime = MinecraftClient.getTime();
         this.mouseClicked(var1, var2, this.lastButton);
      } else if (var3 != -1) {
         if (this.client.options.touchscreen && --this.multiTouch > 0) {
            return;
         }

         this.lastButton = -1;
         this.mouseReleased(var1, var2, var3);
      } else if (this.lastButton != -1 && this.lastUpdateTime > 0L) {
         long var4 = MinecraftClient.getTime() - this.lastUpdateTime;
         this.mouseDragged(var1, var2, this.lastButton, var4);
      }
   }

   public void handleKeyboard() {
      if (Keyboard.getEventKeyState()) {
         this.keyPressed(Keyboard.getEventCharacter(), Keyboard.getEventKey());
      }

      this.client.handleKeyBindings();
   }

   public void tick() {
   }

   public void removed() {
   }

   public void renderBackground() {
      this.renderBackground(0);
   }

   public void renderBackground(int offset) {
      if (this.client.world != null) {
         this.fillGradient(0, 0, this.titleWidth, this.height, -1072689136, -804253680);
      } else {
         this.drawBackgroundTexture(offset);
      }
   }

   public void drawBackgroundTexture(int offset) {
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      Tessellator var2 = Tessellator.getInstance();
      BufferBuilder var3 = var2.getBufferBuilder();
      this.client.getTextureManager().bind(OPTIONS_BACKGROUND);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float var4 = 32.0F;
      var3.start();
      var3.color(4210752);
      var3.vertex(0.0, (double)this.height, 0.0, 0.0, (double)((float)this.height / var4 + (float)offset));
      var3.vertex(
         (double)this.titleWidth, (double)this.height, 0.0, (double)((float)this.titleWidth / var4), (double)((float)this.height / var4 + (float)offset)
      );
      var3.vertex((double)this.titleWidth, 0.0, 0.0, (double)((float)this.titleWidth / var4), (double)offset);
      var3.vertex(0.0, 0.0, 0.0, 0.0, (double)offset);
      var2.end();
   }

   public boolean shouldPauseGame() {
      return true;
   }

   @Override
   public void confirmResult(boolean result, int id) {
      if (id == 31102009) {
         if (result) {
            this.openLink(this.link);
         }

         this.link = null;
         this.client.openScreen(this);
      }
   }

   private void openLink(URI link) {
      try {
         Class var2 = Class.forName("java.awt.Desktop");
         Object var3 = var2.getMethod("getDesktop").invoke(null);
         var2.getMethod("browse", URI.class).invoke(var3, link);
      } catch (Throwable var4) {
         LOGGER.error("Couldn't open link", var4);
      }
   }

   public static boolean isControlDown() {
      if (MinecraftClient.IS_MAC) {
         return Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220);
      } else {
         return Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
      }
   }

   public static boolean isShiftDown() {
      return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
   }

   public static boolean isAltDown() {
      return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
   }
}
