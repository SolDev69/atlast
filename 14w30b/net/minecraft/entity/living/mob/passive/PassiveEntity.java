package net.minecraft.entity.living.mob.passive;

import net.minecraft.entity.Entities;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class PassiveEntity extends PathAwareEntity {
   protected int f_13maveljd;
   protected int f_10tflpcsb;
   protected int f_14qxxiaaf;
   private float ageWidth = -1.0F;
   private float ageHeight;

   public PassiveEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   public abstract PassiveEntity makeChild(PassiveEntity other);

   @Override
   public boolean canInteract(PlayerEntity player) {
      ItemStack var2 = player.inventory.getMainHandStack();
      if (var2 != null && var2.getItem() == Items.SPAWN_EGG) {
         if (!this.world.isClient) {
            Class var3 = Entities.getType(var2.getMetadata());
            if (var3 != null && this.getClass() == var3) {
               PassiveEntity var4 = this.makeChild(this);
               if (var4 != null) {
                  var4.setBreedingAge(-24000);
                  var4.refreshPositionAndAngles(this.x, this.y, this.z, 0.0F, 0.0F);
                  this.world.addEntity(var4);
                  if (var2.hasCustomHoverName()) {
                     var4.setCustomName(var2.getHoverName());
                  }

                  if (!player.abilities.creativeMode) {
                     --var2.size;
                     if (var2.size <= 0) {
                        player.inventory.setStack(player.inventory.selectedSlot, null);
                     }
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(12, (byte)0);
   }

   public int getBreedingAge() {
      return this.world.isClient ? this.dataTracker.getByte(12) : this.f_13maveljd;
   }

   public void m_80bwrrzkm(int i, boolean bl) {
      int var3 = this.getBreedingAge();
      var3 += i * 20;
      if (var3 > 0) {
         var3 = 0;
         if (var3 < 0) {
            this.m_90sjxdogf();
         }
      }

      int var5 = var3 - var3;
      this.setBreedingAge(var3);
      if (bl) {
         this.f_10tflpcsb += var5;
         if (this.f_14qxxiaaf == 0) {
            this.f_14qxxiaaf = 40;
         }
      }

      if (this.getBreedingAge() == 0) {
         this.setBreedingAge(this.f_10tflpcsb);
      }
   }

   public void growUp(int age) {
      this.m_80bwrrzkm(age, false);
   }

   public void setBreedingAge(int age) {
      this.dataTracker.update(12, (byte)MathHelper.clamp(age, -1, 1));
      this.f_13maveljd = age;
      this.setAgeSize(this.isBaby());
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putInt("Age", this.getBreedingAge());
      nbt.putInt("ForcedAge", this.f_10tflpcsb);
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.setBreedingAge(nbt.getInt("Age"));
      this.f_10tflpcsb = nbt.getInt("ForcedAge");
   }

   @Override
   public void tickAI() {
      super.tickAI();
      if (this.world.isClient) {
         if (this.f_14qxxiaaf > 0) {
            if (this.f_14qxxiaaf % 4 == 0) {
               this.world
                  .addParticle(
                     ParticleType.VILLAGER_HAPPY,
                     this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
                     this.y + 0.5 + (double)(this.random.nextFloat() * this.height),
                     this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
                     0.0,
                     0.0,
                     0.0
                  );
            }

            --this.f_14qxxiaaf;
         }

         this.setAgeSize(this.isBaby());
      } else {
         int var1 = this.getBreedingAge();
         if (var1 < 0) {
            this.setBreedingAge(++var1);
            if (var1 == 0) {
               this.m_90sjxdogf();
            }
         } else if (var1 > 0) {
            this.setBreedingAge(--var1);
         }
      }
   }

   protected void m_90sjxdogf() {
   }

   @Override
   public boolean isBaby() {
      return this.getBreedingAge() < 0;
   }

   public void setAgeSize(boolean isBaby) {
      this.resizeBounds(isBaby ? 0.5F : 1.0F);
   }

   @Override
   protected final void setDimensions(float width, float height) {
      boolean var3 = this.ageWidth > 0.0F;
      this.ageWidth = width;
      this.ageHeight = height;
      if (!var3) {
         this.resizeBounds(1.0F);
      }
   }

   protected final void resizeBounds(float size) {
      super.setDimensions(this.ageWidth * size, this.ageHeight * size);
   }
}
