package net.minecraft.client.render.model.entity;

import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class CreeperModel extends Model {
   public ModelPart head;
   public ModelPart largeHead;
   public ModelPart body;
   public ModelPart rightBackLeg;
   public ModelPart leftBackleg;
   public ModelPart rightFrontLeg;
   public ModelPart leftFrontLeg;

   public CreeperModel() {
      this(0.0F);
   }

   public CreeperModel(float reduction) {
      byte var2 = 6;
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, reduction);
      this.head.setPivot(0.0F, (float)var2, 0.0F);
      this.largeHead = new ModelPart(this, 32, 0);
      this.largeHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, reduction + 0.5F);
      this.largeHead.setPivot(0.0F, (float)var2, 0.0F);
      this.body = new ModelPart(this, 16, 16);
      this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, reduction);
      this.body.setPivot(0.0F, (float)var2, 0.0F);
      this.rightBackLeg = new ModelPart(this, 0, 16);
      this.rightBackLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, reduction);
      this.rightBackLeg.setPivot(-2.0F, (float)(12 + var2), 4.0F);
      this.leftBackleg = new ModelPart(this, 0, 16);
      this.leftBackleg.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, reduction);
      this.leftBackleg.setPivot(2.0F, (float)(12 + var2), 4.0F);
      this.rightFrontLeg = new ModelPart(this, 0, 16);
      this.rightFrontLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, reduction);
      this.rightFrontLeg.setPivot(-2.0F, (float)(12 + var2), -4.0F);
      this.leftFrontLeg = new ModelPart(this, 0, 16);
      this.leftFrontLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, reduction);
      this.leftFrontLeg.setPivot(2.0F, (float)(12 + var2), -4.0F);
   }

   @Override
   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
      this.setAngles(handSwing, handSwingAmount, age, yaw, pitch, scale, entity);
      this.head.render(scale);
      this.body.render(scale);
      this.rightBackLeg.render(scale);
      this.leftBackleg.render(scale);
      this.rightFrontLeg.render(scale);
      this.leftFrontLeg.render(scale);
   }

   @Override
   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
      this.head.rotationY = yaw / (180.0F / (float)Math.PI);
      this.head.rotationX = pitch / (180.0F / (float)Math.PI);
      this.rightBackLeg.rotationX = MathHelper.cos(handSwing * 0.6662F) * 1.4F * handSwingAmount;
      this.leftBackleg.rotationX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.4F * handSwingAmount;
      this.rightFrontLeg.rotationX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.4F * handSwingAmount;
      this.leftFrontLeg.rotationX = MathHelper.cos(handSwing * 0.6662F) * 1.4F * handSwingAmount;
   }
}
