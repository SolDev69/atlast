package net.minecraft.client.entity.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.DyeItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class FireworksParticles {
   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      @Override
      public Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
         FireworksParticles.Spark var16 = new FireworksParticles.Spark(
            world, x, y, z, velocityX, velocityY, velocityZ, MinecraftClient.getInstance().particleManager
         );
         var16.setAlpha(0.99F);
         return var16;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Overlay extends Particle {
      protected Overlay(World c_54ruxjwzt, double d, double e, double f) {
         super(c_54ruxjwzt, d, e, f);
         this.maxAge = 4;
      }

      @Override
      public void render(BufferBuilder bufferBuilder, Entity camera, float tickDelta, float dx, float dy, float dz, float forwards, float sideways) {
         float var9 = 0.25F;
         float var10 = var9 + 0.25F;
         float var11 = 0.125F;
         float var12 = var11 + 0.25F;
         float var13 = 7.1F * MathHelper.sin(((float)this.age + tickDelta - 1.0F) * 0.25F * (float) Math.PI);
         this.alpha = 0.6F - ((float)this.age + tickDelta - 1.0F) * 0.25F * 0.5F;
         float var14 = (float)(this.prevX + (this.x - this.prevX) * (double)tickDelta - currentX);
         float var15 = (float)(this.prevY + (this.y - this.prevY) * (double)tickDelta - currentY);
         float var16 = (float)(this.prevZ + (this.z - this.prevZ) * (double)tickDelta - currentZ);
         bufferBuilder.color(this.red, this.green, this.blue, this.alpha);
         bufferBuilder.vertex(
            (double)(var14 - dx * var13 - forwards * var13),
            (double)(var15 - dy * var13),
            (double)(var16 - dz * var13 - sideways * var13),
            (double)var10,
            (double)var12
         );
         bufferBuilder.vertex(
            (double)(var14 - dx * var13 + forwards * var13),
            (double)(var15 + dy * var13),
            (double)(var16 - dz * var13 + sideways * var13),
            (double)var10,
            (double)var11
         );
         bufferBuilder.vertex(
            (double)(var14 + dx * var13 + forwards * var13),
            (double)(var15 + dy * var13),
            (double)(var16 + dz * var13 + sideways * var13),
            (double)var9,
            (double)var11
         );
         bufferBuilder.vertex(
            (double)(var14 + dx * var13 - forwards * var13),
            (double)(var15 - dy * var13),
            (double)(var16 + dz * var13 - sideways * var13),
            (double)var9,
            (double)var12
         );
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Spark extends Particle {
      private int texture = 160;
      private boolean trail;
      private boolean flicker;
      private final ParticleManager manager;
      private float fadeRed;
      private float fadeGreen;
      private float fadeBlue;
      private boolean fade;

      public Spark(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, ParticleManager manager) {
         super(world, x, y, z);
         this.velocityX = velocityX;
         this.velocityY = velocityY;
         this.velocityZ = velocityZ;
         this.manager = manager;
         this.scale *= 0.75F;
         this.maxAge = 48 + this.random.nextInt(12);
         this.noClip = false;
      }

      public void setTrail(boolean trail) {
         this.trail = trail;
      }

      public void setFlicker(boolean flicker) {
         this.flicker = flicker;
      }

      public void setColor(int color) {
         float var2 = (float)((color & 0xFF0000) >> 16) / 255.0F;
         float var3 = (float)((color & 0xFF00) >> 8) / 255.0F;
         float var4 = (float)((color & 0xFF) >> 0) / 255.0F;
         float var5 = 1.0F;
         this.setColor(var2 * var5, var3 * var5, var4 * var5);
      }

      public void setFade(int color) {
         this.fadeRed = (float)((color & 0xFF0000) >> 16) / 255.0F;
         this.fadeGreen = (float)((color & 0xFF00) >> 8) / 255.0F;
         this.fadeBlue = (float)((color & 0xFF) >> 0) / 255.0F;
         this.fade = true;
      }

      @Override
      public Box getBox() {
         return null;
      }

      @Override
      public boolean isPushable() {
         return false;
      }

      @Override
      public void render(BufferBuilder bufferBuilder, Entity camera, float tickDelta, float dx, float dy, float dz, float forwards, float sideways) {
         if (!this.flicker || this.age < this.maxAge / 3 || (this.age + this.maxAge) / 3 % 2 == 0) {
            super.render(bufferBuilder, camera, tickDelta, dx, dy, dz, forwards, sideways);
         }
      }

      @Override
      public void tick() {
         this.prevX = this.x;
         this.prevY = this.y;
         this.prevZ = this.z;
         if (this.age++ >= this.maxAge) {
            this.remove();
         }

         if (this.age > this.maxAge / 2) {
            this.setAlpha(1.0F - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
            if (this.fade) {
               this.red += (this.fadeRed - this.red) * 0.2F;
               this.green += (this.fadeGreen - this.green) * 0.2F;
               this.blue += (this.fadeBlue - this.blue) * 0.2F;
            }
         }

         this.setMiscTexture(this.texture + (7 - this.age * 8 / this.maxAge));
         this.velocityY -= 0.004;
         this.move(this.velocityX, this.velocityY, this.velocityZ);
         this.velocityX *= 0.91F;
         this.velocityY *= 0.91F;
         this.velocityZ *= 0.91F;
         if (this.onGround) {
            this.velocityX *= 0.7F;
            this.velocityZ *= 0.7F;
         }

         if (this.trail && this.age < this.maxAge / 2 && (this.age + this.maxAge) % 2 == 0) {
            FireworksParticles.Spark var1 = new FireworksParticles.Spark(this.world, this.x, this.y, this.z, 0.0, 0.0, 0.0, this.manager);
            var1.setAlpha(0.99F);
            var1.setColor(this.red, this.green, this.blue);
            var1.age = var1.maxAge / 2;
            if (this.fade) {
               var1.fade = true;
               var1.fadeRed = this.fadeRed;
               var1.fadeGreen = this.fadeGreen;
               var1.fadeBlue = this.fadeBlue;
            }

            var1.flicker = this.flicker;
            this.manager.addParticle(var1);
         }
      }

      @Override
      public int getLightLevel(float tickDelta) {
         return 15728880;
      }

      @Override
      public float getBrightness(float tickDelta) {
         return 1.0F;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Starter extends Particle {
      private int life;
      private final ParticleManager manager;
      private NbtList explosions;
      boolean twinkleDelay;

      public Starter(
         World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, ParticleManager manager, NbtCompound explosions
      ) {
         super(world, x, y, z, 0.0, 0.0, 0.0);
         this.velocityX = velocityX;
         this.velocityY = velocityY;
         this.velocityZ = velocityZ;
         this.manager = manager;
         this.maxAge = 8;
         if (explosions != null) {
            this.explosions = explosions.getList("Explosions", 10);
            if (this.explosions.size() == 0) {
               this.explosions = null;
            } else {
               this.maxAge = this.explosions.size() * 2 - 1;

               for(int var16 = 0; var16 < this.explosions.size(); ++var16) {
                  NbtCompound var17 = this.explosions.getCompound(var16);
                  if (var17.getBoolean("Flicker")) {
                     this.twinkleDelay = true;
                     this.maxAge += 15;
                     break;
                  }
               }
            }
         }
      }

      @Override
      public void render(BufferBuilder bufferBuilder, Entity camera, float tickDelta, float dx, float dy, float dz, float forwards, float sideways) {
      }

      @Override
      public void tick() {
         if (this.life == 0 && this.explosions != null) {
            boolean var1 = this.isFarAway();
            boolean var2 = false;
            if (this.explosions.size() >= 3) {
               var2 = true;
            } else {
               for(int var3 = 0; var3 < this.explosions.size(); ++var3) {
                  NbtCompound var4 = this.explosions.getCompound(var3);
                  if (var4.getByte("Type") == 1) {
                     var2 = true;
                     break;
                  }
               }
            }

            String var17 = "fireworks." + (var2 ? "largeBlast" : "blast") + (var1 ? "_far" : "");
            this.world.playSound(this.x, this.y, this.z, var17, 20.0F, 0.95F + this.random.nextFloat() * 0.1F, true);
         }

         if (this.life % 2 == 0 && this.explosions != null && this.life / 2 < this.explosions.size()) {
            int var13 = this.life / 2;
            NbtCompound var15 = this.explosions.getCompound(var13);
            byte var18 = var15.getByte("Type");
            boolean var19 = var15.getBoolean("Trail");
            boolean var5 = var15.getBoolean("Flicker");
            int[] var6 = var15.getIntArray("Colors");
            int[] var7 = var15.getIntArray("FadeColors");
            if (var6.length == 0) {
               var6 = new int[]{DyeItem.COLORS[0]};
            }

            if (var18 == 1) {
               this.createBall(0.5, 4, var6, var7, var19, var5);
            } else if (var18 == 2) {
               this.createShape(
                  0.5,
                  new double[][]{
                     {0.0, 1.0},
                     {0.3455, 0.309},
                     {0.9511, 0.309},
                     {0.3795918367346939, -0.12653061224489795},
                     {0.6122448979591837, -0.8040816326530612},
                     {0.0, -0.35918367346938773}
                  },
                  var6,
                  var7,
                  var19,
                  var5,
                  false
               );
            } else if (var18 == 3) {
               this.createShape(
                  0.5,
                  new double[][]{
                     {0.0, 0.2},
                     {0.2, 0.2},
                     {0.2, 0.6},
                     {0.6, 0.6},
                     {0.6, 0.2},
                     {0.2, 0.2},
                     {0.2, 0.0},
                     {0.4, 0.0},
                     {0.4, -0.6},
                     {0.2, -0.6},
                     {0.2, -0.4},
                     {0.0, -0.4}
                  },
                  var6,
                  var7,
                  var19,
                  var5,
                  true
               );
            } else if (var18 == 4) {
               this.createBurst(var6, var7, var19, var5);
            } else {
               this.createBall(0.25, 2, var6, var7, var19, var5);
            }

            int var8 = var6[0];
            float var9 = (float)((var8 & 0xFF0000) >> 16) / 255.0F;
            float var10 = (float)((var8 & 0xFF00) >> 8) / 255.0F;
            float var11 = (float)((var8 & 0xFF) >> 0) / 255.0F;
            FireworksParticles.Overlay var12 = new FireworksParticles.Overlay(this.world, this.x, this.y, this.z);
            var12.setColor(var9, var10, var11);
            this.manager.addParticle(var12);
         }

         ++this.life;
         if (this.life > this.maxAge) {
            if (this.twinkleDelay) {
               boolean var14 = this.isFarAway();
               String var16 = "fireworks." + (var14 ? "twinkle_far" : "twinkle");
               this.world.playSound(this.x, this.y, this.z, var16, 20.0F, 0.9F + this.random.nextFloat() * 0.15F, true);
            }

            this.remove();
         }
      }

      private boolean isFarAway() {
         MinecraftClient var1 = MinecraftClient.getInstance();
         return var1 == null || var1.getCamera() == null || !(var1.getCamera().getSquaredDistanceTo(this.x, this.y, this.z) < 256.0);
      }

      private void create(
         double x, double y, double z, double velocityX, double velocityY, double velocityZ, int[] colors, int[] fadeColors, boolean trail, boolean flicker
      ) {
         FireworksParticles.Spark var17 = new FireworksParticles.Spark(this.world, x, y, z, velocityX, velocityY, velocityZ, this.manager);
         var17.setAlpha(0.99F);
         var17.setTrail(trail);
         var17.setFlicker(flicker);
         int var18 = this.random.nextInt(colors.length);
         var17.setColor(colors[var18]);
         if (fadeColors != null && fadeColors.length > 0) {
            var17.setFade(fadeColors[this.random.nextInt(fadeColors.length)]);
         }

         this.manager.addParticle(var17);
      }

      private void createBall(double size, int amount, int[] colors, int[] fadeColors, boolean trail, boolean flicker) {
         double var8 = this.x;
         double var10 = this.y;
         double var12 = this.z;

         for(int var14 = -amount; var14 <= amount; ++var14) {
            for(int var15 = -amount; var15 <= amount; ++var15) {
               for(int var16 = -amount; var16 <= amount; ++var16) {
                  double var17 = (double)var15 + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                  double var19 = (double)var14 + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                  double var21 = (double)var16 + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                  double var23 = (double)MathHelper.sqrt(var17 * var17 + var19 * var19 + var21 * var21) / size + this.random.nextGaussian() * 0.05;
                  this.create(var8, var10, var12, var17 / var23, var19 / var23, var21 / var23, colors, fadeColors, trail, flicker);
                  if (var14 != -amount && var14 != amount && var15 != -amount && var15 != amount) {
                     var16 += amount * 2 - 1;
                  }
               }
            }
         }
      }

      private void createShape(double size, double[][] pattern, int[] colors, int[] fadeColors, boolean trail, boolean flicker, boolean keepShape) {
         double var9 = pattern[0][0];
         double var11 = pattern[0][1];
         this.create(this.x, this.y, this.z, var9 * size, var11 * size, 0.0, colors, fadeColors, trail, flicker);
         float var13 = this.random.nextFloat() * (float) Math.PI;
         double var14 = keepShape ? 0.034 : 0.34;

         for(int var16 = 0; var16 < 3; ++var16) {
            double var17 = (double)var13 + (double)((float)var16 * (float) Math.PI) * var14;
            double var19 = var9;
            double var21 = var11;

            for(int var23 = 1; var23 < pattern.length; ++var23) {
               double var24 = pattern[var23][0];
               double var26 = pattern[var23][1];

               for(double var28 = 0.25; var28 <= 1.0; var28 += 0.25) {
                  double var30 = (var19 + (var24 - var19) * var28) * size;
                  double var32 = (var21 + (var26 - var21) * var28) * size;
                  double var34 = var30 * Math.sin(var17);
                  var30 *= Math.cos(var17);

                  for(double var36 = -1.0; var36 <= 1.0; var36 += 2.0) {
                     this.create(this.x, this.y, this.z, var30 * var36, var32, var34 * var36, colors, fadeColors, trail, flicker);
                  }
               }

               var19 = var24;
               var21 = var26;
            }
         }
      }

      private void createBurst(int[] colors, int[] fadeColors, boolean trail, boolean flicker) {
         double var5 = this.random.nextGaussian() * 0.05;
         double var7 = this.random.nextGaussian() * 0.05;

         for(int var9 = 0; var9 < 70; ++var9) {
            double var10 = this.velocityX * 0.5 + this.random.nextGaussian() * 0.15 + var5;
            double var12 = this.velocityZ * 0.5 + this.random.nextGaussian() * 0.15 + var7;
            double var14 = this.velocityY * 0.5 + this.random.nextDouble() * 0.5;
            this.create(this.x, this.y, this.z, var10, var14, var12, colors, fadeColors, trail, flicker);
         }
      }

      @Override
      public int getTextureType() {
         return 0;
      }
   }
}
