package net.minecraft.client.render.model.entity;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.layer.EndermanCarriedBlockLayer;
import net.minecraft.client.render.entity.layer.EndermanEyesLayer;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.hostile.EndermanEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class EndermanRenderer extends MobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/enderman/enderman.png");
   private EndermanModel model;
   private Random random = new Random();

   public EndermanRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh, new EndermanModel(0.0F), 0.5F);
      this.model = (EndermanModel)super.model;
      this.addLayer(new EndermanEyesLayer(this));
      this.addLayer(new EndermanCarriedBlockLayer(this));
   }

   public void render(EndermanEntity c_12ysvaaev, double d, double e, double f, float g, float h) {
      this.model.carryingBlock = c_12ysvaaev.getCarriedBlock().getBlock().getMaterial() != Material.AIR;
      this.model.angry = c_12ysvaaev.isAngry();
      if (c_12ysvaaev.isAngry()) {
         double var10 = 0.02;
         d += this.random.nextGaussian() * var10;
         f += this.random.nextGaussian() * var10;
      }

      super.render((MobEntity)c_12ysvaaev, d, e, f, g, h);
   }

   protected Identifier getTexture(EndermanEntity c_12ysvaaev) {
      return TEXTURE;
   }
}
