package net.minecraft.client.gui.screen.world;

import com.google.common.base.Predicate;
import com.google.common.primitives.Floats;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.util.Random;
import net.minecraft.C_13fsheoyt;
import net.minecraft.C_37rnsjynt;
import net.minecraft.C_78mxohtpi;
import net.minecraft.C_97enwlcph;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.GeneratorOptions;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class CustomizeWorldScreen extends Screen implements C_97enwlcph.C_56tbhdubs, C_37rnsjynt.C_59lpdyoky {
   private CreateWorldScreen parentScreen;
   protected String screenTitle = "Customize World Settings";
   protected String basePageCountString = "Page 1 of 3";
   protected String baseSettingsTypeString = "Basic Settings";
   protected String[] f_50jrrucif = new String[4];
   private C_37rnsjynt f_21fzmvwrz;
   private ButtonWidget doneButton;
   private ButtonWidget randomizeButton;
   private ButtonWidget defaultsButton;
   private ButtonWidget prevPageButton;
   private ButtonWidget nextPageButton;
   private ButtonWidget confirmYesButton;
   private ButtonWidget confirmNoButton;
   private ButtonWidget presetsButton;
   private boolean f_49somoqoc = false;
   private int f_17vyrxzhb = 0;
   private boolean f_43njazqzp = false;
   private Predicate f_91jditnvk = new Predicate() {
      public boolean apply(String string) {
         Float var2 = Floats.tryParse(string);
         return string.length() == 0 || var2 != null && Floats.isFinite(var2) && var2 >= 0.0F;
      }
   };
   private GeneratorOptions.Factory f_20bovtupi = new GeneratorOptions.Factory();
   private GeneratorOptions.Factory f_26qbqwhnf;
   private Random f_76fsscyow = new Random();

   public CustomizeWorldScreen(Screen parentScreen, String string) {
      this.parentScreen = (CreateWorldScreen)parentScreen;
      this.m_54dgesdof(string);
   }

   @Override
   public void init() {
      this.screenTitle = I18n.translate("options.customizeTitle");
      this.buttons.clear();
      this.buttons.add(this.prevPageButton = new ButtonWidget(302, 20, 5, 80, 20, I18n.translate("createWorld.customize.custom.prev")));
      this.buttons.add(this.nextPageButton = new ButtonWidget(303, this.titleWidth - 100, 5, 80, 20, I18n.translate("createWorld.customize.custom.next")));
      this.buttons
         .add(
            this.defaultsButton = new ButtonWidget(
               304, this.titleWidth / 2 - 187, this.height - 27, 90, 20, I18n.translate("createWorld.customize.custom.defaults")
            )
         );
      this.buttons
         .add(
            this.randomizeButton = new ButtonWidget(
               301, this.titleWidth / 2 - 92, this.height - 27, 90, 20, I18n.translate("createWorld.customize.custom.randomize")
            )
         );
      this.buttons
         .add(
            this.presetsButton = new ButtonWidget(
               305, this.titleWidth / 2 + 3, this.height - 27, 90, 20, I18n.translate("createWorld.customize.custom.presets")
            )
         );
      this.buttons.add(this.doneButton = new ButtonWidget(300, this.titleWidth / 2 + 98, this.height - 27, 90, 20, I18n.translate("gui.done")));
      this.confirmYesButton = new ButtonWidget(306, this.titleWidth / 2 - 55, 160, 50, 20, I18n.translate("gui.yes"));
      this.confirmYesButton.visible = false;
      this.buttons.add(this.confirmYesButton);
      this.confirmNoButton = new ButtonWidget(307, this.titleWidth / 2 + 5, 160, 50, 20, I18n.translate("gui.no"));
      this.confirmNoButton.visible = false;
      this.buttons.add(this.confirmNoButton);
      this.m_32tbfupgx();
   }

   @Override
   public void handleMouse() {
      super.handleMouse();
      this.f_21fzmvwrz.m_94jnhyuiz();
   }

   private void m_32tbfupgx() {
      C_37rnsjynt.C_30sltyycm[] var1 = new C_37rnsjynt.C_30sltyycm[]{
         new C_37rnsjynt.C_95ddunyyj(160, I18n.translate("createWorld.customize.custom.seaLevel"), true, this, 1.0F, 255.0F, (float)this.f_26qbqwhnf.seaLevel),
         new C_37rnsjynt.C_60wnrvuxr(148, I18n.translate("createWorld.customize.custom.useCaves"), true, this.f_26qbqwhnf.useCaves),
         new C_37rnsjynt.C_60wnrvuxr(150, I18n.translate("createWorld.customize.custom.useStrongholds"), true, this.f_26qbqwhnf.useStrongholds),
         new C_37rnsjynt.C_60wnrvuxr(151, I18n.translate("createWorld.customize.custom.useVillages"), true, this.f_26qbqwhnf.useVillages),
         new C_37rnsjynt.C_60wnrvuxr(152, I18n.translate("createWorld.customize.custom.useMineShafts"), true, this.f_26qbqwhnf.useMineshafts),
         new C_37rnsjynt.C_60wnrvuxr(153, I18n.translate("createWorld.customize.custom.useTemples"), true, this.f_26qbqwhnf.useTemples),
         new C_37rnsjynt.C_60wnrvuxr(210, I18n.translate("createWorld.customize.custom.useMonuments"), true, this.f_26qbqwhnf.useMonuments),
         new C_37rnsjynt.C_60wnrvuxr(154, I18n.translate("createWorld.customize.custom.useRavines"), true, this.f_26qbqwhnf.useRavines),
         new C_37rnsjynt.C_60wnrvuxr(149, I18n.translate("createWorld.customize.custom.useDungeons"), true, this.f_26qbqwhnf.useDungeons),
         new C_37rnsjynt.C_95ddunyyj(
            157, I18n.translate("createWorld.customize.custom.dungeonChance"), true, this, 1.0F, 100.0F, (float)this.f_26qbqwhnf.dungeonChance
         ),
         new C_37rnsjynt.C_60wnrvuxr(155, I18n.translate("createWorld.customize.custom.useWaterLakes"), true, this.f_26qbqwhnf.useWaterLakes),
         new C_37rnsjynt.C_95ddunyyj(
            158, I18n.translate("createWorld.customize.custom.waterLakeChance"), true, this, 1.0F, 100.0F, (float)this.f_26qbqwhnf.waterLakeChance
         ),
         new C_37rnsjynt.C_60wnrvuxr(156, I18n.translate("createWorld.customize.custom.useLavaLakes"), true, this.f_26qbqwhnf.useLavaLakes),
         new C_37rnsjynt.C_95ddunyyj(
            159, I18n.translate("createWorld.customize.custom.lavaLakeChance"), true, this, 10.0F, 100.0F, (float)this.f_26qbqwhnf.lavaLakeChance
         ),
         new C_37rnsjynt.C_60wnrvuxr(161, I18n.translate("createWorld.customize.custom.useLavaOceans"), true, this.f_26qbqwhnf.useLavaOceans),
         new C_37rnsjynt.C_95ddunyyj(
            162, I18n.translate("createWorld.customize.custom.fixedBiome"), true, this, -1.0F, 37.0F, (float)this.f_26qbqwhnf.fixedBiome
         ),
         new C_37rnsjynt.C_95ddunyyj(163, I18n.translate("createWorld.customize.custom.biomeSize"), true, this, 1.0F, 8.0F, (float)this.f_26qbqwhnf.biomeSize),
         new C_37rnsjynt.C_95ddunyyj(164, I18n.translate("createWorld.customize.custom.riverSize"), true, this, 1.0F, 5.0F, (float)this.f_26qbqwhnf.riverSize)
      };
      C_37rnsjynt.C_30sltyycm[] var2 = new C_37rnsjynt.C_30sltyycm[]{
         new C_37rnsjynt.C_37zmgzpzj(416, I18n.translate("tile.dirt.name"), false),
         null,
         new C_37rnsjynt.C_95ddunyyj(165, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.f_26qbqwhnf.dirtSize),
         new C_37rnsjynt.C_95ddunyyj(166, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.f_26qbqwhnf.dirtCount),
         new C_37rnsjynt.C_95ddunyyj(
            167, I18n.translate("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.dirtMinHeight
         ),
         new C_37rnsjynt.C_95ddunyyj(
            168, I18n.translate("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.dirtMaxHeight
         ),
         new C_37rnsjynt.C_37zmgzpzj(417, I18n.translate("tile.gravel.name"), false),
         null,
         new C_37rnsjynt.C_95ddunyyj(169, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.f_26qbqwhnf.gravelSize),
         new C_37rnsjynt.C_95ddunyyj(170, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.f_26qbqwhnf.gravelCount),
         new C_37rnsjynt.C_95ddunyyj(
            171, I18n.translate("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.gravelMinHeight
         ),
         new C_37rnsjynt.C_95ddunyyj(
            172, I18n.translate("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.gravelMaxHeight
         ),
         new C_37rnsjynt.C_37zmgzpzj(418, I18n.translate("tile.stone.granite.name"), false),
         null,
         new C_37rnsjynt.C_95ddunyyj(173, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.f_26qbqwhnf.graniteSize),
         new C_37rnsjynt.C_95ddunyyj(174, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.f_26qbqwhnf.graniteCount),
         new C_37rnsjynt.C_95ddunyyj(
            175, I18n.translate("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.graniteMinHeight
         ),
         new C_37rnsjynt.C_95ddunyyj(
            176, I18n.translate("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.graniteMaxHeight
         ),
         new C_37rnsjynt.C_37zmgzpzj(419, I18n.translate("tile.stone.diorite.name"), false),
         null,
         new C_37rnsjynt.C_95ddunyyj(177, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.f_26qbqwhnf.dioriteSize),
         new C_37rnsjynt.C_95ddunyyj(178, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.f_26qbqwhnf.dioriteCount),
         new C_37rnsjynt.C_95ddunyyj(
            179, I18n.translate("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.dioriteMinHeight
         ),
         new C_37rnsjynt.C_95ddunyyj(
            180, I18n.translate("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.dioriteMaxHeight
         ),
         new C_37rnsjynt.C_37zmgzpzj(420, I18n.translate("tile.stone.andesite.name"), false),
         null,
         new C_37rnsjynt.C_95ddunyyj(181, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.f_26qbqwhnf.andesiteSize),
         new C_37rnsjynt.C_95ddunyyj(182, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.f_26qbqwhnf.andesiteCount),
         new C_37rnsjynt.C_95ddunyyj(
            183, I18n.translate("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.andesiteMinHeight
         ),
         new C_37rnsjynt.C_95ddunyyj(
            184, I18n.translate("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.andesiteMaxHeight
         ),
         new C_37rnsjynt.C_37zmgzpzj(421, I18n.translate("tile.oreCoal.name"), false),
         null,
         new C_37rnsjynt.C_95ddunyyj(185, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.f_26qbqwhnf.coalSize),
         new C_37rnsjynt.C_95ddunyyj(186, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.f_26qbqwhnf.coalCount),
         new C_37rnsjynt.C_95ddunyyj(
            187, I18n.translate("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.coalMinHeight
         ),
         new C_37rnsjynt.C_95ddunyyj(
            189, I18n.translate("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.coalMaxHeight
         ),
         new C_37rnsjynt.C_37zmgzpzj(422, I18n.translate("tile.oreIron.name"), false),
         null,
         new C_37rnsjynt.C_95ddunyyj(190, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.f_26qbqwhnf.ironSize),
         new C_37rnsjynt.C_95ddunyyj(191, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.f_26qbqwhnf.ironCount),
         new C_37rnsjynt.C_95ddunyyj(
            192, I18n.translate("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.ironMinHeight
         ),
         new C_37rnsjynt.C_95ddunyyj(
            193, I18n.translate("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.ironMaxHeight
         ),
         new C_37rnsjynt.C_37zmgzpzj(423, I18n.translate("tile.oreGold.name"), false),
         null,
         new C_37rnsjynt.C_95ddunyyj(194, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.f_26qbqwhnf.goldSize),
         new C_37rnsjynt.C_95ddunyyj(195, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.f_26qbqwhnf.goldCount),
         new C_37rnsjynt.C_95ddunyyj(
            196, I18n.translate("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.goldMinHeight
         ),
         new C_37rnsjynt.C_95ddunyyj(
            197, I18n.translate("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.goldMaxHeight
         ),
         new C_37rnsjynt.C_37zmgzpzj(424, I18n.translate("tile.oreRedstone.name"), false),
         null,
         new C_37rnsjynt.C_95ddunyyj(198, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.f_26qbqwhnf.redstoneSize),
         new C_37rnsjynt.C_95ddunyyj(199, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.f_26qbqwhnf.redstoneCount),
         new C_37rnsjynt.C_95ddunyyj(
            200, I18n.translate("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.redstoneMinHeight
         ),
         new C_37rnsjynt.C_95ddunyyj(
            201, I18n.translate("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.redstoneMaxHeight
         ),
         new C_37rnsjynt.C_37zmgzpzj(425, I18n.translate("tile.oreDiamond.name"), false),
         null,
         new C_37rnsjynt.C_95ddunyyj(202, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.f_26qbqwhnf.diamondSize),
         new C_37rnsjynt.C_95ddunyyj(203, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.f_26qbqwhnf.diamondCount),
         new C_37rnsjynt.C_95ddunyyj(
            204, I18n.translate("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.diamondMinHeight
         ),
         new C_37rnsjynt.C_95ddunyyj(
            205, I18n.translate("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.diamondMaxHeight
         ),
         new C_37rnsjynt.C_37zmgzpzj(426, I18n.translate("tile.oreLapis.name"), false),
         null,
         new C_37rnsjynt.C_95ddunyyj(206, I18n.translate("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, (float)this.f_26qbqwhnf.lapisSize),
         new C_37rnsjynt.C_95ddunyyj(207, I18n.translate("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, (float)this.f_26qbqwhnf.lapisCount),
         new C_37rnsjynt.C_95ddunyyj(
            208, I18n.translate("createWorld.customize.custom.center"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.lapisMinHeight
         ),
         new C_37rnsjynt.C_95ddunyyj(
            209, I18n.translate("createWorld.customize.custom.spread"), false, this, 0.0F, 255.0F, (float)this.f_26qbqwhnf.lapisMaxHeight
         )
      };
      C_37rnsjynt.C_30sltyycm[] var3 = new C_37rnsjynt.C_30sltyycm[]{
         new C_37rnsjynt.C_95ddunyyj(
            100, I18n.translate("createWorld.customize.custom.mainNoiseScaleX"), false, this, 1.0F, 5000.0F, this.f_26qbqwhnf.mainNoiseScaleX
         ),
         new C_37rnsjynt.C_95ddunyyj(
            101, I18n.translate("createWorld.customize.custom.mainNoiseScaleY"), false, this, 1.0F, 5000.0F, this.f_26qbqwhnf.mainNoiseScaleY
         ),
         new C_37rnsjynt.C_95ddunyyj(
            102, I18n.translate("createWorld.customize.custom.mainNoiseScaleZ"), false, this, 1.0F, 5000.0F, this.f_26qbqwhnf.mainNoiseScaleZ
         ),
         new C_37rnsjynt.C_95ddunyyj(
            103, I18n.translate("createWorld.customize.custom.depthNoiseScaleX"), false, this, 1.0F, 2000.0F, this.f_26qbqwhnf.depthNoisescaleX
         ),
         new C_37rnsjynt.C_95ddunyyj(
            104, I18n.translate("createWorld.customize.custom.depthNoiseScaleZ"), false, this, 1.0F, 2000.0F, this.f_26qbqwhnf.depthNoiseScaleZ
         ),
         new C_37rnsjynt.C_95ddunyyj(
            105, I18n.translate("createWorld.customize.custom.depthNoiseScaleExponent"), false, this, 0.01F, 20.0F, this.f_26qbqwhnf.depthNoiseScaleExponent
         ),
         new C_37rnsjynt.C_95ddunyyj(106, I18n.translate("createWorld.customize.custom.baseSize"), false, this, 1.0F, 25.0F, this.f_26qbqwhnf.baseSize),
         new C_37rnsjynt.C_95ddunyyj(
            107, I18n.translate("createWorld.customize.custom.coordinateScale"), false, this, 1.0F, 6000.0F, this.f_26qbqwhnf.coordinateScale
         ),
         new C_37rnsjynt.C_95ddunyyj(108, I18n.translate("createWorld.customize.custom.heightScale"), false, this, 1.0F, 6000.0F, this.f_26qbqwhnf.heightScale),
         new C_37rnsjynt.C_95ddunyyj(109, I18n.translate("createWorld.customize.custom.stretchY"), false, this, 0.01F, 50.0F, this.f_26qbqwhnf.stretchY),
         new C_37rnsjynt.C_95ddunyyj(
            110, I18n.translate("createWorld.customize.custom.upperLimitScale"), false, this, 1.0F, 5000.0F, this.f_26qbqwhnf.upperLimitScale
         ),
         new C_37rnsjynt.C_95ddunyyj(
            111, I18n.translate("createWorld.customize.custom.lowerLimitScale"), false, this, 1.0F, 5000.0F, this.f_26qbqwhnf.lowerLimitScale
         ),
         new C_37rnsjynt.C_95ddunyyj(
            112, I18n.translate("createWorld.customize.custom.biomeDepthWeight"), false, this, 1.0F, 20.0F, this.f_26qbqwhnf.biomeDepthWeight
         ),
         new C_37rnsjynt.C_95ddunyyj(
            113, I18n.translate("createWorld.customize.custom.biomeDepthOffset"), false, this, 0.0F, 20.0F, this.f_26qbqwhnf.biomeDepthOffset
         ),
         new C_37rnsjynt.C_95ddunyyj(
            114, I18n.translate("createWorld.customize.custom.biomeScaleWeight"), false, this, 1.0F, 20.0F, this.f_26qbqwhnf.biomeScaleWeight
         ),
         new C_37rnsjynt.C_95ddunyyj(
            115, I18n.translate("createWorld.customize.custom.biomeScaleOffset"), false, this, 0.0F, 20.0F, this.f_26qbqwhnf.biomeScaleOffset
         )
      };
      C_37rnsjynt.C_30sltyycm[] var4 = new C_37rnsjynt.C_30sltyycm[]{
         new C_37rnsjynt.C_37zmgzpzj(400, I18n.translate("createWorld.customize.custom.mainNoiseScaleX") + ":", false),
         new C_37rnsjynt.C_04tnjsgih(132, String.format("%5.3f", this.f_26qbqwhnf.mainNoiseScaleX), false, this.f_91jditnvk),
         new C_37rnsjynt.C_37zmgzpzj(401, I18n.translate("createWorld.customize.custom.mainNoiseScaleY") + ":", false),
         new C_37rnsjynt.C_04tnjsgih(133, String.format("%5.3f", this.f_26qbqwhnf.mainNoiseScaleY), false, this.f_91jditnvk),
         new C_37rnsjynt.C_37zmgzpzj(402, I18n.translate("createWorld.customize.custom.mainNoiseScaleZ") + ":", false),
         new C_37rnsjynt.C_04tnjsgih(134, String.format("%5.3f", this.f_26qbqwhnf.mainNoiseScaleZ), false, this.f_91jditnvk),
         new C_37rnsjynt.C_37zmgzpzj(403, I18n.translate("createWorld.customize.custom.depthNoiseScaleX") + ":", false),
         new C_37rnsjynt.C_04tnjsgih(135, String.format("%5.3f", this.f_26qbqwhnf.depthNoisescaleX), false, this.f_91jditnvk),
         new C_37rnsjynt.C_37zmgzpzj(404, I18n.translate("createWorld.customize.custom.depthNoiseScaleZ") + ":", false),
         new C_37rnsjynt.C_04tnjsgih(136, String.format("%5.3f", this.f_26qbqwhnf.depthNoiseScaleZ), false, this.f_91jditnvk),
         new C_37rnsjynt.C_37zmgzpzj(405, I18n.translate("createWorld.customize.custom.depthNoiseScaleExponent") + ":", false),
         new C_37rnsjynt.C_04tnjsgih(137, String.format("%2.3f", this.f_26qbqwhnf.depthNoiseScaleExponent), false, this.f_91jditnvk),
         new C_37rnsjynt.C_37zmgzpzj(406, I18n.translate("createWorld.customize.custom.baseSize") + ":", false),
         new C_37rnsjynt.C_04tnjsgih(138, String.format("%2.3f", this.f_26qbqwhnf.baseSize), false, this.f_91jditnvk),
         new C_37rnsjynt.C_37zmgzpzj(407, I18n.translate("createWorld.customize.custom.coordinateScale") + ":", false),
         new C_37rnsjynt.C_04tnjsgih(139, String.format("%5.3f", this.f_26qbqwhnf.coordinateScale), false, this.f_91jditnvk),
         new C_37rnsjynt.C_37zmgzpzj(408, I18n.translate("createWorld.customize.custom.heightScale") + ":", false),
         new C_37rnsjynt.C_04tnjsgih(140, String.format("%5.3f", this.f_26qbqwhnf.heightScale), false, this.f_91jditnvk),
         new C_37rnsjynt.C_37zmgzpzj(409, I18n.translate("createWorld.customize.custom.stretchY") + ":", false),
         new C_37rnsjynt.C_04tnjsgih(141, String.format("%2.3f", this.f_26qbqwhnf.stretchY), false, this.f_91jditnvk),
         new C_37rnsjynt.C_37zmgzpzj(410, I18n.translate("createWorld.customize.custom.upperLimitScale") + ":", false),
         new C_37rnsjynt.C_04tnjsgih(142, String.format("%5.3f", this.f_26qbqwhnf.upperLimitScale), false, this.f_91jditnvk),
         new C_37rnsjynt.C_37zmgzpzj(411, I18n.translate("createWorld.customize.custom.lowerLimitScale") + ":", false),
         new C_37rnsjynt.C_04tnjsgih(143, String.format("%5.3f", this.f_26qbqwhnf.lowerLimitScale), false, this.f_91jditnvk),
         new C_37rnsjynt.C_37zmgzpzj(412, I18n.translate("createWorld.customize.custom.biomeDepthWeight") + ":", false),
         new C_37rnsjynt.C_04tnjsgih(144, String.format("%2.3f", this.f_26qbqwhnf.biomeDepthWeight), false, this.f_91jditnvk),
         new C_37rnsjynt.C_37zmgzpzj(413, I18n.translate("createWorld.customize.custom.biomeDepthOffset") + ":", false),
         new C_37rnsjynt.C_04tnjsgih(145, String.format("%2.3f", this.f_26qbqwhnf.biomeDepthOffset), false, this.f_91jditnvk),
         new C_37rnsjynt.C_37zmgzpzj(414, I18n.translate("createWorld.customize.custom.biomeScaleWeight") + ":", false),
         new C_37rnsjynt.C_04tnjsgih(146, String.format("%2.3f", this.f_26qbqwhnf.biomeScaleWeight), false, this.f_91jditnvk),
         new C_37rnsjynt.C_37zmgzpzj(415, I18n.translate("createWorld.customize.custom.biomeScaleOffset") + ":", false),
         new C_37rnsjynt.C_04tnjsgih(147, String.format("%2.3f", this.f_26qbqwhnf.biomeScaleOffset), false, this.f_91jditnvk)
      };
      this.f_21fzmvwrz = new C_37rnsjynt(this.client, this.titleWidth, this.height, 32, this.height - 32, 25, this, var1, var2, var3, var4);

      for(int var5 = 0; var5 < 4; ++var5) {
         this.f_50jrrucif[var5] = I18n.translate("createWorld.customize.custom.page" + var5);
      }

      this.m_85wnedcty();
   }

   public String m_59kocieia() {
      return this.f_26qbqwhnf.toString().replace("\n", "");
   }

   public void m_54dgesdof(String string) {
      if (string != null && string.length() != 0) {
         this.f_26qbqwhnf = GeneratorOptions.Factory.fromJson(string);
      } else {
         this.f_26qbqwhnf = new GeneratorOptions.Factory();
      }
   }

   @Override
   public void m_40mldmjxh(int i, String string) {
      float var3 = 0.0F;

      try {
         var3 = Float.parseFloat(string);
      } catch (NumberFormatException var5) {
      }

      float var4 = 0.0F;
      switch(i) {
         case 132:
            var4 = this.f_26qbqwhnf.mainNoiseScaleX = MathHelper.clamp(var3, 1.0F, 5000.0F);
            break;
         case 133:
            var4 = this.f_26qbqwhnf.mainNoiseScaleY = MathHelper.clamp(var3, 1.0F, 5000.0F);
            break;
         case 134:
            var4 = this.f_26qbqwhnf.mainNoiseScaleZ = MathHelper.clamp(var3, 1.0F, 5000.0F);
            break;
         case 135:
            var4 = this.f_26qbqwhnf.depthNoisescaleX = MathHelper.clamp(var3, 1.0F, 2000.0F);
            break;
         case 136:
            var4 = this.f_26qbqwhnf.depthNoiseScaleZ = MathHelper.clamp(var3, 1.0F, 2000.0F);
            break;
         case 137:
            var4 = this.f_26qbqwhnf.depthNoiseScaleExponent = MathHelper.clamp(var3, 0.01F, 20.0F);
            break;
         case 138:
            var4 = this.f_26qbqwhnf.baseSize = MathHelper.clamp(var3, 1.0F, 25.0F);
            break;
         case 139:
            var4 = this.f_26qbqwhnf.coordinateScale = MathHelper.clamp(var3, 1.0F, 6000.0F);
            break;
         case 140:
            var4 = this.f_26qbqwhnf.heightScale = MathHelper.clamp(var3, 1.0F, 6000.0F);
            break;
         case 141:
            var4 = this.f_26qbqwhnf.stretchY = MathHelper.clamp(var3, 0.01F, 50.0F);
            break;
         case 142:
            var4 = this.f_26qbqwhnf.upperLimitScale = MathHelper.clamp(var3, 1.0F, 5000.0F);
            break;
         case 143:
            var4 = this.f_26qbqwhnf.lowerLimitScale = MathHelper.clamp(var3, 1.0F, 5000.0F);
            break;
         case 144:
            var4 = this.f_26qbqwhnf.biomeDepthWeight = MathHelper.clamp(var3, 1.0F, 20.0F);
            break;
         case 145:
            var4 = this.f_26qbqwhnf.biomeDepthOffset = MathHelper.clamp(var3, 0.0F, 20.0F);
            break;
         case 146:
            var4 = this.f_26qbqwhnf.biomeScaleWeight = MathHelper.clamp(var3, 1.0F, 20.0F);
            break;
         case 147:
            var4 = this.f_26qbqwhnf.biomeScaleOffset = MathHelper.clamp(var3, 0.0F, 20.0F);
      }

      if (var4 != var3 && var3 != 0.0F) {
         ((TextFieldWidget)this.f_21fzmvwrz.m_24dbyhzbl(i)).setText(this.m_42dqssits(i, var4));
      }

      ((C_97enwlcph)this.f_21fzmvwrz.m_24dbyhzbl(i - 132 + 100)).m_18ssfzdfp(var4, false);
      if (!this.f_26qbqwhnf.equals(this.f_20bovtupi)) {
         this.f_49somoqoc = true;
      }
   }

   @Override
   public String m_77mlhcero(int i, String string, float f) {
      return string + ": " + this.m_42dqssits(i, f);
   }

   private String m_42dqssits(int i, float f) {
      switch(i) {
         case 100:
         case 101:
         case 102:
         case 103:
         case 104:
         case 107:
         case 108:
         case 110:
         case 111:
         case 132:
         case 133:
         case 134:
         case 135:
         case 136:
         case 139:
         case 140:
         case 142:
         case 143:
            return String.format("%5.3f", f);
         case 105:
         case 106:
         case 109:
         case 112:
         case 113:
         case 114:
         case 115:
         case 137:
         case 138:
         case 141:
         case 144:
         case 145:
         case 146:
         case 147:
            return String.format("%2.3f", f);
         case 116:
         case 117:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         case 123:
         case 124:
         case 125:
         case 126:
         case 127:
         case 128:
         case 129:
         case 130:
         case 131:
         case 148:
         case 149:
         case 150:
         case 151:
         case 152:
         case 153:
         case 154:
         case 155:
         case 156:
         case 157:
         case 158:
         case 159:
         case 160:
         case 161:
         default:
            return String.format("%d", (int)f);
         case 162:
            if (f < 0.0F) {
               return I18n.translate("gui.all");
            } else {
               return (int)f >= Biome.HELL.id ? Biome.getAll()[(int)f + 2].name : Biome.getAll()[(int)f].name;
            }
      }
   }

   @Override
   public void m_10bjktzqq(int i, boolean bl) {
      switch(i) {
         case 148:
            this.f_26qbqwhnf.useCaves = bl;
            break;
         case 149:
            this.f_26qbqwhnf.useDungeons = bl;
            break;
         case 150:
            this.f_26qbqwhnf.useStrongholds = bl;
            break;
         case 151:
            this.f_26qbqwhnf.useVillages = bl;
            break;
         case 152:
            this.f_26qbqwhnf.useMineshafts = bl;
            break;
         case 153:
            this.f_26qbqwhnf.useTemples = bl;
            break;
         case 154:
            this.f_26qbqwhnf.useRavines = bl;
            break;
         case 155:
            this.f_26qbqwhnf.useWaterLakes = bl;
            break;
         case 156:
            this.f_26qbqwhnf.useLavaLakes = bl;
            break;
         case 161:
            this.f_26qbqwhnf.useLavaOceans = bl;
            break;
         case 210:
            this.f_26qbqwhnf.useMonuments = bl;
      }

      if (!this.f_26qbqwhnf.equals(this.f_20bovtupi)) {
         this.f_49somoqoc = true;
      }
   }

   @Override
   public void m_03bvoeuzb(int i, float f) {
      switch(i) {
         case 100:
            this.f_26qbqwhnf.mainNoiseScaleX = f;
            break;
         case 101:
            this.f_26qbqwhnf.mainNoiseScaleY = f;
            break;
         case 102:
            this.f_26qbqwhnf.mainNoiseScaleZ = f;
            break;
         case 103:
            this.f_26qbqwhnf.depthNoisescaleX = f;
            break;
         case 104:
            this.f_26qbqwhnf.depthNoiseScaleZ = f;
            break;
         case 105:
            this.f_26qbqwhnf.depthNoiseScaleExponent = f;
            break;
         case 106:
            this.f_26qbqwhnf.baseSize = f;
            break;
         case 107:
            this.f_26qbqwhnf.coordinateScale = f;
            break;
         case 108:
            this.f_26qbqwhnf.heightScale = f;
            break;
         case 109:
            this.f_26qbqwhnf.stretchY = f;
            break;
         case 110:
            this.f_26qbqwhnf.upperLimitScale = f;
            break;
         case 111:
            this.f_26qbqwhnf.lowerLimitScale = f;
            break;
         case 112:
            this.f_26qbqwhnf.biomeDepthWeight = f;
            break;
         case 113:
            this.f_26qbqwhnf.biomeDepthOffset = f;
            break;
         case 114:
            this.f_26qbqwhnf.biomeScaleWeight = f;
            break;
         case 115:
            this.f_26qbqwhnf.biomeScaleOffset = f;
         case 116:
         case 117:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         case 123:
         case 124:
         case 125:
         case 126:
         case 127:
         case 128:
         case 129:
         case 130:
         case 131:
         case 132:
         case 133:
         case 134:
         case 135:
         case 136:
         case 137:
         case 138:
         case 139:
         case 140:
         case 141:
         case 142:
         case 143:
         case 144:
         case 145:
         case 146:
         case 147:
         case 148:
         case 149:
         case 150:
         case 151:
         case 152:
         case 153:
         case 154:
         case 155:
         case 156:
         case 161:
         case 188:
         default:
            break;
         case 157:
            this.f_26qbqwhnf.dungeonChance = (int)f;
            break;
         case 158:
            this.f_26qbqwhnf.waterLakeChance = (int)f;
            break;
         case 159:
            this.f_26qbqwhnf.lavaLakeChance = (int)f;
            break;
         case 160:
            this.f_26qbqwhnf.seaLevel = (int)f;
            break;
         case 162:
            this.f_26qbqwhnf.fixedBiome = (int)f;
            break;
         case 163:
            this.f_26qbqwhnf.biomeSize = (int)f;
            break;
         case 164:
            this.f_26qbqwhnf.riverSize = (int)f;
            break;
         case 165:
            this.f_26qbqwhnf.dirtSize = (int)f;
            break;
         case 166:
            this.f_26qbqwhnf.dirtCount = (int)f;
            break;
         case 167:
            this.f_26qbqwhnf.dirtMinHeight = (int)f;
            break;
         case 168:
            this.f_26qbqwhnf.dirtMaxHeight = (int)f;
            break;
         case 169:
            this.f_26qbqwhnf.gravelSize = (int)f;
            break;
         case 170:
            this.f_26qbqwhnf.gravelCount = (int)f;
            break;
         case 171:
            this.f_26qbqwhnf.gravelMinHeight = (int)f;
            break;
         case 172:
            this.f_26qbqwhnf.gravelMaxHeight = (int)f;
            break;
         case 173:
            this.f_26qbqwhnf.graniteSize = (int)f;
            break;
         case 174:
            this.f_26qbqwhnf.graniteCount = (int)f;
            break;
         case 175:
            this.f_26qbqwhnf.graniteMinHeight = (int)f;
            break;
         case 176:
            this.f_26qbqwhnf.graniteMaxHeight = (int)f;
            break;
         case 177:
            this.f_26qbqwhnf.dioriteSize = (int)f;
            break;
         case 178:
            this.f_26qbqwhnf.dioriteCount = (int)f;
            break;
         case 179:
            this.f_26qbqwhnf.dioriteMinHeight = (int)f;
            break;
         case 180:
            this.f_26qbqwhnf.dioriteMaxHeight = (int)f;
            break;
         case 181:
            this.f_26qbqwhnf.andesiteSize = (int)f;
            break;
         case 182:
            this.f_26qbqwhnf.andesiteCount = (int)f;
            break;
         case 183:
            this.f_26qbqwhnf.andesiteMinHeight = (int)f;
            break;
         case 184:
            this.f_26qbqwhnf.andesiteMaxHeight = (int)f;
            break;
         case 185:
            this.f_26qbqwhnf.coalSize = (int)f;
            break;
         case 186:
            this.f_26qbqwhnf.coalCount = (int)f;
            break;
         case 187:
            this.f_26qbqwhnf.coalMinHeight = (int)f;
            break;
         case 189:
            this.f_26qbqwhnf.coalMaxHeight = (int)f;
            break;
         case 190:
            this.f_26qbqwhnf.ironSize = (int)f;
            break;
         case 191:
            this.f_26qbqwhnf.ironCount = (int)f;
            break;
         case 192:
            this.f_26qbqwhnf.ironMinHeight = (int)f;
            break;
         case 193:
            this.f_26qbqwhnf.ironMaxHeight = (int)f;
            break;
         case 194:
            this.f_26qbqwhnf.goldSize = (int)f;
            break;
         case 195:
            this.f_26qbqwhnf.goldCount = (int)f;
            break;
         case 196:
            this.f_26qbqwhnf.goldMinHeight = (int)f;
            break;
         case 197:
            this.f_26qbqwhnf.goldMaxHeight = (int)f;
            break;
         case 198:
            this.f_26qbqwhnf.redstoneSize = (int)f;
            break;
         case 199:
            this.f_26qbqwhnf.redstoneCount = (int)f;
            break;
         case 200:
            this.f_26qbqwhnf.redstoneMinHeight = (int)f;
            break;
         case 201:
            this.f_26qbqwhnf.redstoneMaxHeight = (int)f;
            break;
         case 202:
            this.f_26qbqwhnf.diamondSize = (int)f;
            break;
         case 203:
            this.f_26qbqwhnf.diamondCount = (int)f;
            break;
         case 204:
            this.f_26qbqwhnf.diamondMinHeight = (int)f;
            break;
         case 205:
            this.f_26qbqwhnf.diamondMaxHeight = (int)f;
            break;
         case 206:
            this.f_26qbqwhnf.lapisSize = (int)f;
            break;
         case 207:
            this.f_26qbqwhnf.lapisCount = (int)f;
            break;
         case 208:
            this.f_26qbqwhnf.lapisMinHeight = (int)f;
            break;
         case 209:
            this.f_26qbqwhnf.lapisMaxHeight = (int)f;
      }

      if (i >= 100 && i < 116) {
         GuiElement var3 = this.f_21fzmvwrz.m_24dbyhzbl(i - 100 + 132);
         if (var3 != null) {
            ((TextFieldWidget)var3).setText(this.m_42dqssits(i, f));
         }
      }

      if (!this.f_26qbqwhnf.equals(this.f_20bovtupi)) {
         this.f_49somoqoc = true;
      }
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         switch(buttonWidget.id) {
            case 300:
               this.parentScreen.generatorOptions = this.f_26qbqwhnf.toString();
               this.client.openScreen(this.parentScreen);
               break;
            case 301:
               for(int var2 = 0; var2 < this.f_21fzmvwrz.getEntriesSize(); ++var2) {
                  C_37rnsjynt.C_22axpichd var3 = this.f_21fzmvwrz.getEntry(var2);
                  GuiElement var4 = var3.m_50ucuzcll();
                  if (var4 instanceof ButtonWidget) {
                     ButtonWidget var5 = (ButtonWidget)var4;
                     if (var5 instanceof C_97enwlcph) {
                        float var6 = ((C_97enwlcph)var5).m_64efbxezm() * (0.75F + this.f_76fsscyow.nextFloat() * 0.5F)
                           + (this.f_76fsscyow.nextFloat() * 0.1F - 0.05F);
                        ((C_97enwlcph)var5).m_00hqugfaj(MathHelper.clamp(var6, 0.0F, 1.0F));
                     } else if (var5 instanceof C_13fsheoyt) {
                        ((C_13fsheoyt)var5).m_05dnxwzsu(this.f_76fsscyow.nextBoolean());
                     }
                  }

                  GuiElement var8 = var3.m_72ddynkhz();
                  if (var8 instanceof ButtonWidget) {
                     ButtonWidget var9 = (ButtonWidget)var8;
                     if (var9 instanceof C_97enwlcph) {
                        float var7 = ((C_97enwlcph)var9).m_64efbxezm() * (0.75F + this.f_76fsscyow.nextFloat() * 0.5F)
                           + (this.f_76fsscyow.nextFloat() * 0.1F - 0.05F);
                        ((C_97enwlcph)var9).m_00hqugfaj(MathHelper.clamp(var7, 0.0F, 1.0F));
                     } else if (var9 instanceof C_13fsheoyt) {
                        ((C_13fsheoyt)var9).m_05dnxwzsu(this.f_76fsscyow.nextBoolean());
                     }
                  }
               }
               break;
            case 302:
               this.f_21fzmvwrz.m_82ubxazmc();
               this.m_85wnedcty();
               break;
            case 303:
               this.f_21fzmvwrz.m_95jabtuaf();
               this.m_85wnedcty();
               break;
            case 304:
               if (this.f_49somoqoc) {
                  this.m_38cjgkhxj(304);
               }
               break;
            case 305:
               this.client.openScreen(new C_78mxohtpi(this));
               break;
            case 306:
               this.m_24wbsbnrr();
               break;
            case 307:
               this.f_17vyrxzhb = 0;
               this.m_24wbsbnrr();
         }
      }
   }

   private void m_63cfabkfv() {
      this.f_26qbqwhnf.reset();
      this.m_32tbfupgx();
   }

   private void m_38cjgkhxj(int i) {
      this.f_17vyrxzhb = i;
      this.m_40qgvzxfo(true);
   }

   private void m_24wbsbnrr() {
      switch(this.f_17vyrxzhb) {
         case 300:
            this.buttonClicked((C_13fsheoyt)this.f_21fzmvwrz.m_24dbyhzbl(300));
            break;
         case 304:
            this.m_63cfabkfv();
      }

      this.f_17vyrxzhb = 0;
      this.f_43njazqzp = true;
      this.m_40qgvzxfo(false);
   }

   private void m_40qgvzxfo(boolean bl) {
      this.confirmYesButton.visible = bl;
      this.confirmNoButton.visible = bl;
      this.randomizeButton.active = !bl;
      this.doneButton.active = !bl;
      this.prevPageButton.active = !bl;
      this.nextPageButton.active = !bl;
      this.defaultsButton.active = !bl;
      this.presetsButton.active = !bl;
   }

   private void m_85wnedcty() {
      this.prevPageButton.active = this.f_21fzmvwrz.m_43mhodhmv() != 0;
      this.nextPageButton.active = this.f_21fzmvwrz.m_43mhodhmv() != this.f_21fzmvwrz.m_53kgxgxjx() - 1;
      this.basePageCountString = I18n.translate("book.pageIndicator", this.f_21fzmvwrz.m_43mhodhmv() + 1, this.f_21fzmvwrz.m_53kgxgxjx());
      this.baseSettingsTypeString = this.f_50jrrucif[this.f_21fzmvwrz.m_43mhodhmv()];
      this.randomizeButton.active = this.f_21fzmvwrz.m_43mhodhmv() != this.f_21fzmvwrz.m_53kgxgxjx() - 1;
   }

   @Override
   protected void keyPressed(char chr, int key) {
      super.keyPressed(chr, key);
      if (this.f_17vyrxzhb == 0) {
         switch(key) {
            case 200:
               this.m_96mmtahfb(1.0F);
               break;
            case 208:
               this.m_96mmtahfb(-1.0F);
               break;
            default:
               this.f_21fzmvwrz.m_85eqxnfua(chr, key);
         }
      }
   }

   private void m_96mmtahfb(float f) {
      GuiElement var2 = this.f_21fzmvwrz.m_58djpuafj();
      if (var2 instanceof TextFieldWidget) {
         float var3 = f;
         if (Screen.isShiftDown()) {
            var3 = f * 0.1F;
            if (Screen.isControlDown()) {
               var3 *= 0.1F;
            }
         } else if (Screen.isControlDown()) {
            var3 = f * 10.0F;
            if (Screen.isAltDown()) {
               var3 *= 10.0F;
            }
         }

         TextFieldWidget var4 = (TextFieldWidget)var2;
         Float var5 = Floats.tryParse(var4.getText());
         if (var5 != null) {
            var5 = var5 + var3;
            int var6 = var4.m_80ivbzupk();
            String var7 = this.m_42dqssits(var4.m_80ivbzupk(), var5);
            var4.setText(var7);
            this.m_40mldmjxh(var6, var7);
         }
      }
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (this.f_17vyrxzhb == 0 && !this.f_43njazqzp) {
         this.f_21fzmvwrz.mouseClicked(mouseX, mouseY, mouseButton);
      }
   }

   @Override
   protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
      super.mouseReleased(mouseX, mouseY, mouseButton);
      if (this.f_43njazqzp) {
         this.f_43njazqzp = false;
      } else if (this.f_17vyrxzhb == 0) {
         this.f_21fzmvwrz.mouseReleased(mouseX, mouseY, mouseButton);
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.renderBackground();
      this.f_21fzmvwrz.render(mouseX, mouseY, tickDelta);
      this.drawCenteredString(this.textRenderer, this.screenTitle, this.titleWidth / 2, 2, 16777215);
      this.drawCenteredString(this.textRenderer, this.basePageCountString, this.titleWidth / 2, 12, 16777215);
      this.drawCenteredString(this.textRenderer, this.baseSettingsTypeString, this.titleWidth / 2, 22, 16777215);
      super.render(mouseX, mouseY, tickDelta);
      if (this.f_17vyrxzhb != 0) {
         fill(0, 0, this.titleWidth, this.height, Integer.MIN_VALUE);
         this.drawHorizontalLine(this.titleWidth / 2 - 91, this.titleWidth / 2 + 91, 99, -2039584);
         this.drawHorizontalLine(this.titleWidth / 2 - 91, this.titleWidth / 2 + 91, 186, -6250336);
         this.drawVerticalLine(this.titleWidth / 2 - 91, 99, 186, -2039584);
         this.drawVerticalLine(this.titleWidth / 2 + 91, 99, 186, -6250336);
         float var4 = 85.0F;
         float var5 = 180.0F;
         GlStateManager.disableLighting();
         GlStateManager.disableFog();
         Tessellator var6 = Tessellator.getInstance();
         BufferBuilder var7 = var6.getBufferBuilder();
         this.client.getTextureManager().bind(OPTIONS_BACKGROUND);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         float var8 = 32.0F;
         var7.start();
         var7.color(4210752);
         var7.vertex((double)(this.titleWidth / 2 - 90), 185.0, 0.0, 0.0, 2.65625);
         var7.vertex((double)(this.titleWidth / 2 + 90), 185.0, 0.0, 5.625, 2.65625);
         var7.vertex((double)(this.titleWidth / 2 + 90), 100.0, 0.0, 5.625, 0.0);
         var7.vertex((double)(this.titleWidth / 2 - 90), 100.0, 0.0, 0.0, 0.0);
         var6.end();
         this.drawCenteredString(this.textRenderer, I18n.translate("createWorld.customize.custom.confirmTitle"), this.titleWidth / 2, 105, 16777215);
         this.drawCenteredString(this.textRenderer, I18n.translate("createWorld.customize.custom.confirm1"), this.titleWidth / 2, 125, 16777215);
         this.drawCenteredString(this.textRenderer, I18n.translate("createWorld.customize.custom.confirm2"), this.titleWidth / 2, 135, 16777215);
         this.confirmYesButton.render(this.client, mouseX, mouseY);
         this.confirmNoButton.render(this.client, mouseX, mouseY);
      }
   }
}
