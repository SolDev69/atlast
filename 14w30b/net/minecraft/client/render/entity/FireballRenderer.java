package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Items;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class FireballRenderer extends EntityRenderer {
   private float speed;

   public FireballRenderer(EntityRenderDispatcher speed, float f) {
      super(speed);
      this.speed = f;
   }

   public void render(ProjectileEntity c_26eoehpcb, double d, double e, double f, float g, float h) {
      GlStateManager.pushMatrix();
      this.bindTexture(c_26eoehpcb);
      GlStateManager.translatef((float)d, (float)e, (float)f);
      GlStateManager.enableRescaleNormal();
      float var10 = this.speed;
      GlStateManager.scalef(var10 / 1.0F, var10 / 1.0F, var10 / 1.0F);
      TextureAtlasSprite var11 = MinecraftClient.getInstance().getItemRenderer().getModelShaper().getParticleIcon(Items.FIRE_CHARGE);
      Tessellator var12 = Tessellator.getInstance();
      BufferBuilder var13 = var12.getBufferBuilder();
      float var14 = var11.getUMin();
      float var15 = var11.getUMax();
      float var16 = var11.getVMin();
      float var17 = var11.getVMax();
      float var18 = 1.0F;
      float var19 = 0.5F;
      float var20 = 0.25F;
      GlStateManager.rotatef(180.0F - this.dispatcher.cameraYaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(-this.dispatcher.cameraPitch, 1.0F, 0.0F, 0.0F);
      var13.start();
      var13.normal(0.0F, 1.0F, 0.0F);
      var13.vertex((double)(0.0F - var19), (double)(0.0F - var20), 0.0, (double)var14, (double)var17);
      var13.vertex((double)(var18 - var19), (double)(0.0F - var20), 0.0, (double)var15, (double)var17);
      var13.vertex((double)(var18 - var19), (double)(1.0F - var20), 0.0, (double)var15, (double)var16);
      var13.vertex((double)(0.0F - var19), (double)(1.0F - var20), 0.0, (double)var14, (double)var16);
      var12.end();
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.render(c_26eoehpcb, d, e, f, g, h);
   }

   protected Identifier getTexture(ProjectileEntity c_26eoehpcb) {
      return SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS;
   }
}
