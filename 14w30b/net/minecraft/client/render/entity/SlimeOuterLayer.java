package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.layer.EntityRenderLayer;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.entity.SlimeModel;
import net.minecraft.entity.living.mob.SlimeEntity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SlimeOuterLayer implements EntityRenderLayer {
   private final SlimeRenderer parent;
   private final Model model = new SlimeModel(0);

   public SlimeOuterLayer(SlimeRenderer parent) {
      this.parent = parent;
   }

   public void render(SlimeEntity c_66oqmtrvn, float f, float g, float h, float i, float j, float k, float l) {
      if (!c_66oqmtrvn.isInvisible()) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableNormalize();
         GlStateManager.disableBlend();
         GlStateManager.blendFunc(770, 771);
         this.model.copyPropertiesFrom(this.parent.getModel());
         this.model.render(c_66oqmtrvn, f, g, i, j, k, l);
         GlStateManager.enableBlend();
         GlStateManager.disableNormalize();
      }
   }

   @Override
   public boolean colorsWhenDamaged() {
      return true;
   }
}
