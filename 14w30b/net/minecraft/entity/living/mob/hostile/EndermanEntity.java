package net.minecraft.entity.living.mob.hostile;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.living.attribute.AttributeModifier;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.attribute.IEntityAttributeInstance;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EndermanEntity extends HostileEntity {
   private static final UUID ATTACK_SPEED_BOOST_UUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
   private static final AttributeModifier ATTACK_SPEED_BOOST = new AttributeModifier(ATTACK_SPEED_BOOST_UUID, "Attacking speed boost", 0.15F, 0)
      .setSerialized(false);
   private static final Set f_90nnvxjtz = Sets.newIdentityHashSet();
   private boolean angry;

   public EndermanEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.6F, 2.9F);
      this.stepHeight = 1.0F;
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
      this.goalSelector.addGoal(7, new WanderAroundGoal(this, 1.0));
      this.goalSelector.addGoal(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(8, new LookAroundGoal(this));
      this.goalSelector.addGoal(10, new EndermanEntity.C_36augrzuq(this));
      this.goalSelector.addGoal(11, new EndermanEntity.PickUpBlockGoal(this));
      this.targetSelector.addGoal(1, new RevengeGoal(this, false));
      this.targetSelector.addGoal(2, new EndermanEntity.C_59xibkrql(this));
      this.targetSelector.addGoal(3, new ActiveTargetGoal(this, EndermiteEntity.class, 10, true, false, new Predicate() {
         public boolean apply(EndermiteEntity c_69mympzol) {
            return c_69mympzol.m_16conjvuj();
         }
      }));
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(40.0);
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.3F);
      this.initializeAttribute(EntityAttributes.ATTACK_DAMAGE).setBase(7.0);
      this.initializeAttribute(EntityAttributes.FOLLOW_RANGE).setBase(64.0);
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(16, new Short((short)0));
      this.dataTracker.put(17, new Byte((byte)0));
      this.dataTracker.put(18, new Byte((byte)0));
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      BlockState var2 = this.getCarriedBlock();
      nbt.putShort("carried", (short)Block.getRawId(var2.getBlock()));
      nbt.putShort("carriedData", (short)var2.getBlock().getMetadataFromState(var2));
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      BlockState var2 = Blocks.AIR.defaultState();
      if (nbt.isType("carried", 8)) {
         var2 = Block.byId(nbt.getString("carried")).getStateFromMetadata(nbt.getShort("carriedData") & '\uffff');
      } else {
         var2 = Block.byRawId(nbt.getShort("carried")).getStateFromMetadata(nbt.getShort("carriedData") & '\uffff');
      }

      this.m_76vfoiwnq(var2);
   }

   private boolean canGetAngryAt(PlayerEntity target) {
      ItemStack var2 = target.inventory.armorSlots[3];
      if (var2 != null && var2.getItem() == Item.byBlock(Blocks.PUMPKIN)) {
         return false;
      } else {
         Vec3d var3 = target.m_01qqqsfds(1.0F).normalize();
         Vec3d var4 = new Vec3d(
            this.x - target.x, this.getBoundingBox().minY + (double)(this.height / 2.0F) - (target.y + (double)target.getEyeHeight()), this.z - target.z
         );
         double var5 = var4.length();
         var4 = var4.normalize();
         double var7 = var3.dot(var4);
         return var7 > 1.0 - 0.025 / var5 ? target.canSee(this) : false;
      }
   }

   @Override
   public float getEyeHeight() {
      return 2.55F;
   }

   @Override
   public void tickAI() {
      if (this.world.isClient) {
         for(int var1 = 0; var1 < 2; ++var1) {
            this.world
               .addParticle(
                  ParticleType.PORTAL,
                  this.x + (this.random.nextDouble() - 0.5) * (double)this.width,
                  this.y + this.random.nextDouble() * (double)this.height - 0.25,
                  this.z + (this.random.nextDouble() - 0.5) * (double)this.width,
                  (this.random.nextDouble() - 0.5) * 2.0,
                  -this.random.nextDouble(),
                  (this.random.nextDouble() - 0.5) * 2.0
               );
         }
      }

      this.jumping = false;
      super.tickAI();
   }

   @Override
   protected void m_45jbqtvrb() {
      if (this.isWet()) {
         this.damage(DamageSource.DROWN, 1.0F);
      }

      if (this.isAngry() && !this.angry && this.random.nextInt(100) == 0) {
         this.setAngry(false);
      }

      if (this.world.isSunny()) {
         float var1 = this.getBrightness(1.0F);
         if (var1 > 0.5F && this.world.hasSkyAccess(new BlockPos(this)) && this.random.nextFloat() * 30.0F < (var1 - 0.4F) * 2.0F) {
            this.setAttackTarget(null);
            this.setAngry(false);
            this.angry = false;
            this.tryRandomTeleport();
         }
      }

      super.m_45jbqtvrb();
   }

   protected boolean tryRandomTeleport() {
      double var1 = this.x + (this.random.nextDouble() - 0.5) * 64.0;
      double var3 = this.y + (double)(this.random.nextInt(64) - 32);
      double var5 = this.z + (this.random.nextDouble() - 0.5) * 64.0;
      return this.tryTeleport(var1, var3, var5);
   }

   protected boolean tryTeleportTo(Entity target) {
      Vec3d var2 = new Vec3d(
         this.x - target.x, this.getBoundingBox().minY + (double)(this.height / 2.0F) - target.y + (double)target.getEyeHeight(), this.z - target.z
      );
      var2 = var2.normalize();
      double var3 = 16.0;
      double var5 = this.x + (this.random.nextDouble() - 0.5) * 8.0 - var2.x * var3;
      double var7 = this.y + (double)(this.random.nextInt(16) - 8) - var2.y * var3;
      double var9 = this.z + (this.random.nextDouble() - 0.5) * 8.0 - var2.z * var3;
      return this.tryTeleport(var5, var7, var9);
   }

   protected boolean tryTeleport(double x, double y, double z) {
      double var7 = this.x;
      double var9 = this.y;
      double var11 = this.z;
      this.x = x;
      this.y = y;
      this.z = z;
      boolean var13 = false;
      BlockPos var14 = new BlockPos(this.x, this.y, this.z);
      if (this.world.isLoaded(var14)) {
         boolean var15 = false;

         while(!var15 && var14.getY() > 0) {
            BlockPos var16 = var14.down();
            Block var17 = this.world.getBlockState(var16).getBlock();
            if (var17.getMaterial().blocksMovement()) {
               var15 = true;
            } else {
               --this.y;
               var14 = var16;
            }
         }

         if (var15) {
            super.refreshPosition(this.x, this.y, this.z);
            if (this.world.getCollisions(this, this.getBoundingBox()).isEmpty() && !this.world.containsLiquid(this.getBoundingBox())) {
               var13 = true;
            }
         }
      }

      if (!var13) {
         this.setPosition(var7, var9, var11);
         return false;
      } else {
         short var28 = 128;

         for(int var29 = 0; var29 < var28; ++var29) {
            double var30 = (double)var29 / ((double)var28 - 1.0);
            float var19 = (this.random.nextFloat() - 0.5F) * 0.2F;
            float var20 = (this.random.nextFloat() - 0.5F) * 0.2F;
            float var21 = (this.random.nextFloat() - 0.5F) * 0.2F;
            double var22 = var7 + (this.x - var7) * var30 + (this.random.nextDouble() - 0.5) * (double)this.width * 2.0;
            double var24 = var9 + (this.y - var9) * var30 + this.random.nextDouble() * (double)this.height;
            double var26 = var11 + (this.z - var11) * var30 + (this.random.nextDouble() - 0.5) * (double)this.width * 2.0;
            this.world.addParticle(ParticleType.PORTAL, var22, var24, var26, (double)var19, (double)var20, (double)var21);
         }

         this.world.playSound(var7, var9, var11, "mob.endermen.portal", 1.0F, 1.0F);
         this.playSound("mob.endermen.portal", 1.0F, 1.0F);
         return true;
      }
   }

   @Override
   protected String getAmbientSound() {
      return this.isAngry() ? "mob.endermen.scream" : "mob.endermen.idle";
   }

   @Override
   protected String getHurtSound() {
      return "mob.endermen.hit";
   }

   @Override
   protected String getDeathSound() {
      return "mob.endermen.death";
   }

   @Override
   protected Item getDefaultDropLoot() {
      return Items.ENDER_PEARL;
   }

   @Override
   protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
      Item var3 = this.getDefaultDropLoot();
      if (var3 != null) {
         int var4 = this.random.nextInt(2 + lootingMultiplier);

         for(int var5 = 0; var5 < var4; ++var5) {
            this.dropItem(var3, 1);
         }
      }
   }

   public void m_76vfoiwnq(BlockState c_17agfiprw) {
      this.dataTracker.update(16, (short)(Block.serialize(c_17agfiprw) & 65535));
   }

   public BlockState getCarriedBlock() {
      return Block.deserialize(this.dataTracker.getShort(16) & '\uffff');
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerable(source)) {
         return false;
      } else {
         if (source.getAttacker() == null || !(source.getAttacker() instanceof EndermiteEntity)) {
            if (!this.world.isClient) {
               this.setAngry(true);
            }

            if (source instanceof EntityDamageSource && source.getAttacker() instanceof PlayerEntity) {
               this.angry = true;
            }

            if (source instanceof ProjectileDamageSource) {
               this.angry = false;

               for(int var4 = 0; var4 < 64; ++var4) {
                  if (this.tryRandomTeleport()) {
                     return true;
                  }
               }

               return false;
            }
         }

         boolean var3 = super.damage(source, amount);
         if (source.bypassesArmor() && this.random.nextInt(10) != 0) {
            this.tryRandomTeleport();
         }

         return var3;
      }
   }

   public boolean isAngry() {
      return this.dataTracker.getByte(18) > 0;
   }

   public void setAngry(boolean angry) {
      this.dataTracker.update(18, (byte)(angry ? 1 : 0));
   }

   static {
      f_90nnvxjtz.add(Blocks.GRASS);
      f_90nnvxjtz.add(Blocks.DIRT);
      f_90nnvxjtz.add(Blocks.SAND);
      f_90nnvxjtz.add(Blocks.GRAVEL);
      f_90nnvxjtz.add(Blocks.YELLOW_FLOWER);
      f_90nnvxjtz.add(Blocks.RED_FLOWER);
      f_90nnvxjtz.add(Blocks.BROWN_MUSHROOM);
      f_90nnvxjtz.add(Blocks.RED_MUSHROOM);
      f_90nnvxjtz.add(Blocks.TNT);
      f_90nnvxjtz.add(Blocks.CACTUS);
      f_90nnvxjtz.add(Blocks.CLAY);
      f_90nnvxjtz.add(Blocks.PUMPKIN);
      f_90nnvxjtz.add(Blocks.MELON_BLOCK);
      f_90nnvxjtz.add(Blocks.MYCELIUM);
   }

   static class C_36augrzuq extends Goal {
      private EndermanEntity f_62gxnbmfx;

      public C_36augrzuq(EndermanEntity c_12ysvaaev) {
         this.f_62gxnbmfx = c_12ysvaaev;
      }

      @Override
      public boolean canStart() {
         if (!this.f_62gxnbmfx.world.getGameRules().getBoolean("mobGriefing")) {
            return false;
         } else if (this.f_62gxnbmfx.getCarriedBlock().getBlock().getMaterial() == Material.AIR) {
            return false;
         } else {
            return this.f_62gxnbmfx.getRandom().nextInt(2000) == 0;
         }
      }

      @Override
      public void tick() {
         Random var1 = this.f_62gxnbmfx.getRandom();
         World var2 = this.f_62gxnbmfx.world;
         int var3 = MathHelper.floor(this.f_62gxnbmfx.x - 1.0 + var1.nextDouble() * 2.0);
         int var4 = MathHelper.floor(this.f_62gxnbmfx.y + var1.nextDouble() * 2.0);
         int var5 = MathHelper.floor(this.f_62gxnbmfx.z - 1.0 + var1.nextDouble() * 2.0);
         BlockPos var6 = new BlockPos(var3, var4, var5);
         Block var7 = var2.getBlockState(var6).getBlock();
         Block var8 = var2.getBlockState(var6.down()).getBlock();
         if (var7.getMaterial() == Material.AIR && var8.getMaterial() != Material.AIR && var8.isFullCube()) {
            var2.setBlockState(var6, this.f_62gxnbmfx.getCarriedBlock(), 3);
            this.f_62gxnbmfx.m_76vfoiwnq(Blocks.AIR.defaultState());
         }
      }
   }

   static class C_59xibkrql extends ActiveTargetGoal {
      private PlayerEntity f_00lakmnht;
      private int f_37eozfdpw;
      private int f_85qpcquln;
      private EndermanEntity f_51ngoguxb;

      public C_59xibkrql(EndermanEntity c_12ysvaaev) {
         super(c_12ysvaaev, PlayerEntity.class, true);
         this.f_51ngoguxb = c_12ysvaaev;
      }

      @Override
      public boolean canStart() {
         double var1 = this.getFollowRange();
         List var3 = this.entity.world.getEntities(PlayerEntity.class, this.entity.getBoundingBox().expand(var1, 4.0, var1), this.canTargetEntityFilter);
         Collections.sort(var3, this.entityDistanceComparator);
         if (var3.isEmpty()) {
            return false;
         } else {
            this.f_00lakmnht = (PlayerEntity)var3.get(0);
            return true;
         }
      }

      @Override
      public void start() {
         this.f_37eozfdpw = 5;
         this.f_85qpcquln = 0;
      }

      @Override
      public void stop() {
         this.f_00lakmnht = null;
         this.f_51ngoguxb.setAngry(false);
         IEntityAttributeInstance var1 = this.f_51ngoguxb.initializeAttribute(EntityAttributes.MOVEMENT_SPEED);
         var1.removeModifier(EndermanEntity.ATTACK_SPEED_BOOST);
         super.stop();
      }

      @Override
      public boolean shouldContinue() {
         if (this.f_00lakmnht != null) {
            if (!this.f_51ngoguxb.canGetAngryAt(this.f_00lakmnht)) {
               return false;
            } else {
               this.f_51ngoguxb.angry = true;
               this.f_51ngoguxb.lookAtEntity(this.f_00lakmnht, 10.0F, 10.0F);
               return true;
            }
         } else {
            return super.shouldContinue();
         }
      }

      @Override
      public void tick() {
         if (this.f_00lakmnht != null) {
            if (--this.f_37eozfdpw <= 0) {
               this.targetEntity = this.f_00lakmnht;
               this.f_00lakmnht = null;
               super.start();
               this.f_51ngoguxb.playSound("mob.endermen.stare", 1.0F, 1.0F);
               this.f_51ngoguxb.setAngry(true);
               IEntityAttributeInstance var1 = this.f_51ngoguxb.initializeAttribute(EntityAttributes.MOVEMENT_SPEED);
               var1.addModifier(EndermanEntity.ATTACK_SPEED_BOOST);
            }
         } else {
            if (this.targetEntity != null) {
               if (this.targetEntity instanceof PlayerEntity && this.f_51ngoguxb.canGetAngryAt((PlayerEntity)this.targetEntity)) {
                  if (this.targetEntity.getSquaredDistanceTo(this.f_51ngoguxb) < 16.0) {
                     this.f_51ngoguxb.tryRandomTeleport();
                  }

                  this.f_85qpcquln = 0;
               } else if (this.targetEntity.getSquaredDistanceTo(this.f_51ngoguxb) > 256.0
                  && this.f_85qpcquln++ >= 30
                  && this.f_51ngoguxb.tryTeleportTo(this.targetEntity)) {
                  this.f_85qpcquln = 0;
               }
            }

            super.tick();
         }
      }
   }

   static class PickUpBlockGoal extends Goal {
      private EndermanEntity enderman;

      public PickUpBlockGoal(EndermanEntity enderman) {
         this.enderman = enderman;
      }

      @Override
      public boolean canStart() {
         if (!this.enderman.world.getGameRules().getBoolean("mobGriefing")) {
            return false;
         } else if (this.enderman.getCarriedBlock().getBlock().getMaterial() != Material.AIR) {
            return false;
         } else {
            return this.enderman.getRandom().nextInt(20) == 0;
         }
      }

      @Override
      public void tick() {
         Random var1 = this.enderman.getRandom();
         World var2 = this.enderman.world;
         int var3 = MathHelper.floor(this.enderman.x - 2.0 + var1.nextDouble() * 4.0);
         int var4 = MathHelper.floor(this.enderman.y + var1.nextDouble() * 3.0);
         int var5 = MathHelper.floor(this.enderman.z - 2.0 + var1.nextDouble() * 4.0);
         BlockPos var6 = new BlockPos(var3, var4, var5);
         BlockState var7 = var2.getBlockState(var6);
         Block var8 = var7.getBlock();
         if (EndermanEntity.f_90nnvxjtz.contains(var8)) {
            this.enderman.m_76vfoiwnq(var7);
            var2.setBlockState(var6, Blocks.AIR.defaultState());
         }
      }
   }
}
