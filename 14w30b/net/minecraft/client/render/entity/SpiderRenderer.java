package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.layer.SpiderEyesLayer;
import net.minecraft.client.render.model.entity.SpiderModel;
import net.minecraft.entity.living.mob.hostile.SpiderEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SpiderRenderer extends MobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/spider/spider.png");

   public SpiderRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh, new SpiderModel(), 1.0F);
      this.addLayer(new SpiderEyesLayer(this));
   }

   protected float getYawWhileDead(SpiderEntity c_83fmezsnj) {
      return 180.0F;
   }

   protected Identifier getTexture(SpiderEntity c_83fmezsnj) {
      return TEXTURE;
   }
}
