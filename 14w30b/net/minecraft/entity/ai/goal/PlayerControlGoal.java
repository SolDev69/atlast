package net.minecraft.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.pathing.GroundPathNodeHelper;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class PlayerControlGoal extends Goal {
   private final MobEntity mob;
   private final float maxVelocity;
   private float speed;
   private boolean moving;
   private int movingTicks;
   private int maxMovingTicks;

   public PlayerControlGoal(MobEntity mob, float maxVelocity) {
      this.mob = mob;
      this.maxVelocity = maxVelocity;
      this.setControls(7);
   }

   @Override
   public void start() {
      this.speed = 0.0F;
   }

   @Override
   public void stop() {
      this.moving = false;
      this.speed = 0.0F;
   }

   @Override
   public boolean canStart() {
      return this.mob.isAlive() && this.mob.rider != null && this.mob.rider instanceof PlayerEntity && (this.moving || this.mob.canBeControlledByRider());
   }

   @Override
   public void tick() {
      PlayerEntity var1 = (PlayerEntity)this.mob.rider;
      PathAwareEntity var2 = (PathAwareEntity)this.mob;
      float var3 = MathHelper.wrapDegrees(var1.yaw - this.mob.yaw) * 0.5F;
      if (var3 > 5.0F) {
         var3 = 5.0F;
      }

      if (var3 < -5.0F) {
         var3 = -5.0F;
      }

      this.mob.yaw = MathHelper.wrapDegrees(this.mob.yaw + var3);
      if (this.speed < this.maxVelocity) {
         this.speed += (this.maxVelocity - this.speed) * 0.01F;
      }

      if (this.speed > this.maxVelocity) {
         this.speed = this.maxVelocity;
      }

      int var4 = MathHelper.floor(this.mob.x);
      int var5 = MathHelper.floor(this.mob.y);
      int var6 = MathHelper.floor(this.mob.z);
      float var7 = this.speed;
      if (this.moving) {
         if (this.movingTicks++ > this.maxMovingTicks) {
            this.moving = false;
         }

         var7 += var7 * 1.15F * MathHelper.sin((float)this.movingTicks / (float)this.maxMovingTicks * (float) Math.PI);
      }

      float var8 = 0.91F;
      if (this.mob.onGround) {
         var8 = this.mob
               .world
               .getBlockState(new BlockPos(MathHelper.floor((float)var4), MathHelper.floor((float)var5) - 1, MathHelper.floor((float)var6)))
               .getBlock()
               .slipperiness
            * 0.91F;
      }

      float var9 = 0.16277136F / (var8 * var8 * var8);
      float var10 = MathHelper.sin(var2.yaw * (float) Math.PI / 180.0F);
      float var11 = MathHelper.cos(var2.yaw * (float) Math.PI / 180.0F);
      float var12 = var2.getMovementSpeed() * var9;
      float var13 = Math.max(var7, 1.0F);
      var13 = var12 / var13;
      float var14 = var7 * var13;
      float var15 = -(var14 * var10);
      float var16 = var14 * var11;
      if (MathHelper.abs(var15) > MathHelper.abs(var16)) {
         if (var15 < 0.0F) {
            var15 -= this.mob.width / 2.0F;
         }

         if (var15 > 0.0F) {
            var15 += this.mob.width / 2.0F;
         }

         var16 = 0.0F;
      } else {
         var15 = 0.0F;
         if (var16 < 0.0F) {
            var16 -= this.mob.width / 2.0F;
         }

         if (var16 > 0.0F) {
            var16 += this.mob.width / 2.0F;
         }
      }

      int var17 = MathHelper.floor(this.mob.x + (double)var15);
      int var18 = MathHelper.floor(this.mob.z + (double)var16);
      int var19 = MathHelper.floor(this.mob.width + 1.0F);
      int var20 = MathHelper.floor(this.mob.height + var1.height + 1.0F);
      int var21 = MathHelper.floor(this.mob.width + 1.0F);
      if (var4 != var17 || var6 != var18) {
         Block var22 = this.mob.world.getBlockState(new BlockPos(var4, var5, var6)).getBlock();
         boolean var23 = !this.canStepOnto(var22)
            && (var22.getMaterial() != Material.AIR || !this.canStepOnto(this.mob.world.getBlockState(new BlockPos(var4, var5 - 1, var6)).getBlock()));
         if (var23
            && 0 == GroundPathNodeHelper.m_96ogompic(this.mob.world, this.mob, var17, var5, var18, var19, var20, var21, false, false, true)
            && 1 == GroundPathNodeHelper.m_96ogompic(this.mob.world, this.mob, var4, var5 + 1, var6, var19, var20, var21, false, false, true)
            && 1 == GroundPathNodeHelper.m_96ogompic(this.mob.world, this.mob, var17, var5 + 1, var18, var19, var20, var21, false, false, true)) {
            var2.getJumpControl().setActive();
         }
      }

      if (!var1.abilities.creativeMode && this.speed >= this.maxVelocity * 0.5F && this.mob.getRandom().nextFloat() < 0.006F && !this.moving) {
         ItemStack var25 = var1.getStackInHand();
         if (var25 != null && var25.getItem() == Items.CARROT_ON_A_STICK) {
            var25.damageAndBreak(1, var1);
            if (var25.size == 0) {
               ItemStack var26 = new ItemStack(Items.FISHING_ROD);
               var26.setNbt(var25.getNbt());
               var1.inventory.inventorySlots[var1.inventory.selectedSlot] = var26;
            }
         }
      }

      this.mob.moveEntityWithVelocity(0.0F, var7);
   }

   private boolean canStepOnto(Block block) {
      return block instanceof StairsBlock || block instanceof SlabBlock;
   }

   public boolean isMoving() {
      return this.moving;
   }

   public void startMoving() {
      this.moving = true;
      this.movingTicks = 0;
      this.maxMovingTicks = this.mob.getRandom().nextInt(841) + 140;
   }

   public boolean canStartMoving() {
      return !this.isMoving() && this.speed > this.maxVelocity * 0.3F;
   }
}
