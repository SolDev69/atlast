package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ConfirmationListener;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DirectConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.menu.AddServerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.LanServerEntry;
import net.minecraft.client.gui.widget.ServerListEntryWidget;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.options.ServerList;
import net.minecraft.client.options.ServerListEntry;
import net.minecraft.client.resource.language.I18n;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

@Environment(EnvType.CLIENT)
public class MultiplayerScreen extends Screen implements ConfirmationListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final MultiplayerServerListPinger pinger = new MultiplayerServerListPinger();
   private Screen parent;
   private MultiplayerServerListWidget serverListWidget;
   private ServerList serverList;
   private ButtonWidget editButton;
   private ButtonWidget joinButton;
   private ButtonWidget deleteButton;
   private boolean deleteServerConfirmationDialogOpen;
   private boolean addServerDialogOpen;
   private boolean editServerDialogOpen;
   private boolean serverOpen;
   private String tooltipText;
   private ServerListEntry serverEntry;
   private LanServerQueryManager.LanServerList lanServerList;
   private LanServerQueryManager.LanServerDetector lanServerDetector;
   private boolean initialized;

   public MultiplayerScreen(Screen parent) {
      this.parent = parent;
   }

   @Override
   public void init() {
      Keyboard.enableRepeatEvents(true);
      this.buttons.clear();
      if (!this.initialized) {
         this.initialized = true;
         this.serverList = new ServerList(this.client);
         this.serverList.load();
         this.lanServerList = new LanServerQueryManager.LanServerList();

         try {
            this.lanServerDetector = new LanServerQueryManager.LanServerDetector(this.lanServerList);
            this.lanServerDetector.start();
         } catch (Exception var2) {
            LOGGER.warn("Unable to start LAN server detection: " + var2.getMessage());
         }

         this.serverListWidget = new MultiplayerServerListWidget(this, this.client, this.titleWidth, this.height, 32, this.height - 64, 36);
         this.serverListWidget.setServers(this.serverList);
      } else {
         this.serverListWidget.updateBounds(this.titleWidth, this.height, 32, this.height - 64);
      }

      this.addButtons();
   }

   @Override
   public void handleMouse() {
      super.handleMouse();
      this.serverListWidget.m_94jnhyuiz();
   }

   public void addButtons() {
      this.buttons.add(this.editButton = new ButtonWidget(7, this.titleWidth / 2 - 154, this.height - 28, 70, 20, I18n.translate("selectServer.edit")));
      this.buttons.add(this.deleteButton = new ButtonWidget(2, this.titleWidth / 2 - 74, this.height - 28, 70, 20, I18n.translate("selectServer.delete")));
      this.buttons.add(this.joinButton = new ButtonWidget(1, this.titleWidth / 2 - 154, this.height - 52, 100, 20, I18n.translate("selectServer.select")));
      this.buttons.add(new ButtonWidget(4, this.titleWidth / 2 - 50, this.height - 52, 100, 20, I18n.translate("selectServer.direct")));
      this.buttons.add(new ButtonWidget(3, this.titleWidth / 2 + 4 + 50, this.height - 52, 100, 20, I18n.translate("selectServer.add")));
      this.buttons.add(new ButtonWidget(8, this.titleWidth / 2 + 4, this.height - 28, 70, 20, I18n.translate("selectServer.refresh")));
      this.buttons.add(new ButtonWidget(0, this.titleWidth / 2 + 4 + 76, this.height - 28, 75, 20, I18n.translate("gui.cancel")));
      this.moveToServer(this.serverListWidget.getCurrentServerIndex());
   }

   @Override
   public void tick() {
      super.tick();
      if (this.lanServerList.needsUpdate()) {
         List var1 = this.lanServerList.getServers();
         this.lanServerList.markClean();
         this.serverListWidget.setLanServers(var1);
      }

      this.pinger.tick();
   }

   @Override
   public void removed() {
      Keyboard.enableRepeatEvents(false);
      if (this.lanServerDetector != null) {
         this.lanServerDetector.interrupt();
         this.lanServerDetector = null;
      }

      this.pinger.cancel();
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         EntryListWidget.Entry var2 = this.serverListWidget.getCurrentServerIndex() < 0
            ? null
            : this.serverListWidget.getEntry(this.serverListWidget.getCurrentServerIndex());
         if (buttonWidget.id == 2 && var2 instanceof ServerListEntryWidget) {
            String var9 = ((ServerListEntryWidget)var2).fetchServer().name;
            if (var9 != null) {
               this.deleteServerConfirmationDialogOpen = true;
               String var4 = I18n.translate("selectServer.deleteQuestion");
               String var5 = "'" + var9 + "' " + I18n.translate("selectServer.deleteWarning");
               String var6 = I18n.translate("selectServer.deleteButton");
               String var7 = I18n.translate("gui.cancel");
               ConfirmScreen var8 = new ConfirmScreen(this, var4, var5, var6, var7, this.serverListWidget.getCurrentServerIndex());
               this.client.openScreen(var8);
            }
         } else if (buttonWidget.id == 1) {
            this.connect();
         } else if (buttonWidget.id == 4) {
            this.serverOpen = true;
            this.client.openScreen(new DirectConnectScreen(this, this.serverEntry = new ServerListEntry(I18n.translate("selectServer.defaultName"), "")));
         } else if (buttonWidget.id == 3) {
            this.addServerDialogOpen = true;
            this.client.openScreen(new AddServerScreen(this, this.serverEntry = new ServerListEntry(I18n.translate("selectServer.defaultName"), "")));
         } else if (buttonWidget.id == 7 && var2 instanceof ServerListEntryWidget) {
            this.editServerDialogOpen = true;
            ServerListEntry var3 = ((ServerListEntryWidget)var2).fetchServer();
            this.serverEntry = new ServerListEntry(var3.name, var3.address);
            this.serverEntry.set(var3);
            this.client.openScreen(new AddServerScreen(this, this.serverEntry));
         } else if (buttonWidget.id == 0) {
            this.client.openScreen(this.parent);
         } else if (buttonWidget.id == 8) {
            this.refresh();
         }
      }
   }

   private void refresh() {
      this.client.openScreen(new MultiplayerScreen(this.parent));
   }

   @Override
   public void confirmResult(boolean result, int id) {
      EntryListWidget.Entry var3 = this.serverListWidget.getCurrentServerIndex() < 0
         ? null
         : this.serverListWidget.getEntry(this.serverListWidget.getCurrentServerIndex());
      if (this.deleteServerConfirmationDialogOpen) {
         this.deleteServerConfirmationDialogOpen = false;
         if (result && var3 instanceof ServerListEntryWidget) {
            this.serverList.remove(this.serverListWidget.getCurrentServerIndex());
            this.serverList.save();
            this.serverListWidget.setCurrentServerIndex(-1);
            this.serverListWidget.setServers(this.serverList);
         }

         this.client.openScreen(this);
      } else if (this.serverOpen) {
         this.serverOpen = false;
         if (result) {
            this.connect(this.serverEntry);
         } else {
            this.client.openScreen(this);
         }
      } else if (this.addServerDialogOpen) {
         this.addServerDialogOpen = false;
         if (result) {
            this.serverList.add(this.serverEntry);
            this.serverList.save();
            this.serverListWidget.setCurrentServerIndex(-1);
            this.serverListWidget.setServers(this.serverList);
         }

         this.client.openScreen(this);
      } else if (this.editServerDialogOpen) {
         this.editServerDialogOpen = false;
         if (result && var3 instanceof ServerListEntryWidget) {
            ServerListEntry var4 = ((ServerListEntryWidget)var3).fetchServer();
            var4.name = this.serverEntry.name;
            var4.address = this.serverEntry.address;
            var4.set(this.serverEntry);
            this.serverList.save();
            this.serverListWidget.setServers(this.serverList);
         }

         this.client.openScreen(this);
      }
   }

   @Override
   protected void keyPressed(char chr, int key) {
      int var3 = this.serverListWidget.getCurrentServerIndex();
      EntryListWidget.Entry var4 = var3 < 0 ? null : this.serverListWidget.getEntry(var3);
      if (key == 63) {
         this.refresh();
      } else {
         if (var3 >= 0) {
            if (key == 200) {
               if (isShiftDown()) {
                  if (var3 > 0 && var4 instanceof ServerListEntryWidget) {
                     this.serverList.swap(var3, var3 - 1);
                     this.moveToServer(this.serverListWidget.getCurrentServerIndex() - 1);
                     this.serverListWidget.scroll(-this.serverListWidget.getItemHeight());
                     this.serverListWidget.setServers(this.serverList);
                  }
               } else if (var3 > 0) {
                  this.moveToServer(this.serverListWidget.getCurrentServerIndex() - 1);
                  this.serverListWidget.scroll(-this.serverListWidget.getItemHeight());
                  if (this.serverListWidget.getEntry(this.serverListWidget.getCurrentServerIndex()) instanceof LanScanWidget) {
                     if (this.serverListWidget.getCurrentServerIndex() > 0) {
                        this.moveToServer(this.serverListWidget.getEntriesSize() - 1);
                        this.serverListWidget.scroll(-this.serverListWidget.getItemHeight());
                     } else {
                        this.moveToServer(-1);
                     }
                  }
               } else {
                  this.moveToServer(-1);
               }
            } else if (key == 208) {
               if (isShiftDown()) {
                  if (var3 < this.serverList.size() - 1) {
                     this.serverList.swap(var3, var3 + 1);
                     this.moveToServer(var3 + 1);
                     this.serverListWidget.scroll(this.serverListWidget.getItemHeight());
                     this.serverListWidget.setServers(this.serverList);
                  }
               } else if (var3 < this.serverListWidget.getEntriesSize()) {
                  this.moveToServer(this.serverListWidget.getCurrentServerIndex() + 1);
                  this.serverListWidget.scroll(this.serverListWidget.getItemHeight());
                  if (this.serverListWidget.getEntry(this.serverListWidget.getCurrentServerIndex()) instanceof LanScanWidget) {
                     if (this.serverListWidget.getCurrentServerIndex() < this.serverListWidget.getEntriesSize() - 1) {
                        this.moveToServer(this.serverListWidget.getEntriesSize() + 1);
                        this.serverListWidget.scroll(this.serverListWidget.getItemHeight());
                     } else {
                        this.moveToServer(-1);
                     }
                  }
               } else {
                  this.moveToServer(-1);
               }
            } else if (key != 28 && key != 156) {
               super.keyPressed(chr, key);
            } else {
               this.buttonClicked((ButtonWidget)this.buttons.get(2));
            }
         } else {
            super.keyPressed(chr, key);
         }
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.tooltipText = null;
      this.renderBackground();
      this.serverListWidget.render(mouseX, mouseY, tickDelta);
      this.drawCenteredString(this.textRenderer, I18n.translate("multiplayer.title"), this.titleWidth / 2, 20, 16777215);
      super.render(mouseX, mouseY, tickDelta);
      if (this.tooltipText != null) {
         this.renderTooltip(Lists.newArrayList(Splitter.on("\n").split(this.tooltipText)), mouseX, mouseY);
      }
   }

   public void connect() {
      EntryListWidget.Entry var1 = this.serverListWidget.getCurrentServerIndex() < 0
         ? null
         : this.serverListWidget.getEntry(this.serverListWidget.getCurrentServerIndex());
      if (var1 instanceof ServerListEntryWidget) {
         this.connect(((ServerListEntryWidget)var1).fetchServer());
      } else if (var1 instanceof LanServerEntry) {
         LanServerQueryManager.LanServerInfo var2 = ((LanServerEntry)var1).getLanServerInfo();
         this.connect(new ServerListEntry(var2.getMotd(), var2.getPort()));
      }
   }

   private void connect(ServerListEntry entry) {
      this.client.openScreen(new ConnectScreen(this, this.client, entry));
   }

   public void moveToServer(int index) {
      this.serverListWidget.setCurrentServerIndex(index);
      EntryListWidget.Entry var2 = index < 0 ? null : this.serverListWidget.getEntry(index);
      this.joinButton.active = false;
      this.editButton.active = false;
      this.deleteButton.active = false;
      if (var2 != null && !(var2 instanceof LanScanWidget)) {
         this.joinButton.active = true;
         if (var2 instanceof ServerListEntryWidget) {
            this.editButton.active = true;
            this.deleteButton.active = true;
         }
      }
   }

   public MultiplayerServerListPinger getServerListPinger() {
      return this.pinger;
   }

   public void setTooltip(String text) {
      this.tooltipText = text;
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.serverListWidget.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
      super.mouseReleased(mouseX, mouseY, mouseButton);
      this.serverListWidget.mouseReleased(mouseX, mouseY, mouseButton);
   }

   public ServerList getServerList() {
      return this.serverList;
   }

   public boolean m_48cnpmgoy(ServerListEntryWidget c_42gznsdwj, int i) {
      return i > 0;
   }

   public boolean m_47mxrsgpt(ServerListEntryWidget c_42gznsdwj, int i) {
      return i < this.serverList.size() - 1;
   }

   public void m_19quqxqzd(ServerListEntryWidget c_42gznsdwj, int i, boolean bl) {
      int var4 = bl ? 0 : i - 1;
      this.serverList.swap(i, var4);
      if (this.serverListWidget.getCurrentServerIndex() == i) {
         this.moveToServer(var4);
      }

      this.serverListWidget.setServers(this.serverList);
   }

   public void m_76ikivfmk(ServerListEntryWidget c_42gznsdwj, int i, boolean bl) {
      int var4 = bl ? this.serverList.size() - 1 : i + 1;
      this.serverList.swap(i, var4);
      if (this.serverListWidget.getCurrentServerIndex() == i) {
         this.moveToServer(var4);
      }

      this.serverListWidget.setServers(this.serverList);
   }
}
