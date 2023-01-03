package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.client.render.Culler;
import net.minecraft.client.render.model.entity.GuardianModel;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.hostile.GuardianEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class GuardianRenderer extends MobRenderer {
   private static final Identifier GUARDIAN_TEXTURE = new Identifier("textures/entity/guardian.png");
   private static final Identifier ELDER_GUARDIAN_TEXTURE = new Identifier("textures/entity/guardian_elder.png");
   private static final Identifier GUARDIAN_BEAM_TEXTURE = new Identifier("textures/entity/guardian_beam.png");
   int f_62ymmpiam = ((GuardianModel)this.model).m_60xlazxax();

   public GuardianRenderer(EntityRenderDispatcher c_28wsgstbh) {
      super(c_28wsgstbh, new GuardianModel(), 0.5F);
   }

   public boolean shouldRender(GuardianEntity c_65xasibhg, Culler c_72tlvecqx, double d, double e, double f) {
      if (super.shouldRender((MobEntity)c_65xasibhg, c_72tlvecqx, d, e, f)) {
         return true;
      } else {
         if (c_65xasibhg.m_16dqbnsqq()) {
            LivingEntity var9 = c_65xasibhg.m_74mbxcnur();
            if (var9 != null) {
               Vec3d var10 = this.m_27tccydrm(var9, (double)var9.height * 0.5, 1.0F);
               Vec3d var11 = this.m_27tccydrm(c_65xasibhg, (double)c_65xasibhg.getEyeHeight(), 1.0F);
               if (c_72tlvecqx.isVisible(Box.of(var11.x, var11.y, var11.z, var10.x, var10.y, var10.z))) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private Vec3d m_27tccydrm(LivingEntity c_97zulxhng, double d, float f) {
      double var5 = c_97zulxhng.prevTickX + (c_97zulxhng.x - c_97zulxhng.prevTickX) * (double)f;
      double var7 = d + c_97zulxhng.prevTickY + (c_97zulxhng.y - c_97zulxhng.prevTickY) * (double)f;
      double var9 = c_97zulxhng.prevTickZ + (c_97zulxhng.z - c_97zulxhng.prevTickZ) * (double)f;
      return new Vec3d(var5, var7, var9);
   }

   public void render(GuardianEntity c_65xasibhg, double d, double e, double f, float g, float h) {
      if (this.f_62ymmpiam != ((GuardianModel)this.model).m_60xlazxax()) {
         this.model = new GuardianModel();
         this.f_62ymmpiam = ((GuardianModel)this.model).m_60xlazxax();
      }

      super.render((MobEntity)c_65xasibhg, d, e, f, g, h);
      LivingEntity var10 = c_65xasibhg.m_74mbxcnur();
      if (var10 != null) {
         float var11 = c_65xasibhg.m_76qczvcqr(h);
         Tessellator var12 = Tessellator.getInstance();
         BufferBuilder var13 = var12.getBufferBuilder();
         this.bindTexture(GUARDIAN_BEAM_TEXTURE);
         GL11.glTexParameterf(3553, 10242, 10497.0F);
         GL11.glTexParameterf(3553, 10243, 10497.0F);
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         GlStateManager.enableBlend();
         GlStateManager.depthMask(true);
         float var14 = 240.0F;
         GLX.multiTexCoord2f(GLX.GL_TEXTURE1, var14, var14);
         GlStateManager.blendFuncSeparate(770, 1, 1, 0);
         float var15 = (float)c_65xasibhg.world.getTime() + h;
         float var16 = var15 * 0.5F % 1.0F;
         float var17 = c_65xasibhg.getEyeHeight();
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)d, (float)e + var17, (float)f);
         Vec3d var18 = this.m_27tccydrm(var10, (double)var10.height * 0.5, h);
         Vec3d var19 = this.m_27tccydrm(c_65xasibhg, (double)var17, h);
         Vec3d var20 = var18.subtract(var19);
         double var21 = var20.length() + 1.0;
         var20 = var20.normalize();
         float var23 = (float)Math.acos(var20.y);
         float var24 = (float)Math.atan2(var20.z, var20.x);
         GlStateManager.rotatef(((float) (Math.PI / 2) + -var24) * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(var23 * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
         byte var25 = 1;
         double var26 = (double)var15 * 0.05 * (1.0 - (double)(var25 & 1) * 2.5);
         var13.start();
         float var28 = var11 * var11;
         var13.color(64 + (int)(var28 * 240.0F), 32 + (int)(var28 * 192.0F), 128 - (int)(var28 * 64.0F), 255);
         double var29 = (double)var25 * 0.2;
         double var31 = var29 * 1.41;
         double var33 = 0.0 + Math.cos(var26 + (Math.PI * 3.0 / 4.0)) * var31;
         double var35 = 0.0 + Math.sin(var26 + (Math.PI * 3.0 / 4.0)) * var31;
         double var37 = 0.0 + Math.cos(var26 + (Math.PI / 4)) * var31;
         double var39 = 0.0 + Math.sin(var26 + (Math.PI / 4)) * var31;
         double var41 = 0.0 + Math.cos(var26 + (Math.PI * 5.0 / 4.0)) * var31;
         double var43 = 0.0 + Math.sin(var26 + (Math.PI * 5.0 / 4.0)) * var31;
         double var45 = 0.0 + Math.cos(var26 + (Math.PI * 7.0 / 4.0)) * var31;
         double var47 = 0.0 + Math.sin(var26 + (Math.PI * 7.0 / 4.0)) * var31;
         double var49 = 0.0 + Math.cos(var26 + Math.PI) * var29;
         double var51 = 0.0 + Math.sin(var26 + Math.PI) * var29;
         double var53 = 0.0 + Math.cos(var26 + 0.0) * var29;
         double var55 = 0.0 + Math.sin(var26 + 0.0) * var29;
         double var57 = 0.0 + Math.cos(var26 + (Math.PI / 2)) * var29;
         double var59 = 0.0 + Math.sin(var26 + (Math.PI / 2)) * var29;
         double var61 = 0.0 + Math.cos(var26 + (Math.PI * 3.0 / 2.0)) * var29;
         double var63 = 0.0 + Math.sin(var26 + (Math.PI * 3.0 / 2.0)) * var29;
         double var67 = 0.0;
         double var69 = 0.4999;
         double var71 = (double)(-1.0F + var16);
         double var73 = var21 * (0.5 / var29) + var71;
         var13.vertex(var49, var21, var51, var69, var73);
         var13.vertex(var49, 0.0, var51, var69, var71);
         var13.vertex(var53, 0.0, var55, var67, var71);
         var13.vertex(var53, var21, var55, var67, var73);
         var13.vertex(var57, var21, var59, var69, var73);
         var13.vertex(var57, 0.0, var59, var69, var71);
         var13.vertex(var61, 0.0, var63, var67, var71);
         var13.vertex(var61, var21, var63, var67, var73);
         double var75 = 0.0;
         if (c_65xasibhg.time % 2 == 0) {
            var75 = 0.5;
         }

         var13.vertex(var33, var21, var35, 0.5, var75 + 0.5);
         var13.vertex(var37, var21, var39, 1.0, var75 + 0.5);
         var13.vertex(var45, var21, var47, 1.0, var75);
         var13.vertex(var41, var21, var43, 0.5, var75);
         var12.end();
         GlStateManager.popMatrix();
      }
   }

   protected void scale(GuardianEntity c_65xasibhg, float f) {
      if (c_65xasibhg.isElder()) {
         GlStateManager.scalef(2.35F, 2.35F, 2.35F);
      }
   }

   protected Identifier getTexture(GuardianEntity c_65xasibhg) {
      return c_65xasibhg.isElder() ? ELDER_GUARDIAN_TEXTURE : GUARDIAN_TEXTURE;
   }
}
