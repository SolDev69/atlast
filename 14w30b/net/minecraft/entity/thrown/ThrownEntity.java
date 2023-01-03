package net.minecraft.entity.thrown;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Dispensable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.Identifier;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class ThrownEntity extends Entity implements Dispensable {
   private int blockX = -1;
   private int blockY = -1;
   private int blockZ = -1;
   private Block inBlock;
   protected boolean inGround;
   public int shake;
   private LivingEntity thrower;
   private String ownerName;
   private int ticksInBlock;
   private int ticksInAir;

   public ThrownEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.25F, 0.25F);
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

   public ThrownEntity(World world, LivingEntity thrower) {
      super(world);
      this.thrower = thrower;
      this.setDimensions(0.25F, 0.25F);
      this.refreshPositionAndAngles(thrower.x, thrower.y + (double)thrower.getEyeHeight(), thrower.z, thrower.yaw, thrower.pitch);
      this.x -= (double)(MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * 0.16F);
      this.y -= 0.1F;
      this.z -= (double)(MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * 0.16F);
      this.setPosition(this.x, this.y, this.z);
      float var3 = 0.4F;
      this.velocityX = (double)(-MathHelper.sin(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * var3);
      this.velocityZ = (double)(MathHelper.cos(this.yaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.pitch / 180.0F * (float) Math.PI) * var3);
      this.velocityY = (double)(-MathHelper.sin((this.pitch + this.getStartPitchOffset()) / 180.0F * (float) Math.PI) * var3);
      this.setVelocity(this.velocityX, this.velocityY, this.velocityZ, this.getSpeed(), 1.0F);
   }

   public ThrownEntity(World world, double x, double y, double z) {
      super(world);
      this.ticksInBlock = 0;
      this.setDimensions(0.25F, 0.25F);
      this.setPosition(x, y, z);
   }

   protected float getSpeed() {
      return 1.5F;
   }

   protected float getStartPitchOffset() {
      return 0.0F;
   }

   @Override
   public void setVelocity(double velocityX, double velocityY, double velocityZ, float min, float scale) {
      float var9 = MathHelper.sqrt(velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ);
      velocityX /= (double)var9;
      velocityY /= (double)var9;
      velocityZ /= (double)var9;
      velocityX += this.random.nextGaussian() * 0.0075F * (double)scale;
      velocityY += this.random.nextGaussian() * 0.0075F * (double)scale;
      velocityZ += this.random.nextGaussian() * 0.0075F * (double)scale;
      velocityX *= (double)min;
      velocityY *= (double)min;
      velocityZ *= (double)min;
      this.velocityX = velocityX;
      this.velocityY = velocityY;
      this.velocityZ = velocityZ;
      float var10 = MathHelper.sqrt(velocityX * velocityX + velocityZ * velocityZ);
      this.prevYaw = this.yaw = (float)(Math.atan2(velocityX, velocityZ) * 180.0 / (float) Math.PI);
      this.prevPitch = this.pitch = (float)(Math.atan2(velocityY, (double)var10) * 180.0 / (float) Math.PI);
      this.ticksInBlock = 0;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void setVelocity(double velocityX, double velocityY, double velocityZ) {
      this.velocityX = velocityX;
      this.velocityY = velocityY;
      this.velocityZ = velocityZ;
      if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
         float var7 = MathHelper.sqrt(velocityX * velocityX + velocityZ * velocityZ);
         this.prevYaw = this.yaw = (float)(Math.atan2(velocityX, velocityZ) * 180.0 / (float) Math.PI);
         this.prevPitch = this.pitch = (float)(Math.atan2(velocityY, (double)var7) * 180.0 / (float) Math.PI);
      }
   }

   @Override
   public void tick() {
      this.prevTickX = this.x;
      this.prevTickY = this.y;
      this.prevTickZ = this.z;
      super.tick();
      if (this.shake > 0) {
         --this.shake;
      }

      if (this.inGround) {
         if (this.world.getBlockState(new BlockPos(this.blockX, this.blockY, this.blockZ)).getBlock() == this.inBlock) {
            ++this.ticksInBlock;
            if (this.ticksInBlock == 1200) {
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

      if (!this.world.isClient) {
         Entity var4 = null;
         List var5 = this.world.getEntities(this, this.getBoundingBox().grow(this.velocityX, this.velocityY, this.velocityZ).expand(1.0, 1.0, 1.0));
         double var6 = 0.0;
         LivingEntity var8 = this.getOwner();

         for(int var9 = 0; var9 < var5.size(); ++var9) {
            Entity var10 = (Entity)var5.get(var9);
            if (var10.hasCollision() && (var10 != var8 || this.ticksInAir >= 5)) {
               float var11 = 0.3F;
               Box var12 = var10.getBoundingBox().expand((double)var11, (double)var11, (double)var11);
               HitResult var13 = var12.clip(var1, var2);
               if (var13 != null) {
                  double var14 = var1.distanceTo(var13.pos);
                  if (var14 < var6 || var6 == 0.0) {
                     var4 = var10;
                     var6 = var14;
                  }
               }
            }
         }

         if (var4 != null) {
            var3 = new HitResult(var4);
         }
      }

      if (var3 != null) {
         if (var3.type == HitResult.Type.BLOCK && this.world.getBlockState(var3.getBlockPos()).getBlock() == Blocks.NETHER_PORTAL) {
            this.onPortalCollision();
         } else {
            this.onCollision(var3);
         }
      }

      this.x += this.velocityX;
      this.y += this.velocityY;
      this.z += this.velocityZ;
      float var18 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
      this.yaw = (float)(Math.atan2(this.velocityX, this.velocityZ) * 180.0 / (float) Math.PI);
      this.pitch = (float)(Math.atan2(this.velocityY, (double)var18) * 180.0 / (float) Math.PI);

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
      float var19 = 0.99F;
      float var20 = this.getGravity();
      if (this.isInWater()) {
         for(int var7 = 0; var7 < 4; ++var7) {
            float var21 = 0.25F;
            this.world
               .addParticle(
                  ParticleType.WATER_BUBBLE,
                  this.x - this.velocityX * (double)var21,
                  this.y - this.velocityY * (double)var21,
                  this.z - this.velocityZ * (double)var21,
                  this.velocityX,
                  this.velocityY,
                  this.velocityZ
               );
         }

         var19 = 0.8F;
      }

      this.velocityX *= (double)var19;
      this.velocityY *= (double)var19;
      this.velocityZ *= (double)var19;
      this.velocityY -= (double)var20;
      this.setPosition(this.x, this.y, this.z);
   }

   protected float getGravity() {
      return 0.03F;
   }

   protected abstract void onCollision(HitResult result);

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      nbt.putShort("xTile", (short)this.blockX);
      nbt.putShort("yTile", (short)this.blockY);
      nbt.putShort("zTile", (short)this.blockZ);
      Identifier var2 = (Identifier)Block.REGISTRY.getKey(this.inBlock);
      nbt.putString("inTile", var2 == null ? "" : var2.toString());
      nbt.putByte("shake", (byte)this.shake);
      nbt.putByte("inGround", (byte)(this.inGround ? 1 : 0));
      if ((this.ownerName == null || this.ownerName.length() == 0) && this.thrower instanceof PlayerEntity) {
         this.ownerName = this.thrower.getName();
      }

      nbt.putString("ownerName", this.ownerName == null ? "" : this.ownerName);
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

      this.shake = nbt.getByte("shake") & 255;
      this.inGround = nbt.getByte("inGround") == 1;
      this.ownerName = nbt.getString("ownerName");
      if (this.ownerName != null && this.ownerName.length() == 0) {
         this.ownerName = null;
      }
   }

   public LivingEntity getOwner() {
      if (this.thrower == null && this.ownerName != null && this.ownerName.length() > 0) {
         this.thrower = this.world.getPlayer(this.ownerName);
      }

      return this.thrower;
   }
}
