package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.layer.WitherChargeLayer;
import net.minecraft.client.render.model.entity.WitherModel;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.hostile.boss.BossBar;
import net.minecraft.entity.living.mob.hostile.boss.WitherEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class WitherRenderer extends MobRenderer {
   private static final Identifier INVULNERABLE_TEXTURE = new Identifier("textures/entity/wither/wither_invulnerable.png");
   private static final Identifier TEXTURE = new Identifier("textures/entity/wither/wither.png");

   public WitherRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh, new WitherModel(0.0F), 1.0F);
      this.addLayer(new WitherChargeLayer(this));
   }

   public void render(WitherEntity c_34mvphgih, double d, double e, double f, float g, float h) {
      BossBar.update(c_34mvphgih, true);
      super.render((MobEntity)c_34mvphgih, d, e, f, g, h);
   }

   protected Identifier getTexture(WitherEntity c_34mvphgih) {
      int var2 = c_34mvphgih.getInvulerabilityTimer();
      return var2 > 0 && (var2 > 80 || var2 / 5 % 2 != 1) ? INVULNERABLE_TEXTURE : TEXTURE;
   }

   protected void scale(WitherEntity c_34mvphgih, float f) {
      float var3 = 2.0F;
      int var4 = c_34mvphgih.getInvulerabilityTimer();
      if (var4 > 0) {
         var3 -= ((float)var4 - f) / 220.0F * 0.5F;
      }

      GlStateManager.scalef(var3, var3, var3);
   }
}
