package net.minecraft.client.render.model.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.passive.animal.RabbitEntity;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class RabbitModel extends Model {
   ModelPart f_98ubiyqby;
   ModelPart f_15jebfkco;
   ModelPart f_66yyezjvd;
   ModelPart f_65vtydbcl;
   ModelPart f_69wywhrvj;
   ModelPart f_49ojpankx;
   ModelPart f_40uocjshw;
   ModelPart f_60smnxvjd;
   ModelPart f_18iujpsqj;
   ModelPart f_37qjqaasx;
   ModelPart f_89tzhohdq;
   ModelPart f_61axngkpp;
   private float f_84davqldj = 0.0F;
   private float f_47qyadqwg = 0.0F;

   public RabbitModel() {
      this.setTexturePos("head.main", 0, 0);
      this.setTexturePos("head.nose", 0, 24);
      this.setTexturePos("head.ear1", 0, 10);
      this.setTexturePos("head.ear2", 6, 10);
      this.f_98ubiyqby = new ModelPart(this, 26, 24);
      this.f_98ubiyqby.addBox(-1.0F, 5.5F, -3.7F, 2, 1, 7);
      this.f_98ubiyqby.setPivot(3.0F, 17.5F, 3.7F);
      this.f_98ubiyqby.flipped = true;
      this.m_89ttvhzec(this.f_98ubiyqby, 0.0F, 0.0F, 0.0F);
      this.f_15jebfkco = new ModelPart(this, 8, 24);
      this.f_15jebfkco.addBox(-1.0F, 5.5F, -3.7F, 2, 1, 7);
      this.f_15jebfkco.setPivot(-3.0F, 17.5F, 3.7F);
      this.f_15jebfkco.flipped = true;
      this.m_89ttvhzec(this.f_15jebfkco, 0.0F, 0.0F, 0.0F);
      this.f_66yyezjvd = new ModelPart(this, 30, 15);
      this.f_66yyezjvd.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 5);
      this.f_66yyezjvd.setPivot(3.0F, 17.5F, 3.7F);
      this.f_66yyezjvd.flipped = true;
      this.m_89ttvhzec(this.f_66yyezjvd, (float) (-Math.PI / 9), 0.0F, 0.0F);
      this.f_65vtydbcl = new ModelPart(this, 16, 15);
      this.f_65vtydbcl.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 5);
      this.f_65vtydbcl.setPivot(-3.0F, 17.5F, 3.7F);
      this.f_65vtydbcl.flipped = true;
      this.m_89ttvhzec(this.f_65vtydbcl, (float) (-Math.PI / 9), 0.0F, 0.0F);
      this.f_69wywhrvj = new ModelPart(this, 0, 0);
      this.f_69wywhrvj.addBox(-3.0F, -2.0F, -10.0F, 6, 5, 10);
      this.f_69wywhrvj.setPivot(0.0F, 19.0F, 8.0F);
      this.f_69wywhrvj.flipped = true;
      this.m_89ttvhzec(this.f_69wywhrvj, (float) (-Math.PI / 9), 0.0F, 0.0F);
      this.f_49ojpankx = new ModelPart(this, 8, 15);
      this.f_49ojpankx.addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2);
      this.f_49ojpankx.setPivot(3.0F, 17.0F, -1.0F);
      this.f_49ojpankx.flipped = true;
      this.m_89ttvhzec(this.f_49ojpankx, (float) (-Math.PI / 18), 0.0F, 0.0F);
      this.f_40uocjshw = new ModelPart(this, 0, 15);
      this.f_40uocjshw.addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2);
      this.f_40uocjshw.setPivot(-3.0F, 17.0F, -1.0F);
      this.f_40uocjshw.flipped = true;
      this.m_89ttvhzec(this.f_40uocjshw, (float) (-Math.PI / 18), 0.0F, 0.0F);
      this.f_60smnxvjd = new ModelPart(this, 32, 0);
      this.f_60smnxvjd.addBox(-2.5F, -4.0F, -5.0F, 5, 4, 5);
      this.f_60smnxvjd.setPivot(0.0F, 16.0F, -1.0F);
      this.f_60smnxvjd.flipped = true;
      this.m_89ttvhzec(this.f_60smnxvjd, 0.0F, 0.0F, 0.0F);
      this.f_18iujpsqj = new ModelPart(this, 52, 0);
      this.f_18iujpsqj.addBox(-2.5F, -9.0F, -1.0F, 2, 5, 1);
      this.f_18iujpsqj.setPivot(0.0F, 16.0F, -1.0F);
      this.f_18iujpsqj.flipped = true;
      this.m_89ttvhzec(this.f_18iujpsqj, 0.0F, (float) (-Math.PI / 12), 0.0F);
      this.f_37qjqaasx = new ModelPart(this, 58, 0);
      this.f_37qjqaasx.addBox(0.5F, -9.0F, -1.0F, 2, 5, 1);
      this.f_37qjqaasx.setPivot(0.0F, 16.0F, -1.0F);
      this.f_37qjqaasx.flipped = true;
      this.m_89ttvhzec(this.f_37qjqaasx, 0.0F, (float) (Math.PI / 12), 0.0F);
      this.f_89tzhohdq = new ModelPart(this, 52, 6);
      this.f_89tzhohdq.addBox(-1.5F, -1.5F, 0.0F, 3, 3, 2);
      this.f_89tzhohdq.setPivot(0.0F, 20.0F, 7.0F);
      this.f_89tzhohdq.flipped = true;
      this.m_89ttvhzec(this.f_89tzhohdq, -0.3490659F, 0.0F, 0.0F);
      this.f_61axngkpp = new ModelPart(this, 32, 9);
      this.f_61axngkpp.addBox(-0.5F, -2.5F, -5.5F, 1, 1, 1);
      this.f_61axngkpp.setPivot(0.0F, 16.0F, -1.0F);
      this.f_61axngkpp.flipped = true;
      this.m_89ttvhzec(this.f_61axngkpp, 0.0F, 0.0F, 0.0F);
   }

   private void m_89ttvhzec(ModelPart c_40uztjpfb, float f, float g, float h) {
      c_40uztjpfb.rotationX = f;
      c_40uztjpfb.rotationY = g;
      c_40uztjpfb.rotationZ = h;
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      this.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      if (this.isBaby) {
         float var8 = 2.0F;
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, 5.0F * scale, 2.0F * scale);
         this.f_60smnxvjd.render(scale);
         this.f_37qjqaasx.render(scale);
         this.f_18iujpsqj.render(scale);
         this.f_61axngkpp.render(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(1.0F / var8, 1.0F / var8, 1.0F / var8);
         GlStateManager.translatef(0.0F, 24.0F * scale, 0.0F);
         this.f_98ubiyqby.render(scale);
         this.f_15jebfkco.render(scale);
         this.f_66yyezjvd.render(scale);
         this.f_65vtydbcl.render(scale);
         this.f_69wywhrvj.render(scale);
         this.f_49ojpankx.render(scale);
         this.f_40uocjshw.render(scale);
         this.f_89tzhohdq.render(scale);
         GlStateManager.popMatrix();
      } else {
         this.f_98ubiyqby.render(scale);
         this.f_15jebfkco.render(scale);
         this.f_66yyezjvd.render(scale);
         this.f_65vtydbcl.render(scale);
         this.f_69wywhrvj.render(scale);
         this.f_49ojpankx.render(scale);
         this.f_40uocjshw.render(scale);
         this.f_60smnxvjd.render(scale);
         this.f_18iujpsqj.render(scale);
         this.f_37qjqaasx.render(scale);
         this.f_89tzhohdq.render(scale);
         this.f_61axngkpp.render(scale);
      }
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      RabbitEntity var8 = (RabbitEntity)entity;
      this.f_61axngkpp.rotationX = this.f_60smnxvjd.rotationX = this.f_18iujpsqj.rotationX = this.f_37qjqaasx.rotationX = pitch * (float) (Math.PI / 180.0);
      this.f_61axngkpp.rotationY = this.f_60smnxvjd.rotationY = yaw * (float) (Math.PI / 180.0);
      this.f_18iujpsqj.rotationY = this.f_61axngkpp.rotationY - (float) (Math.PI / 12);
      this.f_37qjqaasx.rotationY = this.f_61axngkpp.rotationY + (float) (Math.PI / 12);
      this.f_84davqldj = MathHelper.sin(var8.m_17rnscnpc() * (float) Math.PI);
      this.f_66yyezjvd.rotationX = this.f_65vtydbcl.rotationX = (this.f_84davqldj * 50.0F - 21.0F) * (float) (Math.PI / 180.0);
      this.f_98ubiyqby.rotationX = this.f_15jebfkco.rotationX = this.f_84davqldj * 50.0F * (float) (Math.PI / 180.0);
      this.f_49ojpankx.rotationX = this.f_40uocjshw.rotationX = (this.f_84davqldj * -40.0F - 11.0F) * (float) (Math.PI / 180.0);
   }

   @Override
   public void renderMobAnimation(LivingEntity entity, float handSwing, float handSwingAmount, float tickDelta) {
   }
}
