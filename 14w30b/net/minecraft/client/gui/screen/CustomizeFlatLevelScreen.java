package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.gen.FlatWorldGenerator;
import net.minecraft.world.gen.FlatWorldLayer;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class CustomizeFlatLevelScreen extends Screen {
   private final CreateWorldScreen parent;
   private FlatWorldGenerator helper = FlatWorldGenerator.ofDefault();
   private String title;
   private String tileText;
   private String heightText;
   private CustomizeFlatLevelScreen.CustomizeFlatLevelListWidget customizeFlatLevelListWidget;
   private ButtonWidget addLayer;
   private ButtonWidget editLayer;
   private ButtonWidget removeLayer;

   public CustomizeFlatLevelScreen(CreateWorldScreen parent, String config) {
      this.parent = parent;
      this.setConfigString(config);
   }

   public String getConfigString() {
      return this.helper.toString();
   }

   public void setConfigString(String config) {
      this.helper = FlatWorldGenerator.of(config);
   }

   @Override
   public void init() {
      this.buttons.clear();
      this.title = I18n.translate("createWorld.customize.flat.title");
      this.tileText = I18n.translate("createWorld.customize.flat.tile");
      this.heightText = I18n.translate("createWorld.customize.flat.height");
      this.customizeFlatLevelListWidget = new CustomizeFlatLevelScreen.CustomizeFlatLevelListWidget();
      this.buttons
         .add(
            this.addLayer = new ButtonWidget(
               2, this.titleWidth / 2 - 154, this.height - 52, 100, 20, I18n.translate("createWorld.customize.flat.addLayer") + " (NYI)"
            )
         );
      this.buttons
         .add(
            this.editLayer = new ButtonWidget(
               3, this.titleWidth / 2 - 50, this.height - 52, 100, 20, I18n.translate("createWorld.customize.flat.editLayer") + " (NYI)"
            )
         );
      this.buttons
         .add(
            this.removeLayer = new ButtonWidget(
               4, this.titleWidth / 2 - 155, this.height - 52, 150, 20, I18n.translate("createWorld.customize.flat.removeLayer")
            )
         );
      this.buttons.add(new ButtonWidget(0, this.titleWidth / 2 - 155, this.height - 28, 150, 20, I18n.translate("gui.done")));
      this.buttons.add(new ButtonWidget(5, this.titleWidth / 2 + 5, this.height - 52, 150, 20, I18n.translate("createWorld.customize.presets")));
      this.buttons.add(new ButtonWidget(1, this.titleWidth / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel")));
      this.addLayer.visible = this.editLayer.visible = false;
      this.helper.processLayers();
      this.setActive();
   }

   @Override
   public void handleMouse() {
      super.handleMouse();
      this.customizeFlatLevelListWidget.m_94jnhyuiz();
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      int var2 = this.helper.getLayers().size() - this.customizeFlatLevelListWidget.focusedEntry - 1;
      if (buttonWidget.id == 1) {
         this.client.openScreen(this.parent);
      } else if (buttonWidget.id == 0) {
         this.parent.generatorOptions = this.getConfigString();
         this.client.openScreen(this.parent);
      } else if (buttonWidget.id == 5) {
         this.client.openScreen(new PresetsScreen(this));
      } else if (buttonWidget.id == 4 && this.isActive()) {
         this.helper.getLayers().remove(var2);
         this.customizeFlatLevelListWidget.focusedEntry = Math.min(this.customizeFlatLevelListWidget.focusedEntry, this.helper.getLayers().size() - 1);
      }

      this.helper.processLayers();
      this.setActive();
   }

   public void setActive() {
      boolean var1 = this.isActive();
      this.removeLayer.active = var1;
      this.editLayer.active = var1;
      this.editLayer.active = false;
      this.addLayer.active = false;
   }

   private boolean isActive() {
      return this.customizeFlatLevelListWidget.focusedEntry > -1 && this.customizeFlatLevelListWidget.focusedEntry < this.helper.getLayers().size();
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.customizeFlatLevelListWidget.render(mouseX, mouseY, tickDelta);
      this.drawCenteredString(this.textRenderer, this.title, this.titleWidth / 2, 8, 16777215);
      int var4 = this.titleWidth / 2 - 92 - 16;
      this.drawString(this.textRenderer, this.tileText, var4, 32, 16777215);
      this.drawString(this.textRenderer, this.heightText, var4 + 2 + 213 - this.textRenderer.getStringWidth(this.heightText), 32, 16777215);
      super.render(mouseX, mouseY, tickDelta);
   }

   @Environment(EnvType.CLIENT)
   class CustomizeFlatLevelListWidget extends ListWidget {
      public int focusedEntry = -1;

      public CustomizeFlatLevelListWidget() {
         super(
            CustomizeFlatLevelScreen.this.client,
            CustomizeFlatLevelScreen.this.titleWidth,
            CustomizeFlatLevelScreen.this.height,
            43,
            CustomizeFlatLevelScreen.this.height - 60,
            24
         );
      }

      private void m_01jvuozca(int i, int j, ItemStack c_72owraavl) {
         this.m_96lcbwrqk(i + 1, j + 1);
         GlStateManager.enableRescaleNormal();
         if (c_72owraavl != null && c_72owraavl.getItem() != null) {
            Lighting.turnOnGui();
            CustomizeFlatLevelScreen.this.itemRenderer.renderGuiItemModel(c_72owraavl, i + 2, j + 2);
            Lighting.turnOff();
         }

         GlStateManager.disableRescaleNormal();
      }

      private void m_96lcbwrqk(int i, int j) {
         this.m_97ylerrlz(i, j, 0, 0);
      }

      private void m_97ylerrlz(int i, int j, int k, int l) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.client.getTextureManager().bind(GuiElement.STATS_ICONS);
         float var5 = 0.0078125F;
         float var6 = 0.0078125F;
         boolean var7 = true;
         boolean var8 = true;
         Tessellator var9 = Tessellator.getInstance();
         BufferBuilder var10 = var9.getBufferBuilder();
         var10.start();
         var10.vertex(
            (double)(i + 0),
            (double)(j + 18),
            (double)CustomizeFlatLevelScreen.this.drawOffset,
            (double)((float)(k + 0) * 0.0078125F),
            (double)((float)(l + 18) * 0.0078125F)
         );
         var10.vertex(
            (double)(i + 18),
            (double)(j + 18),
            (double)CustomizeFlatLevelScreen.this.drawOffset,
            (double)((float)(k + 18) * 0.0078125F),
            (double)((float)(l + 18) * 0.0078125F)
         );
         var10.vertex(
            (double)(i + 18),
            (double)(j + 0),
            (double)CustomizeFlatLevelScreen.this.drawOffset,
            (double)((float)(k + 18) * 0.0078125F),
            (double)((float)(l + 0) * 0.0078125F)
         );
         var10.vertex(
            (double)(i + 0),
            (double)(j + 0),
            (double)CustomizeFlatLevelScreen.this.drawOffset,
            (double)((float)(k + 0) * 0.0078125F),
            (double)((float)(l + 0) * 0.0078125F)
         );
         var9.end();
      }

      @Override
      protected int getEntriesSize() {
         return CustomizeFlatLevelScreen.this.helper.getLayers().size();
      }

      @Override
      protected void selectEntry(int y, boolean isValid, int lastMouseX, int lastMouseY) {
         this.focusedEntry = y;
         CustomizeFlatLevelScreen.this.setActive();
      }

      @Override
      protected boolean isEntrySelected(int index) {
         return index == this.focusedEntry;
      }

      @Override
      protected void renderBackground() {
      }

      @Override
      protected void renderEntry(int index, int x, int y, int rowHeight, int bufferBuilder, int mouseX) {
         FlatWorldLayer var7 = (FlatWorldLayer)CustomizeFlatLevelScreen.this.helper
            .getLayers()
            .get(CustomizeFlatLevelScreen.this.helper.getLayers().size() - index - 1);
         Block var8 = var7.getBlock();
         Item var9 = Item.byBlock(var8);
         ItemStack var10 = var8 != Blocks.AIR && var9 != null ? new ItemStack(var9, 1, var7.getBlockMetadata()) : null;
         String var11 = var10 == null ? "Air" : var9.getName(var10);
         if (var9 == null) {
            if (var8 == Blocks.WATER || var8 == Blocks.FLOWING_WATER) {
               var9 = Items.WATER_BUCKET;
            } else if (var8 == Blocks.LAVA || var8 == Blocks.FLOWING_LAVA) {
               var9 = Items.LAVA_BUCKET;
            }

            if (var9 != null) {
               var10 = new ItemStack(var9, 1, var7.getBlockMetadata());
               var11 = var8.getName();
            }
         }

         this.m_01jvuozca(x, y, var10);
         CustomizeFlatLevelScreen.this.textRenderer.drawWithoutShadow(var11, x + 18 + 5, y + 3, 16777215);
         String var12;
         if (index == 0) {
            var12 = I18n.translate("createWorld.customize.flat.layer.top", var7.getSize());
         } else if (index == CustomizeFlatLevelScreen.this.helper.getLayers().size() - 1) {
            var12 = I18n.translate("createWorld.customize.flat.layer.bottom", var7.getSize());
         } else {
            var12 = I18n.translate("createWorld.customize.flat.layer", var7.getSize());
         }

         CustomizeFlatLevelScreen.this.textRenderer
            .drawWithoutShadow(var12, x + 2 + 213 - CustomizeFlatLevelScreen.this.textRenderer.getStringWidth(var12), y + 3, 16777215);
      }

      @Override
      protected int getScrollbarPosition() {
         return this.width - 70;
      }
   }
}
