package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.layer.ArmorLayer;
import net.minecraft.client.render.entity.layer.HeldItemLayer;
import net.minecraft.client.render.model.entity.ZombieModel;
import net.minecraft.entity.living.mob.hostile.ZombiePigmanEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ZombiePigmanRenderer extends UndeadMobRenderer {
   private static final Identifier f_55lrwfigs = new Identifier("textures/entity/zombie_pigman.png");

   public ZombiePigmanRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh, new ZombieModel(), 0.5F, 1.0F);
      this.addLayer(new HeldItemLayer(this));
      this.addLayer(new ArmorLayer(this) {
         @Override
         protected void hideAll() {
            this.innerModel = new ZombieModel(0.5F, true);
            this.outerModel = new ZombieModel(1.0F, true);
         }
      });
   }

   protected Identifier getTexture(ZombiePigmanEntity c_45zomsjnb) {
      return f_55lrwfigs;
   }
}
