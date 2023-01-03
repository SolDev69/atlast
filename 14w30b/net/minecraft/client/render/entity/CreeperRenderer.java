package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.layer.CreeperChargeLayer;
import net.minecraft.client.render.model.entity.CreeperModel;
import net.minecraft.entity.living.mob.hostile.CreeperEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class CreeperRenderer extends MobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/creeper/creeper.png");

   public CreeperRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh, new CreeperModel(), 0.5F);
      this.addLayer(new CreeperChargeLayer(this));
   }

   protected void scale(CreeperEntity c_24qhtkqyz, float f) {
      float var3 = c_24qhtkqyz.getFuseTime(f);
      float var4 = 1.0F + MathHelper.sin(var3 * 100.0F) * var3 * 0.01F;
      var3 = MathHelper.clamp(var3, 0.0F, 1.0F);
      var3 *= var3;
      var3 *= var3;
      float var5 = (1.0F + var3 * 0.4F) * var4;
      float var6 = (1.0F + var3 * 0.1F) / var4;
      GlStateManager.scalef(var5, var6, var5);
   }

   protected int getCreeperFuseTime(CreeperEntity c_24qhtkqyz, float f, float g) {
      float var4 = c_24qhtkqyz.getFuseTime(g);
      if ((int)(var4 * 10.0F) % 2 == 0) {
         return 0;
      } else {
         int var5 = (int)(var4 * 0.2F * 255.0F);
         var5 = MathHelper.clamp(var5, 0, 255);
         return var5 << 24 | 16777215;
      }
   }

   protected Identifier getTexture(CreeperEntity c_24qhtkqyz) {
      return TEXTURE;
   }
}
