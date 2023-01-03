package net.minecraft.entity.vehicle;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class TntMinecartEntity extends MinecartEntity {
   private int fuseTicks = -1;

   public TntMinecartEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   public TntMinecartEntity(World c_54ruxjwzt, double d, double e, double f) {
      super(c_54ruxjwzt, d, e, f);
   }

   @Override
   public MinecartEntity.Type getMinecartType() {
      return MinecartEntity.Type.TNT;
   }

   @Override
   public Block getDefaultDisplayBlock() {
      return Blocks.TNT;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.fuseTicks > 0) {
         --this.fuseTicks;
         this.world.addParticle(ParticleType.SMOKE_NORMAL, this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
      } else if (this.fuseTicks == 0) {
         this.explode(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
      }

      if (this.collidingHorizontally) {
         double var1 = this.velocityX * this.velocityX + this.velocityZ * this.velocityZ;
         if (var1 >= 0.01F) {
            this.explode(var1);
         }
      }
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      Entity var3 = source.getSource();
      if (var3 instanceof ArrowEntity) {
         ArrowEntity var4 = (ArrowEntity)var3;
         if (var4.isOnFire()) {
            this.explode(var4.velocityX * var4.velocityX + var4.velocityY * var4.velocityY + var4.velocityZ * var4.velocityZ);
         }
      }

      return super.damage(source, amount);
   }

   @Override
   public void dropItems(DamageSource damageSource) {
      super.dropItems(damageSource);
      double var2 = this.velocityX * this.velocityX + this.velocityZ * this.velocityZ;
      if (!damageSource.isExplosive()) {
         this.dropItem(new ItemStack(Blocks.TNT, 1), 0.0F);
      }

      if (damageSource.isFire() || damageSource.isExplosive() || var2 >= 0.01F) {
         this.explode(var2);
      }
   }

   protected void explode(double distance) {
      if (!this.world.isClient) {
         double var3 = Math.sqrt(distance);
         if (var3 > 5.0) {
            var3 = 5.0;
         }

         this.world.explode(this, this.x, this.y, this.z, (float)(4.0 + this.random.nextDouble() * 1.5 * var3), true);
         this.remove();
      }
   }

   @Override
   public void applyFallDamage(float distance, float g) {
      if (distance >= 3.0F) {
         float var3 = distance / 10.0F;
         this.explode((double)(var3 * var3));
      }

      super.applyFallDamage(distance, g);
   }

   @Override
   public void onActivatorRail(int x, int y, int z, boolean powered) {
      if (powered && this.fuseTicks < 0) {
         this.prime();
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void doEvent(byte event) {
      if (event == 10) {
         this.prime();
      } else {
         super.doEvent(event);
      }
   }

   public void prime() {
      this.fuseTicks = 80;
      if (!this.world.isClient) {
         this.world.doEntityEvent(this, (byte)10);
         if (!this.isSilent()) {
            this.world.playSound(this, "game.tnt.primed", 1.0F, 1.0F);
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public int getFuseTicks() {
      return this.fuseTicks;
   }

   public boolean isPrimed() {
      return this.fuseTicks > -1;
   }

   @Override
   public float getBlastResistance(Explosion explosion, World world, BlockPos x, BlockState y) {
      return !this.isPrimed() || !AbstractRailBlock.isRail(y) && !AbstractRailBlock.isRail(world, x.up())
         ? super.getBlastResistance(explosion, world, x, y)
         : 0.0F;
   }

   @Override
   public boolean canExplodeBlock(Explosion explosion, World world, BlockPos x, BlockState y, float z) {
      return !this.isPrimed() || !AbstractRailBlock.isRail(y) && !AbstractRailBlock.isRail(world, x.up())
         ? super.canExplodeBlock(explosion, world, x, y, z)
         : false;
   }

   @Override
   protected void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      if (nbt.isType("TNTFuse", 99)) {
         this.fuseTicks = nbt.getInt("TNTFuse");
      }
   }

   @Override
   protected void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putInt("TNTFuse", this.fuseTicks);
   }
}
