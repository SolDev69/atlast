package net.minecraft.client.gui.screen.resourcepack;

import com.mojang.blaze3d.vertex.Tessellator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.Formatting;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class ResourcePackListWidget extends EntryListWidget {
   protected final MinecraftClient clientInstance;
   protected final List widgets;

   public ResourcePackListWidget(MinecraftClient client, int x, int y, List widgets) {
      super(client, x, y, 32, y - 55 + 4, 36);
      this.clientInstance = client;
      this.widgets = widgets;
      this.centerAlongY = false;
      this.setHeader(true, (int)((float)client.textRenderer.fontHeight * 1.5F));
   }

   @Override
   protected void renderHeader(int x, int y, Tessellator bufferBuilder) {
      String var4 = Formatting.UNDERLINE + "" + Formatting.BOLD + this.getTitle();
      this.clientInstance
         .textRenderer
         .drawWithoutShadow(var4, x + this.width / 2 - this.clientInstance.textRenderer.getStringWidth(var4) / 2, Math.min(this.yStart + 3, y), 16777215);
   }

   protected abstract String getTitle();

   public List getWidgets() {
      return this.widgets;
   }

   @Override
   protected int getEntriesSize() {
      return this.getWidgets().size();
   }

   public AbstractResourcePackEntryWidget getEntry(int i) {
      return (AbstractResourcePackEntryWidget)this.getWidgets().get(i);
   }

   @Override
   public int getRowWidth() {
      return this.width;
   }

   @Override
   protected int getScrollbarPosition() {
      return this.xEnd - 6;
   }
}
