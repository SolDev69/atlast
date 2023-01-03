package net.minecraft.client.render.entity.layer;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.model.entity.HumanoidModel;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ArmorLayer extends AbstractArmorLayer {
   public ArmorLayer(LivingEntityRenderer c_63ujlquhf) {
      super(c_63ujlquhf);
   }

   @Override
   protected void hideAll() {
      this.innerModel = new HumanoidModel(0.5F);
      this.outerModel = new HumanoidModel(1.0F);
   }

   protected void setVisible(HumanoidModel c_85hwjrnsi, int i) {
      this.m_95ehekmlt(c_85hwjrnsi);
      switch(i) {
         case 1:
            c_85hwjrnsi.rightLeg.visible = true;
            c_85hwjrnsi.leftLeg.visible = true;
            break;
         case 2:
            c_85hwjrnsi.body.visible = true;
            c_85hwjrnsi.rightLeg.visible = true;
            c_85hwjrnsi.leftLeg.visible = true;
            break;
         case 3:
            c_85hwjrnsi.body.visible = true;
            c_85hwjrnsi.rightArm.visible = true;
            c_85hwjrnsi.leftArm.visible = true;
            break;
         case 4:
            c_85hwjrnsi.head.visible = true;
            c_85hwjrnsi.hat.visible = true;
      }
   }

   protected void m_95ehekmlt(HumanoidModel c_85hwjrnsi) {
      c_85hwjrnsi.setVisible(false);
   }
}
