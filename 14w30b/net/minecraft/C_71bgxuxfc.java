package net.minecraft;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.GoToEntityGoal;
import net.minecraft.entity.living.mob.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

public class C_71bgxuxfc extends GoToEntityGoal {
   private int f_14yewbovu;
   private VillagerEntity f_09tpozwmt;

   public C_71bgxuxfc(VillagerEntity c_21keykoxl) {
      super(c_21keykoxl, VillagerEntity.class, 3.0F, 0.02F);
      this.f_09tpozwmt = c_21keykoxl;
   }

   @Override
   public void start() {
      super.start();
      if (this.f_09tpozwmt.m_53whewcuu() && this.targetEntity instanceof VillagerEntity && ((VillagerEntity)this.targetEntity).m_19khndpdy()) {
         this.f_14yewbovu = 10;
      } else {
         this.f_14yewbovu = 0;
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.f_14yewbovu > 0) {
         --this.f_14yewbovu;
         if (this.f_14yewbovu == 0) {
            SimpleInventory var1 = this.f_09tpozwmt.m_73ivhbact();

            for(int var2 = 0; var2 < var1.getSize(); ++var2) {
               ItemStack var3 = var1.getStack(var2);
               ItemStack var4 = null;
               if (var3 != null) {
                  Item var5 = var3.getItem();
                  if (var5 != Items.BREAD && var5 != Items.POTATO && (var5 != Items.CARROT || var3.size <= 3)) {
                     if (var5 == Items.WHEAT && var3.size > 5) {
                        int var12 = var3.size / 2 / 3 * 3;
                        int var7 = var12 / 3;
                        var3.size -= var12;
                        var4 = new ItemStack(Items.BREAD, var7, 0);
                     }
                  } else {
                     int var6 = var3.size / 2;
                     var3.size -= var6;
                     var4 = new ItemStack(var5, var6, var3.getMetadata());
                  }

                  if (var3.size <= 0) {
                     var1.setStack(var2, null);
                  }
               }

               if (var4 != null) {
                  double var11 = this.f_09tpozwmt.y - 0.3F + (double)this.f_09tpozwmt.getEyeHeight();
                  ItemEntity var13 = new ItemEntity(this.f_09tpozwmt.world, this.f_09tpozwmt.x, var11, this.f_09tpozwmt.z, var4);
                  float var8 = 0.3F;
                  float var9 = this.f_09tpozwmt.headYaw;
                  float var10 = this.f_09tpozwmt.pitch;
                  var13.velocityX = (double)(-MathHelper.sin(var9 / 180.0F * (float) Math.PI) * MathHelper.cos(var10 / 180.0F * (float) Math.PI) * var8);
                  var13.velocityZ = (double)(MathHelper.cos(var9 / 180.0F * (float) Math.PI) * MathHelper.cos(var10 / 180.0F * (float) Math.PI) * var8);
                  var13.velocityY = (double)(-MathHelper.sin(var10 / 180.0F * (float) Math.PI) * var8 + 0.1F);
                  var13.resetPickupCooldown();
                  this.f_09tpozwmt.world.addEntity(var13);
                  break;
               }
            }
         }
      }
   }
}
