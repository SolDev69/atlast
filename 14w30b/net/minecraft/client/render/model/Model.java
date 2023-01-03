package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class Model {
   public float handSwingProgress;
   public boolean hasVehicle;
   public boolean isBaby = true;
   public List parts = Lists.newArrayList();
   private Map texturePositions = Maps.newHashMap();
   public int textureWidth = 64;
   public int textureHeight = 32;

   public void render(Entity entity, float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale) {
   }

   public void setAngles(float handSwing, float handSwingAmount, float age, float yaw, float pitch, float scale, Entity entity) {
   }

   public void renderMobAnimation(LivingEntity entity, float handSwing, float handSwingAmount, float tickDelta) {
   }

   public ModelPart pickPart(Random random) {
      return (ModelPart)this.parts.get(random.nextInt(this.parts.size()));
   }

   protected void setTexturePos(String id, int u, int v) {
      this.texturePositions.put(id, new TexturePos(u, v));
   }

   public TexturePos getTexturePos(String id) {
      return (TexturePos)this.texturePositions.get(id);
   }

   public static void copyRotation(ModelPart from, ModelPart to) {
      to.rotationX = from.rotationX;
      to.rotationY = from.rotationY;
      to.rotationZ = from.rotationZ;
      to.pivotX = from.pivotX;
      to.pivotY = from.pivotY;
      to.pivotZ = from.pivotZ;
   }

   public void copyPropertiesFrom(Model model) {
      this.handSwingProgress = model.handSwingProgress;
      this.hasVehicle = model.hasVehicle;
      this.isBaby = model.isBaby;
   }
}
