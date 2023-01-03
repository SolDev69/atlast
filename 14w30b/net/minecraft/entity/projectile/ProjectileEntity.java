package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.resource.Identifier;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class ProjectileEntity extends Entity {
   private int blockX = -1;
   private int blockY = -1;
   private int blockZ = -1;
   private Block inBlock;
   private boolean inGround;
   public LivingEntity shooter;
   private int ticksInBlock;
   private int ticksInAir;
   public double accelerationX;
   public double accelerationY;
   public double accelerationZ;

   public ProjectileEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(1.0F, 1.0F);
   }

   @Override
   protected void initDataTracker() {
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean isWithinViewDistance(double distance) {
      double var3 = this.getBoundingBox().getAverageSideLength() * 4.0;
      var3 *= 64.0;
      return distance < var3 * var3;
   }

   public ProjectileEntity(World world, double x, double y, double z, double g, double h, double i) {
      super(world);
      this.setDimensions(1.0F, 1.0F);
      this.refreshPositionAndAngles(x, y, z, this.yaw, this.pitch);
      this.setPosition(x, y, z);
      double var14 = (double)MathHelper.sqrt(g * g + h * h + i * i);
      this.accelerationX = g / var14 * 0.1;
      this.accelerationY = h / var14 * 0.1;
      this.accelerationZ = i / var14 * 0.1;
   }

   public ProjectileEntity(World world, LivingEntity shooter, double x, double y, double f) {
      super(world);
      this.shooter = shooter;
      this.setDimensions(1.0F, 1.0F);
      this.refreshPositionAndAngles(shooter.x, shooter.y, shooter.z, shooter.yaw, shooter.pitch);
      this.setPosition(this.x, this.y, this.z);
      this.velocityX = this.velocityY = this.velocityZ = 0.0;
      x += this.random.nextGaussian() * 0.4;
      y += this.random.nextGaussian() * 0.4;
      f += this.random.nextGaussian() * 0.4;
      double var9 = (double)MathHelper.sqrt(x * x + y * y + f * f);
      this.accelerationX = x / var9 * 0.1;
      this.accelerationY = y / var9 * 0.1;
      this.accelerationZ = f / var9 * 0.1;
   }

   @Override
   public void tick() {
      if (this.world.isClient || (this.shooter == null || !this.shooter.removed) && this.world.isLoaded(new BlockPos(this))) {
         super.tick();
         this.setOnFireFor(1);
         if (this.inGround) {
            if (this.world.getBlockState(new BlockPos(this.blockX, this.blockY, this.blockZ)).getBlock() == this.inBlock) {
               ++this.ticksInBlock;
               if (this.ticksInBlock == 600) {
                  this.remove();
               }

               return;
            }

            this.inGround = false;
            this.velocityX *= (double)(this.random.nextFloat() * 0.2F);
            this.velocityY *= (double)(this.random.nextFloat() * 0.2F);
            this.velocityZ *= (double)(this.random.nextFloat() * 0.2F);
            this.ticksInBlock = 0;
            this.ticksInAir = 0;
         } else {
            ++this.ticksInAir;
         }

         Vec3d var1 = new Vec3d(this.x, this.y, this.z);
         Vec3d var2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
         HitResult var3 = this.world.rayTrace(var1, var2);
         var1 = new Vec3d(this.x, this.y, this.z);
         var2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
         if (var3 != null) {
            var2 = new Vec3d(var3.pos.x, var3.pos.y, var3.pos.z);
         }

         Entity var4 = null;
         List var5 = this.world.getEntities(this, this.getBoundingBox().grow(this.velocityX, this.velocityY, this.velocityZ).expand(1.0, 1.0, 1.0));
         double var6 = 0.0;

         for(int var8 = 0; var8 < var5.size(); ++var8) {
            Entity var9 = (Entity)var5.get(var8);
            if (var9.hasCollision() && (!var9.is(this.shooter) || this.ticksInAir >= 25)) {
               float var10 = 0.3F;
               Box var11 = var9.getBoundingBox().expand((double)var10, (double)var10, (double)var10);
               HitResult var12 = var11.clip(var1, var2);
               if (var12 != null) {
                  double var13 = var1.distanceTo(var12.pos);
                  if (var13 < var6 || var6 == 0.0) {
                     var4 = var9;
                     var6 = var13;
                  }
               }
            }
         }

         if (var4 != null) {
            var3 = new HitResult(var4);
         }

         if (var3 != null) {
            this.onHit(var3);
         }

         this.x += this.velocityX;
         this.y += this.velocityY;
         this.z += this.velocityZ;
         float var17 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
         this.yaw = (float)(Math.atan2(this.velocityZ, this.velocityX) * 180.0 / (float) Math.PI) + 90.0F;
         this.pitch = (float)(Math.atan2((double)var17, this.velocityY) * 180.0 / (float) Math.PI) - 90.0F;

         while(this.pitch - this.prevPitch < -180.0F) {
            this.prevPitch -= 360.0F;
         }

         while(this.pitch - this.prevPitch >= 180.0F) {
            this.prevPitch += 360.0F;
         }

         while(this.yaw - this.prevYaw < -180.0F) {
            this.prevYaw -= 360.0F;
         }

         while(this.yaw - this.prevYaw >= 180.0F) {
            this.prevYaw += 360.0F;
         }

         this.pitch = this.prevPitch + (this.pitch - this.prevPitch) * 0.2F;
         this.yaw = this.prevYaw + (this.yaw - this.prevYaw) * 0.2F;
         float var18 = this.getDrag();
         if (this.isInWater()) {
            for(int var19 = 0; var19 < 4; ++var19) {
               float var20 = 0.25F;
               this.world
                  .addParticle(
                     ParticleType.WATER_BUBBLE,
                     this.x - this.velocityX * (double)var20,
                     this.y - this.velocityY * (double)var20,
                     this.z - this.velocityZ * (double)var20,
                     this.velocityX,
                     this.velocityY,
                     this.velocityZ
                  );
            }

            var18 = 0.8F;
         }

         this.velocityX += this.accelerationX;
         this.velocityY += this.accelerationY;
         this.velocityZ += this.accelerationZ;
         this.velocityX *= (double)var18;
         this.velocityY *= (double)var18;
         this.velocityZ *= (double)var18;
         this.world.addParticle(ParticleType.SMOKE_NORMAL, this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
         this.setPosition(this.x, this.y, this.z);
      } else {
         this.remove();
      }
   }

   protected float getDrag() {
      return 0.95F;
   }

   protected abstract void onHit(HitResult hitResult);

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      nbt.putShort("xTile", (short)this.blockX);
      nbt.putShort("yTile", (short)this.blockY);
      nbt.putShort("zTile", (short)this.blockZ);
      Identifier var2 = (Identifier)Block.REGISTRY.getKey(this.inBlock);
      nbt.putString("inTile", var2 == null ? "" : var2.toString());
      nbt.putByte("inGround", (byte)(this.inGround ? 1 : 0));
      nbt.put("direction", this.toNbtList(new double[]{this.velocityX, this.velocityY, this.velocityZ}));
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      this.blockX = nbt.getShort("xTile");
      this.blockY = nbt.getShort("yTile");
      this.blockZ = nbt.getShort("zTile");
      if (nbt.isType("inTile", 8)) {
         this.inBlock = Block.byId(nbt.getString("inTile"));
      } else {
         this.inBlock = Block.byRawId(nbt.getByte("inTile") & 255);
      }

      this.inGround = nbt.getByte("inGround") == 1;
      if (nbt.isType("direction", 9)) {
         NbtList var2 = nbt.getList("direction", 6);
         this.velocityX = var2.getDouble(0);
         this.velocityY = var2.getDouble(1);
         this.velocityZ = var2.getDouble(2);
      } else {
         this.remove();
      }
   }

   @Override
   public boolean hasCollision() {
      return true;
   }

   @Override
   public float getExtraHitboxSize() {
      return 1.0F;
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerable(source)) {
         return false;
      } else {
         this.onDamaged();
         if (source.getAttacker() != null) {
            Vec3d var3 = source.getAttacker().getCameraRotation();
            if (var3 != null) {
               this.velocityX = var3.x;
               this.velocityY = var3.y;
               this.velocityZ = var3.z;
               this.accelerationX = this.velocityX * 0.1;
               this.accelerationY = this.velocityY * 0.1;
               this.accelerationZ = this.velocityZ * 0.1;
            }

            if (source.getAttacker() instanceof LivingEntity) {
               this.shooter = (LivingEntity)source.getAttacker();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public float getBrightness(float tickDelta) {
      return 1.0F;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getLightLevel(float tickDelta) {
      return 15728880;
   }
}
