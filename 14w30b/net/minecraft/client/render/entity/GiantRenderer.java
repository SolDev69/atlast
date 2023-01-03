package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.layer.ArmorLayer;
import net.minecraft.client.render.entity.layer.HeldItemLayer;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.entity.ZombieModel;
import net.minecraft.entity.living.mob.hostile.GiantEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class GiantRenderer extends MobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/zombie/zombie.png");
   private float size;

   public GiantRenderer(EntityRenderDispatcher model, Model shadowSize, float size, float g) {
      super(model, shadowSize, size * g);
      this.size = g;
      this.addLayer(new HeldItemLayer(this));
      this.addLayer(new ArmorLayer(this) {
         @Override
         protected void hideAll() {
            this.innerModel = new ZombieModel(0.5F, true);
            this.outerModel = new ZombieModel(1.0F, true);
         }
      });
   }

   @Override
   public void m_81npivqro() {
      GlStateManager.translatef(0.0F, 0.1875F, 0.0F);
   }

   protected void scale(GiantEntity c_39lcxxjvm, float f) {
      GlStateManager.scalef(this.size, this.size, this.size);
   }

   protected Identifier getTexture(GiantEntity c_39lcxxjvm) {
      return TEXTURE;
   }
}
