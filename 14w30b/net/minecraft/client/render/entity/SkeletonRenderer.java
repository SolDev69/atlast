package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.layer.ArmorLayer;
import net.minecraft.client.render.entity.layer.HeldItemLayer;
import net.minecraft.client.render.model.entity.SkeletonModel;
import net.minecraft.entity.living.mob.hostile.SkeletonEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SkeletonRenderer extends UndeadMobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/skeleton/skeleton.png");
   private static final Identifier WITHER_SKELETON_TEXTURE = new Identifier("textures/entity/skeleton/wither_skeleton.png");

   public SkeletonRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh, new SkeletonModel(), 0.5F);
      this.addLayer(new HeldItemLayer(this));
      this.addLayer(new ArmorLayer(this) {
         @Override
         protected void hideAll() {
            this.innerModel = new SkeletonModel(0.5F, true);
            this.outerModel = new SkeletonModel(1.0F, true);
         }
      });
   }

   protected void scale(SkeletonEntity c_85bwkvsib, float f) {
      if (c_85bwkvsib.getType() == 1) {
         GlStateManager.scalef(1.2F, 1.2F, 1.2F);
      }
   }

   @Override
   public void m_81npivqro() {
      GlStateManager.translatef(0.09375F, 0.1875F, 0.0F);
   }

   protected Identifier getTexture(SkeletonEntity c_85bwkvsib) {
      return c_85bwkvsib.getType() == 1 ? WITHER_SKELETON_TEXTURE : TEXTURE;
   }
}
