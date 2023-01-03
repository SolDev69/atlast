package net.minecraft.entity.thrown;

import java.util.List;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PotionEntity extends ThrownEntity {
   private ItemStack stack;

   public PotionEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   public PotionEntity(World world, LivingEntity thrower, int metadata) {
      this(world, thrower, new ItemStack(Items.POTION, 1, metadata));
   }

   public PotionEntity(World world, LivingEntity thrower, ItemStack stack) {
      super(world, thrower);
      this.stack = stack;
   }

   @Environment(EnvType.CLIENT)
   public PotionEntity(World world, double x, double y, double f, int i) {
      this(world, x, y, f, new ItemStack(Items.POTION, 1, i));
   }

   public PotionEntity(World world, double x, double y, double f, ItemStack c_72owraavl) {
      super(world, x, y, f);
      this.stack = c_72owraavl;
   }

   @Override
   protected float getGravity() {
      return 0.05F;
   }

   @Override
   protected float getSpeed() {
      return 0.5F;
   }

   @Override
   protected float getStartPitchOffset() {
      return -20.0F;
   }

   public void setPotionValue(int damage) {
      if (this.stack == null) {
         this.stack = new ItemStack(Items.POTION, 1, 0);
      }

      this.stack.setDamage(damage);
   }

   public int getMetadata() {
      if (this.stack == null) {
         this.stack = new ItemStack(Items.POTION, 1, 0);
      }

      return this.stack.getMetadata();
   }

   @Override
   protected void onCollision(HitResult result) {
      if (!this.world.isClient) {
         List var2 = Items.POTION.getPotionEffects(this.stack);
         if (var2 != null && !var2.isEmpty()) {
            Box var3 = this.getBoundingBox().expand(4.0, 2.0, 4.0);
            List var4 = this.world.getEntities(LivingEntity.class, var3);
            if (!var4.isEmpty()) {
               for(LivingEntity var6 : var4) {
                  double var7 = this.getSquaredDistanceTo(var6);
                  if (var7 < 16.0) {
                     double var9 = 1.0 - Math.sqrt(var7) / 4.0;
                     if (var6 == result.entity) {
                        var9 = 1.0;
                     }

                     for(StatusEffectInstance var12 : var2) {
                        int var13 = var12.getId();
                        if (StatusEffect.BY_ID[var13].isInstant()) {
                           StatusEffect.BY_ID[var13].affectHealth(this, this.getOwner(), var6, var12.getAmplifier(), var9);
                        } else {
                           int var14 = (int)(var9 * (double)var12.getDuration() + 0.5);
                           if (var14 > 20) {
                              var6.addStatusEffect(new StatusEffectInstance(var13, var14, var12.getAmplifier()));
                           }
                        }
                     }
                  }
               }
            }
         }

         this.world.doEvent(2002, new BlockPos(this), this.getMetadata());
         this.remove();
      }
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      if (nbt.isType("Potion", 10)) {
         this.stack = ItemStack.fromNbt(nbt.getCompound("Potion"));
      } else {
         this.setPotionValue(nbt.getInt("potionValue"));
      }

      if (this.stack == null) {
         this.remove();
      }
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      if (this.stack != null) {
         nbt.put("Potion", this.stack.writeNbt(new NbtCompound()));
      }
   }
}
