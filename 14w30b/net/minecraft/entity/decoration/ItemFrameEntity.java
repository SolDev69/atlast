package net.minecraft.entity.decoration;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.map.SavedMapData;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class ItemFrameEntity extends DecorationEntity {
   private float setDropChance = 1.0F;

   public ItemFrameEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   public ItemFrameEntity(World c_54ruxjwzt, BlockPos c_76varpwca, Direction c_69garkogr) {
      super(c_54ruxjwzt, c_76varpwca);
      this.setDirection(c_69garkogr);
   }

   @Override
   protected void initDataTracker() {
      this.getDataTracker().add(8, 5);
      this.getDataTracker().put(9, (byte)0);
   }

   @Override
   public float getExtraHitboxSize() {
      return 0.0F;
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerable(source)) {
         return false;
      } else if (this.getItemStackInItemFrame() != null) {
         if (!this.world.isClient) {
            this.dropItemOrItemFrame(source.getAttacker(), false);
            this.addItemToItemFrame(null);
         }

         return true;
      } else {
         return super.damage(source, amount);
      }
   }

   @Override
   public int getWidth() {
      return 12;
   }

   @Override
   public int getHeight() {
      return 12;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean isWithinViewDistance(double distance) {
      double var3 = 16.0;
      var3 *= 64.0 * this.viewDistanceScaling;
      return distance < var3 * var3;
   }

   @Override
   public void onAttack(Entity entity) {
      this.dropItemOrItemFrame(entity, true);
   }

   public void dropItemOrItemFrame(Entity entity, boolean shouldDropSelf) {
      ItemStack var3 = this.getItemStackInItemFrame();
      if (entity instanceof PlayerEntity) {
         PlayerEntity var4 = (PlayerEntity)entity;
         if (var4.abilities.creativeMode) {
            this.removeItem(var3);
            return;
         }
      }

      if (shouldDropSelf) {
         this.dropItem(new ItemStack(Items.ITEM_FRAME), 0.0F);
      }

      if (var3 != null && this.random.nextFloat() < this.setDropChance) {
         var3 = var3.copy();
         this.removeItem(var3);
         this.dropItem(var3, 0.0F);
      }
   }

   private void removeItem(ItemStack item) {
      if (item != null) {
         if (item.getItem() == Items.FILLED_MAP) {
            SavedMapData var2 = ((FilledMapItem)item.getItem()).getSavedMapData(item, this.world);
            var2.decorations.remove("frame-" + this.getNetworkId());
         }

         item.setItemFrame(null);
      }
   }

   public ItemStack getItemStackInItemFrame() {
      return this.getDataTracker().getStack(8);
   }

   public void addItemToItemFrame(ItemStack itemStack) {
      if (itemStack != null) {
         itemStack = itemStack.copy();
         itemStack.size = 1;
         itemStack.setItemFrame(this);
      }

      this.getDataTracker().update(8, itemStack);
      this.getDataTracker().markDirty(8);
      if (this.pos != null) {
         this.world.updateComparators(this.pos, Blocks.AIR);
      }
   }

   public int rotation() {
      return this.getDataTracker().getByte(9);
   }

   public void setItemRotation(int rotation) {
      this.getDataTracker().update(9, (byte)(rotation % 8));
      if (this.pos != null) {
         this.world.updateComparators(this.pos, Blocks.AIR);
      }
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      if (this.getItemStackInItemFrame() != null) {
         nbt.put("Item", this.getItemStackInItemFrame().writeNbt(new NbtCompound()));
         nbt.putByte("ItemRotation", (byte)this.rotation());
         nbt.putFloat("ItemDropChance", this.setDropChance);
      }

      super.writeCustomNbt(nbt);
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      NbtCompound var2 = nbt.getCompound("Item");
      if (var2 != null && !var2.isEmpty()) {
         this.addItemToItemFrame(ItemStack.fromNbt(var2));
         this.setItemRotation(nbt.getByte("ItemRotation"));
         if (nbt.isType("ItemDropChance", 99)) {
            this.setDropChance = nbt.getFloat("ItemDropChance");
         }

         if (nbt.contains("Direction")) {
            this.setItemRotation(this.rotation() * 2);
         }
      }

      super.readCustomNbt(nbt);
   }

   @Override
   public boolean interact(PlayerEntity player) {
      if (this.getItemStackInItemFrame() == null) {
         ItemStack var2 = player.getStackInHand();
         if (var2 != null && !this.world.isClient) {
            this.addItemToItemFrame(var2);
            if (!player.abilities.creativeMode && --var2.size <= 0) {
               player.inventory.setStack(player.inventory.selectedSlot, null);
            }
         }
      } else if (!this.world.isClient) {
         this.setItemRotation(this.rotation() + 1);
      }

      return true;
   }

   public int m_45dhrnlgy() {
      return this.getItemStackInItemFrame() == null ? 0 : this.rotation() % 8 + 1;
   }
}
