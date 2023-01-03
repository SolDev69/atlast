package net.minecraft.client.gui.screen.resourcepack;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.screen.ResourcePackScreen;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class AbstractResourcePackEntryWidget implements EntryListWidget.Entry {
   private static final Identifier TEXTURE = new Identifier("textures/gui/resource_packs.png");
   protected final MinecraftClient client;
   protected final ResourcePackScreen parent;

   public AbstractResourcePackEntryWidget(ResourcePackScreen parent) {
      this.parent = parent;
      this.client = MinecraftClient.getInstance();
   }

   @Override
   public void render(int id, int x, int y, int width, int height, int bufferBuilder, int mouseX, boolean mouseY) {
      this.bindIcon();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GuiElement.drawTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
      if ((this.client.options.touchscreen || mouseY) && this.hasResourcePack()) {
         this.client.getTextureManager().bind(TEXTURE);
         GuiElement.fill(x, y, x + 32, y + 32, -1601138544);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         int var9 = bufferBuilder - x;
         int var10 = mouseX - y;
         if (this.isNotNamed()) {
            if (var9 < 32) {
               GuiElement.drawTexture(x, y, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               GuiElement.drawTexture(x, y, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         } else {
            if (this.isNamed()) {
               if (var9 < 16) {
                  GuiElement.drawTexture(x, y, 32.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  GuiElement.drawTexture(x, y, 32.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }

            if (this.moveDown()) {
               if (var9 < 32 && var9 > 16 && var10 < 16) {
                  GuiElement.drawTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  GuiElement.drawTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }

            if (this.moveUp()) {
               if (var9 < 32 && var9 > 16 && var10 > 16) {
                  GuiElement.drawTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  GuiElement.drawTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }
         }
      }

      String var13 = this.getName();
      int var14 = this.client.textRenderer.getStringWidth(var13);
      if (var14 > 157) {
         var13 = this.client.textRenderer.trimToWidth(var13, 157 - this.client.textRenderer.getStringWidth("...")) + "...";
      }

      this.client.textRenderer.drawWithShadow(var13, (float)(x + 32 + 2), (float)(y + 1), 16777215);
      List var11 = this.client.textRenderer.wrapLines(this.getDescription(), 157);

      for(int var12 = 0; var12 < 2 && var12 < var11.size(); ++var12) {
         this.client.textRenderer.drawWithShadow((String)var11.get(var12), (float)(x + 32 + 2), (float)(y + 12 + 10 * var12), 8421504);
      }
   }

   protected abstract String getDescription();

   protected abstract String getName();

   protected abstract void bindIcon();

   protected boolean hasResourcePack() {
      return true;
   }

   protected boolean isNotNamed() {
      return !this.parent.isResourcePackEntryNamed(this);
   }

   protected boolean isNamed() {
      return this.parent.isResourcePackEntryNamed(this);
   }

   protected boolean moveDown() {
      List var1 = this.parent.getListContainingResourcePackEntry(this);
      int var2 = var1.indexOf(this);
      return var2 > 0 && ((AbstractResourcePackEntryWidget)var1.get(var2 - 1)).hasResourcePack();
   }

   protected boolean moveUp() {
      List var1 = this.parent.getListContainingResourcePackEntry(this);
      int var2 = var1.indexOf(this);
      return var2 >= 0 && var2 < var1.size() - 1 && ((AbstractResourcePackEntryWidget)var1.get(var2 + 1)).hasResourcePack();
   }

   @Override
   public boolean mouseClicked(int id, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
      if (this.hasResourcePack() && entryMouseX <= 32) {
         if (this.isNotNamed()) {
            this.parent.getListContainingResourcePackEntry(this).remove(this);
            this.parent.getNamedResourcePackEntryList().add(0, this);
            this.parent.m_60oatrcpx();
            return true;
         }

         if (entryMouseX < 16 && this.isNamed()) {
            this.parent.getListContainingResourcePackEntry(this).remove(this);
            this.parent.getUnnamedResourcePackEntryList().add(0, this);
            this.parent.m_60oatrcpx();
            return true;
         }

         if (entryMouseX > 16 && entryMouseY < 16 && this.moveDown()) {
            List var9 = this.parent.getListContainingResourcePackEntry(this);
            int var10 = var9.indexOf(this);
            var9.remove(this);
            var9.add(var10 - 1, this);
            this.parent.m_60oatrcpx();
            return true;
         }

         if (entryMouseX > 16 && entryMouseY > 16 && this.moveUp()) {
            List var7 = this.parent.getListContainingResourcePackEntry(this);
            int var8 = var7.indexOf(this);
            var7.remove(this);
            var7.add(var8 + 1, this);
            this.parent.m_60oatrcpx();
            return true;
         }
      }

      return false;
   }

   @Override
   public void m_82anuocxe(int i, int j, int k) {
   }

   @Override
   public void mouseReleased(int id, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
   }
}
