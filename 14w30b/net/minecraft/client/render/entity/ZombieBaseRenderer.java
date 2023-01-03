package net.minecraft.client.render.entity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.C_17pxotddv;
import net.minecraft.client.render.entity.layer.ArmorLayer;
import net.minecraft.client.render.entity.layer.EntityRenderLayer;
import net.minecraft.client.render.entity.layer.HeldItemLayer;
import net.minecraft.client.render.entity.layer.WornSkullLayer;
import net.minecraft.client.render.model.entity.HumanoidModel;
import net.minecraft.client.render.model.entity.ZombieModel;
import net.minecraft.client.render.model.entity.ZombieVillagerModel;
import net.minecraft.entity.living.mob.hostile.ZombieEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ZombieBaseRenderer extends UndeadMobRenderer {
   private static final Identifier TEXTURE = new Identifier("textures/entity/zombie/zombie.png");
   private static final Identifier ZOMBIE_VILLAGER_TEXTURE = new Identifier("textures/entity/zombie/zombie_villager.png");
   private final HumanoidModel zombieModel;
   private final ZombieVillagerModel zombieVillagerModel;
   private final List zombieVillagerModel2;
   private final List zombieVillagerModel3;

   public ZombieBaseRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh, new ZombieModel(), 0.5F, 1.0F);
      EntityRenderLayer var2 = (EntityRenderLayer)this.layers.get(0);
      this.zombieModel = this.f_35kzpvqyv;
      this.zombieVillagerModel = new ZombieVillagerModel();
      this.addLayer(new HeldItemLayer(this));
      ArmorLayer var3 = new ArmorLayer(this) {
         @Override
         protected void hideAll() {
            this.innerModel = new ZombieModel(0.5F, true);
            this.outerModel = new ZombieModel(1.0F, true);
         }
      };
      this.addLayer(var3);
      this.zombieVillagerModel3 = Lists.newArrayList(this.layers);
      if (var2 instanceof WornSkullLayer) {
         this.removeLayer(var2);
         this.addLayer(new WornSkullLayer(this.zombieVillagerModel.head));
      }

      this.removeLayer(var3);
      this.addLayer(new C_17pxotddv(this));
      this.zombieVillagerModel2 = Lists.newArrayList(this.layers);
   }

   public void render(ZombieEntity c_96bgttdiz, double d, double e, double f, float g, float h) {
      this.updateModels(c_96bgttdiz);
      super.render(c_96bgttdiz, d, e, f, g, h);
   }

   protected Identifier getTexture(ZombieEntity c_96bgttdiz) {
      return c_96bgttdiz.isVillager() ? ZOMBIE_VILLAGER_TEXTURE : TEXTURE;
   }

   private void updateModels(ZombieEntity zombie) {
      if (zombie.isVillager()) {
         this.model = this.zombieVillagerModel;
         this.layers = this.zombieVillagerModel2;
      } else {
         this.model = this.zombieModel;
         this.layers = this.zombieVillagerModel3;
      }

      this.f_35kzpvqyv = (HumanoidModel)this.model;
   }

   protected void applyRotation(ZombieEntity c_96bgttdiz, float f, float g, float h) {
      if (c_96bgttdiz.isConverting()) {
         g += (float)(Math.cos((double)c_96bgttdiz.time * 3.25) * Math.PI * 0.25);
      }

      super.applyRotation(c_96bgttdiz, f, g, h);
   }
}
