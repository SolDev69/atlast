package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class EndPortalRenderer extends BlockEntityRenderer {
   private static final Identifier SKY_TEXTURE = new Identifier("textures/environment/end_sky.png");
   private static final Identifier PORTAL_TEXTURE = new Identifier("textures/entity/end_portal.png");
   private static final Random RANDOM = new Random(31100L);
   FloatBuffer glFloatBuffer = MemoryTracker.createFloatBuffer(16);

   public void render(EndPortalBlockEntity c_67skttixs, double d, double e, double f, float g, int i) {
      float var10 = (float)this.dispatcher.cameraX;
      float var11 = (float)this.dispatcher.cameraY;
      float var12 = (float)this.dispatcher.cameraZ;
      GlStateManager.disableLighting();
      RANDOM.setSeed(31100L);
      float var13 = 0.75F;

      for(int var14 = 0; var14 < 16; ++var14) {
         GlStateManager.pushMatrix();
         float var15 = (float)(16 - var14);
         float var16 = 0.0625F;
         float var17 = 1.0F / (var15 + 1.0F);
         if (var14 == 0) {
            this.bindTexture(SKY_TEXTURE);
            var17 = 0.1F;
            var15 = 65.0F;
            var16 = 0.125F;
            GlStateManager.disableBlend();
            GlStateManager.blendFunc(770, 771);
         }

         if (var14 >= 1) {
            this.bindTexture(PORTAL_TEXTURE);
         }

         if (var14 == 1) {
            GlStateManager.disableBlend();
            GlStateManager.blendFunc(1, 1);
            var16 = 0.5F;
         }

         float var18 = (float)(-(e + (double)var13));
         float var19 = var18 + (float)Camera.offset().y;
         float var20 = var18 + var15 + (float)Camera.offset().y;
         float var21 = var19 / var20;
         var21 = (float)(e + (double)var13) + var21;
         GlStateManager.translatef(var10, var21, var12);
         GlStateManager.texGenMode(GlStateManager.TexGenMode.S, 9217);
         GlStateManager.texGenMode(GlStateManager.TexGenMode.T, 9217);
         GlStateManager.texGenMode(GlStateManager.TexGenMode.R, 9217);
         GlStateManager.texGenMode(GlStateManager.TexGenMode.Q, 9216);
         GlStateManager.texGenParam(GlStateManager.TexGenMode.S, 9473, this.getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
         GlStateManager.texGenParam(GlStateManager.TexGenMode.T, 9473, this.getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
         GlStateManager.texGenParam(GlStateManager.TexGenMode.R, 9473, this.getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
         GlStateManager.texGenParam(GlStateManager.TexGenMode.Q, 9474, this.getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
         GlStateManager.enableTexGen(GlStateManager.TexGenMode.S);
         GlStateManager.enableTexGen(GlStateManager.TexGenMode.T);
         GlStateManager.enableTexGen(GlStateManager.TexGenMode.R);
         GlStateManager.enableTexGen(GlStateManager.TexGenMode.Q);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.loadIdentity();
         GlStateManager.translatef(0.0F, (float)(MinecraftClient.getTime() % 700000L) / 700000.0F, 0.0F);
         GlStateManager.scalef(var16, var16, var16);
         GlStateManager.translatef(0.5F, 0.5F, 0.0F);
         GlStateManager.rotatef((float)(var14 * var14 * 4321 + var14 * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.translatef(-0.5F, -0.5F, 0.0F);
         GlStateManager.translatef(-var10, -var12, -var11);
         var19 = var18 + (float)Camera.offset().y;
         GlStateManager.translatef((float)Camera.offset().x * var15 / var19, (float)Camera.offset().z * var15 / var19, -var11);
         Tessellator var26 = Tessellator.getInstance();
         BufferBuilder var28 = var26.getBufferBuilder();
         var28.start();
         float var22 = RANDOM.nextFloat() * 0.5F + 0.1F;
         float var23 = RANDOM.nextFloat() * 0.5F + 0.4F;
         float var24 = RANDOM.nextFloat() * 0.5F + 0.5F;
         if (var14 == 0) {
            var24 = 1.0F;
            var23 = 1.0F;
            var22 = 1.0F;
         }

         var28.color(var22 * var17, var23 * var17, var24 * var17, 1.0F);
         var28.vertex(d, e + (double)var13, f);
         var28.vertex(d, e + (double)var13, f + 1.0);
         var28.vertex(d + 1.0, e + (double)var13, f + 1.0);
         var28.vertex(d + 1.0, e + (double)var13, f);
         var26.end();
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }

      GlStateManager.enableBlend();
      GlStateManager.disableTexGen(GlStateManager.TexGenMode.S);
      GlStateManager.disableTexGen(GlStateManager.TexGenMode.T);
      GlStateManager.disableTexGen(GlStateManager.TexGenMode.R);
      GlStateManager.disableTexGen(GlStateManager.TexGenMode.Q);
      GlStateManager.enableLighting();
   }

   private FloatBuffer getBuffer(float r, float g, float b, float a) {
      ((Buffer)this.glFloatBuffer).clear();
      this.glFloatBuffer.put(r).put(g).put(b).put(a);
      ((Buffer)this.glFloatBuffer).flip();
      return this.glFloatBuffer;
   }
}
