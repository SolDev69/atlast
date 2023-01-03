package net.minecraft.client.gui.widget;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.image.BufferedImage;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.options.ServerListEntry;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.Identifier;
import net.minecraft.text.Formatting;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ServerListEntryWidget implements EntryListWidget.Entry {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(
      5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).build()
   );
   private static final Identifier f_46nmybnwg = new Identifier("textures/misc/unknown_server.png");
   private static final Identifier f_51ovrpmvn = new Identifier("textures/gui/server_selection.png");
   private final MultiplayerScreen screen;
   private final MinecraftClient client;
   private final ServerListEntry entry;
   private final Identifier iconIdentifier;
   private String icon;
   private NativeImageBackedTexture iconTexture;
   private long f_43jysdtxi;

   protected ServerListEntryWidget(MultiplayerScreen screen, ServerListEntry entry) {
      this.screen = screen;
      this.entry = entry;
      this.client = MinecraftClient.getInstance();
      this.iconIdentifier = new Identifier("servers/" + entry.address + "/icon");
      this.iconTexture = (NativeImageBackedTexture)this.client.getTextureManager().getTexture(this.iconIdentifier);
   }

   @Override
   public void render(int id, int x, int y, int width, int height, int bufferBuilder, int mouseX, boolean mouseY) {
      if (!this.entry.isLoaded) {
         this.entry.isLoaded = true;
         this.entry.ping = -2L;
         this.entry.description = "";
         this.entry.onlinePlayers = "";
         EXECUTOR.submit(new Runnable() {
            @Override
            public void run() {
               try {
                  ServerListEntryWidget.this.screen.getServerListPinger().add(ServerListEntryWidget.this.entry);
               } catch (UnknownHostException var2) {
                  ServerListEntryWidget.this.entry.ping = -1L;
                  ServerListEntryWidget.this.entry.description = Formatting.DARK_RED + "Can't resolve hostname";
               } catch (Exception var3) {
                  ServerListEntryWidget.this.entry.ping = -1L;
                  ServerListEntryWidget.this.entry.description = Formatting.DARK_RED + "Can't connect to server.";
               }
            }
         });
      }

      boolean var9 = this.entry.protocol > 31;
      boolean var10 = this.entry.protocol < 31;
      boolean var11 = var9 || var10;
      this.client.textRenderer.drawWithoutShadow(this.entry.name, x + 32 + 3, y + 1, 16777215);
      List var12 = this.client.textRenderer.wrapLines(this.entry.description, width - 32 - 2);

      for(int var13 = 0; var13 < Math.min(var12.size(), 2); ++var13) {
         this.client.textRenderer.drawWithoutShadow((String)var12.get(var13), x + 32 + 3, y + 12 + this.client.textRenderer.fontHeight * var13, 8421504);
      }

      String var23 = var11 ? Formatting.DARK_RED + this.entry.version : this.entry.onlinePlayers;
      int var14 = this.client.textRenderer.getStringWidth(var23);
      this.client.textRenderer.drawWithoutShadow(var23, x + width - var14 - 15 - 2, y + 1, 8421504);
      byte var15 = 0;
      String var17 = null;
      int var16;
      String var18;
      if (var11) {
         var16 = 5;
         var18 = var9 ? "Client out of date!" : "Server out of date!";
         var17 = this.entry.playerListString;
      } else if (this.entry.isLoaded && this.entry.ping != -2L) {
         if (this.entry.ping < 0L) {
            var16 = 5;
         } else if (this.entry.ping < 150L) {
            var16 = 0;
         } else if (this.entry.ping < 300L) {
            var16 = 1;
         } else if (this.entry.ping < 600L) {
            var16 = 2;
         } else if (this.entry.ping < 1000L) {
            var16 = 3;
         } else {
            var16 = 4;
         }

         if (this.entry.ping < 0L) {
            var18 = "(no connection)";
         } else {
            var18 = this.entry.ping + "ms";
            var17 = this.entry.playerListString;
         }
      } else {
         var15 = 1;
         var16 = (int)(MinecraftClient.getTime() / 100L + (long)(id * 2) & 7L);
         if (var16 > 4) {
            var16 = 8 - var16;
         }

         var18 = "Pinging...";
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.client.getTextureManager().bind(GuiElement.ICONS);
      GuiElement.drawTexture(x + width - 15, y, (float)(var15 * 10), (float)(176 + var16 * 8), 10, 8, 256.0F, 256.0F);
      if (this.entry.getIcon() != null && !this.entry.getIcon().equals(this.icon)) {
         this.icon = this.entry.getIcon();
         this.loadServerIcon();
         this.screen.getServerList().save();
      }

      if (this.iconTexture != null) {
         this.m_68sepivzw(x, y, this.iconIdentifier);
      } else {
         this.m_68sepivzw(x, y, f_46nmybnwg);
      }

      int var19 = bufferBuilder - x;
      int var20 = mouseX - y;
      if (var19 >= width - 15 && var19 <= width - 5 && var20 >= 0 && var20 <= 8) {
         this.screen.setTooltip(var18);
      } else if (var19 >= width - var14 - 15 - 2 && var19 <= width - 15 - 2 && var20 >= 0 && var20 <= 8) {
         this.screen.setTooltip(var17);
      }

      if (this.client.options.touchscreen || mouseY) {
         this.client.getTextureManager().bind(f_51ovrpmvn);
         GuiElement.fill(x, y, x + 32, y + 32, -1601138544);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         int var21 = bufferBuilder - x;
         int var22 = mouseX - y;
         if (this.m_00kdnqqbx()) {
            if (var21 < 32 && var21 > 16) {
               GuiElement.drawTexture(x, y, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               GuiElement.drawTexture(x, y, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }

         if (this.screen.m_48cnpmgoy(this, id)) {
            if (var21 < 16 && var22 < 16) {
               GuiElement.drawTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               GuiElement.drawTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }

         if (this.screen.m_47mxrsgpt(this, id)) {
            if (var21 < 16 && var22 > 16) {
               GuiElement.drawTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               GuiElement.drawTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }
      }
   }

   protected void m_68sepivzw(int i, int j, Identifier c_07ipdbewr) {
      this.client.getTextureManager().bind(c_07ipdbewr);
      GlStateManager.disableBlend();
      GuiElement.drawTexture(i, j, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
      GlStateManager.enableBlend();
   }

   private boolean m_00kdnqqbx() {
      return true;
   }

   private void loadServerIcon() {
      if (this.entry.getIcon() == null) {
         this.client.getTextureManager().close(this.iconIdentifier);
         this.iconTexture = null;
      } else {
         ByteBuf var2 = Unpooled.copiedBuffer(this.entry.getIcon(), Charsets.UTF_8);
         ByteBuf var3 = Base64.decode(var2);

         BufferedImage var1;
         label62: {
            try {
               var1 = TextureUtil.readImage(new ByteBufInputStream(var3));
               Validate.validState(var1.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
               Validate.validState(var1.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
               break label62;
            } catch (Exception var8) {
               LOGGER.error("Invalid icon for server " + this.entry.name + " (" + this.entry.address + ")", var8);
               this.entry.setIcon(null);
            } finally {
               var2.release();
               var3.release();
            }

            return;
         }

         if (this.iconTexture == null) {
            this.iconTexture = new NativeImageBackedTexture(var1.getWidth(), var1.getHeight());
            this.client.getTextureManager().register(this.iconIdentifier, this.iconTexture);
         }

         var1.getRGB(0, 0, var1.getWidth(), var1.getHeight(), this.iconTexture.getRgbArray(), 0, var1.getWidth());
         this.iconTexture.upload();
      }
   }

   @Override
   public boolean mouseClicked(int id, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
      if (entryMouseX <= 32) {
         if (entryMouseX < 32 && entryMouseX > 16 && this.m_00kdnqqbx()) {
            this.screen.moveToServer(id);
            this.screen.connect();
            return true;
         }

         if (entryMouseX < 16 && entryMouseY < 16 && this.screen.m_48cnpmgoy(this, id)) {
            this.screen.m_19quqxqzd(this, id, Screen.isShiftDown());
            return true;
         }

         if (entryMouseX < 16 && entryMouseY > 16 && this.screen.m_47mxrsgpt(this, id)) {
            this.screen.m_76ikivfmk(this, id, Screen.isShiftDown());
            return true;
         }
      }

      this.screen.moveToServer(id);
      if (MinecraftClient.getTime() - this.f_43jysdtxi < 250L) {
         this.screen.connect();
      }

      this.f_43jysdtxi = MinecraftClient.getTime();
      return false;
   }

   @Override
   public void m_82anuocxe(int i, int j, int k) {
   }

   @Override
   public void mouseReleased(int id, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
   }

   public ServerListEntry fetchServer() {
      return this.entry;
   }
}
