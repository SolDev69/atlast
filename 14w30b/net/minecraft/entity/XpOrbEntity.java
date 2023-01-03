package net.minecraft.entity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class XpOrbEntity extends Entity {
   public int renderTicks;
   public int orbAge;
   public int pickupDelay;
   private int health = 5;
   private int xp;
   private PlayerEntity igniter;
   private int lastTargetUpdateTick;

   public XpOrbEntity(World world, double x, double y, double f, int i) {
      super(world);
      this.setDimensions(0.5F, 0.5F);
      this.setPosition(x, y, f);
      this.yaw = (float)(Math.random() * 360.0);
      this.velocityX = (double)((float)(Math.random() * 0.2F - 0.1F) * 2.0F);
      this.velocityY = (double)((float)(Math.random() * 0.2) * 2.0F);
      this.velocityZ = (double)((float)(Math.random() * 0.2F - 0.1F) * 2.0F);
      this.xp = i;
   }

   @Override
   protected boolean canClimb() {
      return false;
   }

   public XpOrbEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.25F, 0.25F);
   }

   @Override
   protected void initDataTracker() {
   }

   @Environment(EnvType.CLIENT)
   @Override
   public int getLightLevel(float tickDelta) {
      float var2 = 0.5F;
      var2 = MathHelper.clamp(var2, 0.0F, 1.0F);
      int var3 = super.getLightLevel(tickDelta);
      int var4 = var3 & 0xFF;
      int var5 = var3 >> 16 & 0xFF;
      var4 += (int)(var2 * 15.0F * 16.0F);
      if (var4 > 240) {
         var4 = 240;
      }

      return var4 | var5 << 16;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.pickupDelay > 0) {
         --this.pickupDelay;
      }

      this.prevX = this.x;
      this.prevY = this.y;
      this.prevZ = this.z;
      this.velocityY -= 0.03F;
      if (this.world.getBlockState(new BlockPos(this)).getBlock().getMaterial() == Material.LAVA) {
         this.velocityY = 0.2F;
         this.velocityX = (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
         this.velocityZ = (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
         this.playSound("random.fizz", 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
      }

      this.pushAwayFrom(this.x, (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.z);
      double var1 = 8.0;
      if (this.lastTargetUpdateTick < this.renderTicks - 20 + this.getNetworkId() % 100) {
         if (this.igniter == null || this.igniter.getSquaredDistanceTo(this) > var1 * var1) {
            this.igniter = this.world.getClosestPlayer(this, var1);
         }

         this.lastTargetUpdateTick = this.renderTicks;
      }

      if (this.igniter != null) {
         double var3 = (this.igniter.x - this.x) / var1;
         double var5 = (this.igniter.y + (double)this.igniter.getEyeHeight() - this.y) / var1;
         double var7 = (this.igniter.z - this.z) / var1;
         double var9 = Math.sqrt(var3 * var3 + var5 * var5 + var7 * var7);
         double var11 = 1.0 - var9;
         if (var11 > 0.0) {
            var11 *= var11;
            this.velocityX += var3 / var9 * var11 * 0.1;
            this.velocityY += var5 / var9 * var11 * 0.1;
            this.velocityZ += var7 / var9 * var11 * 0.1;
         }
      }

      this.move(this.velocityX, this.velocityY, this.velocityZ);
      float var13 = 0.98F;
      if (this.onGround) {
         var13 = this.world
               .getBlockState(new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.z)))
               .getBlock()
               .slipperiness
            * 0.98F;
      }

      this.velocityX *= (double)var13;
      this.velocityY *= 0.98F;
      this.velocityZ *= (double)var13;
      if (this.onGround) {
         this.velocityY *= -0.9F;
      }

      ++this.renderTicks;
      ++this.orbAge;
      if (this.orbAge >= 6000) {
         this.remove();
      }
   }

   @Override
   public boolean checkWaterCollision() {
      return this.world.applyMaterialDrag(this.getBoundingBox(), Material.WATER, this);
   }

   @Override
   protected void applyFireDamage(int amount) {
      this.damage(DamageSource.FIRE, (float)amount);
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerable(source)) {
         return false;
      } else {
         this.onDamaged();
         this.health = (int)((float)this.health - amount);
         if (this.health <= 0) {
            this.remove();
         }

         return false;
      }
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      nbt.putShort("Health", (short)((byte)this.health));
      nbt.putShort("Age", (short)this.orbAge);
      nbt.putShort("Value", (short)this.xp);
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      this.health = nbt.getShort("Health") & 255;
      this.orbAge = nbt.getShort("Age");
      this.xp = nbt.getShort("Value");
   }

   @Override
   public void onPlayerCollision(PlayerEntity player) {
      if (!this.world.isClient) {
         if (this.pickupDelay == 0 && player.xpCooldown == 0) {
            player.xpCooldown = 2;
            this.world.playSound((Entity)player, "random.orb", 0.1F, 0.5F * ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.8F));
            player.sendPickup(this, 1);
            player.increaseXp(this.xp);
            this.remove();
         }
      }
   }

   public int getXp() {
      return this.xp;
   }

   @Environment(EnvType.CLIENT)
   public int getSize() {
      if (this.xp >= 2477) {
         return 10;
      } else if (this.xp >= 1237) {
         return 9;
      } else if (this.xp >= 617) {
         return 8;
      } else if (this.xp >= 307) {
         return 7;
      } else if (this.xp >= 149) {
         return 6;
      } else if (this.xp >= 73) {
         return 5;
      } else if (this.xp >= 37) {
         return 4;
      } else if (this.xp >= 17) {
         return 3;
      } else if (this.xp >= 7) {
         return 2;
      } else {
         return this.xp >= 3 ? 1 : 0;
      }
   }

   public static int roundSize(int size) {
      if (size >= 2477) {
         return 2477;
      } else if (size >= 1237) {
         return 1237;
      } else if (size >= 617) {
         return 617;
      } else if (size >= 307) {
         return 307;
      } else if (size >= 149) {
         return 149;
      } else if (size >= 73) {
         return 73;
      } else if (size >= 37) {
         return 37;
      } else if (size >= 17) {
         return 17;
      } else if (size >= 7) {
         return 7;
      } else {
         return size >= 3 ? 3 : 1;
      }
   }

   @Override
   public boolean canBePunched() {
      return false;
   }
}
