package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.layer.WornSkullLayer;
import net.minecraft.client.render.model.entity.VillagerModel;
import net.minecraft.entity.living.mob.passive.VillagerEntity;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class VillagerRenderer extends MobRenderer {
   private static final Identifier UNEMPLOYED_TEXTURE = new Identifier("textures/entity/villager/villager.png");
   private static final Identifier FARMER_TEXTURE = new Identifier("textures/entity/villager/farmer.png");
   private static final Identifier LIBRARIAN_TEXTURE = new Identifier("textures/entity/villager/librarian.png");
   private static final Identifier PRIEST_TEXTURE = new Identifier("textures/entity/villager/priest.png");
   private static final Identifier BLACKSMITH_TEXTURE = new Identifier("textures/entity/villager/smith.png");
   private static final Identifier BUTCHER_TEXTURE = new Identifier("textures/entity/villager/butcher.png");

   public VillagerRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh, new VillagerModel(0.0F), 0.5F);
      this.addLayer(new WornSkullLayer(this.getModel().head));
   }

   public VillagerModel getModel() {
      return (VillagerModel)super.getModel();
   }

   protected Identifier getTexture(VillagerEntity c_21keykoxl) {
      switch(c_21keykoxl.getProfession()) {
         case 0:
            return FARMER_TEXTURE;
         case 1:
            return LIBRARIAN_TEXTURE;
         case 2:
            return PRIEST_TEXTURE;
         case 3:
            return BLACKSMITH_TEXTURE;
         case 4:
            return BUTCHER_TEXTURE;
         default:
            return UNEMPLOYED_TEXTURE;
      }
   }

   protected void scale(VillagerEntity c_21keykoxl, float f) {
      float var3 = 0.9375F;
      if (c_21keykoxl.getBreedingAge() < 0) {
         var3 = (float)((double)var3 * 0.5);
         this.shadowSize = 0.25F;
      } else {
         this.shadowSize = 0.5F;
      }

      GlStateManager.scalef(var3, var3, var3);
   }
}
