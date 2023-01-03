package net.minecraft.entity.living.mob;

import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;

public class MagmaCubeEntity extends SlimeEntity {
   public MagmaCubeEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.immuneToFire = true;
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.2F);
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
   public int getArmorProtection() {
      return this.getSize() * 3;
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   @Override
   public int getLightLevel(float tickDelta) {
      return 15728880;
   }

   @Override
   public float getBrightness(float tickDelta) {
      return 1.0F;
   }

   @Override
   protected ParticleType getParticleName() {
      return ParticleType.FLAME;
   }

   @Override
   protected SlimeEntity getInstance() {
      return new MagmaCubeEntity(this.world);
   }

   @Override
   protected Item getDefaultDropLoot() {
      return Items.MAGMA_CREAM;
   }

   @Override
   protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
      Item var3 = this.getDefaultDropLoot();
      if (var3 != null && this.getSize() > 1) {
         int var4 = this.random.nextInt(4) - 2;
         if (lootingMultiplier > 0) {
            var4 += this.random.nextInt(lootingMultiplier + 1);
         }

         for(int var5 = 0; var5 < var4; ++var5) {
            this.dropItem(var3, 1);
         }
      }
   }

   @Override
   public boolean isOnFire() {
      return false;
   }

   @Override
   protected int getTicksUntilNextJump() {
      return super.getTicksUntilNextJump() * 4;
   }

   @Override
   protected void updateStretch() {
      this.targetStretch *= 0.9F;
   }

   @Override
   protected void jump() {
      this.velocityY = (double)(0.42F + (float)this.getSize() * 0.1F);
      this.velocityDirty = true;
   }

   @Override
   protected void m_24htnczxz() {
      this.velocityY = (double)(0.22F + (float)this.getSize() * 0.05F);
      this.velocityDirty = true;
   }

   @Override
   public void applyFallDamage(float distance, float g) {
   }

   @Override
   protected boolean isBig() {
      return true;
   }

   @Override
   protected int getDamageAmount() {
      return super.getDamageAmount() + 2;
   }

   @Override
   protected String getSoundName() {
      return this.getSize() > 1 ? "mob.magmacube.big" : "mob.magmacube.small";
   }

   @Override
   protected boolean makesLandSound() {
      return true;
   }
}
