package net.minecraft.entity.vehicle;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FurnaceMinecartEntity extends MinecartEntity {
   private int fuel;
   public double pushX;
   public double pushZ;

   public FurnaceMinecartEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   public FurnaceMinecartEntity(World c_54ruxjwzt, double d, double e, double f) {
      super(c_54ruxjwzt, d, e, f);
   }

   @Override
   public MinecartEntity.Type getMinecartType() {
      return MinecartEntity.Type.FURNACE;
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(16, new Byte((byte)0));
   }

   @Override
   public void tick() {
      super.tick();
      if (this.fuel > 0) {
         --this.fuel;
      }

      if (this.fuel <= 0) {
         this.pushX = this.pushZ = 0.0;
      }

      this.setLit(this.fuel > 0);
      if (this.isLit() && this.random.nextInt(4) == 0) {
         this.world.addParticle(ParticleType.SMOKE_LARGE, this.x, this.y + 0.8, this.z, 0.0, 0.0, 0.0);
      }
   }

   @Override
   protected double m_41gmsyuoz() {
      return 0.2;
   }

   @Override
   public void dropItems(DamageSource damageSource) {
      super.dropItems(damageSource);
      if (!damageSource.isExplosive()) {
         this.dropItem(new ItemStack(Blocks.FURNACE, 1), 0.0F);
      }
   }

   @Override
   protected void moveOnRail(BlockPos x, BlockState y) {
      super.moveOnRail(x, y);
      double var3 = this.pushX * this.pushX + this.pushZ * this.pushZ;
      if (var3 > 1.0E-4 && this.velocityX * this.velocityX + this.velocityZ * this.velocityZ > 0.001) {
         var3 = (double)MathHelper.sqrt(var3);
         this.pushX /= var3;
         this.pushZ /= var3;
         if (this.pushX * this.velocityX + this.pushZ * this.velocityZ < 0.0) {
            this.pushX = 0.0;
            this.pushZ = 0.0;
         } else {
            double var5 = var3 / this.m_41gmsyuoz();
            this.pushX *= var5;
            this.pushZ *= var5;
         }
      }
   }

   @Override
   protected void applySlowdown() {
      double var1 = this.pushX * this.pushX + this.pushZ * this.pushZ;
      if (var1 > 1.0E-4) {
         var1 = (double)MathHelper.sqrt(var1);
         this.pushX /= var1;
         this.pushZ /= var1;
         double var3 = 1.0;
         this.velocityX *= 0.8F;
         this.velocityY *= 0.0;
         this.velocityZ *= 0.8F;
         this.velocityX += this.pushX * var3;
         this.velocityZ += this.pushZ * var3;
      } else {
         this.velocityX *= 0.98F;
         this.velocityY *= 0.0;
         this.velocityZ *= 0.98F;
      }

      super.applySlowdown();
   }

   @Override
   public boolean interact(PlayerEntity player) {
      ItemStack var2 = player.inventory.getMainHandStack();
      if (var2 != null && var2.getItem() == Items.COAL) {
         if (!player.abilities.creativeMode && --var2.size == 0) {
            player.inventory.setStack(player.inventory.selectedSlot, null);
         }

         this.fuel += 3600;
      }

      this.pushX = this.x - player.x;
      this.pushZ = this.z - player.z;
      return true;
   }

   @Override
   protected void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putDouble("PushX", this.pushX);
      nbt.putDouble("PushZ", this.pushZ);
      nbt.putShort("Fuel", (short)this.fuel);
   }

   @Override
   protected void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.pushX = nbt.getDouble("PushX");
      this.pushZ = nbt.getDouble("PushZ");
      this.fuel = nbt.getShort("Fuel");
   }

   protected boolean isLit() {
      return (this.dataTracker.getByte(16) & 1) != 0;
   }

   protected void setLit(boolean lit) {
      if (lit) {
         this.dataTracker.update(16, (byte)(this.dataTracker.getByte(16) | 1));
      } else {
         this.dataTracker.update(16, (byte)(this.dataTracker.getByte(16) & -2));
      }
   }

   @Override
   public Block getDefaultDisplayBlock() {
      return this.isLit() ? Blocks.LIT_FURNACE : Blocks.FURNACE;
   }

   @Override
   public int getDefaultDisplayBlockMetadata() {
      return 2;
   }
}
