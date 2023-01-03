package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.layer.SnowGolemHeadLayer;
import net.minecraft.client.render.model.entity.SnowGolemModel;
import net.minecraft.entity.living.mob.SnowGolemEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SnowGolemRenderer extends MobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/snowman.png");

   public SnowGolemRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh, new SnowGolemModel(), 0.5F);
      this.addLayer(new SnowGolemHeadLayer(this));
   }

   protected Identifier getTexture(SnowGolemEntity c_64nrvqmxb) {
      return TEXTURE;
   }

   public SnowGolemModel getModel() {
      return (SnowGolemModel)super.getModel();
   }
}
