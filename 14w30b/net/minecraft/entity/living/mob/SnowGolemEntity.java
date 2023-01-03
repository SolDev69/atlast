package net.minecraft.entity.living.mob;

import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SnowGolemEntity extends GolemEntity implements RangedAttackMob {
   public SnowGolemEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.7F, 1.9F);
      ((MobEntityNavigation)this.getNavigation()).m_61diarbat(true);
      this.goalSelector.addGoal(1, new ProjectileAttackGoal(this, 1.25, 20, 10.0F));
      this.goalSelector.addGoal(2, new WanderAroundGoal(this, 1.0));
      this.goalSelector.addGoal(3, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(4, new LookAroundGoal(this));
      this.targetSelector.addGoal(1, new ActiveTargetGoal(this, MobEntity.class, 10, true, false, Monster.MONSTER_FILTER));
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(4.0);
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.2F);
   }

   @Override
   public void tickAI() {
      super.tickAI();
      if (!this.world.isClient) {
         int var1 = MathHelper.floor(this.x);
         int var2 = MathHelper.floor(this.y);
         int var3 = MathHelper.floor(this.z);
         if (this.isWet()) {
            this.damage(DamageSource.DROWN, 1.0F);
         }

         if (this.world.getBiome(new BlockPos(var1, 0, var3)).getTemperature(new BlockPos(var1, var2, var3)) > 1.0F) {
            this.damage(DamageSource.ON_FIRE, 1.0F);
         }

         for(int var4 = 0; var4 < 4; ++var4) {
            var1 = MathHelper.floor(this.x + (double)((float)(var4 % 2 * 2 - 1) * 0.25F));
            var2 = MathHelper.floor(this.y);
            var3 = MathHelper.floor(this.z + (double)((float)(var4 / 2 % 2 * 2 - 1) * 0.25F));
            if (this.world.getBlockState(new BlockPos(var1, var2, var3)).getBlock().getMaterial() == Material.AIR
               && this.world.getBiome(new BlockPos(var1, 0, var3)).getTemperature(new BlockPos(var1, var2, var3)) < 0.8F
               && Blocks.SNOW_LAYER.canSurvive(this.world, new BlockPos(var1, var2, var3))) {
               this.world.setBlockState(new BlockPos(var1, var2, var3), Blocks.SNOW_LAYER.defaultState());
            }
         }
      }
   }

   @Override
   protected Item getDefaultDropLoot() {
      return Items.SNOWBALL;
   }

   @Override
   protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
      int var3 = this.random.nextInt(16);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.dropItem(Items.SNOWBALL, 1);
      }
   }

   @Override
   public void doRangedAttack(LivingEntity target, float range) {
      SnowballEntity var3 = new SnowballEntity(this.world, this);
      double var4 = target.y + (double)target.getEyeHeight() - 1.1F;
      double var6 = target.x - this.x;
      double var8 = var4 - var3.y;
      double var10 = target.z - this.z;
      float var12 = MathHelper.sqrt(var6 * var6 + var10 * var10) * 0.2F;
      var3.setVelocity(var6, var8 + (double)var12, var10, 1.6F, 12.0F);
      this.playSound("random.bow", 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
      this.world.addEntity(var3);
   }

   @Override
   public float getEyeHeight() {
      return 1.7F;
   }
}
