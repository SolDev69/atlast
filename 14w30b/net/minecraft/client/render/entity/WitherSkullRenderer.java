package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.block.entity.SkullModel;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class WitherSkullRenderer extends EntityRenderer {
   private static final Identifier INVULNERABLE_TEXTURE = new Identifier("textures/entity/wither/wither_invulnerable.png");
   private static final Identifier TEXTURE = new Identifier("textures/entity/wither/wither.png");
   private final SkullModel model = new SkullModel();

   public WitherSkullRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh);
   }

   private float getYaw(float prevYaw, float yaw, float tickDelta) {
      float var4 = yaw - prevYaw;

      while(var4 < -180.0F) {
         var4 += 360.0F;
      }

      while(var4 >= 180.0F) {
         var4 -= 360.0F;
      }

      return prevYaw + tickDelta * var4;
   }

   public void render(WitherSkullEntity c_13holikot, double d, double e, double f, float g, float h) {
      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      float var10 = this.getYaw(c_13holikot.prevYaw, c_13holikot.yaw, h);
      float var11 = c_13holikot.prevPitch + (c_13holikot.pitch - c_13holikot.prevPitch) * h;
      GlStateManager.translatef((float)d, (float)e, (float)f);
      float var12 = 0.0625F;
      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      GlStateManager.enableAlphaTest();
      this.bindTexture(c_13holikot);
      this.model.render(c_13holikot, 0.0F, 0.0F, 0.0F, var10, var11, var12);
      GlStateManager.popMatrix();
      super.render(c_13holikot, d, e, f, g, h);
   }

   protected Identifier getTexture(WitherSkullEntity c_13holikot) {
      return c_13holikot.isCharged() ? INVULNERABLE_TEXTURE : TEXTURE;
   }
}
