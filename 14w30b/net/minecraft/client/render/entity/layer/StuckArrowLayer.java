package net.minecraft.client.render.entity.layer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Random;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.model.Box;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class StuckArrowLayer implements EntityRenderLayer {
   private final LivingEntityRenderer parent;

   public StuckArrowLayer(LivingEntityRenderer parent) {
      this.parent = parent;
   }

   @Override
   public void render(LivingEntity entity, float handSwingAmount, float handSwing, float tickDelta, float age, float headYaw, float headPitch, float scale) {
      int var9 = entity.getStuckArrows();
      if (var9 > 0) {
         ArrowEntity var10 = new ArrowEntity(entity.world, entity.x, entity.y, entity.z);
         Random var11 = new Random((long)entity.getNetworkId());
         Lighting.turnOff();

         for(int var12 = 0; var12 < var9; ++var12) {
            GlStateManager.pushMatrix();
            ModelPart var13 = this.parent.getModel().pickPart(var11);
            Box var14 = (Box)var13.boxes.get(var11.nextInt(var13.boxes.size()));
            var13.translate(0.0625F);
            float var15 = var11.nextFloat();
            float var16 = var11.nextFloat();
            float var17 = var11.nextFloat();
            float var18 = (var14.minX + (var14.maxX - var14.minX) * var15) / 16.0F;
            float var19 = (var14.minY + (var14.maxY - var14.minY) * var16) / 16.0F;
            float var20 = (var14.minZ + (var14.maxZ - var14.minZ) * var17) / 16.0F;
            GlStateManager.translatef(var18, var19, var20);
            var15 = var15 * 2.0F - 1.0F;
            var16 = var16 * 2.0F - 1.0F;
            var17 = var17 * 2.0F - 1.0F;
            var15 *= -1.0F;
            var16 *= -1.0F;
            var17 *= -1.0F;
            float var21 = MathHelper.sqrt(var15 * var15 + var17 * var17);
            var10.prevYaw = var10.yaw = (float)(Math.atan2((double)var15, (double)var17) * 180.0 / (float) Math.PI);
            var10.prevPitch = var10.pitch = (float)(Math.atan2((double)var16, (double)var21) * 180.0 / (float) Math.PI);
            double var22 = 0.0;
            double var24 = 0.0;
            double var26 = 0.0;
            this.parent.getDispatcher().render(var10, var22, var24, var26, 0.0F, tickDelta);
            GlStateManager.popMatrix();
         }

         Lighting.turnOn();
      }
   }

   @Override
   public boolean colorsWhenDamaged() {
      return false;
   }
}
