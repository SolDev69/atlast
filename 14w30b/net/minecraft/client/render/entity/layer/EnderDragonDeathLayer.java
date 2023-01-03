package net.minecraft.client.render.entity.layer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.util.Random;
import net.minecraft.entity.living.mob.hostile.boss.EnderDragonEntity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class EnderDragonDeathLayer implements EntityRenderLayer {
   public void render(EnderDragonEntity c_36agmpvyu, float f, float g, float h, float i, float j, float k, float l) {
      if (c_36agmpvyu.ticksSinceDeath > 0) {
         Tessellator var9 = Tessellator.getInstance();
         BufferBuilder var10 = var9.getBufferBuilder();
         Lighting.turnOff();
         float var11 = ((float)c_36agmpvyu.ticksSinceDeath + h) / 200.0F;
         float var12 = 0.0F;
         if (var11 > 0.8F) {
            var12 = (var11 - 0.8F) / 0.2F;
         }

         Random var13 = new Random(432L);
         GlStateManager.disableTexture();
         GlStateManager.shadeModel(7425);
         GlStateManager.disableBlend();
         GlStateManager.blendFunc(770, 1);
         GlStateManager.disableAlphaTest();
         GlStateManager.enableCull();
         GlStateManager.depthMask(false);
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, -1.0F, -2.0F);

         for(int var14 = 0; (float)var14 < (var11 + var11 * var11) / 2.0F * 60.0F; ++var14) {
            GlStateManager.rotatef(var13.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(var13.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(var13.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(var13.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(var13.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(var13.nextFloat() * 360.0F + var11 * 90.0F, 0.0F, 0.0F, 1.0F);
            var10.start(6);
            float var15 = var13.nextFloat() * 20.0F + 5.0F + var12 * 10.0F;
            float var16 = var13.nextFloat() * 2.0F + 1.0F + var12 * 2.0F;
            var10.color(16777215, (int)(255.0F * (1.0F - var12)));
            var10.vertex(0.0, 0.0, 0.0);
            var10.color(16711935, 0);
            var10.vertex(-0.866 * (double)var16, (double)var15, (double)(-0.5F * var16));
            var10.vertex(0.866 * (double)var16, (double)var15, (double)(-0.5F * var16));
            var10.vertex(0.0, (double)var15, (double)(1.0F * var16));
            var10.vertex(-0.866 * (double)var16, (double)var15, (double)(-0.5F * var16));
            var9.end();
         }

         GlStateManager.popMatrix();
         GlStateManager.depthMask(true);
         GlStateManager.disableCull();
         GlStateManager.enableBlend();
         GlStateManager.shadeModel(7424);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableTexture();
         GlStateManager.enableAlphaTest();
         Lighting.turnOn();
      }
   }

   @Override
   public boolean colorsWhenDamaged() {
      return false;
   }
}
