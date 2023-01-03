package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.util.List;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CustomizeWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.resource.Identifier;
import net.minecraft.world.gen.chunk.GeneratorOptions;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.input.Keyboard;

@Environment(EnvType.CLIENT)
public class C_78mxohtpi extends Screen {
   private static final List f_85wgwksmu = Lists.newArrayList();
   private C_78mxohtpi.C_54jovmscg f_67ccdznlw;
   private ButtonWidget f_38efufvuy;
   private TextFieldWidget f_78qdlqmpj;
   private CustomizeWorldScreen f_66lvebqqc;
   protected String f_83amspxdu = "Customize World Presets";
   private String f_41xirnyto;
   private String f_18axdbeva;

   public C_78mxohtpi(CustomizeWorldScreen c_84ufhqyev) {
      this.f_66lvebqqc = c_84ufhqyev;
   }

   @Override
   public void init() {
      this.buttons.clear();
      Keyboard.enableRepeatEvents(true);
      this.f_83amspxdu = I18n.translate("createWorld.customize.custom.presets.title");
      this.f_41xirnyto = I18n.translate("createWorld.customize.presets.share");
      this.f_18axdbeva = I18n.translate("createWorld.customize.presets.list");
      this.f_78qdlqmpj = new TextFieldWidget(2, this.textRenderer, 50, 40, this.titleWidth - 100, 20);
      this.f_67ccdznlw = new C_78mxohtpi.C_54jovmscg();
      this.f_78qdlqmpj.setMaxLength(2000);
      this.f_78qdlqmpj.setText(this.f_66lvebqqc.m_59kocieia());
      this.buttons
         .add(
            this.f_38efufvuy = new ButtonWidget(0, this.titleWidth / 2 - 102, this.height - 27, 100, 20, I18n.translate("createWorld.customize.presets.select"))
         );
      this.buttons.add(new ButtonWidget(1, this.titleWidth / 2 + 3, this.height - 27, 100, 20, I18n.translate("gui.cancel")));
      this.m_44wivebyn();
   }

   @Override
   public void handleMouse() {
      super.handleMouse();
      this.f_67ccdznlw.m_94jnhyuiz();
   }

