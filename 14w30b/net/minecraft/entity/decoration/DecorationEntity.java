package net.minecraft.entity.decoration;

import net.minecraft.block.Block;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public abstract class DecorationEntity extends Entity {
   private int validationCooldown;
   protected BlockPos pos;
   public Direction getFacing;

   public DecorationEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.5F, 0.5F);
   }

   public DecorationEntity(World world, BlockPos pos) {
      this(world);
      this.pos = pos;
   }

   @Override
   protected void initDataTracker() {
   }

   protected void setDirection(Direction dir) {
      Validate.notNull(dir);
      Validate.isTrue(dir.getAxis().isHorizontal());
      this.getFacing = dir;
      this.prevYaw = this.yaw = (float)(this.getFacing.getIdHorizontal() * 90);
      this.updateHitbox();
   }

   private void updateHitbox() {
      if (this.getFacing != null) {
         float var1 = (float)this.pos.getX() + 0.5F;
         float var2 = (float)this.pos.getY() + 0.5F;
         float var3 = (float)this.pos.getZ() + 0.5F;
         float var4 = 0.46875F;
         float var5 = this.getPositionOffset(this.getWidth());
         float var6 = this.getPositionOffset(this.getHeight());
         var1 -= (float)this.getFacing.getOffsetX() * 0.46875F;
         var3 -= (float)this.getFacing.getOffsetZ() * 0.46875F;
         var2 += var6;
         Direction var7 = this.getFacing.counterClockwiseY();
         var1 += var5 * (float)var7.getOffsetX();
         var3 += var5 * (float)var7.getOffsetZ();
         this.x = (double)var1;
         this.y = (double)var2;
         this.z = (double)var3;
         float var8 = (float)this.getWidth();
         float var9 = (float)this.getHeight();
         float var10 = (float)this.getWidth();
         if (this.getFacing.getAxis() == Direction.Axis.Z) {
            var10 = 1.0F;
         } else {
            var8 = 1.0F;
         }

         var8 /= 32.0F;
         var9 /= 32.0F;
         var10 /= 32.0F;
         this.setHitbox(
            new Box((double)(var1 - var8), (double)(var2 - var9), (double)(var3 - var10), (double)(var1 + var8), (double)(var2 + var9), (double)(var3 + var10))
         );
      }
   }

   private float getPositionOffset(int length) {
      return length % 32 == 0 ? 0.5F : 0.0F;
   }

   @Override
   public void tick() {
      this.prevX = this.x;
      this.prevY = this.y;
      this.prevZ = this.z;
      if (this.validationCooldown++ == 100 && !this.world.isClient) {
         this.validationCooldown = 0;
         if (!this.removed && !this.isPosValid()) {
            this.remove();
            this.onAttack(null);
         }
      }
   }

   public boolean isPosValid() {
      if (!this.world.getCollisions(this, this.getBoundingBox()).isEmpty()) {
         return false;
      } else {
         int var1 = Math.max(1, this.getWidth() / 16);
         int var2 = Math.max(1, this.getHeight() / 16);
         BlockPos var3 = this.pos.offset(this.getFacing.getOpposite());
         Direction var4 = this.getFacing.counterClockwiseY();

         for(int var5 = 0; var5 < var1; ++var5) {
            for(int var6 = 0; var6 < var2; ++var6) {
               BlockPos var7 = var3.offset(var4, var5).up(var6);
               Block var8 = this.world.getBlockState(var7).getBlock();
               if (!var8.getMaterial().isSolid() && !RedstoneDiodeBlock.isDiode(var8)) {
                  return false;
               }
            }
         }

         for(Entity var11 : this.world.getEntities(this, this.getBoundingBox())) {
            if (var11 instanceof DecorationEntity) {
               return false;
            }
         }

         return true;
      }
   }

   @Override
   public boolean hasCollision() {
      return true;
   }

   @Override
   public boolean onPunched(Entity attacker) {
      return attacker instanceof PlayerEntity ? this.damage(DamageSource.player((PlayerEntity)attacker), 0.0F) : false;
   }

   @Override
   public Direction getDirection() {
      return this.getFacing;
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerable(source)) {
         return false;
      } else {
         if (!this.removed && !this.world.isClient) {
            this.remove();
            this.onDamaged();
            this.onAttack(source.getAttacker());
         }

         return true;
      }
   }

   @Override
   public void move(double dx, double dy, double dz) {
      if (!this.world.isClient && !this.removed && dx * dx + dy * dy + dz * dz > 0.0) {
         this.remove();
         this.onAttack(null);
      }
   }

   @Override
   public void addVelocity(double velocityX, double velocityY, double velocityZ) {
      if (!this.world.isClient && !this.removed && velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ > 0.0) {
         this.remove();
         this.onAttack(null);
      }
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      nbt.putByte("Facing", (byte)this.getFacing.getIdHorizontal());
      nbt.putInt("TileX", this.getBlockPos().getX());
      nbt.putInt("TileY", this.getBlockPos().getY());
      nbt.putInt("TileZ", this.getBlockPos().getZ());
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      this.pos = new BlockPos(nbt.getInt("TileX"), nbt.getInt("TileY"), nbt.getInt("TileZ"));
      Direction var2;
      if (nbt.isType("Direction", 99)) {
         var2 = Direction.byIdHorizontal(nbt.getByte("Direction"));
         this.pos = this.pos.offset(var2);
      } else if (nbt.isType("Facing", 99)) {
         var2 = Direction.byIdHorizontal(nbt.getByte("Facing"));
      } else {
         var2 = Direction.byIdHorizontal(nbt.getByte("Dir"));
      }

      this.setDirection(var2);
   }

   public abstract int getWidth();

   public abstract int getHeight();

   public abstract void onAttack(Entity entity);

   @Override
   protected boolean shouldSetPositionOnLoad() {
      return false;
   }

   @Override
   public void setPosition(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
      BlockPos var7 = this.pos;
      this.pos = new BlockPos(x, y, z);
      if (!this.pos.equals(var7)) {
         this.updateHitbox();
         this.velocityDirty = true;
      }
   }

   public BlockPos getBlockPos() {
      return this.pos;
   }
}
