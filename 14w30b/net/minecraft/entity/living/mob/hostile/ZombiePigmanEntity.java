package net.minecraft.entity.living.mob.hostile;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.AttributeModifier;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.attribute.IEntityAttributeInstance;
import net.minecraft.entity.living.mob.PathAwareEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class ZombiePigmanEntity extends ZombieEntity {
   private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
   private static final AttributeModifier ATTACKING_SPEED_BOOST = new AttributeModifier(ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", 0.05, 0)
      .setSerialized(false);
   private int angerValue;
   private int angerSoundDelay;
   private UUID attackerUuid;

   public ZombiePigmanEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.immuneToFire = true;
   }

   @Override
   public void setAttacker(LivingEntity attacker) {
      super.setAttacker(attacker);
      if (attacker != null) {
         this.attackerUuid = attacker.getUuid();
      }
   }

   @Override
   protected void m_47ewtstup() {
      this.targetSelector.addGoal(1, new ZombiePigmanEntity.C_49xommuji(this));
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(REINFORCEMENTS_ATTRIBUTE).setBase(0.0);
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.23F);
      this.initializeAttribute(EntityAttributes.ATTACK_DAMAGE).setBase(5.0);
   }

   @Override
   public void tick() {
      super.tick();
   }

   @Override
   protected void m_45jbqtvrb() {
      IEntityAttributeInstance var1 = this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED);
      if (this.m_90ubiwroe() && !this.isBaby()) {
         if (!var1.hasModifier(ATTACKING_SPEED_BOOST)) {
            var1.addModifier(ATTACKING_SPEED_BOOST);
         }

         --this.angerValue;
      } else if (var1.hasModifier(ATTACKING_SPEED_BOOST)) {
         var1.removeModifier(ATTACKING_SPEED_BOOST);
      }

      if (this.angerSoundDelay > 0 && --this.angerSoundDelay == 0) {
         this.playSound("mob.zombiepig.zpigangry", this.getSoundVolume() * 2.0F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 1.8F);
      }

      if (this.angerValue > 0 && this.attackerUuid != null && this.getAttacker() == null) {
         PlayerEntity var2 = this.world.getPlayer(this.attackerUuid);
         this.setAttacker(var2);
         this.attackingPlayer = var2;
         this.playerHitTimer = this.getLastAttackedTime();
      }

      super.m_45jbqtvrb();
   }

   @Override
   public boolean canSpawn() {
      return this.world.getDifficulty() != Difficulty.PEACEFUL;
   }

   @Override
   public boolean m_52qkzdxky() {
      return this.world.canBuildIn(this.getBoundingBox(), this)
         && this.world.getCollisions(this, this.getBoundingBox()).isEmpty()
         && !this.world.containsLiquid(this.getBoundingBox());
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putShort("Anger", (short)this.angerValue);
      if (this.attackerUuid != null) {
         nbt.putString("HurtBy", this.attackerUuid.toString());
      } else {
         nbt.putString("HurtBy", "");
      }
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.angerValue = nbt.getShort("Anger");
      String var2 = nbt.getString("HurtBy");
      if (var2.length() > 0) {
         this.attackerUuid = UUID.fromString(var2);
         PlayerEntity var3 = this.world.getPlayer(this.attackerUuid);
         this.setAttacker(var3);
         if (var3 != null) {
            this.attackingPlayer = var3;
            this.playerHitTimer = this.getLastAttackedTime();
         }
      }
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerable(source)) {
         return false;
      } else {
         Entity var3 = source.getAttacker();
         if (var3 instanceof PlayerEntity) {
            this.getAngryTo(var3);
         }

         return super.damage(source, amount);
      }
   }

   private void getAngryTo(Entity target) {
      this.angerValue = 400 + this.random.nextInt(400);
      this.angerSoundDelay = this.random.nextInt(40);
      if (target instanceof LivingEntity) {
         this.setAttacker((LivingEntity)target);
      }
   }

   public boolean m_90ubiwroe() {
      return this.angerValue > 0;
   }

   @Override
   protected String getAmbientSound() {
      return "mob.zombiepig.zpig";
   }

   @Override
   protected String getHurtSound() {
      return "mob.zombiepig.zpighurt";
   }

   @Override
   protected String getDeathSound() {
      return "mob.zombiepig.zpigdeath";
   }

   @Override
   protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
      int var3 = this.random.nextInt(2 + lootingMultiplier);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.dropItem(Items.ROTTEN_FLESH, 1);
      }

      var3 = this.random.nextInt(2 + lootingMultiplier);

      for(int var6 = 0; var6 < var3; ++var6) {
         this.dropItem(Items.GOLD_NUGGET, 1);
      }
   }

   @Override
   public boolean canInteract(PlayerEntity player) {
      return false;
   }

   @Override
   protected void dropRareItem() {
      this.dropItem(Items.GOLD_INGOT, 1);
   }

   @Override
   protected void initSpawningEquipment(LocalDifficulty localDifficulty) {
      this.setEquipmentStack(0, new ItemStack(Items.GOLDEN_SWORD));
   }

   @Override
   public EntityData initialize(LocalDifficulty localDifficulty, EntityData entityData) {
      super.initialize(localDifficulty, entityData);
      this.setVillager(false);
      return entityData;
   }

   static class C_49xommuji extends RevengeGoal {
      public C_49xommuji(ZombiePigmanEntity c_45zomsjnb) {
         super(c_45zomsjnb, true);
      }

      @Override
      protected void m_01exkogok(PathAwareEntity c_60guwxsid, LivingEntity c_97zulxhng) {
         super.m_01exkogok(c_60guwxsid, c_97zulxhng);
         if (c_60guwxsid instanceof ZombiePigmanEntity) {
            ((ZombiePigmanEntity)c_60guwxsid).getAngryTo(c_97zulxhng);
         }
      }
   }
}
