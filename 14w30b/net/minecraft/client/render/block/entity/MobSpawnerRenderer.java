package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.spawner.MobSpawner;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class MobSpawnerRenderer extends BlockEntityRenderer {
   public void render(MobSpawnerBlockEntity c_85rvadrpl, double d, double e, double f, float g, int i) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)d + 0.5F, (float)e, (float)f + 0.5F);
      renderMobSpawner(c_85rvadrpl.getSpawner(), d, e, f, g);
      GlStateManager.popMatrix();
   }

   public static void renderMobSpawner(MobSpawner mobSpawner, double x, double y, double z, float tickDelta) {
      Entity var8 = mobSpawner.getDisplayEntity(mobSpawner.getWorld());
      if (var8 != null) {
         float var9 = 0.4375F;
         GlStateManager.translatef(0.0F, 0.4F, 0.0F);
         GlStateManager.rotatef(
            (float)(mobSpawner.getLastRotation() + (mobSpawner.getRotation() - mobSpawner.getLastRotation()) * (double)tickDelta) * 10.0F, 0.0F, 1.0F, 0.0F
         );
         GlStateManager.rotatef(-30.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translatef(0.0F, -0.4F, 0.0F);
         GlStateManager.scalef(var9, var9, var9);
         var8.refreshPositionAndAngles(x, y, z, 0.0F, 0.0F);
         MinecraftClient.getInstance().getEntityRenderDispatcher().render(var8, 0.0, 0.0, 0.0, 0.0F, tickDelta);
      }
   }
}
