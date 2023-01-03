package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.client.render.entity.layer.ArmorLayer;
import net.minecraft.client.render.entity.layer.CapeLayer;
import net.minecraft.client.render.entity.layer.Deadmou5Layer;
import net.minecraft.client.render.entity.layer.HeldItemLayer;
import net.minecraft.client.render.entity.layer.StuckArrowLayer;
import net.minecraft.client.render.entity.layer.WornSkullLayer;
import net.minecraft.client.render.model.PlayerModelPart;
import net.minecraft.client.render.model.entity.PlayerModel;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.resource.Identifier;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class PlayerRenderer extends LivingEntityRenderer {
   private boolean thinArms;

   public PlayerRenderer(EntityRenderDispatcher c_28wsgstbh) {
      this(c_28wsgstbh, false);
   }

   public PlayerRenderer(EntityRenderDispatcher dispatcher, boolean thinArms) {
      super(dispatcher, new PlayerModel(0.0F, thinArms), 0.5F);
      this.thinArms = thinArms;
      this.addLayer(new ArmorLayer(this));
      this.addLayer(new HeldItemLayer(this));
      this.addLayer(new StuckArrowLayer(this));
      this.addLayer(new Deadmou5Layer(this));
      this.addLayer(new CapeLayer(this));
      this.addLayer(new WornSkullLayer(this.getModel().head));
   }

   public PlayerModel getModel() {
      return (PlayerModel)super.getModel();
   }

   public void render(ClientPlayerEntity c_95zrfkavi, double d, double e, double f, float g, float h) {
      if (!c_95zrfkavi.m_08txklcju() || this.dispatcher.camera == c_95zrfkavi) {
         double var10 = e;
         if (c_95zrfkavi.isSneaking() && !(c_95zrfkavi instanceof LocalClientPlayerEntity)) {
            var10 = e - 0.125;
         }

         this.m_46lexmkwu(c_95zrfkavi);
         super.render((LivingEntity)c_95zrfkavi, d, var10, f, g, h);
      }
   }

   private void m_46lexmkwu(ClientPlayerEntity c_95zrfkavi) {
      PlayerModel var2 = this.getModel();
      if (c_95zrfkavi.isSpectator()) {
         var2.setVisible(false);
         var2.head.visible = true;
         var2.hat.visible = true;
      } else {
         ItemStack var3 = c_95zrfkavi.inventory.getMainHandStack();
         var2.setVisible(true);
         var2.hat.visible = c_95zrfkavi.hidesCape(PlayerModelPart.HAT);
         var2.f_80hdikubr.visible = c_95zrfkavi.hidesCape(PlayerModelPart.JACKET);
         var2.f_39rzcdjce.visible = c_95zrfkavi.hidesCape(PlayerModelPart.LEFT_PANTS_LEG);
         var2.f_04tvtlvbb.visible = c_95zrfkavi.hidesCape(PlayerModelPart.RIGHT_PANTS_LEG);
         var2.f_55ogitseo.visible = c_95zrfkavi.hidesCape(PlayerModelPart.LEFT_SLEEVE);
         var2.f_17nzqhcqy.visible = c_95zrfkavi.hidesCape(PlayerModelPart.RIGHT_SLEEVE);
         var2.leftHandItemId = 0;
         var2.aimingBow = false;
         var2.sneaking = c_95zrfkavi.isSneaking();
         if (var3 == null) {
            var2.rightHandItemId = 0;
         } else {
            var2.rightHandItemId = 1;
            if (c_95zrfkavi.getItemUseTimer() > 0) {
               UseAction var4 = var3.getUseAction();
               if (var4 == UseAction.BLOCK) {
                  var2.rightHandItemId = 3;
               } else if (var4 == UseAction.BOW) {
                  var2.aimingBow = true;
               }
            }
         }
      }
   }

   protected Identifier getTexture(ClientPlayerEntity c_95zrfkavi) {
      return c_95zrfkavi.getSkinTexture();
   }

   @Override
   public void m_81npivqro() {
      GlStateManager.translatef(0.0F, 0.1875F, 0.0F);
   }

   protected void scale(ClientPlayerEntity c_95zrfkavi, float f) {
      float var3 = 0.9375F;
      GlStateManager.scalef(var3, var3, var3);
   }

   protected void renderNameTags(ClientPlayerEntity c_95zrfkavi, double d, double e, double f, String string, float g, double h) {
      if (h < 100.0) {
         Scoreboard var12 = c_95zrfkavi.getScoreboard();
         ScoreboardObjective var13 = var12.getDisplayObjective(2);
         if (var13 != null) {
            ScoreboardScore var14 = var12.getScore(c_95zrfkavi.getName(), var13);
            this.renderNameTag(c_95zrfkavi, var14.get() + " " + var13.getDisplayName(), d, e, f, 64);
            e += (double)((float)this.getFontRenderer().fontHeight * 1.15F * g);
         }
      }

      super.renderNameTags(c_95zrfkavi, d, e, f, string, g, h);
   }

   public void renderPlayerHandModel(ClientPlayerEntity player) {
      float var2 = 1.0F;
      GlStateManager.color3f(var2, var2, var2);
      PlayerModel var3 = this.getModel();
      this.m_46lexmkwu(player);
      var3.handSwingProgress = 0.0F;
      var3.sneaking = false;
      var3.setAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
      var3.m_37hhumyue();
   }

   public void m_23uzhiwco(ClientPlayerEntity c_95zrfkavi) {
      float var2 = 1.0F;
      GlStateManager.color3f(var2, var2, var2);
      PlayerModel var3 = this.getModel();
      this.m_46lexmkwu(c_95zrfkavi);
      var3.sneaking = false;
      var3.handSwingProgress = 0.0F;
      var3.setAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, c_95zrfkavi);
      var3.m_95hdkquge();
   }

   protected void applyTranslation(ClientPlayerEntity c_95zrfkavi, double d, double e, double f) {
      if (c_95zrfkavi.isAlive() && c_95zrfkavi.isSleeping()) {
         super.applyTranslation(c_95zrfkavi, d + (double)c_95zrfkavi.sleepOffsetX, e + (double)c_95zrfkavi.sleepOffsetY, f + (double)c_95zrfkavi.sleepOffsetZ);
      } else {
         super.applyTranslation(c_95zrfkavi, d, e, f);
      }
   }

   protected void applyRotation(ClientPlayerEntity c_95zrfkavi, float f, float g, float h) {
      if (c_95zrfkavi.isAlive() && c_95zrfkavi.isSleeping()) {
         GlStateManager.rotatef(c_95zrfkavi.getRespawnDirection(), 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(this.getYawWhileDead(c_95zrfkavi), 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(270.0F, 0.0F, 1.0F, 0.0F);
      } else {
         super.applyRotation(c_95zrfkavi, f, g, h);
      }
   }
}
