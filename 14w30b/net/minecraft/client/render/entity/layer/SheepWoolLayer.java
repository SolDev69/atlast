package net.minecraft.client.render.entity.layer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.SheepRenderer;
import net.minecraft.client.render.model.entity.SheepWoolModel;
import net.minecraft.entity.living.mob.passive.animal.SheepEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SheepWoolLayer implements EntityRenderLayer {
   private static final Identifier WOOL_TEXTURE = new Identifier("textures/entity/sheep/sheep_fur.png");
   private final SheepRenderer parent;
   private final SheepWoolModel model = new SheepWoolModel();

   public SheepWoolLayer(SheepRenderer parent) {
      this.parent = parent;
   }

   public void render(SheepEntity c_42soehjyi, float f, float g, float h, float i, float j, float k, float l) {
      if (!c_42soehjyi.isSheared() && !c_42soehjyi.isInvisible()) {
         this.parent.bindTexture(WOOL_TEXTURE);
         if (c_42soehjyi.hasCustomName() && "jeb_".equals(c_42soehjyi.getCustomName())) {
            boolean var17 = true;
            int var10 = c_42soehjyi.time / 25 + c_42soehjyi.getNetworkId();
            int var11 = DyeColor.values().length;
            int var12 = var10 % var11;
            int var13 = (var10 + 1) % var11;
            float var14 = ((float)(c_42soehjyi.time % 25) + h) / 25.0F;
            float[] var15 = SheepEntity.getColorRgb(DyeColor.byIndex(var12));
            float[] var16 = SheepEntity.getColorRgb(DyeColor.byIndex(var13));
            GlStateManager.color3f(
               var15[0] * (1.0F - var14) + var16[0] * var14, var15[1] * (1.0F - var14) + var16[1] * var14, var15[2] * (1.0F - var14) + var16[2] * var14
            );
         } else {
            float[] var9 = SheepEntity.getColorRgb(c_42soehjyi.getColorId());
            GlStateManager.color3f(var9[0], var9[1], var9[2]);
         }

         this.model.copyPropertiesFrom(this.parent.getModel());
         this.model.renderMobAnimation(c_42soehjyi, f, g, h);
         this.model.render(c_42soehjyi, f, g, i, j, k, l);
      }
   }

   @Override
   public boolean colorsWhenDamaged() {
      return true;
   }
}
