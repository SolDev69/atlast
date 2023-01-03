package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.FlatWorldGenerator;
import net.minecraft.world.gen.FlatWorldLayer;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.input.Keyboard;

@Environment(EnvType.CLIENT)
public class PresetsScreen extends Screen {
   private static final List PRESETS = Lists.newArrayList();
   private final CustomizeFlatLevelScreen parent;
   private String titleText;
   private String shareText;
   private String listText;
   private PresetsScreen.PresetsListWidget listWidget;
   private ButtonWidget selectButton;
   private TextFieldWidget customPresetField;

   public PresetsScreen(CustomizeFlatLevelScreen parent) {
      this.parent = parent;
   }

   @Override
   public void init() {
      this.buttons.clear();
      Keyboard.enableRepeatEvents(true);
      this.titleText = I18n.translate("createWorld.customize.presets.title");
      this.shareText = I18n.translate("createWorld.customize.presets.share");
      this.listText = I18n.translate("createWorld.customize.presets.list");
      this.customPresetField = new TextFieldWidget(2, this.textRenderer, 50, 40, this.titleWidth - 100, 20);
      this.listWidget = new PresetsScreen.PresetsListWidget();
      this.customPresetField.setMaxLength(1230);
      this.customPresetField.setText(this.parent.getConfigString());
      this.buttons
         .add(
            this.selectButton = new ButtonWidget(
               0, this.titleWidth / 2 - 155, this.height - 28, 150, 20, I18n.translate("createWorld.customize.presets.select")
            )
         );
      this.buttons.add(new ButtonWidget(1, this.titleWidth / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel")));
      this.activateSelectButtonIfPresetSelected();
   }

   @Override
   public void handleMouse() {
      super.handleMouse();
      this.listWidget.m_94jnhyuiz();
   }

   @Override
   public void removed() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      this.customPresetField.mouseClicked(mouseX, mouseY, mouseButton);
      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   protected void keyPressed(char chr, int key) {
      if (!this.customPresetField.keyPressed(chr, key)) {
         super.keyPressed(chr, key);
      }
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.id == 0 && this.isPresetSelected()) {
         this.parent.setConfigString(this.customPresetField.getText());
         this.client.openScreen(this.parent);
      } else if (buttonWidget.id == 1) {
         this.client.openScreen(this.parent);
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.listWidget.render(mouseX, mouseY, tickDelta);
      this.drawCenteredString(this.textRenderer, this.titleText, this.titleWidth / 2, 8, 16777215);
      this.drawString(this.textRenderer, this.shareText, 50, 30, 10526880);
      this.drawString(this.textRenderer, this.listText, 50, 70, 10526880);
      this.customPresetField.render();
      super.render(mouseX, mouseY, tickDelta);
   }

   @Override
   public void tick() {
      this.customPresetField.tick();
      super.tick();
   }

   public void activateSelectButtonIfPresetSelected() {
      boolean var1 = this.isPresetSelected();
      this.selectButton.active = var1;
   }

   private boolean isPresetSelected() {
      return this.listWidget.selectedEntryIndex > -1 && this.listWidget.selectedEntryIndex < PRESETS.size() || this.customPresetField.getText().length() > 1;
   }

   private static void addRedstonePreset(String name, Item icon, Biome iconDamage, FlatWorldLayer... layers) {
      addFlatWorldPreset(name, icon, 0, iconDamage, null, layers);
   }

   private static void m_77hiwfunb(String string, Item c_30vndvelc, Biome c_72robrvqq, List list, FlatWorldLayer... c_00oxvoajps) {
      addFlatWorldPreset(string, c_30vndvelc, 0, c_72robrvqq, list, c_00oxvoajps);
   }

   private static void addFlatWorldPreset(String name, Item item, int biome, Biome biomesAndStructures, List layers, FlatWorldLayer... c_00oxvoajps) {
      FlatWorldGenerator var6 = new FlatWorldGenerator();

      for(int var7 = c_00oxvoajps.length - 1; var7 >= 0; --var7) {
         var6.getLayers().add(c_00oxvoajps[var7]);
      }

      var6.setBiomeId(biomesAndStructures.id);
      var6.processLayers();
      if (layers != null) {
         for(String var8 : layers) {
            var6.getFeatures().put(var8, Maps.newHashMap());
         }
      }

      PRESETS.add(new PresetsScreen.FlatWorldPreset(item, biome, name, var6.toString()));
   }

   static {
      m_77hiwfunb(
         "Classic Flat",
         Item.byBlock(Blocks.GRASS),
         Biome.PLAINS,
         Arrays.asList("village"),
         new FlatWorldLayer(1, Blocks.GRASS),
         new FlatWorldLayer(2, Blocks.DIRT),
         new FlatWorldLayer(1, Blocks.BEDROCK)
      );
      m_77hiwfunb(
         "Tunnelers' Dream",
         Item.byBlock(Blocks.STONE),
         Biome.EXTREME_HILLS,
         Arrays.asList("biome_1", "dungeon", "decoration", "stronghold", "mineshaft"),
         new FlatWorldLayer(1, Blocks.GRASS),
         new FlatWorldLayer(5, Blocks.DIRT),
         new FlatWorldLayer(230, Blocks.STONE),
         new FlatWorldLayer(1, Blocks.BEDROCK)
      );
      m_77hiwfunb(
         "Water World",
         Items.WATER_BUCKET,
         Biome.DEEP_OCEAN,
         Arrays.asList("biome_1", "oceanmonument"),
         new FlatWorldLayer(90, Blocks.WATER),
         new FlatWorldLayer(5, Blocks.SAND),
         new FlatWorldLayer(5, Blocks.DIRT),
         new FlatWorldLayer(5, Blocks.STONE),
         new FlatWorldLayer(1, Blocks.BEDROCK)
      );
      addFlatWorldPreset(
         "Overworld",
         Item.byBlock(Blocks.TALLGRASS),
         TallPlantBlock.Type.GRASS.getIndex(),
         Biome.PLAINS,
         Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake"),
         new FlatWorldLayer(1, Blocks.GRASS),
         new FlatWorldLayer(3, Blocks.DIRT),
         new FlatWorldLayer(59, Blocks.STONE),
         new FlatWorldLayer(1, Blocks.BEDROCK)
      );
      m_77hiwfunb(
         "Snowy Kingdom",
         Item.byBlock(Blocks.SNOW_LAYER),
         Biome.ICE_PLAINS,
         Arrays.asList("village", "biome_1"),
         new FlatWorldLayer(1, Blocks.SNOW_LAYER),
         new FlatWorldLayer(1, Blocks.GRASS),
         new FlatWorldLayer(3, Blocks.DIRT),
         new FlatWorldLayer(59, Blocks.STONE),
         new FlatWorldLayer(1, Blocks.BEDROCK)
      );
      m_77hiwfunb(
         "Bottomless Pit",
         Items.FEATHER,
         Biome.PLAINS,
         Arrays.asList("village", "biome_1"),
         new FlatWorldLayer(1, Blocks.GRASS),
         new FlatWorldLayer(3, Blocks.DIRT),
         new FlatWorldLayer(2, Blocks.COBBLESTONE)
      );
      m_77hiwfunb(
         "Desert",
         Item.byBlock(Blocks.SAND),
         Biome.DESERT,
         Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon"),
         new FlatWorldLayer(8, Blocks.SAND),
         new FlatWorldLayer(52, Blocks.SANDSTONE),
         new FlatWorldLayer(3, Blocks.STONE),
         new FlatWorldLayer(1, Blocks.BEDROCK)
      );
      addRedstonePreset(
         "Redstone Ready",
         Items.REDSTONE,
         Biome.DESERT,
         new FlatWorldLayer(52, Blocks.SANDSTONE),
         new FlatWorldLayer(3, Blocks.STONE),
         new FlatWorldLayer(1, Blocks.BEDROCK)
      );
   }

   @Environment(EnvType.CLIENT)
   static class FlatWorldPreset {
      public Item presetItem;
      public int f_00tcivnnf;
      public String presetName;
      public String presetLayers;

      public FlatWorldPreset(Item presetItem, int presetName, String presetLayers, String string2) {
         this.presetItem = presetItem;
         this.f_00tcivnnf = presetName;
         this.presetName = presetLayers;
         this.presetLayers = string2;
      }
   }

   @Environment(EnvType.CLIENT)
   class PresetsListWidget extends ListWidget {
      public int selectedEntryIndex = -1;

      public PresetsListWidget() {
         super(PresetsScreen.this.client, PresetsScreen.this.titleWidth, PresetsScreen.this.height, 80, PresetsScreen.this.height - 37, 24);
      }

      private void renderEntry(int x, int y, Item item, int k) {
         this.render(x + 1, y + 1);
         GlStateManager.enableRescaleNormal();
         Lighting.turnOnGui();
         PresetsScreen.this.itemRenderer.renderGuiItemModel(new ItemStack(item, 1, k), x + 2, y + 2);
         Lighting.turnOff();
         GlStateManager.disableRescaleNormal();
      }

      private void render(int x, int y) {
         this.render(x, y, 0, 0);
      }

      private void render(int x, int y, int u, int v) {
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
            (double)(x + 0),
            (double)(y + 18),
            (double)PresetsScreen.this.drawOffset,
            (double)((float)(u + 0) * 0.0078125F),
            (double)((float)(v + 18) * 0.0078125F)
         );
         var10.vertex(
            (double)(x + 18),
            (double)(y + 18),
            (double)PresetsScreen.this.drawOffset,
            (double)((float)(u + 18) * 0.0078125F),
            (double)((float)(v + 18) * 0.0078125F)
         );
         var10.vertex(
            (double)(x + 18),
            (double)(y + 0),
            (double)PresetsScreen.this.drawOffset,
            (double)((float)(u + 18) * 0.0078125F),
            (double)((float)(v + 0) * 0.0078125F)
         );
         var10.vertex(
            (double)(x + 0),
            (double)(y + 0),
            (double)PresetsScreen.this.drawOffset,
            (double)((float)(u + 0) * 0.0078125F),
            (double)((float)(v + 0) * 0.0078125F)
         );
         var9.end();
      }

      @Override
      protected int getEntriesSize() {
         return PresetsScreen.PRESETS.size();
      }

      @Override
      protected void selectEntry(int y, boolean isValid, int lastMouseX, int lastMouseY) {
         this.selectedEntryIndex = y;
         PresetsScreen.this.activateSelectButtonIfPresetSelected();
         PresetsScreen.this.customPresetField
            .setText(((PresetsScreen.FlatWorldPreset)PresetsScreen.PRESETS.get(PresetsScreen.this.listWidget.selectedEntryIndex)).presetLayers);
      }

      @Override
      protected boolean isEntrySelected(int index) {
         return index == this.selectedEntryIndex;
      }

      @Override
      protected void renderBackground() {
      }

      @Override
      protected void renderEntry(int index, int x, int y, int rowHeight, int bufferBuilder, int mouseX) {
         PresetsScreen.FlatWorldPreset var7 = (PresetsScreen.FlatWorldPreset)PresetsScreen.PRESETS.get(index);
         this.renderEntry(x, y, var7.presetItem, var7.f_00tcivnnf);
         PresetsScreen.this.textRenderer.drawWithoutShadow(var7.presetName, x + 18 + 5, y + 6, 16777215);
      }
   }
}