   @Override
   public void removed() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      this.f_78qdlqmpj.mouseClicked(mouseX, mouseY, mouseButton);
      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   protected void keyPressed(char chr, int key) {
      if (!this.f_78qdlqmpj.keyPressed(chr, key)) {
         super.keyPressed(chr, key);
      }
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      switch(buttonWidget.id) {
         case 0:
            this.f_66lvebqqc.m_54dgesdof(this.f_78qdlqmpj.getText());
            this.client.openScreen(this.f_66lvebqqc);
            break;
         case 1:
            this.client.openScreen(this.f_66lvebqqc);
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.f_67ccdznlw.render(mouseX, mouseY, tickDelta);
      this.drawCenteredString(this.textRenderer, this.f_83amspxdu, this.titleWidth / 2, 8, 16777215);
      this.drawString(this.textRenderer, this.f_41xirnyto, 50, 30, 10526880);
      this.drawString(this.textRenderer, this.f_18axdbeva, 50, 70, 10526880);
      this.f_78qdlqmpj.render();
      super.render(mouseX, mouseY, tickDelta);
   }

   @Override
   public void tick() {
      this.f_78qdlqmpj.tick();
      super.tick();
   }

   public void m_44wivebyn() {
      this.f_38efufvuy.active = this.m_92qdnsknk();
   }

   private boolean m_92qdnsknk() {
      return this.f_67ccdznlw.f_79lppkiuy > -1 && this.f_67ccdznlw.f_79lppkiuy < f_85wgwksmu.size() || this.f_78qdlqmpj.getText().length() > 1;
   }

   static {
      GeneratorOptions.Factory var0 = GeneratorOptions.Factory.fromJson(
         "{ \"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":5000.0, \"mainNoiseScaleY\":1000.0, \"mainNoiseScaleZ\":5000.0, \"baseSize\":8.5, \"stretchY\":8.0, \"biomeDepthWeight\":2.0, \"biomeDepthOffset\":0.5, \"biomeScaleWeight\":2.0, \"biomeScaleOffset\":0.375, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":255 }"
      );
      Identifier var1 = new Identifier("textures/gui/presets/water.png");
      f_85wgwksmu.add(new C_78mxohtpi.C_39hhjgukr(I18n.translate("createWorld.customize.custom.preset.waterWorld"), var1, var0));
      var0 = GeneratorOptions.Factory.fromJson(
         "{\"coordinateScale\":3000.0, \"heightScale\":6000.0, \"upperLimitScale\":250.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":80.0, \"mainNoiseScaleY\":160.0, \"mainNoiseScaleZ\":80.0, \"baseSize\":8.5, \"stretchY\":10.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":63 }"
      );
      var1 = new Identifier("textures/gui/presets/isles.png");
      f_85wgwksmu.add(new C_78mxohtpi.C_39hhjgukr(I18n.translate("createWorld.customize.custom.preset.isleLand"), var1, var0));
      var0 = GeneratorOptions.Factory.fromJson(
         "{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":5000.0, \"mainNoiseScaleY\":1000.0, \"mainNoiseScaleZ\":5000.0, \"baseSize\":8.5, \"stretchY\":5.0, \"biomeDepthWeight\":2.0, \"biomeDepthOffset\":1.0, \"biomeScaleWeight\":4.0, \"biomeScaleOffset\":1.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":63 }"
      );
      var1 = new Identifier("textures/gui/presets/delight.png");
      f_85wgwksmu.add(new C_78mxohtpi.C_39hhjgukr(I18n.translate("createWorld.customize.custom.preset.caveDelight"), var1, var0));
      var0 = GeneratorOptions.Factory.fromJson(
         "{\"coordinateScale\":738.41864, \"heightScale\":157.69133, \"upperLimitScale\":801.4267, \"lowerLimitScale\":1254.1643, \"depthNoiseScaleX\":374.93652, \"depthNoiseScaleZ\":288.65228, \"depthNoiseScaleExponent\":1.2092624, \"mainNoiseScaleX\":1355.9908, \"mainNoiseScaleY\":745.5343, \"mainNoiseScaleZ\":1183.464, \"baseSize\":1.8758626, \"stretchY\":1.7137525, \"biomeDepthWeight\":1.7553768, \"biomeDepthOffset\":3.4701107, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":2.535211, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":63 }"
      );
      var1 = new Identifier("textures/gui/presets/madness.png");
      f_85wgwksmu.add(new C_78mxohtpi.C_39hhjgukr(I18n.translate("createWorld.customize.custom.preset.mountains"), var1, var0));
      var0 = GeneratorOptions.Factory.fromJson(
         "{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":1000.0, \"mainNoiseScaleY\":3000.0, \"mainNoiseScaleZ\":1000.0, \"baseSize\":8.5, \"stretchY\":10.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":20 }"
      );
      var1 = new Identifier("textures/gui/presets/drought.png");
      f_85wgwksmu.add(new C_78mxohtpi.C_39hhjgukr(I18n.translate("createWorld.customize.custom.preset.drought"), var1, var0));
      var0 = GeneratorOptions.Factory.fromJson(
         "{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":2.0, \"lowerLimitScale\":64.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":80.0, \"mainNoiseScaleY\":160.0, \"mainNoiseScaleZ\":80.0, \"baseSize\":8.5, \"stretchY\":12.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":6 }"
      );
      var1 = new Identifier("textures/gui/presets/chaos.png");
      f_85wgwksmu.add(new C_78mxohtpi.C_39hhjgukr(I18n.translate("createWorld.customize.custom.preset.caveChaos"), var1, var0));
      var0 = GeneratorOptions.Factory.fromJson(
         "{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":80.0, \"mainNoiseScaleY\":160.0, \"mainNoiseScaleZ\":80.0, \"baseSize\":8.5, \"stretchY\":12.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":true, \"seaLevel\":40 }"
      );
      var1 = new Identifier("textures/gui/presets/luck.png");
      f_85wgwksmu.add(new C_78mxohtpi.C_39hhjgukr(I18n.translate("createWorld.customize.custom.preset.goodLuck"), var1, var0));
   }

   @Environment(EnvType.CLIENT)
   static class C_39hhjgukr {
      public String f_28yensvdg;
      public Identifier f_23xtzvdgs;
      public GeneratorOptions.Factory f_99myuozsi;

      public C_39hhjgukr(String string, Identifier c_07ipdbewr, GeneratorOptions.Factory c_28uqpzzjq) {
         this.f_28yensvdg = string;
         this.f_23xtzvdgs = c_07ipdbewr;
         this.f_99myuozsi = c_28uqpzzjq;
      }
   }

   @Environment(EnvType.CLIENT)
   class C_54jovmscg extends ListWidget {
      public int f_79lppkiuy = -1;

      public C_54jovmscg() {
         super(C_78mxohtpi.this.client, C_78mxohtpi.this.titleWidth, C_78mxohtpi.this.height, 80, C_78mxohtpi.this.height - 32, 38);
      }

      @Override
      protected int getEntriesSize() {
         return C_78mxohtpi.f_85wgwksmu.size();
      }

      @Override
      protected void selectEntry(int y, boolean isValid, int lastMouseX, int lastMouseY) {
         this.f_79lppkiuy = y;
         C_78mxohtpi.this.m_44wivebyn();
         C_78mxohtpi.this.f_78qdlqmpj
            .setText(((C_78mxohtpi.C_39hhjgukr)C_78mxohtpi.f_85wgwksmu.get(C_78mxohtpi.this.f_67ccdznlw.f_79lppkiuy)).f_99myuozsi.toString());
      }

      @Override
      protected boolean isEntrySelected(int index) {
         return index == this.f_79lppkiuy;
      }

      @Override
      protected void renderBackground() {
      }

      private void m_13hlcdqrr(int i, int j, Identifier c_07ipdbewr) {
         int var4 = i + 5;
         C_78mxohtpi.this.drawHorizontalLine(var4 - 1, var4 + 32, j - 1, -2039584);
         C_78mxohtpi.this.drawHorizontalLine(var4 - 1, var4 + 32, j + 32, -6250336);
         C_78mxohtpi.this.drawVerticalLine(var4 - 1, j - 1, j + 32, -2039584);
         C_78mxohtpi.this.drawVerticalLine(var4 + 32, j - 1, j + 32, -6250336);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.client.getTextureManager().bind(c_07ipdbewr);
         boolean var6 = true;
         boolean var7 = true;
         Tessellator var8 = Tessellator.getInstance();
         BufferBuilder var9 = var8.getBufferBuilder();
         var9.start();
         var9.vertex((double)(var4 + 0), (double)(j + 32), 0.0, 0.0, 1.0);
         var9.vertex((double)(var4 + 32), (double)(j + 32), 0.0, 1.0, 1.0);
         var9.vertex((double)(var4 + 32), (double)(j + 0), 0.0, 1.0, 0.0);
         var9.vertex((double)(var4 + 0), (double)(j + 0), 0.0, 0.0, 0.0);
         var8.end();
      }

      @Override
      protected void renderEntry(int index, int x, int y, int rowHeight, int bufferBuilder, int mouseX) {
         C_78mxohtpi.C_39hhjgukr var7 = (C_78mxohtpi.C_39hhjgukr)C_78mxohtpi.f_85wgwksmu.get(index);
         this.m_13hlcdqrr(x, y, var7.f_23xtzvdgs);
         C_78mxohtpi.this.textRenderer.drawWithoutShadow(var7.f_28yensvdg, x + 32 + 10, y + 14, 16777215);
      }
   }
}
