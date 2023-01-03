package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.entity.LeashModel;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class LeadKnotRenderer extends EntityRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/lead_knot.png");
   private LeashModel model = new LeashModel();

   public LeadKnotRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh);
   }

   public void render(LeadKnotEntity c_40zaikzez, double d, double e, double f, float g, float h) {
      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      GlStateManager.translatef((float)d, (float)e, (float)f);
      float var10 = 0.0625F;
      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      GlStateManager.enableAlphaTest();
      this.bindTexture(c_40zaikzez);
      this.model.render(c_40zaikzez, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, var10);
      GlStateManager.popMatrix();
      super.render(c_40zaikzez, d, e, f, g, h);
   }

   protected Identifier getTexture(LeadKnotEntity c_40zaikzez) {
      return TEXTURE;
   }
}
