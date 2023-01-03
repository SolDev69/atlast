package net.minecraft.client.entity.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class Particle extends Entity {
   protected int miscTexRow;
   protected int miscTexColumn;
   protected float randomUOffset;
   protected float randomVOffset;
   protected int age;
   protected int maxAge;
   protected float scale;
   protected float gravity;
   protected float red;
   protected float green;
   protected float blue;
   protected float alpha = 1.0F;
   protected TextureAtlasSprite sprite;
   public static double currentX;
   public static double currentY;
   public static double currentZ;

   protected Particle(World world, double x, double y, double z) {
      super(world);
      this.setDimensions(0.2F, 0.2F);
      this.setPosition(x, y, z);
      this.prevTickX = x;
      this.prevTickY = y;
      this.prevTickZ = z;
      this.red = this.green = this.blue = 1.0F;
      this.randomUOffset = this.random.nextFloat() * 3.0F;
      this.randomVOffset = this.random.nextFloat() * 3.0F;
      this.scale = (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
      this.maxAge = (int)(4.0F / (this.random.nextFloat() * 0.9F + 0.1F));
      this.age = 0;
   }

   public Particle(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
      this(world, x, y, z);
      this.velocityX = velocityX + (Math.random() * 2.0 - 1.0) * 0.4F;
      this.velocityY = velocityY + (Math.random() * 2.0 - 1.0) * 0.4F;
      this.velocityZ = velocityZ + (Math.random() * 2.0 - 1.0) * 0.4F;
      float var14 = (float)(Math.random() + Math.random() + 1.0) * 0.15F;
      float var15 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
      this.velocityX = this.velocityX / (double)var15 * (double)var14 * 0.4F;
      this.velocityY = this.velocityY / (double)var15 * (double)var14 * 0.4F + 0.1F;
      this.velocityZ = this.velocityZ / (double)var15 * (double)var14 * 0.4F;
   }

   public Particle multiplyVelocity(float value) {
      this.velocityX *= (double)value;
      this.velocityY = (this.velocityY - 0.1F) * (double)value + 0.1F;
      this.velocityZ *= (double)value;
      return this;
   }

   public Particle multiplyScale(float value) {
      this.setDimensions(0.2F * value, 0.2F * value);
      this.scale *= value;
      return this;
   }

   public void setColor(float red, float green, float blue) {
      this.red = red;
      this.green = green;
      this.blue = blue;
   }

   public void setAlpha(float alpha) {
      if (this.alpha == 1.0F && alpha < 1.0F) {
         MinecraftClient.getInstance().particleManager.m_10rnyabex(this);
      } else if (this.alpha < 1.0F && alpha == 1.0F) {
         MinecraftClient.getInstance().particleManager.m_25qkddiik(this);
      }

      this.alpha = alpha;
   }

   public float getRed() {
      return this.red;
   }

   public float getGreen() {
      return this.green;
   }

   public float getBlue() {
      return this.blue;
   }

   public float getAlpha() {
      return this.alpha;
   }

   @Override
   protected boolean canClimb() {
      return false;
   }

   @Override
   protected void initDataTracker() {
   }

   @Override
   public void tick() {
      this.prevX = this.x;
      this.prevY = this.y;
      this.prevZ = this.z;
      if (this.age++ >= this.maxAge) {
         this.remove();
      }

      this.velocityY -= 0.04 * (double)this.gravity;
      this.move(this.velocityX, this.velocityY, this.velocityZ);
      this.velocityX *= 0.98F;
      this.velocityY *= 0.98F;
      this.velocityZ *= 0.98F;
      if (this.onGround) {
         this.velocityX *= 0.7F;
         this.velocityZ *= 0.7F;
      }
   }

   public void render(BufferBuilder bufferBuilder, Entity camera, float tickDelta, float dx, float dy, float dz, float forwards, float sideways) {
      float var9 = (float)this.miscTexRow / 16.0F;
      float var10 = var9 + 0.0624375F;
      float var11 = (float)this.miscTexColumn / 16.0F;
      float var12 = var11 + 0.0624375F;
      float var13 = 0.1F * this.scale;
      if (this.sprite != null) {
         var9 = this.sprite.getUMin();
         var10 = this.sprite.getUMax();
         var11 = this.sprite.getVMin();
         var12 = this.sprite.getVMax();
      }

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

   public int getTextureType() {
      return 0;
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
   }

   public void setTexture(TextureAtlasSprite sprite) {
      int var2 = this.getTextureType();
      if (var2 == 1) {
         this.sprite = sprite;
      } else {
         throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
      }
   }

   public void setMiscTexture(int textureId) {
      if (this.getTextureType() != 0) {
         throw new RuntimeException("Invalid call to Particle.setMiscTex");
      } else {
         this.miscTexRow = textureId % 16;
         this.miscTexColumn = textureId / 16;
      }
   }

   public void incrMiscTexRow() {
      ++this.miscTexRow;
   }

   @Override
   public boolean canBePunched() {
      return false;
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName()
         + ", Pos ("
         + this.x
         + ","
         + this.y
         + ","
         + this.z
         + "), RGBA ("
         + this.red
         + ","
         + this.green
         + ","
         + this.blue
         + ","
         + this.alpha
         + "), Age "
         + this.age;
   }
}
