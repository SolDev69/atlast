package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.entity.BoatModel;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BoatRenderer extends EntityRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/boat.png");
   protected Model model = new BoatModel();

   public BoatRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh);
      this.shadowSize = 0.5F;
   }

   public void render(BoatEntity c_15vvwsysd, double d, double e, double f, float g, float h) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)d, (float)e + 0.25F, (float)f);
      GlStateManager.rotatef(180.0F - g, 0.0F, 1.0F, 0.0F);
      float var10 = (float)c_15vvwsysd.getBreakingWindow() - h;
      float var11 = c_15vvwsysd.getDamage() - h;
      if (var11 < 0.0F) {
         var11 = 0.0F;
      }

      if (var10 > 0.0F) {
         GlStateManager.rotatef(MathHelper.sin(var10) * var10 * var11 / 10.0F * (float)c_15vvwsysd.getAnimationSide(), 1.0F, 0.0F, 0.0F);
      }

      float var12 = 0.75F;
      GlStateManager.scalef(var12, var12, var12);
      GlStateManager.scalef(1.0F / var12, 1.0F / var12, 1.0F / var12);
      this.bindTexture(c_15vvwsysd);
      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      this.model.render(c_15vvwsysd, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
      super.render(c_15vvwsysd, d, e, f, g, h);
   }

   protected Identifier getTexture(BoatEntity c_15vvwsysd) {
      return TEXTURE;
   }
}
