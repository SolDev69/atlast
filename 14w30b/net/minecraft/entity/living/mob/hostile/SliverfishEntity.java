package net.minecraft.entity.living.mob.hostile;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.InfestedBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.living.LivingEntityType;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SliverfishEntity extends HostileEntity {
   private SliverfishEntity.C_27wpnuezm delay;

   public SliverfishEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.4F, 0.3F);
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(3, this.delay = new SliverfishEntity.C_27wpnuezm(this));
      this.goalSelector.addGoal(4, new MeleeAttackGoal(this, PlayerEntity.class, 1.0, false));
      this.goalSelector.addGoal(5, new SliverfishEntity.C_18fxaygkf(this));
      this.targetSelector.addGoal(1, new RevengeGoal(this, true));
      this.targetSelector.addGoal(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
   }

   @Override
   public float getEyeHeight() {
      return 0.1F;
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(8.0);
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.25);
      this.initializeAttribute(EntityAttributes.ATTACK_DAMAGE).setBase(1.0);
   }

   @Override
   protected boolean canClimb() {
      return false;
   }

   @Override
   protected String getAmbientSound() {
      return "mob.silverfish.say";
   }

   @Override
   protected String getHurtSound() {
      return "mob.silverfish.hit";
   }

   @Override
   protected String getDeathSound() {
      return "mob.silverfish.kill";
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerable(source)) {
         return false;
      } else {
         if (source instanceof EntityDamageSource || source == DamageSource.MAGIC) {
            this.delay.m_62epbvugi();
         }

         return super.damage(source, amount);
      }
   }

   @Override
   protected void playStepSound(BlockPos x, Block y) {
      this.playSound("mob.silverfish.step", 0.15F, 1.0F);
   }

   @Override
   protected Item getDefaultDropLoot() {
      return Item.byRawId(0);
   }

   @Override
   public void tick() {
      this.bodyYaw = this.yaw;
      super.tick();
   }

   @Override
   public float getPathfindingFavor(BlockPos x) {
      return this.world.getBlockState(x.down()).getBlock() == Blocks.STONE ? 10.0F : super.getPathfindingFavor(x);
   }

   @Override
   protected boolean canSpawnAtLightLevel() {
      return true;
   }

   @Override
   public boolean canSpawn() {
      if (super.canSpawn()) {
         PlayerEntity var1 = this.world.getClosestPlayer(this, 5.0);
         return var1 == null;
      } else {
         return false;
      }
   }

   @Override
   public LivingEntityType getMobType() {
      return LivingEntityType.ARTHROPOD;
   }

   static class C_18fxaygkf extends WanderAroundGoal {
      private final SliverfishEntity f_17rodyeil;
      private Direction f_57zqoceda;
      private boolean f_67ffjuwer;

      public C_18fxaygkf(SliverfishEntity c_12vzxufzf) {
         super(c_12vzxufzf, 1.0, 10);
         this.f_17rodyeil = c_12vzxufzf;
         this.setControls(1);
      }

      @Override
      public boolean canStart() {
         if (this.f_17rodyeil.getTargetEntity() != null) {
            return false;
         } else if (!this.f_17rodyeil.getNavigation().isIdle()) {
            return false;
         } else {
            Random var1 = this.f_17rodyeil.getRandom();
            if (var1.nextInt(10) == 0) {
               this.f_57zqoceda = Direction.pick(var1);
               BlockPos var2 = new BlockPos(this.f_17rodyeil.x, this.f_17rodyeil.y + 0.5, this.f_17rodyeil.z).offset(this.f_57zqoceda);
               BlockState var3 = this.f_17rodyeil.world.getBlockState(var2);
               if (InfestedBlock.canBeInfested(var3)) {
                  this.f_67ffjuwer = true;
                  return true;
               }
            }

            this.f_67ffjuwer = false;
            return super.canStart();
         }
      }

      @Override
      public boolean shouldContinue() {
         return this.f_67ffjuwer ? false : super.shouldContinue();
      }

      @Override
      public void start() {
         if (!this.f_67ffjuwer) {
            super.start();
         } else {
            World var1 = this.f_17rodyeil.world;
            BlockPos var2 = new BlockPos(this.f_17rodyeil.x, this.f_17rodyeil.y + 0.5, this.f_17rodyeil.z).offset(this.f_57zqoceda);
            BlockState var3 = var1.getBlockState(var2);
            if (InfestedBlock.canBeInfested(var3)) {
               var1.setBlockState(var2, Blocks.MONSTER_EGG.defaultState().set(InfestedBlock.VARIANT, InfestedBlock.Variant.byHostState(var3)), 3);
               this.f_17rodyeil.doSpawnEffects();
               this.f_17rodyeil.remove();
            }
         }
      }
   }

   static class C_27wpnuezm extends Goal {
      private SliverfishEntity f_39hacfpcr;
      private int f_83ycvuctt;

      public C_27wpnuezm(SliverfishEntity c_12vzxufzf) {
         this.f_39hacfpcr = c_12vzxufzf;
      }

      public void m_62epbvugi() {
         if (this.f_83ycvuctt == 0) {
            this.f_83ycvuctt = 20;
         }
      }

      @Override
      public boolean canStart() {
         return this.f_83ycvuctt > 0;
      }

      @Override
      public void tick() {
         --this.f_83ycvuctt;
         if (this.f_83ycvuctt <= 0) {
            World var1 = this.f_39hacfpcr.world;
            Random var2 = this.f_39hacfpcr.getRandom();
            BlockPos var3 = new BlockPos(this.f_39hacfpcr);

            for(int var4 = 0; var4 <= 5 && var4 >= -5; var4 = var4 <= 0 ? 1 - var4 : 0 - var4) {
               for(int var5 = 0; var5 <= 10 && var5 >= -10; var5 = var5 <= 0 ? 1 - var5 : 0 - var5) {
                  for(int var6 = 0; var6 <= 10 && var6 >= -10; var6 = var6 <= 0 ? 1 - var6 : 0 - var6) {
                     BlockPos var7 = var3.add(var5, var4, var6);
                     BlockState var8 = var1.getBlockState(var7);
                     if (var8.getBlock() == Blocks.MONSTER_EGG) {
                        if (var1.getGameRules().getBoolean("mobGriefing")) {
                           var1.breakBlock(var7, true);
                        } else {
                           var1.setBlockState(var7, ((InfestedBlock.Variant)var8.get(InfestedBlock.VARIANT)).getHostState(), 3);
                        }

                        if (var2.nextBoolean()) {
                           return;
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
