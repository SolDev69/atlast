package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.layer.WolfCollarLayer;
import net.minecraft.client.render.model.Model;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.passive.animal.tamable.WolfEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class WolfRenderer extends MobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/wolf/wolf.png");
   private static final Identifier TAME_TEXTURE = new Identifier("textures/entity/wolf/wolf_tame.png");
   private static final Identifier ANGRY_TEXTURE = new Identifier("textures/entity/wolf/wolf_angry.png");

   public WolfRenderer(EntityRenderDispatcher c_28wsgstbh, Model c_56prnndub, float f) {
      super(c_28wsgstbh, c_56prnndub, f);
      this.addLayer(new WolfCollarLayer(this));
   }

   protected float getEntityAge(WolfEntity c_68kzbahax, float f) {
      return c_68kzbahax.age();
   }

   public void render(WolfEntity c_68kzbahax, double d, double e, double f, float g, float h) {
      if (c_68kzbahax.isFurWet()) {
         float var10 = c_68kzbahax.getBrightness(h) * c_68kzbahax.shakeLerp(h);
         GlStateManager.color3f(var10, var10, var10);
      }

      super.render((MobEntity)c_68kzbahax, d, e, f, g, h);
   }

   protected Identifier getTexture(WolfEntity c_68kzbahax) {
      if (c_68kzbahax.isTamed()) {
         return TAME_TEXTURE;
      } else {
         return c_68kzbahax.isAngry() ? ANGRY_TEXTURE : TEXTURE;
      }
   }
}
