package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.Model;
import net.minecraft.entity.living.mob.passive.animal.tamable.OcelotEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class CatRenderer extends MobRenderer {
   private static final Identifier BLACK_TEXTURE = new Identifier("textures/entity/cat/black.png");
   private static final Identifier OCELOT_TEXTURE = new Identifier("textures/entity/cat/ocelot.png");
   private static final Identifier RED_TEXTUIRE = new Identifier("textures/entity/cat/red.png");
   private static final Identifier SIAMESE_TEXTURE = new Identifier("textures/entity/cat/siamese.png");

   public CatRenderer(EntityRenderDispatcher c_28wsgstbh, Model c_56prnndub, float f) {
      super(c_28wsgstbh, c_56prnndub, f);
   }

   protected Identifier getTexture(OcelotEntity c_18rizzgoj) {
      switch(c_18rizzgoj.getCatVariant()) {
         case 0:
         default:
            return OCELOT_TEXTURE;
         case 1:
            return BLACK_TEXTURE;
         case 2:
            return RED_TEXTUIRE;
         case 3:
            return SIAMESE_TEXTURE;
      }
   }

   protected void scale(OcelotEntity c_18rizzgoj, float f) {
      super.scale(c_18rizzgoj, f);
      if (c_18rizzgoj.isTamed()) {
         GlStateManager.scalef(0.8F, 0.8F, 0.8F);
      }
   }
}
