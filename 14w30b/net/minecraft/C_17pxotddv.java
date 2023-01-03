package net.minecraft;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.layer.ArmorLayer;
import net.minecraft.client.render.model.entity.ZombieVillagerModel;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class C_17pxotddv extends ArmorLayer {
   public C_17pxotddv(LivingEntityRenderer c_63ujlquhf) {
      super(c_63ujlquhf);
   }

   @Override
   protected void hideAll() {
      this.innerModel = new ZombieVillagerModel(0.5F, 0.0F, true);
      this.outerModel = new ZombieVillagerModel(1.0F, 0.0F, true);
   }
}
