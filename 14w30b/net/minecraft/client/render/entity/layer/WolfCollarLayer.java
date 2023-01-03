package net.minecraft.client.render.entity.layer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.WolfRenderer;
import net.minecraft.entity.living.mob.passive.animal.SheepEntity;
import net.minecraft.entity.living.mob.passive.animal.tamable.WolfEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class WolfCollarLayer implements EntityRenderLayer {
   private static final Identifier COLLAR_TEXTURE = new Identifier("textures/entity/wolf/wolf_collar.png");
   private final WolfRenderer parent;

   public WolfCollarLayer(WolfRenderer parent) {
      this.parent = parent;
   }

   public void render(WolfEntity c_68kzbahax, float f, float g, float h, float i, float j, float k, float l) {
      if (c_68kzbahax.isTamed() && !c_68kzbahax.isInvisible()) {
         this.parent.bindTexture(COLLAR_TEXTURE);
         DyeColor var9 = DyeColor.byIndex(c_68kzbahax.getCollarColor().getIndex());
         float[] var10 = SheepEntity.getColorRgb(var9);
         GlStateManager.color3f(var10[0], var10[1], var10[2]);
         this.parent.getModel().render(c_68kzbahax, f, g, i, j, k, l);
      }
   }

   @Override
   public boolean colorsWhenDamaged() {
      return true;
   }
}
