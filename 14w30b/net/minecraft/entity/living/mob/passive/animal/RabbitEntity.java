package net.minecraft.entity.living.mob.passive.animal;

import com.google.common.base.Predicate;
import net.minecraft.C_61rczfvzv;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotsBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.MovementControl;
import net.minecraft.entity.ai.goal.AnimalBreedGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.mob.MobEntity;
import net.minecraft.entity.living.mob.passive.PassiveEntity;
import net.minecraft.entity.living.mob.passive.animal.tamable.TameableEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.locale.I18n;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;

public class RabbitEntity extends TameableEntity {
   private RabbitEntity.C_92mywkrmb f_12ewgjkpi;
   private int f_39jevypto = 0;
   private int f_41udvpjds = 0;
   private boolean f_04cpainwz = false;
   private TemptGoal f_92hhueiho;
   private final float f_44zodzboo = 4.0F;
   private boolean f_30zjvenpj = false;
   private boolean f_50tvzmacz = false;
   private int f_95zjmvlzi = 0;
   private RabbitEntity.C_97iklykcw f_07ophcuul = RabbitEntity.C_97iklykcw.NONE;
   private RabbitEntity.C_97iklykcw f_45jnhgmff = RabbitEntity.C_97iklykcw.HOP;
   private RabbitEntity.C_97iklykcw f_61uzhkjxk = RabbitEntity.C_97iklykcw.HOP;
   private int f_75srraldz = 0;
   private PlayerEntity f_92ocvjegn = null;

