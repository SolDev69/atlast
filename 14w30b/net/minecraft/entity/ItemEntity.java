package net.minecraft.entity;

import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.locale.I18n;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.achievement.Achievements;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemEntity extends Entity {
   private static final Logger LOGGER = LogManager.getLogger();
   private int age;
   private int pickupCooldown;
   private int health = 5;
   private String thrower;
   private String owner;
   public float hoverHeight = (float)(Math.random() * Math.PI * 2.0);

   public ItemEntity(World world, double x, double y, double z) {
      super(world);
      this.setDimensions(0.25F, 0.25F);
      this.setPosition(x, y, z);
      this.yaw = (float)(Math.random() * 360.0);
      this.velocityX = (double)((float)(Math.random() * 0.2F - 0.1F));
      this.velocityY = 0.2F;
      this.velocityZ = (double)((float)(Math.random() * 0.2F - 0.1F));
   }

   public ItemEntity(World world, double x, double y, double z, ItemStack stack) {
      this(world, x, y, z);
      this.setItemStack(stack);
   }

   @Override
   protected boolean canClimb() {
      return false;
   }

   public ItemEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.25F, 0.25F);
      this.setItemStack(new ItemStack(Blocks.AIR, 0));
   }

   @Override
   protected void initDataTracker() {
      this.getDataTracker().add(10, 5);
   }

   @Override
   public void tick() {
      if (this.getItemStack() == null) {
         this.remove();
      } else {
         super.tick();
         if (this.pickupCooldown > 0 && this.pickupCooldown != 32767) {
            --this.pickupCooldown;
         }

         this.prevX = this.x;
         this.prevY = this.y;
         this.prevZ = this.z;
         this.velocityY -= 0.04F;
         this.noClip = this.pushAwayFrom(this.x, (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.z);
         this.move(this.velocityX, this.velocityY, this.velocityZ);
         boolean var1 = (int)this.prevX != (int)this.x || (int)this.prevY != (int)this.y || (int)this.prevZ != (int)this.z;
         if (var1 || this.time % 25 == 0) {
            if (this.world.getBlockState(new BlockPos(this)).getBlock().getMaterial() == Material.LAVA) {
               this.velocityY = 0.2F;
               this.velocityX = (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
               this.velocityZ = (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
               this.playSound("random.fizz", 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
            }

            if (!this.world.isClient) {
               this.tryMerge();
            }
         }

         float var2 = 0.98F;
         if (this.onGround) {
            var2 = this.world
                  .getBlockState(new BlockPos(MathHelper.floor(this.x), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.z)))
                  .getBlock()
                  .slipperiness
               * 0.98F;
         }

         this.velocityX *= (double)var2;
         this.velocityY *= 0.98F;
         this.velocityZ *= (double)var2;
         if (this.onGround) {
            this.velocityY *= -0.5;
         }

         if (this.age != -32768) {
            ++this.age;
         }

         if (!this.world.isClient && this.age >= 6000) {
            this.remove();
         }
      }
   }

   private void tryMerge() {
      for(ItemEntity var2 : this.world.getEntities(ItemEntity.class, this.getBoundingBox().expand(0.5, 0.0, 0.5))) {
         this.canStack(var2);
      }
   }

   private boolean canStack(ItemEntity item) {
      if (item == this) {
         return false;
      } else if (item.isAlive() && this.isAlive()) {
         ItemStack var2 = this.getItemStack();
         ItemStack var3 = item.getItemStack();
         if (this.pickupCooldown == 32767 || item.pickupCooldown == 32767) {
            return false;
         } else if (this.age != -32768 && item.age != -32768) {
            if (var3.getItem() != var2.getItem()) {
               return false;
            } else if (var3.hasNbt() ^ var2.hasNbt()) {
               return false;
            } else if (var3.hasNbt() && !var3.getNbt().equals(var2.getNbt())) {
               return false;
            } else if (var3.getItem() == null) {
               return false;
            } else if (var3.getItem().isStackable() && var3.getMetadata() != var2.getMetadata()) {
               return false;
            } else if (var3.size < var2.size) {
               return item.canStack(this);
            } else if (var3.size + var2.size > var3.getMaxSize()) {
               return false;
            } else {
               var3.size += var2.size;
               item.pickupCooldown = Math.max(item.pickupCooldown, this.pickupCooldown);
               item.age = Math.min(item.age, this.age);
               item.setItemStack(var3);
               this.remove();
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void resetAge() {
      this.age = 4800;
   }

   @Override
   public boolean checkWaterCollision() {
      return this.world.applyMaterialDrag(this.getBoundingBox(), Material.WATER, this);
   }

   @Override
   protected void applyFireDamage(int amount) {
      this.damage(DamageSource.FIRE, (float)amount);
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerable(source)) {
         return false;
      } else if (this.getItemStack() != null && this.getItemStack().getItem() == Items.NETHER_STAR && source.isExplosive()) {
         return false;
      } else {
         this.onDamaged();
         this.health = (int)((float)this.health - amount);
         if (this.health <= 0) {
            this.remove();
         }

         return false;
      }
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      nbt.putShort("Health", (short)((byte)this.health));
      nbt.putShort("Age", (short)this.age);
      nbt.putShort("PickupDelay", (short)this.pickupCooldown);
      if (this.getThrower() != null) {
         nbt.putString("Thrower", this.thrower);
      }

      if (this.getOwner() != null) {
         nbt.putString("Owner", this.owner);
      }

      if (this.getItemStack() != null) {
         nbt.put("Item", this.getItemStack().writeNbt(new NbtCompound()));
      }
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      this.health = nbt.getShort("Health") & 255;
      this.age = nbt.getShort("Age");
      if (nbt.contains("PickupDelay")) {
         this.pickupCooldown = nbt.getShort("PickupDelay");
      }

      if (nbt.contains("Owner")) {
         this.owner = nbt.getString("Owner");
      }

      if (nbt.contains("Thrower")) {
         this.thrower = nbt.getString("Thrower");
      }

      NbtCompound var2 = nbt.getCompound("Item");
      this.setItemStack(ItemStack.fromNbt(var2));
      if (this.getItemStack() == null) {
         this.remove();
      }
   }

   @Override
   public void onPlayerCollision(PlayerEntity player) {
      if (!this.world.isClient) {
         ItemStack var2 = this.getItemStack();
         int var3 = var2.size;
         if (this.pickupCooldown == 0
            && (this.owner == null || 6000 - this.age <= 200 || this.owner.equals(player.getName()))
            && player.inventory.insertStack(var2)) {
            if (var2.getItem() == Item.byBlock(Blocks.LOG)) {
               player.incrementStat(Achievements.GET_LOG);
            }

            if (var2.getItem() == Item.byBlock(Blocks.LOG2)) {
               player.incrementStat(Achievements.GET_LOG);
            }

            if (var2.getItem() == Items.LEATHER) {
               player.incrementStat(Achievements.KILL_COW);
            }

            if (var2.getItem() == Items.DIAMOND) {
               player.incrementStat(Achievements.GET_DIAMOND);
            }

            if (var2.getItem() == Items.BLAZE_ROD) {
               player.incrementStat(Achievements.GET_BLAZE_ROD);
            }

            if (var2.getItem() == Items.DIAMOND && this.getThrower() != null) {
               PlayerEntity var4 = this.world.getPlayer(this.getThrower());
               if (var4 != null && var4 != player) {
                  var4.incrementStat(Achievements.GIVE_DIAMOND);
               }
            }

            if (!this.isSilent()) {
               this.world.playSound((Entity)player, "random.pop", 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }

            player.sendPickup(this, var3);
            if (var2.size <= 0) {
               this.remove();
            }
         }
      }
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.getCustomName() : I18n.translate("item." + this.getItemStack().getTranslationKey());
   }

   @Override
   public boolean canBePunched() {
      return false;
   }

   @Override
   public void teleportToDimension(int dimensionId) {
      super.teleportToDimension(dimensionId);
      if (!this.world.isClient) {
         this.tryMerge();
      }
   }

   public ItemStack getItemStack() {
      ItemStack var1 = this.getDataTracker().getStack(10);
      if (var1 == null) {
         if (this.world != null) {
            LOGGER.error("Item entity " + this.getNetworkId() + " has no item?!");
         }

         return new ItemStack(Blocks.STONE);
      } else {
         return var1;
      }
   }

   public void setItemStack(ItemStack stack) {
      this.getDataTracker().update(10, stack);
      this.getDataTracker().markDirty(10);
   }

   public String getOwner() {
      return this.owner;
   }

   public void setOwner(String owner) {
      this.owner = owner;
   }

   public String getThrower() {
      return this.thrower;
   }

   public void setThrower(String thrower) {
      this.thrower = thrower;
   }

   @Environment(EnvType.CLIENT)
   public int getAge() {
      return this.age;
   }

   public void resetPickupCooldown() {
      this.pickupCooldown = 10;
   }

   public void getPickupCooldown() {
      this.pickupCooldown = 0;
   }

   public void m_57ysjbtls() {
      this.pickupCooldown = 32767;
   }

   public void setPickupCooldown(int i) {
      this.pickupCooldown = i;
   }

   public boolean m_40frtuhfs() {
      return this.pickupCooldown > 0;
   }

   public void m_23igftynk() {
      this.age = -6000;
   }

   public void m_67zgljrbu() {
      this.m_57ysjbtls();
      this.age = 5999;
   }
}
