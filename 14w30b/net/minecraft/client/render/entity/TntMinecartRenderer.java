package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderDispatcher;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class TntMinecartRenderer extends MinecartRenderer {
   public TntMinecartRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh);
   }

   protected void renderSpawnerMinecart(TntMinecartEntity c_92ejcfsju, float f, Block c_68zcrzyxg, int i) {
      int var5 = c_92ejcfsju.getFuseTicks();
      if (var5 > -1 && (float)var5 - f + 1.0F < 10.0F) {
         float var6 = 1.0F - ((float)var5 - f + 1.0F) / 10.0F;
         var6 = MathHelper.clamp(var6, 0.0F, 1.0F);
         var6 *= var6;
         var6 *= var6;
         float var7 = 1.0F + var6 * 0.3F;
         GlStateManager.scalef(var7, var7, var7);
      }

      super.renderSpawnerMinecart(c_92ejcfsju, f, c_68zcrzyxg, i);
      if (var5 > -1 && var5 / 5 % 2 == 0) {
         BlockRenderDispatcher var11 = MinecraftClient.getInstance().getBlockRenderDispatcher();
         GlStateManager.disableTexture();
         GlStateManager.disableLighting();
         GlStateManager.disableBlend();
         GlStateManager.blendFunc(770, 772);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, (1.0F - ((float)var5 - f + 1.0F) / 100.0F) * 0.8F);
         GlStateManager.pushMatrix();
         var11.renderDynamic(Blocks.TNT, 0, 1.0F);
         GlStateManager.popMatrix();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableBlend();
         GlStateManager.enableLighting();
         GlStateManager.enableTexture();
      }
   }
}
