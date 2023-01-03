package net.minecraft.entity.living.mob.passive.animal;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategoryProvider;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.mob.passive.PassiveEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;

public abstract class AnimalEntity extends PassiveEntity implements EntityCategoryProvider {
   protected Block f_92ebgqdsn = Blocks.GRASS;
   private int loveTicks;
   private PlayerEntity loveCausingPlayer;

   public AnimalEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   @Override
   protected void m_45jbqtvrb() {
      if (this.getBreedingAge() != 0) {
         this.loveTicks = 0;
      }

      super.m_45jbqtvrb();
   }

   @Override
   public void tickAI() {
      super.tickAI();
      if (this.getBreedingAge() != 0) {
         this.loveTicks = 0;
      }

      if (this.loveTicks > 0) {
         --this.loveTicks;
         if (this.loveTicks % 10 == 0) {
            double var1 = this.random.nextGaussian() * 0.02;
            double var3 = this.random.nextGaussian() * 0.02;
            double var5 = this.random.nextGaussian() * 0.02;
            this.world
               .addParticle(
                  ParticleType.HEART,
                  this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
                  this.y + 0.5 + (double)(this.random.nextFloat() * this.height),
                  this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
                  var1,
                  var3,
                  var5
               );
         }
      }
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerable(source)) {
         return false;
      } else {
         this.loveTicks = 0;
         return super.damage(source, amount);
      }
   }

   @Override
   public float getPathfindingFavor(BlockPos x) {
      return this.world.getBlockState(x.down()).getBlock() == Blocks.GRASS ? 10.0F : this.world.getBrightness(x) - 0.5F;
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putInt("InLove", this.loveTicks);
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.loveTicks = nbt.getInt("InLove");
   }

   @Override
   public boolean canSpawn() {
      int var1 = MathHelper.floor(this.x);
      int var2 = MathHelper.floor(this.getBoundingBox().minY);
      int var3 = MathHelper.floor(this.z);
      BlockPos var4 = new BlockPos(var1, var2, var3);
      return this.world.getBlockState(var4.down()).getBlock() == this.f_92ebgqdsn && this.world.getLight(var4) > 8 && super.canSpawn();
   }

   @Override
   public int getMinAmbientSoundDelay() {
      return 120;
   }

   @Override
   protected boolean canDespawn() {
      return false;
   }

   @Override
   protected int getXpDrop(PlayerEntity playerEntity) {
      return 1 + this.world.random.nextInt(3);
   }

   public boolean isBreedingItem(ItemStack stack) {
      if (stack == null) {
         return false;
      } else {
         return stack.getItem() == Items.WHEAT;
      }
   }

   @Override
   public boolean canInteract(PlayerEntity player) {
      ItemStack var2 = player.inventory.getMainHandStack();
      if (var2 != null) {
         if (this.isBreedingItem(var2) && this.getBreedingAge() == 0 && this.loveTicks <= 0) {
            if (!player.abilities.creativeMode) {
               --var2.size;
               if (var2.size <= 0) {
                  player.inventory.setStack(player.inventory.selectedSlot, null);
               }
            }

            this.lovePlayer(player);
            return true;
         }

         if (this.isBaby() && this.isBreedingItem(var2)) {
            this.m_80bwrrzkm((int)((float)(-this.getBreedingAge() / 20) * 0.1F), true);
            return true;
         }
      }

      return super.canInteract(player);
   }

   public void lovePlayer(PlayerEntity player) {
      this.loveTicks = 600;
      this.loveCausingPlayer = player;
      this.world.doEntityEvent(this, (byte)18);
   }

   public PlayerEntity getLoveCausingPlayer() {
      return this.loveCausingPlayer;
   }

   public boolean isInLove() {
      return this.loveTicks > 0;
   }

   public void resetLoveTicks() {
      this.loveTicks = 0;
   }

   public boolean canBreedWith(AnimalEntity other) {
      if (other == this) {
         return false;
      } else if (other.getClass() != this.getClass()) {
         return false;
      } else {
         return this.isInLove() && other.isInLove();
      }
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   @Override
   public void doEvent(byte event) {
      if (event == 18) {
         for(int var2 = 0; var2 < 7; ++var2) {
            double var3 = this.random.nextGaussian() * 0.02;
            double var5 = this.random.nextGaussian() * 0.02;
            double var7 = this.random.nextGaussian() * 0.02;
            this.world
               .addParticle(
                  ParticleType.HEART,
                  this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
                  this.y + 0.5 + (double)(this.random.nextFloat() * this.height),
                  this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
                  var3,
                  var5,
                  var7
               );
         }
      } else {
         super.doEvent(event);
      }
   }
}
