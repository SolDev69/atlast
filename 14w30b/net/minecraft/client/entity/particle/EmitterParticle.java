package net.minecraft.client.entity.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class EmitterParticle extends Particle {
   private Entity target;
   private int age;
   private int maxAge;
   private ParticleType type;

   public EmitterParticle(World world, Entity target, ParticleType type) {
      super(world, target.x, target.getBoundingBox().minY + (double)(target.height / 2.0F), target.z, target.velocityX, target.velocityY, target.velocityZ);
      this.target = target;
      this.maxAge = 3;
      this.type = type;
      this.tick();
   }

   @Override
   public void render(BufferBuilder bufferBuilder, Entity camera, float tickDelta, float dx, float dy, float dz, float forwards, float sideways) {
   }

   @Override
   public void tick() {
      for(int var1 = 0; var1 < 16; ++var1) {
         double var2 = (double)(this.random.nextFloat() * 2.0F - 1.0F);
         double var4 = (double)(this.random.nextFloat() * 2.0F - 1.0F);
         double var6 = (double)(this.random.nextFloat() * 2.0F - 1.0F);
         if (!(var2 * var2 + var4 * var4 + var6 * var6 > 1.0)) {
            double var8 = this.target.x + var2 * (double)this.target.width / 4.0;
            double var10 = this.target.getBoundingBox().minY + (double)(this.target.height / 2.0F) + var4 * (double)this.target.height / 4.0;
            double var12 = this.target.z + var6 * (double)this.target.width / 4.0;
            this.world.addParticle(this.type, false, var8, var10, var12, var2, var4 + 0.2, var6);
         }
      }

      ++this.age;
      if (this.age >= this.maxAge) {
         this.remove();
      }
   }

   @Override
   public int getTextureType() {
      return 3;
   }
}
