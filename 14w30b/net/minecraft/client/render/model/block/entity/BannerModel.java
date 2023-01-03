package net.minecraft.client.render.model.block.entity;

import net.minecraft.client.render.model.Model;
import net.minecraft.client.render.model.ModelPart;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BannerModel extends Model {
   public ModelPart flag;
   public ModelPart pole;
   public ModelPart bar;

   public BannerModel() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.flag = new ModelPart(this, 0, 0);
      this.flag.addBox(-10.0F, 0.0F, -2.0F, 20, 40, 1, 0.0F);
      this.pole = new ModelPart(this, 44, 0);
      this.pole.addBox(-1.0F, -30.0F, -1.0F, 2, 42, 2, 0.0F);
      this.bar = new ModelPart(this, 0, 42);
      this.bar.addBox(-10.0F, -32.0F, -1.0F, 20, 2, 2, 0.0F);
   }

   public void render() {
      this.flag.pivotY = -32.0F;
      this.flag.render(0.0625F);
      this.pole.render(0.0625F);
      this.bar.render(0.0625F);
   }
}
