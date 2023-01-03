package net.minecraft.client.render.entity;

import net.minecraft.client.render.model.Model;
import net.minecraft.entity.living.mob.passive.animal.ChickenEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ChickenRenderer extends MobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/chicken.png");

   public ChickenRenderer(EntityRenderDispatcher c_28wsgstbh, Model c_56prnndub, float f) {
      super(c_28wsgstbh, c_56prnndub, f);
   }

   protected Identifier getTexture(ChickenEntity c_21yhdvaha) {
      return TEXTURE;
   }

   protected float getEntityAge(ChickenEntity c_21yhdvaha, float f) {
      float var3 = c_21yhdvaha.prevFlapProgress + (c_21yhdvaha.flapProgress - c_21yhdvaha.prevFlapProgress) * f;
      float var4 = c_21yhdvaha.prevMaxWingDeviation + (c_21yhdvaha.maxWingDeviation - c_21yhdvaha.prevMaxWingDeviation) * f;
      return (MathHelper.sin(var3) + 1.0F) * var4;
   }
}
