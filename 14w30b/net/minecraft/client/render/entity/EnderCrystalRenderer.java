package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.block.entity.EnderCrystalModel;
import net.minecraft.entity.EnderCrystalEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class EnderCrystalRenderer extends EntityRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/endercrystal/endercrystal.png");
   private Model model = new EnderCrystalModel(0.0F, true);

   public EnderCrystalRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh);
      this.shadowSize = 0.5F;
   }

   public void render(EnderCrystalEntity c_08nhizqwk, double d, double e, double f, float g, float h) {
      float var10 = (float)c_08nhizqwk.age + h;
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)d, (float)e, (float)f);
      this.bindTexture(TEXTURE);
      float var11 = MathHelper.sin(var10 * 0.2F) / 2.0F + 0.5F;
      var11 = var11 * var11 + var11;
      this.model.render(c_08nhizqwk, 0.0F, var10 * 3.0F, var11 * 0.2F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
      super.render(c_08nhizqwk, d, e, f, g, h);
   }

   protected Identifier getTexture(EnderCrystalEntity c_08nhizqwk) {
      return TEXTURE;
   }
}
