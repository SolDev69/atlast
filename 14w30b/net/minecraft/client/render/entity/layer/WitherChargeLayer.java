package net.minecraft.client.render.entity.layer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.WitherRenderer;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.entity.WitherModel;
import net.minecraft.entity.living.mob.hostile.boss.WitherEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class WitherChargeLayer implements EntityRenderLayer {
   private static final Identifier CHARGE_TEXTURE = new Identifier("textures/entity/wither/wither_invulnerable.png");
   private final WitherRenderer parent;
   private final Model f_51iyryhzl = new WitherModel(0.5F);

   public WitherChargeLayer(WitherRenderer parent) {
      this.parent = parent;
   }

   public void render(WitherEntity c_34mvphgih, float f, float g, float h, float i, float j, float k, float l) {
      if (c_34mvphgih.isAtHalfHealth()) {
         GlStateManager.depthMask(!c_34mvphgih.isInvisible());
         this.parent.bindTexture(CHARGE_TEXTURE);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         float var9 = (float)c_34mvphgih.time + h;
         float var10 = MathHelper.cos(var9 * 0.02F) * 3.0F;
         float var11 = var9 * 0.01F;
         GlStateManager.translatef(var10, var11, 0.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.disableBlend();
         float var12 = 0.5F;
         GlStateManager.color4f(var12, var12, var12, 1.0F);
         GlStateManager.disableLighting();
         GlStateManager.blendFunc(1, 1);
         this.f_51iyryhzl.copyPropertiesFrom(this.f_51iyryhzl);
         this.f_51iyryhzl.render(c_34mvphgih, f, g, i, j, k, l);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         GlStateManager.enableBlend();
      }
   }

   @Override
   public boolean colorsWhenDamaged() {
      return false;
   }
}
