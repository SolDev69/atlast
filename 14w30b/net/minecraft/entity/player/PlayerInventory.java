package net.minecraft.entity.player;

import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.command.TestForBlockCommand;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PlayerInventory implements Inventory {
   public ItemStack[] inventorySlots = new ItemStack[36];
   public ItemStack[] armorSlots = new ItemStack[4];
   public int selectedSlot;
   @Environment(EnvType.CLIENT)
   private ItemStack mainHandStack;
   public PlayerEntity player;
   private ItemStack cursorStack;
   public boolean dirty;

   public PlayerInventory(PlayerEntity player) {
      this.player = player;
   }

   public ItemStack getMainHandStack() {
      return this.selectedSlot < 9 && this.selectedSlot >= 0 ? this.inventorySlots[this.selectedSlot] : null;
   }

   public static int getHotbarSize() {
      return 9;
   }

   private int indexOfItem(Item item) {
      for(int var2 = 0; var2 < this.inventorySlots.length; ++var2) {
         if (this.inventorySlots[var2] != null && this.inventorySlots[var2].getItem() == item) {
            return var2;
         }
      }

      return -1;
   }

   @Environment(EnvType.CLIENT)
   private int indexOfItemWithMetaData(Item item, int metaData) {
      for(int var3 = 0; var3 < this.inventorySlots.length; ++var3) {
         if (this.inventorySlots[var3] != null && this.inventorySlots[var3].getItem() == item && this.inventorySlots[var3].getMetadata() == metaData) {
            return var3;
         }
      }

      return -1;
   }

   private int indexOfItemStack(ItemStack itemStack) {
      for(int var2 = 0; var2 < this.inventorySlots.length; ++var2) {
         if (this.inventorySlots[var2] != null
            && this.inventorySlots[var2].getItem() == itemStack.getItem()
            && this.inventorySlots[var2].isStackable()
            && this.inventorySlots[var2].size < this.inventorySlots[var2].getMaxSize()
            && this.inventorySlots[var2].size < this.getMaxStackSize()
            && (!this.inventorySlots[var2].isItemStackable() || this.inventorySlots[var2].getMetadata() == itemStack.getMetadata())
            && ItemStack.matchesNbt(this.inventorySlots[var2], itemStack)) {
            return var2;
         }
      }

      return -1;
   }

   public int getEmptySlot() {
      for(int var1 = 0; var1 < this.inventorySlots.length; ++var1) {
         if (this.inventorySlots[var1] == null) {
            return var1;
         }
      }

      return -1;
   }

   @Environment(EnvType.CLIENT)
   public void pickItem(Item item, int metaData, boolean isDamageable, boolean creativeMode) {
      int var5 = -1;
      this.mainHandStack = this.getMainHandStack();
      if (isDamageable) {
         var5 = this.indexOfItemWithMetaData(item, metaData);
      } else {
         var5 = this.indexOfItem(item);
      }

      if (var5 >= 0 && var5 < 9) {
         this.selectedSlot = var5;
      } else {
         if (creativeMode && item != null) {
            int var6 = this.getEmptySlot();
            if (var6 >= 0 && var6 < 9) {
               this.selectedSlot = var6;
            }

            this.creativePickItem(item, metaData);
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public void scrollInHotbar(int scroll) {
      if (scroll > 0) {
         scroll = 1;
      }

      if (scroll < 0) {
         scroll = -1;
      }

      this.selectedSlot -= scroll;

      while(this.selectedSlot < 0) {
         this.selectedSlot += 9;
      }

      while(this.selectedSlot >= 9) {
         this.selectedSlot -= 9;
      }
   }

   public int getRemovedAmount(Item item, int metaData, int j, NbtCompound c_54jdujmos) {
      int var5 = 0;

      for(int var6 = 0; var6 < this.inventorySlots.length; ++var6) {
         ItemStack var7 = this.inventorySlots[var6];
         if (var7 != null
            && (item == null || var7.getItem() == item)
            && (metaData <= -1 || var7.getMetadata() == metaData)
            && (c_54jdujmos == null || TestForBlockCommand.matchesNbt(c_54jdujmos, var7.getNbt(), true))) {
            int var8 = j <= 0 ? var7.size : Math.min(j - var5, var7.size);
            var5 += var8;
            if (j != 0) {
               this.inventorySlots[var6].size -= var8;
               if (this.inventorySlots[var6].size == 0) {
                  this.inventorySlots[var6] = null;
               }

               if (j > 0 && var5 >= j) {
                  return var5;
               }
            }
         }
      }

      for(int var9 = 0; var9 < this.armorSlots.length; ++var9) {
         ItemStack var11 = this.armorSlots[var9];
         if (var11 != null
            && (item == null || var11.getItem() == item)
            && (metaData <= -1 || var11.getMetadata() == metaData)
            && (c_54jdujmos == null || TestForBlockCommand.matchesNbt(c_54jdujmos, var11.getNbt(), false))) {
            int var12 = j <= 0 ? var11.size : Math.min(j - var5, var11.size);
            var5 += var12;
            if (j != 0) {
               this.armorSlots[var9].size -= var12;
               if (this.armorSlots[var9].size == 0) {
                  this.armorSlots[var9] = null;
               }

               if (j > 0 && var5 >= j) {
                  return var5;
               }
            }
         }
      }

      if (this.cursorStack != null) {
         if (item != null && this.cursorStack.getItem() != item) {
            return var5;
         }

         if (metaData > -1 && this.cursorStack.getMetadata() != metaData) {
            return var5;
         }

         if (c_54jdujmos != null && !TestForBlockCommand.matchesNbt(c_54jdujmos, this.cursorStack.getNbt(), false)) {
            return var5;
         }

         int var10 = j <= 0 ? this.cursorStack.size : Math.min(j - var5, this.cursorStack.size);
         var5 += var10;
         if (j != 0) {
            this.cursorStack.size -= var10;
            if (this.cursorStack.size == 0) {
               this.cursorStack = null;
            }

            if (j > 0 && var5 >= j) {
               return var5;
            }
         }
      }

      return var5;
   }

   @Environment(EnvType.CLIENT)
   public void creativePickItem(Item item, int metaData) {
      if (item != null) {
         if (this.mainHandStack != null
            && this.mainHandStack.isEnchantable()
            && this.indexOfItemWithMetaData(this.mainHandStack.getItem(), this.mainHandStack.getDamage()) == this.selectedSlot) {
            return;
         }

         int var3 = this.indexOfItemWithMetaData(item, metaData);
         if (var3 >= 0) {
            int var4 = this.inventorySlots[var3].size;
            this.inventorySlots[var3] = this.inventorySlots[this.selectedSlot];
            this.inventorySlots[this.selectedSlot] = new ItemStack(item, var4, metaData);
         } else {
            this.inventorySlots[this.selectedSlot] = new ItemStack(item, 1, metaData);
         }
      }
   }

   private int putStackInInventory(ItemStack itemStack) {
      Item var2 = itemStack.getItem();
      int var3 = itemStack.size;
      if (itemStack.getMaxSize() == 1) {
         int var7 = this.getEmptySlot();
         if (var7 < 0) {
            return var3;
         } else {
            if (this.inventorySlots[var7] == null) {
               this.inventorySlots[var7] = ItemStack.copyOf(itemStack);
            }

            return 0;
         }
      } else {
         int var4 = this.indexOfItemStack(itemStack);
         if (var4 < 0) {
            var4 = this.getEmptySlot();
         }

         if (var4 < 0) {
            return var3;
         } else {
            if (this.inventorySlots[var4] == null) {
               this.inventorySlots[var4] = new ItemStack(var2, 0, itemStack.getMetadata());
               if (itemStack.hasNbt()) {
                  this.inventorySlots[var4].setNbt((NbtCompound)itemStack.getNbt().copy());
               }
            }

            int var5 = var3;
            if (var3 > this.inventorySlots[var4].getMaxSize() - this.inventorySlots[var4].size) {
               var5 = this.inventorySlots[var4].getMaxSize() - this.inventorySlots[var4].size;
            }

            if (var5 > this.getMaxStackSize() - this.inventorySlots[var4].size) {
               var5 = this.getMaxStackSize() - this.inventorySlots[var4].size;
            }

            if (var5 == 0) {
               return var3;
            } else {
               var3 -= var5;
               this.inventorySlots[var4].size += var5;
               this.inventorySlots[var4].popAnimationTime = 5;
               return var3;
            }
         }
      }
   }

   public void tickItems() {
      for(int var1 = 0; var1 < this.inventorySlots.length; ++var1) {
         if (this.inventorySlots[var1] != null) {
            this.inventorySlots[var1].tick(this.player.world, this.player, var1, this.selectedSlot == var1);
         }
      }
   }

   public boolean consumeOne(Item item) {
      int var2 = this.indexOfItem(item);
      if (var2 < 0) {
         return false;
      } else {
         if (--this.inventorySlots[var2].size <= 0) {
            this.inventorySlots[var2] = null;
         }

         return true;
      }
   }

   public boolean contains(Item item) {
      int var2 = this.indexOfItem(item);
      return var2 >= 0;
   }

   public boolean insertStack(ItemStack itemStack) {
      if (itemStack != null && itemStack.size != 0 && itemStack.getItem() != null) {
         try {
            if (itemStack.isDamaged()) {
               int var6 = this.getEmptySlot();
               if (var6 >= 0) {
                  this.inventorySlots[var6] = ItemStack.copyOf(itemStack);
                  this.inventorySlots[var6].popAnimationTime = 5;
                  itemStack.size = 0;
                  return true;
               } else if (this.player.abilities.creativeMode) {
                  itemStack.size = 0;
                  return true;
               } else {
                  return false;
               }
            } else {
               int var2;
               do {
                  var2 = itemStack.size;
                  itemStack.size = this.putStackInInventory(itemStack);
               } while(itemStack.size > 0 && itemStack.size < var2);

               if (itemStack.size == var2 && this.player.abilities.creativeMode) {
                  itemStack.size = 0;
                  return true;
               } else {
                  return itemStack.size < var2;
               }
            }
         } catch (Throwable var5) {
            CrashReport var3 = CrashReport.of(var5, "Adding item to inventory");
            CashReportCategory var4 = var3.addCategory("Item being added");
            var4.add("Item ID", Item.getRawId(itemStack.getItem()));
            var4.add("Item data", itemStack.getMetadata());
            var4.add("Item name", new Callable() {
               public String call() {
                  return itemStack.getHoverName();
               }
            });
            throw new CrashException(var3);
         }
      } else {
         return false;
      }
   }

   @Override
   public ItemStack removeStack(int slot, int amount) {
      ItemStack[] var3 = this.inventorySlots;
      if (slot >= this.inventorySlots.length) {
         var3 = this.armorSlots;
         slot -= this.inventorySlots.length;
      }

      if (var3[slot] != null) {
         if (var3[slot].size <= amount) {
            ItemStack var5 = var3[slot];
            var3[slot] = null;
            return var5;
         } else {
            ItemStack var4 = var3[slot].split(amount);
            if (var3[slot].size == 0) {
               var3[slot] = null;
            }

            return var4;
         }
      } else {
         return null;
      }
   }

   @Override
   public ItemStack removeStackQuietly(int slot) {
      ItemStack[] var2 = this.inventorySlots;
      if (slot >= this.inventorySlots.length) {
         var2 = this.armorSlots;
         slot -= this.inventorySlots.length;
      }

      if (var2[slot] != null) {
         ItemStack var3 = var2[slot];
         var2[slot] = null;
         return var3;
      } else {
         return null;
      }
   }

   @Override
   public void setStack(int slot, ItemStack stack) {
      ItemStack[] var3 = this.inventorySlots;
      if (slot >= var3.length) {
         slot -= var3.length;
         var3 = this.armorSlots;
      }

      var3[slot] = stack;
   }

   public float getMiningSpeed(Block block) {
      float var2 = 1.0F;
      if (this.inventorySlots[this.selectedSlot] != null) {
         var2 *= this.inventorySlots[this.selectedSlot].getMiningSpeed(block);
      }

      return var2;
   }

   public NbtList writeNbt(NbtList list) {
      for(int var2 = 0; var2 < this.inventorySlots.length; ++var2) {
         if (this.inventorySlots[var2] != null) {
            NbtCompound var3 = new NbtCompound();
            var3.putByte("Slot", (byte)var2);
            this.inventorySlots[var2].writeNbt(var3);
            list.add(var3);
         }
      }

      for(int var4 = 0; var4 < this.armorSlots.length; ++var4) {
         if (this.armorSlots[var4] != null) {
            NbtCompound var5 = new NbtCompound();
            var5.putByte("Slot", (byte)(var4 + 100));
            this.armorSlots[var4].writeNbt(var5);
            list.add(var5);
         }
      }

      return list;
   }

   public void readNbt(NbtList list) {
      this.inventorySlots = new ItemStack[36];
      this.armorSlots = new ItemStack[4];

      for(int var2 = 0; var2 < list.size(); ++var2) {
         NbtCompound var3 = list.getCompound(var2);
         int var4 = var3.getByte("Slot") & 255;
         ItemStack var5 = ItemStack.fromNbt(var3);
         if (var5 != null) {
            if (var4 >= 0 && var4 < this.inventorySlots.length) {
               this.inventorySlots[var4] = var5;
            }

            if (var4 >= 100 && var4 < this.armorSlots.length + 100) {
               this.armorSlots[var4 - 100] = var5;
            }
         }
      }
   }

   @Override
   public int getSize() {
      return this.inventorySlots.length + 4;
   }

   @Override
   public ItemStack getStack(int slot) {
      ItemStack[] var2 = this.inventorySlots;
      if (slot >= var2.length) {
         slot -= var2.length;
         var2 = this.armorSlots;
      }

      return var2[slot];
   }

   @Override
   public String getName() {
      return "container.inventory";
   }

   @Override
   public boolean hasCustomName() {
      return false;
   }

   @Override
   public Text getDisplayName() {
      return (Text)(this.hasCustomName() ? new LiteralText(this.getName()) : new TranslatableText(this.getName()));
   }

   @Override
   public int getMaxStackSize() {
      return 64;
   }

   public boolean canToolBreak(Block block) {
      if (block.getMaterial().isToolNotRequired()) {
         return true;
      } else {
         ItemStack var2 = this.getStack(this.selectedSlot);
         return var2 != null ? var2.canEffectivelyMine(block) : false;
      }
   }

   public ItemStack getArmor(int slot) {
      return this.armorSlots[slot];
   }

   public int getArmorProtectionValue() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.armorSlots.length; ++var2) {
         if (this.armorSlots[var2] != null && this.armorSlots[var2].getItem() instanceof ArmorItem) {
            int var3 = ((ArmorItem)this.armorSlots[var2].getItem()).protection;
            var1 += var3;
         }
      }

      return var1;
   }

   public void damageArmor(float armor) {
      armor /= 4.0F;
      if (armor < 1.0F) {
         armor = 1.0F;
      }

      for(int var2 = 0; var2 < this.armorSlots.length; ++var2) {
         if (this.armorSlots[var2] != null && this.armorSlots[var2].getItem() instanceof ArmorItem) {
            this.armorSlots[var2].damageAndBreak((int)armor, this.player);
            if (this.armorSlots[var2].size == 0) {
               this.armorSlots[var2] = null;
            }
         }
      }
   }

   public void dropAll() {
      for(int var1 = 0; var1 < this.inventorySlots.length; ++var1) {
         if (this.inventorySlots[var1] != null) {
            this.player.dropItem(this.inventorySlots[var1], true, false);
            this.inventorySlots[var1] = null;
         }
      }

      for(int var2 = 0; var2 < this.armorSlots.length; ++var2) {
         if (this.armorSlots[var2] != null) {
            this.player.dropItem(this.armorSlots[var2], true, false);
            this.armorSlots[var2] = null;
         }
      }
   }

   @Override
   public void markDirty() {
      this.dirty = true;
   }

   public void setCursorStack(ItemStack stack) {
      this.cursorStack = stack;
   }

   public ItemStack getCursorStack() {
      return this.cursorStack;
   }

   @Override
   public boolean isValid(PlayerEntity player) {
      if (this.player.removed) {
         return false;
      } else {
         return !(player.getSquaredDistanceTo(this.player) > 64.0);
      }
   }

   public boolean contains(ItemStack stack) {
      for(int var2 = 0; var2 < this.armorSlots.length; ++var2) {
         if (this.armorSlots[var2] != null && this.armorSlots[var2].matchesItem(stack)) {
            return true;
         }
      }

      for(int var3 = 0; var3 < this.inventorySlots.length; ++var3) {
         if (this.inventorySlots[var3] != null && this.inventorySlots[var3].matchesItem(stack)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void onOpen(PlayerEntity player) {
   }

   @Override
   public void onClose(PlayerEntity player) {
   }

   @Override
   public boolean canSetStack(int slot, ItemStack stack) {
      return true;
   }

   public void copy(PlayerInventory inventory) {
      for(int var2 = 0; var2 < this.inventorySlots.length; ++var2) {
         this.inventorySlots[var2] = ItemStack.copyOf(inventory.inventorySlots[var2]);
      }

      for(int var3 = 0; var3 < this.armorSlots.length; ++var3) {
         this.armorSlots[var3] = ItemStack.copyOf(inventory.armorSlots[var3]);
      }

      this.selectedSlot = inventory.selectedSlot;
   }

   @Override
   public int getData(int id) {
      return 0;
   }

   @Override
   public void setData(int id, int value) {
   }

   @Override
   public int getDataCount() {
      return 0;
   }

   @Override
   public void clear() {
      for(int var1 = 0; var1 < this.inventorySlots.length; ++var1) {
         this.inventorySlots[var1] = null;
      }

      for(int var2 = 0; var2 < this.armorSlots.length; ++var2) {
         this.armorSlots[var2] = null;
      }
   }
}
