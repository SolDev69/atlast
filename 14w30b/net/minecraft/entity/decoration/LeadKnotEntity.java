package net.minecraft.entity.decoration;

import net.minecraft.block.FenceBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class LeadKnotEntity extends DecorationEntity {
   public LeadKnotEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   public LeadKnotEntity(World c_54ruxjwzt, BlockPos c_76varpwca) {
      super(c_54ruxjwzt, c_76varpwca);
      this.setPosition((double)c_76varpwca.getX() + 0.5, (double)c_76varpwca.getY() + 0.5, (double)c_76varpwca.getZ() + 0.5);
      float var3 = 0.125F;
      float var4 = 0.1875F;
      float var5 = 0.25F;
      this.setHitbox(new Box(this.x - 0.1875, this.y - 0.25 + 0.125, this.z - 0.1875, this.x + 0.1875, this.y + 0.25 + 0.125, this.z + 0.1875));
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
   }

   @Override
   public void setDirection(Direction dir) {
   }

   @Override
   public int getWidth() {
      return 9;
   }

   @Override
   public int getHeight() {
      return 9;
   }

   @Override
   public float getEyeHeight() {
      return -0.0625F;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean isWithinViewDistance(double distance) {
      return distance < 1024.0;
   }

   @Override
   public void onAttack(Entity entity) {
   }

   @Override
   public boolean writeNbtNoRider(NbtCompound nbt) {
      return false;
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
   }

   @Override
   public boolean interact(PlayerEntity player) {
      ItemStack var2 = player.getStackInHand();
      boolean var3 = false;
      if (var2 != null && var2.getItem() == Items.LEAD && !this.world.isClient) {
         double var4 = 7.0;

         for(MobEntity var8 : this.world
            .getEntities(MobEntity.class, new Box(this.x - var4, this.y - var4, this.z - var4, this.x + var4, this.y + var4, this.z + var4))) {
            if (var8.isLeashed() && var8.getHoldingEntity() == player) {
               var8.attachLeash(this, true);
               var3 = true;
            }
         }
      }

      if (!this.world.isClient && !var3) {
         this.remove();
         if (player.abilities.creativeMode) {
            double var9 = 7.0;

            for(MobEntity var12 : this.world
               .getEntities(MobEntity.class, new Box(this.x - var9, this.y - var9, this.z - var9, this.x + var9, this.y + var9, this.z + var9))) {
               if (var12.isLeashed() && var12.getHoldingEntity() == this) {
                  var12.detachLeash(true, false);
               }
            }
         }
      }

      return true;
   }

   @Override
   public boolean isPosValid() {
      return this.world.getBlockState(this.pos).getBlock() instanceof FenceBlock;
   }

   public static LeadKnotEntity attatch(World world, BlockPos x) {
      LeadKnotEntity var2 = new LeadKnotEntity(world, x);
      var2.teleporting = true;
      world.addEntity(var2);
      return var2;
   }

   public static LeadKnotEntity getOrCreate(World world, BlockPos x) {
      int var2 = x.getX();
      int var3 = x.getY();
      int var4 = x.getZ();

      for(LeadKnotEntity var7 : world.getEntities(
         LeadKnotEntity.class, new Box((double)var2 - 1.0, (double)var3 - 1.0, (double)var4 - 1.0, (double)var2 + 1.0, (double)var3 + 1.0, (double)var4 + 1.0)
      )) {
         if (var7.getBlockPos().equals(x)) {
            return var7;
         }
      }

      return null;
   }
}
