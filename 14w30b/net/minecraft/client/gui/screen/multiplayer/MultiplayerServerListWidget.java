package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.LanServerEntry;
import net.minecraft.client.gui.widget.ServerListEntryWidget;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.client.options.ServerList;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class MultiplayerServerListWidget extends EntryListWidget {
   private final MultiplayerScreen parent;
   private final List servers = Lists.newArrayList();
   private final List lanServers = Lists.newArrayList();
   private final EntryListWidget.Entry scanningWidget = new LanScanWidget();
   private int currentServerIndex = -1;

   public MultiplayerServerListWidget(MultiplayerScreen parent, MinecraftClient client, int x, int y, int yStart, int yEnd, int entryHeight) {
      super(client, x, y, yStart, yEnd, entryHeight);
      this.parent = parent;
   }

   @Override
   public EntryListWidget.Entry getEntry(int index) {
      if (index < this.servers.size()) {
         return (EntryListWidget.Entry)this.servers.get(index);
      } else {
         index -= this.servers.size();
         return index == 0 ? this.scanningWidget : (EntryListWidget.Entry)this.lanServers.get(--index);
      }
   }

   @Override
   protected int getEntriesSize() {
      return this.servers.size() + 1 + this.lanServers.size();
   }

   public void setCurrentServerIndex(int index) {
      this.currentServerIndex = index;
   }

   @Override
   protected boolean isEntrySelected(int index) {
      return index == this.currentServerIndex;
   }

   public int getCurrentServerIndex() {
      return this.currentServerIndex;
   }

   public void setServers(ServerList servers) {
      this.servers.clear();

      for(int var2 = 0; var2 < servers.size(); ++var2) {
         this.servers.add(new ServerListEntryWidget(this.parent, servers.get(var2)));
      }
   }

   public void setLanServers(List lanServers) {
      this.lanServers.clear();

      for(LanServerQueryManager.LanServerInfo var3 : lanServers) {
         this.lanServers.add(new LanServerEntry(this.parent, var3));
      }
   }

   @Override
   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 30;
   }

   @Override
   public int getRowWidth() {
      return super.getRowWidth() + 85;
   }
}
