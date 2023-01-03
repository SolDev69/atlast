package net.minecraft.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.dimension.TheEndDimension;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EnderCrystalEntity extends Entity {
   public int age;
   public int explosionCountdown;

   public EnderCrystalEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.blocksBuilding = true;
      this.setDimensions(2.0F, 2.0F);
      this.explosionCountdown = 5;
      this.age = this.random.nextInt(100000);
   }

   @Environment(EnvType.CLIENT)
   public EnderCrystalEntity(World world, double x, double y, double z) {
      this(world);
      this.setPosition(x, y, z);
   }

   @Override
   protected boolean canClimb() {
      return false;
   }

   @Override
   protected void initDataTracker() {
      this.dataTracker.put(8, this.explosionCountdown);
   }

   @Override
   public void tick() {
      this.prevX = this.x;
      this.prevY = this.y;
      this.prevZ = this.z;
      ++this.age;
      this.dataTracker.update(8, this.explosionCountdown);
      int var1 = MathHelper.floor(this.x);
      int var2 = MathHelper.floor(this.y);
      int var3 = MathHelper.floor(this.z);
      if (this.world.dimension instanceof TheEndDimension && this.world.getBlockState(new BlockPos(var1, var2, var3)).getBlock() != Blocks.FIRE) {
         this.world.setBlockState(new BlockPos(var1, var2, var3), Blocks.FIRE.defaultState());
      }
   }

   @Override
   protected void writeCustomNbt(NbtCompound nbt) {
   }

   @Override
   protected void readCustomNbt(NbtCompound nbt) {
   }

   @Override
   public boolean hasCollision() {
      return true;
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerable(source)) {
         return false;
      } else {
         if (!this.removed && !this.world.isClient) {
            this.explosionCountdown = 0;
            if (this.explosionCountdown <= 0) {
               this.remove();
               if (!this.world.isClient) {
                  this.world.explode(null, this.x, this.y, this.z, 6.0F, true);
               }
            }
         }

         return true;
      }
   }
}