   public RabbitEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.6F, 0.7F);
      this.jumpControl = new RabbitEntity.C_87bettpps(this);
      this.movementControl = new RabbitEntity.C_04nwsklpn(this);
      ((MobEntityNavigation)this.getNavigation()).m_61diarbat(true);
      this.entityNavigation.m_74ipbvgvj(2.5F);
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(2, this.f_92hhueiho = new TemptGoal(this, 0.6, Items.CARROT, true));
      this.goalSelector.addGoal(3, new AnimalBreedGoal(this, 0.8));
      this.goalSelector.addGoal(5, new RabbitEntity.C_40mdiyorm(this));
      this.goalSelector.addGoal(5, new WanderAroundGoal(this, 0.0));
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0F, 5.0F));
      this.goalSelector.addGoal(11, new RabbitEntity.C_49oeyuhhf(this, PlayerEntity.class, 10.0F));
      this.m_25shvtcwz(0.0);
      this.m_87jkmgtwv(c_54ruxjwzt.random.nextInt(6));
      if (c_54ruxjwzt.random.nextInt(2500) == 0 && c_54ruxjwzt.getDifficulty() != Difficulty.PEACEFUL) {
         this.m_87jkmgtwv(99);
         this.setCustomName("entity.KillerBunny.name");
      }
   }

   @Override
   protected float m_12vxcslau() {
      return this.f_07ophcuul.m_40pspzlxy();
   }

   public void m_24vizqzyp(RabbitEntity.C_97iklykcw c_97iklykcw) {
      this.f_45jnhgmff = c_97iklykcw;
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public float m_17rnscnpc() {
      return this.dataTracker.getFloat(19);
   }

   private void m_84wjrjjoh(float f) {
      this.dataTracker.update(19, f);
   }

   public void m_25shvtcwz(double d) {
      this.getNavigation().setSpeed(d);
      this.movementControl.update(this.movementControl.m_53wsenqsm(), this.movementControl.m_90fcnurdd(), this.movementControl.m_37abemtzt(), d);
   }

   public void m_70szkginw(boolean bl, RabbitEntity.C_97iklykcw c_97iklykcw) {
      super.setJumping(bl);
      if (!bl) {
         this.m_25shvtcwz(0.0);
         if (this.f_45jnhgmff == RabbitEntity.C_97iklykcw.ATTACK) {
            this.f_45jnhgmff = this.f_61uzhkjxk;
         }
      } else {
         this.m_25shvtcwz(1.5 * (double)c_97iklykcw.m_28ilbnptc());
         String var3 = this.m_94zbqfywh();
         if (var3 != null) {
            this.playSound(this.m_94zbqfywh(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
         }
      }

      this.f_07ophcuul = c_97iklykcw;
      this.f_04cpainwz = bl;
   }

   public void m_97vsxcwad(RabbitEntity.C_97iklykcw c_97iklykcw) {
      this.m_70szkginw(true, c_97iklykcw);
      this.f_41udvpjds = c_97iklykcw.m_01lzpippp();
      this.f_39jevypto = 0;
   }

   public boolean m_68lmvjahs() {
      return this.f_04cpainwz;
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(18, (byte)0);
      this.dataTracker.put(19, 0.0F);
   }

   @Override
   public void m_45jbqtvrb() {
      if (this.getMovementControl().isUpdated()) {
         double var1 = this.getMovementControl().getSpeed();
         if (var1 == 0.6) {
            this.setSneaking(true);
            this.setSprinting(false);
         } else if (var1 == 1.33) {
            this.setSneaking(false);
            this.setSprinting(true);
         } else {
            this.setSneaking(false);
            this.setSprinting(false);
         }
      } else {
         this.setSneaking(false);
         this.setSprinting(false);
      }

      if (this.f_95zjmvlzi > 0) {
         --this.f_95zjmvlzi;
      }

      if (this.f_75srraldz > 0) {
         this.f_75srraldz -= this.random.nextInt(3);
         if (this.f_75srraldz < 0) {
            this.f_75srraldz = 0;
         }
      }

      if (this.onGround) {
         if (!this.f_50tvzmacz) {
            this.m_70szkginw(false, RabbitEntity.C_97iklykcw.NONE);
            this.m_42yoceeeu();
         }

         if (this.m_14jsvwagf() == 99 && this.f_95zjmvlzi == 0) {
            PlayerEntity var4 = this.world.getClosestPlayer(this, 4.0);
            if (var4 != null && this.getSquaredDistanceTo(var4) < 16.0) {
               this.f_92ocvjegn = var4;
               this.m_53vofckfb(var4.x, var4.z);
               this.m_97vsxcwad(RabbitEntity.C_97iklykcw.ATTACK);
               this.f_50tvzmacz = true;
               return;
            }

            this.f_92ocvjegn = null;
         }

         RabbitEntity.C_87bettpps var5 = (RabbitEntity.C_87bettpps)this.jumpControl;
         if (!var5.m_51drpfurx()) {
            if (this.movementControl.isUpdated() && this.f_95zjmvlzi == 0) {
               Path var2 = this.entityNavigation.getCurrentPath();
               Vec3d var3 = new Vec3d(this.movementControl.m_53wsenqsm(), this.movementControl.m_90fcnurdd(), this.movementControl.m_37abemtzt());
               if (var2 != null && var2.getIndexInPath() < var2.getPathLength()) {
                  var3 = var2.getNextPos(this);
               }

               this.m_53vofckfb(var3.x, var3.z);
               this.m_97vsxcwad(this.f_45jnhgmff);
            }
         } else if (!var5.m_65tibwodm()) {
            this.m_06eutvocq();
         }
      } else if (this.m_68lmvjahs()) {
         if (this.f_92ocvjegn != null) {
            this.m_53vofckfb(this.f_92ocvjegn.x, this.f_92ocvjegn.z);
         }

         if (!this.f_30zjvenpj) {
            this.f_30zjvenpj = this.entityNavigation.isIdle();
         }
      }

      this.f_50tvzmacz = this.onGround;
   }

   private void m_53vofckfb(double d, double e) {
      this.yaw = (float)(Math.atan2(e - this.z, d - this.x) * 180.0 / (float) Math.PI) - 90.0F;
   }

   private void m_06eutvocq() {
      ((RabbitEntity.C_87bettpps)this.jumpControl).m_18ktgqery(true);
   }

   private void m_68lcstftf() {
      ((RabbitEntity.C_87bettpps)this.jumpControl).m_18ktgqery(false);
   }

   private void m_79jxagczh() {
      this.f_95zjmvlzi = this.m_58rrzwukh();
   }

   private void m_42yoceeeu() {
      this.m_79jxagczh();
      this.m_68lcstftf();
   }

   @Override
   public void tickAI() {
      super.tickAI();
      if (this.f_39jevypto != this.f_41udvpjds) {
         ++this.f_39jevypto;
         this.m_84wjrjjoh((float)this.f_39jevypto / (float)this.f_41udvpjds);
      } else if (this.f_41udvpjds != 0) {
         this.f_39jevypto = 0;
         this.f_41udvpjds = 0;
         this.m_84wjrjjoh(0.0F);
      }
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(10.0);
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.3F);
   }

   @Override
   public void applyFallDamage(float distance, float g) {
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putInt("RabbitType", this.m_14jsvwagf());
      nbt.putInt("MoreCarrotTicks", this.f_75srraldz);
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.m_87jkmgtwv(nbt.getInt("RabbitType"));
      this.f_75srraldz = nbt.getInt("MoreCarrotTicks");
   }

   @Override
   protected String getAmbientSound() {
      return null;
   }

   @Override
   protected String getHurtSound() {
      return null;
   }

   @Override
   protected String getDeathSound() {
      return null;
   }

   @Override
   protected float getSoundVolume() {
      return 0.4F;
   }

   @Override
   public boolean attack(Entity entity) {
      return entity.damage(DamageSource.mob(this), 3.0F);
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      return this.isInvulnerable(source) ? false : super.damage(source, amount);
   }

   @Override
   protected void dropRareItem() {
      this.dropItem(new ItemStack(Items.RABBIT_FOOT, 1), 0.0F);
   }

   @Override
   protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
      int var3 = this.random.nextInt(2) + this.random.nextInt(1 + lootingMultiplier);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.dropItem(Items.RABBIT_HIDE, 1);
      }

      var3 = this.random.nextInt(2);

      for(int var6 = 0; var6 < var3; ++var6) {
         if (this.isOnFire()) {
            this.dropItem(Items.COOKED_RABBIT, 1);
         } else {
            this.dropItem(Items.RABBIT, 1);
         }
      }
   }

   private boolean m_76pauqbtx(Item c_30vndvelc) {
      return c_30vndvelc == Items.CARROT || c_30vndvelc == Items.GOLDEN_CARROT || c_30vndvelc == Item.byBlock(Blocks.YELLOW_FLOWER);
   }

   @Override
   public void onPlayerCollision(PlayerEntity player) {
      if (this.m_68lmvjahs() && this.f_07ophcuul == RabbitEntity.C_97iklykcw.ATTACK && player.damage(DamageSource.mob(this), 8.0F)) {
         this.playSound("mob.attack", 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
      }
   }

   @Override
   public boolean canInteract(PlayerEntity player) {
      ItemStack var2 = player.inventory.getMainHandStack();
      if (!this.isTamed() && this.f_92hhueiho.isGoalActive() && var2 != null && this.m_76pauqbtx(var2.getItem()) && player.getSquaredDistanceTo(this) < 9.0) {
         if (!player.abilities.creativeMode) {
            --var2.size;
         }

         if (var2.size <= 0) {
            player.inventory.setStack(player.inventory.selectedSlot, null);
         }

         if (!this.world.isClient) {
            if (this.random.nextInt(3) == 0) {
               this.setTamed(true);
               this.setOwner(player.getUuid().toString());
               this.showEmoteParticle(true);
               this.world.doEntityEvent(this, (byte)7);
            } else {
               this.showEmoteParticle(false);
               this.world.doEntityEvent(this, (byte)6);
            }
         }

         return true;
      } else {
         return super.canInteract(player);
      }
   }

   public RabbitEntity makeChild(PassiveEntity c_19nmglwmx) {
      RabbitEntity var2 = new RabbitEntity(this.world);
      if (this.isTamed()) {
         var2.setOwner(this.getOwnerName());
         var2.setTamed(true);
         var2.m_87jkmgtwv(this.m_14jsvwagf());
      }

      return var2;
   }

   @Override
   public boolean isBreedingItem(ItemStack stack) {
      return stack != null && this.m_76pauqbtx(stack.getItem());
   }

   @Override
   public boolean canBreedWith(AnimalEntity other) {
      if (other == this) {
         return false;
      } else if (!this.isTamed()) {
         return false;
      } else if (!(other instanceof RabbitEntity)) {
         return false;
      } else {
         RabbitEntity var2 = (RabbitEntity)other;
         if (!var2.isTamed()) {
            return false;
         } else {
            return this.isInLove() && var2.isInLove();
         }
      }
   }

   public int m_14jsvwagf() {
      return this.dataTracker.getByte(18);
   }

   public void m_87jkmgtwv(int i) {
      this.dataTracker.update(18, (byte)i);
      this.m_65opiswxw();
   }

   @Override
   public String getName() {
      if (this.hasCustomName()) {
         return this.getCustomName();
      } else {
         return this.isTamed() ? I18n.translate("entity.Rabbit.name") : super.getName();
      }
   }

   @Override
   public void setTamed(boolean tamed) {
      super.setTamed(tamed);
   }

   @Override
   protected void m_65opiswxw() {
      if (this.m_14jsvwagf() == 99) {
         if (this.f_12ewgjkpi != null) {
            this.goalSelector.removeGoal(this.f_12ewgjkpi);
         }

         this.f_12ewgjkpi = null;
      } else {
         if (this.f_12ewgjkpi == null) {
            this.f_12ewgjkpi = new RabbitEntity.C_92mywkrmb(this, new Predicate() {
               public boolean apply(Entity c_47ldwddrb) {
                  return c_47ldwddrb instanceof PlayerEntity;
               }
            }, 16.0F, 0.8, 1.33);
         }

         this.goalSelector.removeGoal(this.f_12ewgjkpi);
         if (!this.isTamed()) {
            this.goalSelector.addGoal(4, this.f_12ewgjkpi);
         }
      }
   }

   @Override
   public EntityData initialize(LocalDifficulty localDifficulty, EntityData entityData) {
      entityData = super.initialize(localDifficulty, entityData);
      if (this.world.random.nextInt(7) == 0) {
         for(int var3 = 0; var3 < 2; ++var3) {
            RabbitEntity var4 = new RabbitEntity(this.world);
            var4.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
            this.world.addEntity(var4);
         }
      }

      return entityData;
   }

   private boolean m_17nceushv() {
      return this.f_75srraldz == 0;
   }

   protected int m_58rrzwukh() {
      return this.f_45jnhgmff.m_76etdalbu();
   }

   protected String m_94zbqfywh() {
      return "mob.rabbit.hop";
   }

   protected void m_39ihgveeg() {
      this.world
         .addParticle(
            ParticleType.BLOCK_DUST,
            this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
            this.y + 0.5 + (double)(this.random.nextFloat() * this.height),
            this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
            0.0,
            0.0,
            0.0,
            Block.serialize(Blocks.CARROTS.getStateFromMetadata(7))
         );
      this.f_75srraldz = 100;
   }

   static class C_04nwsklpn extends MovementControl {
      private RabbitEntity f_54pryeguy;

      public C_04nwsklpn(RabbitEntity c_35uygbulw) {
         super(c_35uygbulw);
         this.f_54pryeguy = c_35uygbulw;
      }

      @Override
      public void tickUpdateMovement() {
         if (this.f_54pryeguy.onGround && !this.f_54pryeguy.m_68lmvjahs()) {
            this.f_54pryeguy.m_25shvtcwz(0.0);
         } else {
            this.f_54pryeguy.m_25shvtcwz(1.5);
         }

         super.tickUpdateMovement();
      }
   }

   static class C_40mdiyorm extends C_61rczfvzv {
      private final RabbitEntity f_44pximanu;
      private boolean f_39hcelvym;
      private boolean f_74zclrevt = false;

      public C_40mdiyorm(RabbitEntity c_35uygbulw) {
         super(c_35uygbulw, 0.7F, 16);
         this.f_44pximanu = c_35uygbulw;
      }

      @Override
      public boolean canStart() {
         if (this.f_56jbshthq <= 0) {
            if (!this.f_44pximanu.world.getGameRules().getBoolean("mobGriefing")) {
               return false;
            }

            this.f_74zclrevt = false;
            this.f_39hcelvym = this.f_44pximanu.m_17nceushv();
         }

         return super.canStart();
      }

      @Override
      public boolean shouldContinue() {
         return this.f_74zclrevt && !this.f_44pximanu.isTamed() && super.shouldContinue();
      }

      @Override
      public void start() {
         super.start();
      }

      @Override
      public void stop() {
         super.stop();
      }

      @Override
      public void tick() {
         super.tick();
         this.f_44pximanu
            .getLookControl()
            .lookAt(
               (double)this.f_46iwgcxts.getX() + 0.5,
               (double)(this.f_46iwgcxts.getY() + 1),
               (double)this.f_46iwgcxts.getZ() + 0.5,
               10.0F,
               (float)this.f_44pximanu.getLookPitchSpeed()
            );
         if (this.m_73tnmggyc()) {
            World var1 = this.f_44pximanu.world;
            BlockPos var2 = this.f_46iwgcxts.up();
            BlockState var3 = var1.getBlockState(var2);
            Block var4 = var3.getBlock();
            if (this.f_74zclrevt && var4 instanceof CarrotsBlock && var3.get(CarrotsBlock.AGE) == 7) {
               var1.setBlockState(var2, Blocks.AIR.defaultState(), 2);
               var1.breakBlock(var2, true);
               this.f_44pximanu.m_39ihgveeg();
            }

            this.f_74zclrevt = false;
            this.f_56jbshthq = 10;
         }
      }

      @Override
      protected boolean canSitOnBlock(World c_54ruxjwzt, BlockPos c_76varpwca) {
         Block var3 = c_54ruxjwzt.getBlockState(c_76varpwca).getBlock();
         if (var3 == Blocks.FARMLAND) {
            c_76varpwca = c_76varpwca.up();
            BlockState var4 = c_54ruxjwzt.getBlockState(c_76varpwca);
            var3 = var4.getBlock();
            if (var3 instanceof CarrotsBlock && var4.get(CarrotsBlock.AGE) == 7 && this.f_39hcelvym && !this.f_74zclrevt) {
               this.f_74zclrevt = true;
               return true;
            }
         }

         return false;
      }
   }

   static class C_49oeyuhhf extends LookAtEntityGoal {
      public C_49oeyuhhf(MobEntity c_81psrrogw, Class class_, float f) {
         super(c_81psrrogw, class_, f);
      }

      @Override
      public boolean canStart() {
         this.targetEntity = this.entity.world.getClosestPlayer(this.entity, (double)this.range);
         return this.targetEntity != null;
      }

      @Override
      public boolean shouldContinue() {
         if (!this.targetEntity.isAlive()) {
            return false;
         } else {
            return !(this.entity.getSquaredDistanceTo(this.targetEntity) > (double)(this.range * this.range));
         }
      }

      @Override
      public void tick() {
         super.tick();
         if (this.targetEntity != null) {
            ((RabbitEntity)this.entity).m_53vofckfb(this.targetEntity.x, this.targetEntity.z);
         }
      }
   }

   public class C_87bettpps extends JumpControl {
      private RabbitEntity f_37akojkve;
      private boolean f_54frxktjr = false;

      public C_87bettpps(RabbitEntity c_35uygbulw2) {
         super(c_35uygbulw2);
         this.f_37akojkve = c_35uygbulw2;
      }

      public boolean m_51drpfurx() {
         return this.active;
      }

      public boolean m_65tibwodm() {
         return this.f_54frxktjr;
      }

      public void m_18ktgqery(boolean bl) {
         this.f_54frxktjr = bl;
      }

      @Override
      public void tick() {
         if (this.active) {
            this.f_37akojkve.m_97vsxcwad(RabbitEntity.C_97iklykcw.STEP);
            this.active = false;
         }
      }
   }

   static class C_92mywkrmb extends FleeEntityGoal {
      private RabbitEntity f_77taevqpq;

      public C_92mywkrmb(RabbitEntity c_35uygbulw, Predicate predicate, float f, double d, double e) {
         super(c_35uygbulw, predicate, f, d, e);
         this.f_77taevqpq = c_35uygbulw;
      }

      @Override
      public void tick() {
         super.tick();
         if (this.mob.getSquaredDistanceTo(this.targetEntity) < 49.0) {
            this.f_77taevqpq.m_24vizqzyp(RabbitEntity.C_97iklykcw.SPRINT);
         } else {
            this.f_77taevqpq.m_24vizqzyp(RabbitEntity.C_97iklykcw.HOP);
         }
      }
   }

   static enum C_97iklykcw {
      NONE(0.0F, 0.0F, 30, 1),
      HOP(0.8F, 0.2F, 20, 10),
      STEP(1.0F, 0.45F, 14, 14),
      SPRINT(1.75F, 0.2F, 7, 8),
      ATTACK(1.0F, 0.45F, 7, 8);

      private final float f_31sktylif;
      private final float f_98hzcygpl;
      private final int f_22epeabwp;
      private final int f_22nncvyjs;

      private C_97iklykcw(float f, float g, int j, int k) {
         this.f_31sktylif = f;
         this.f_98hzcygpl = g;
         this.f_22epeabwp = j;
         this.f_22nncvyjs = k;
      }

      public float m_28ilbnptc() {
         return this.f_31sktylif;
      }

      public float m_40pspzlxy() {
         return this.f_98hzcygpl;
      }

      public int m_76etdalbu() {
         return this.f_22epeabwp;
      }

      public int m_01lzpippp() {
         return this.f_22nncvyjs;
      }
   }
}
