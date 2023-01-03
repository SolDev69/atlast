package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.Model;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.SlimeEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SlimeRenderer extends MobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/slime/slime.png");

   public SlimeRenderer(EntityRenderDispatcher c_28wsgstbh, Model c_56prnndub, float f) {
      super(c_28wsgstbh, c_56prnndub, f);
      this.addLayer(new SlimeOuterLayer(this));
   }

   public void render(SlimeEntity c_66oqmtrvn, double d, double e, double f, float g, float h) {
      this.shadowSize = 0.25F * (float)c_66oqmtrvn.getSize();
      super.render((MobEntity)c_66oqmtrvn, d, e, f, g, h);
   }

   protected void scale(SlimeEntity c_66oqmtrvn, float f) {
      float var3 = (float)c_66oqmtrvn.getSize();
      float var4 = (c_66oqmtrvn.lastStretch + (c_66oqmtrvn.stretch - c_66oqmtrvn.lastStretch) * f) / (var3 * 0.5F + 1.0F);
      float var5 = 1.0F / (var4 + 1.0F);
      GlStateManager.scalef(var5 * var3, 1.0F / var5 * var3, var5 * var3);
   }

   protected Identifier getTexture(SlimeEntity c_66oqmtrvn) {
      return TEXTURE;
   }
}
