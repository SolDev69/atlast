package net.minecraft.entity.living.mob.water;

import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;

public class SquidEntity extends WaterMobEntity {
   public float squidPitch;
   public float lastSquidPitch;
   public float squidYaw;
   public float lastSquisYaw;
   public float squidRotation;
   public float lastSquidRotation;
   public float tentacleRotation;
   public float lastTentacleRotation;
   private float constantVelocityRate;
   private float movementVelocity;
   private float rotationVelocity;
   private float randomX;
   private float randomY;
   private float randomZ;

   public SquidEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.95F, 0.95F);
      this.random.setSeed((long)(1 + this.getNetworkId()));
      this.movementVelocity = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
      this.goalSelector.addGoal(0, new SquidEntity.C_44scnqsab(this));
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(10.0);
   }

   @Override
   public float getEyeHeight() {
      return this.height * 0.5F;
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
   protected Item getDefaultDropLoot() {
      return Item.byRawId(0);
   }

   @Override
   protected boolean canClimb() {
      return false;
   }

   @Override
   protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
      int var3 = this.random.nextInt(3 + lootingMultiplier) + 1;

      for(int var4 = 0; var4 < var3; ++var4) {
         this.dropItem(new ItemStack(Items.DYE, 1, DyeColor.BLACK.getMetadata()), 0.0F);
      }
   }

   @Override
   public boolean isInWater() {
      return this.world.applyMaterialDrag(this.getBoundingBox().expand(0.0, -0.6F, 0.0), Material.WATER, this);
   }

   @Override
   public void tickAI() {
      super.tickAI();
      this.lastSquidPitch = this.squidPitch;
      this.lastSquisYaw = this.squidYaw;
      this.lastSquidRotation = this.squidRotation;
      this.lastTentacleRotation = this.tentacleRotation;
      this.squidRotation += this.movementVelocity;
      if ((double)this.squidRotation > Math.PI * 2) {
         if (this.world.isClient) {
            this.squidRotation = (float) (Math.PI * 2);
         } else {
            this.squidRotation = (float)((double)this.squidRotation - (Math.PI * 2));
            if (this.random.nextInt(10) == 0) {
               this.movementVelocity = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
            }

            this.world.doEntityEvent(this, (byte)19);
         }
      }

      if (this.inWater) {
         if (this.squidRotation < (float) Math.PI) {
            float var1 = this.squidRotation / (float) Math.PI;
            this.tentacleRotation = MathHelper.sin(var1 * var1 * (float) Math.PI) * (float) Math.PI * 0.25F;
            if ((double)var1 > 0.75) {
               this.constantVelocityRate = 1.0F;
               this.rotationVelocity = 1.0F;
            } else {
               this.rotationVelocity *= 0.8F;
            }
         } else {
            this.tentacleRotation = 0.0F;
            this.constantVelocityRate *= 0.9F;
            this.rotationVelocity *= 0.99F;
         }

         if (!this.world.isClient) {
            this.velocityX = (double)(this.randomX * this.constantVelocityRate);
            this.velocityY = (double)(this.randomY * this.constantVelocityRate);
            this.velocityZ = (double)(this.randomZ * this.constantVelocityRate);
         }

         float var2 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
         this.bodyYaw += (-((float)Math.atan2(this.velocityX, this.velocityZ)) * 180.0F / (float) Math.PI - this.bodyYaw) * 0.1F;
         this.yaw = this.bodyYaw;
         this.squidYaw = (float)((double)this.squidYaw + Math.PI * (double)this.rotationVelocity * 1.5);
         this.squidPitch += (-((float)Math.atan2((double)var2, this.velocityY)) * 180.0F / (float) Math.PI - this.squidPitch) * 0.1F;
      } else {
         this.tentacleRotation = MathHelper.abs(MathHelper.sin(this.squidRotation)) * (float) Math.PI * 0.25F;
         if (!this.world.isClient) {
            this.velocityX = 0.0;
            this.velocityY -= 0.08;
            this.velocityY *= 0.98F;
            this.velocityZ = 0.0;
         }

         this.squidPitch = (float)((double)this.squidPitch + (double)(-90.0F - this.squidPitch) * 0.02);
      }
   }

   @Override
   public void moveEntityWithVelocity(float sidewaysVelocity, float forwardVelocity) {
      this.move(this.velocityX, this.velocityY, this.velocityZ);
   }

   @Override
   public boolean canSpawn() {
      return this.y > 45.0 && this.y < 63.0 && super.canSpawn();
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   @Override
   public void doEvent(byte event) {
      if (event == 19) {
         this.squidRotation = 0.0F;
      } else {
         super.doEvent(event);
      }
   }

   public void m_04fdxcttc(float f, float g, float h) {
      this.randomX = f;
      this.randomY = g;
      this.randomZ = h;
   }

   public boolean m_16xkzyfve() {
      return this.randomX != 0.0F || this.randomY != 0.0F || this.randomZ != 0.0F;
   }

   static class C_44scnqsab extends Goal {
      private SquidEntity f_01fnuaect;

      public C_44scnqsab(SquidEntity c_36ywfeeve) {
         this.f_01fnuaect = c_36ywfeeve;
      }

      @Override
      public boolean canStart() {
         return true;
      }

      @Override
      public void tick() {
         int var1 = this.f_01fnuaect.getDespawnTimer();
         if (var1 > 100) {
            this.f_01fnuaect.m_04fdxcttc(0.0F, 0.0F, 0.0F);
         } else if (this.f_01fnuaect.getRandom().nextInt(50) == 0 || !this.f_01fnuaect.inWater || !this.f_01fnuaect.m_16xkzyfve()) {
            float var2 = this.f_01fnuaect.getRandom().nextFloat() * (float) Math.PI * 2.0F;
            float var3 = MathHelper.cos(var2) * 0.2F;
            float var4 = -0.1F + this.f_01fnuaect.getRandom().nextFloat() * 0.2F;
            float var5 = MathHelper.sin(var2) * 0.2F;
            this.f_01fnuaect.m_04fdxcttc(var3, var4, var5);
         }
      }
   }
}
