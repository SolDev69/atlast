package net.minecraft.block.entity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.EntityFilter;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Hopper;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.menu.HopperMenu;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class HopperBlockEntity extends InventoryBlockEntity implements Hopper, Tickable {
   private ItemStack[] inventory = new ItemStack[5];
   private String customName;
   private int transferCooldown = -1;

   @Override
   public void readNbt(NbtCompound nbt) {
      super.readNbt(nbt);
      NbtList var2 = nbt.getList("Items", 10);
      this.inventory = new ItemStack[this.getSize()];
      if (nbt.isType("CustomName", 8)) {
         this.customName = nbt.getString("CustomName");
      }

      this.transferCooldown = nbt.getInt("TransferCooldown");

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         NbtCompound var4 = var2.getCompound(var3);
         byte var5 = var4.getByte("Slot");
         if (var5 >= 0 && var5 < this.inventory.length) {
            this.inventory[var5] = ItemStack.fromNbt(var4);
         }
      }
   }

   @Override
   public void writeNbt(NbtCompound nbt) {
      super.writeNbt(nbt);
      NbtList var2 = new NbtList();

      for(int var3 = 0; var3 < this.inventory.length; ++var3) {
         if (this.inventory[var3] != null) {
            NbtCompound var4 = new NbtCompound();
            var4.putByte("Slot", (byte)var3);
            this.inventory[var3].writeNbt(var4);
            var2.add(var4);
         }
      }

      nbt.put("Items", var2);
      nbt.putInt("TransferCooldown", this.transferCooldown);
      if (this.hasCustomName()) {
         nbt.putString("CustomName", this.customName);
      }
   }

   @Override
   public void markDirty() {
      super.markDirty();
   }

   @Override
   public int getSize() {
      return this.inventory.length;
   }

   @Override
   public ItemStack getStack(int slot) {
      return this.inventory[slot];
   }

   @Override
   public ItemStack removeStack(int slot, int amount) {
      if (this.inventory[slot] != null) {
         if (this.inventory[slot].size <= amount) {
            ItemStack var4 = this.inventory[slot];
            this.inventory[slot] = null;
            return var4;
         } else {
            ItemStack var3 = this.inventory[slot].split(amount);
            if (this.inventory[slot].size == 0) {
               this.inventory[slot] = null;
            }

            return var3;
         }
      } else {
         return null;
      }
   }

   @Override
   public ItemStack removeStackQuietly(int slot) {
      if (this.inventory[slot] != null) {
         ItemStack var2 = this.inventory[slot];
         this.inventory[slot] = null;
         return var2;
      } else {
         return null;
      }
   }

   @Override
   public void setStack(int slot, ItemStack stack) {
      this.inventory[slot] = stack;
      if (stack != null && stack.size > this.getMaxStackSize()) {
         stack.size = this.getMaxStackSize();
      }
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.hopper";
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null && this.customName.length() > 0;
   }

   public void setCustomName(String name) {
      this.customName = name;
   }

   @Override
   public int getMaxStackSize() {
      return 64;
   }

   @Override
   public boolean isValid(PlayerEntity player) {
      if (this.world.getBlockEntity(this.pos) != this) {
         return false;
      } else {
         return !(player.getSquaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
      }
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

   @Override
   public void tick() {
      if (this.world != null && !this.world.isClient) {
         --this.transferCooldown;
         if (!this.isOnCooldown()) {
            this.setCooldown(0);
            this.transferItems();
         }
      }
   }

   public boolean transferItems() {
      if (this.world != null && !this.world.isClient) {
         if (!this.isOnCooldown() && HopperBlock.getEnabledFromMetadata(this.getCachedMetadata())) {
            boolean var1 = false;
            if (!this.isInventoryEmpty()) {
               var1 = this.pushItems();
            }

            if (!this.isFull()) {
               var1 = pullItems(this) || var1;
            }

            if (var1) {
               this.setCooldown(8);
               this.markDirty();
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean isInventoryEmpty() {
      for(ItemStack var4 : this.inventory) {
         if (var4 != null) {
            return false;
         }
      }

      return true;
   }

   private boolean isFull() {
      for(ItemStack var4 : this.inventory) {
         if (var4 == null || var4.size != var4.getMaxSize()) {
            return false;
         }
      }

      return true;
   }

   private boolean pushItems() {
      Inventory var1 = this.getTargetInventory();
      if (var1 == null) {
         return false;
      } else {
         Direction var2 = HopperBlock.getFacingFromMetadata(this.getCachedMetadata()).getOpposite();
         if (this.isFullInventory(var1, var2)) {
            return false;
         } else {
            for(int var3 = 0; var3 < this.getSize(); ++var3) {
               if (this.getStack(var3) != null) {
                  ItemStack var4 = this.getStack(var3).copy();
                  ItemStack var5 = pushItems(var1, this.removeStack(var3, 1), var2);
                  if (var5 == null || var5.size == 0) {
                     var1.markDirty();
                     return true;
                  }

                  this.setStack(var3, var4);
               }
            }

            return false;
         }
      }
   }

   private boolean isFullInventory(Inventory inventory, Direction side) {
      if (inventory instanceof SidedInventory) {
         SidedInventory var3 = (SidedInventory)inventory;
         int[] var4 = var3.getSlots(side);

         for(int var5 = 0; var5 < var4.length; ++var5) {
            ItemStack var6 = var3.getStack(var4[var5]);
            if (var6 == null || var6.size != var6.getMaxSize()) {
               return false;
            }
         }
      } else {
         int var7 = inventory.getSize();

         for(int var8 = 0; var8 < var7; ++var8) {
            ItemStack var9 = inventory.getStack(var8);
            if (var9 == null || var9.size != var9.getMaxSize()) {
               return false;
            }
         }
      }

      return true;
   }

   private static boolean isEmptyInventory(Inventory inventory, Direction side) {
      if (inventory instanceof SidedInventory) {
         SidedInventory var2 = (SidedInventory)inventory;
         int[] var3 = var2.getSlots(side);

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (var2.getStack(var3[var4]) != null) {
               return false;
            }
         }
      } else {
         int var5 = inventory.getSize();

         for(int var6 = 0; var6 < var5; ++var6) {
            if (inventory.getStack(var6) != null) {
               return false;
            }
         }
      }

      return true;
   }

   public static boolean pullItems(Hopper hopper) {
      Inventory var1 = getInventoryAbove(hopper);
      if (var1 != null) {
         Direction var2 = Direction.DOWN;
         if (isEmptyInventory(var1, var2)) {
            return false;
         }

         if (var1 instanceof SidedInventory) {
            SidedInventory var3 = (SidedInventory)var1;
            int[] var4 = var3.getSlots(var2);

            for(int var5 = 0; var5 < var4.length; ++var5) {
               if (pullItems(hopper, var1, var4[var5], var2)) {
                  return true;
               }
            }
         } else {
            int var7 = var1.getSize();

            for(int var8 = 0; var8 < var7; ++var8) {
               if (pullItems(hopper, var1, var8, var2)) {
                  return true;
               }
            }
         }
      } else {
         ItemEntity var6 = getItems(hopper.getWorld(), hopper.getX(), hopper.getY() + 1.0, hopper.getZ());
         if (var6 != null) {
            return pickUpItems(hopper, var6);
         }
      }

      return false;
   }

   private static boolean pullItems(Hopper hopper, Inventory inventory, int slot, Direction side) {
      ItemStack var4 = inventory.getStack(slot);
      if (var4 != null && canPullItems(inventory, var4, slot, side)) {
         ItemStack var5 = var4.copy();
         ItemStack var6 = pushItems(hopper, inventory.removeStack(slot, 1), null);
         if (var6 == null || var6.size == 0) {
            inventory.markDirty();
            return true;
         }

         inventory.setStack(slot, var5);
      }

      return false;
   }

   public static boolean pickUpItems(Inventory inventory, ItemEntity items) {
      boolean var2 = false;
      if (items == null) {
         return false;
      } else {
         ItemStack var3 = items.getItemStack().copy();
         ItemStack var4 = pushItems(inventory, var3, null);
         if (var4 != null && var4.size != 0) {
            items.setItemStack(var4);
         } else {
            var2 = true;
            items.remove();
         }

         return var2;
      }
   }

   public static ItemStack pushItems(Inventory inventory, ItemStack stack, Direction side) {
      if (inventory instanceof SidedInventory && side != null) {
         SidedInventory var6 = (SidedInventory)inventory;
         int[] var7 = var6.getSlots(side);

         for(int var5 = 0; var5 < var7.length && stack != null && stack.size > 0; ++var5) {
            stack = pushItems(inventory, stack, var7[var5], side);
         }
      } else {
         int var3 = inventory.getSize();

         for(int var4 = 0; var4 < var3 && stack != null && stack.size > 0; ++var4) {
            stack = pushItems(inventory, stack, var4, side);
         }
      }

      if (stack != null && stack.size == 0) {
         stack = null;
      }

      return stack;
   }

   private static boolean canPushItems(Inventory inventory, ItemStack stack, int slot, Direction side) {
      if (!inventory.canSetStack(slot, stack)) {
         return false;
      } else {
         return !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canHopperAddStack(slot, stack, side);
      }
   }

   private static boolean canPullItems(Inventory inventory, ItemStack stack, int slot, Direction side) {
      return !(inventory instanceof SidedInventory) || ((SidedInventory)inventory).canHopperRemoveStack(slot, stack, side);
   }

   private static ItemStack pushItems(Inventory inventory, ItemStack stack, int slot, Direction side) {
      ItemStack var4 = inventory.getStack(slot);
      if (canPushItems(inventory, stack, slot, side)) {
         boolean var5 = false;
         if (var4 == null) {
            inventory.setStack(slot, stack);
            stack = null;
            var5 = true;
         } else if (canMergeItems(var4, stack)) {
            int var6 = stack.getMaxSize() - var4.size;
            int var7 = Math.min(stack.size, var6);
            stack.size -= var7;
            var4.size += var7;
            var5 = var7 > 0;
         }

         if (var5) {
            if (inventory instanceof HopperBlockEntity) {
               ((HopperBlockEntity)inventory).setCooldown(8);
               inventory.markDirty();
            }

            inventory.markDirty();
         }
      }

      return stack;
   }

   private Inventory getTargetInventory() {
      Direction var1 = HopperBlock.getFacingFromMetadata(this.getCachedMetadata());
      return getInventoryAt(
         this.getWorld(),
         (double)(this.pos.getX() + var1.getOffsetX()),
         (double)(this.pos.getY() + var1.getOffsetY()),
         (double)(this.pos.getZ() + var1.getOffsetZ())
      );
   }

   public static Inventory getInventoryAbove(Hopper hopper) {
      return getInventoryAt(hopper.getWorld(), hopper.getX(), hopper.getY() + 1.0, hopper.getZ());
   }

   public static ItemEntity getItems(World world, double x, double y, double z) {
      List var7 = world.getEntities(ItemEntity.class, new Box(x, y, z, x + 1.0, y + 1.0, z + 1.0), EntityFilter.ALIVE);
      return var7.size() > 0 ? (ItemEntity)var7.get(0) : null;
   }

   public static Inventory getInventoryAt(World world, double x, double y, double z) {
      Object var7 = null;
      int var8 = MathHelper.floor(x);
      int var9 = MathHelper.floor(y);
      int var10 = MathHelper.floor(z);
      BlockPos var11 = new BlockPos(var8, var9, var10);
      BlockEntity var12 = world.getBlockEntity(new BlockPos(var8, var9, var10));
      if (var12 instanceof Inventory) {
         var7 = (Inventory)var12;
         if (var7 instanceof ChestBlockEntity) {
            Block var13 = world.getBlockState(new BlockPos(var8, var9, var10)).getBlock();
            if (var13 instanceof ChestBlock) {
               var7 = ((ChestBlock)var13).getInventory(world, var11);
            }
         }
      }

      if (var7 == null) {
         List var14 = world.getEntities(null, new Box(x, y, z, x + 1.0, y + 1.0, z + 1.0), EntityFilter.INVENTORY);
         if (var14.size() > 0) {
            var7 = (Inventory)var14.get(world.random.nextInt(var14.size()));
         }
      }

      return (Inventory)var7;
   }

   private static boolean canMergeItems(ItemStack stack1, ItemStack stack2) {
      if (stack1.getItem() != stack2.getItem()) {
         return false;
      } else if (stack1.getMetadata() != stack2.getMetadata()) {
         return false;
      } else if (stack1.size > stack1.getMaxSize()) {
         return false;
      } else {
         return ItemStack.matchesNbt(stack1, stack2);
      }
   }

   @Override
   public double getX() {
      return (double)this.pos.getX();
   }

   @Override
   public double getY() {
      return (double)this.pos.getY();
   }

   @Override
   public double getZ() {
      return (double)this.pos.getZ();
   }

   public void setCooldown(int cooldown) {
      this.transferCooldown = cooldown;
   }

   public boolean isOnCooldown() {
      return this.transferCooldown > 0;
   }

   @Override
   public String getMenuType() {
      return "minecraft:hopper";
   }

   @Override
   public InventoryMenu createMenu(PlayerInventory playerInventory, PlayerEntity player) {
      return new HopperMenu(playerInventory, this, player);
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
      for(int var1 = 0; var1 < this.inventory.length; ++var1) {
         this.inventory[var1] = null;
      }
   }
}
