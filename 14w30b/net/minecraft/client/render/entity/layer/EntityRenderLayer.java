package net.minecraft.client.render.entity.layer;

import net.minecraft.entity.living.LivingEntity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface EntityRenderLayer {
   void render(LivingEntity entity, float handSwingAmount, float handSwing, float tickDelta, float age, float headYaw, float headPitch, float scale);

   boolean colorsWhenDamaged();
}
