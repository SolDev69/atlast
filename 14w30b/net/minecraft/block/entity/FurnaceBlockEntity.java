package net.minecraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.menu.FurnaceMenu;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.slot.FurnaceFuelSlot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.smelting.SmeltingManager;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class FurnaceBlockEntity extends InventoryBlockEntity implements Tickable, SidedInventory {
   private static final int[] INVENTORY_SLOTS_TOP = new int[]{0};
   private static final int[] INVENTORY_SLOTS_BOTTOM = new int[]{2, 1};
   private static final int[] INVENTORY_SLOTS_SIDES = new int[]{1};
   private ItemStack[] inventory = new ItemStack[3];
   private int fuelTime;
   private int totalFuelTime;
   private int cookTime;
   private int totalCookTime;
   private String customName;

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

      if (slot == 0) {
         this.totalCookTime = this.getTotalCookTime(stack);
         this.cookTime = 0;
         this.markDirty();
      }
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.furnace";
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null && this.customName.length() > 0;
   }

   public void setCustomName(String name) {
      this.customName = name;
   }

   @Override
   public void readNbt(NbtCompound nbt) {
      super.readNbt(nbt);
      NbtList var2 = nbt.getList("Items", 10);
      this.inventory = new ItemStack[this.getSize()];

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         NbtCompound var4 = var2.getCompound(var3);
         byte var5 = var4.getByte("Slot");
         if (var5 >= 0 && var5 < this.inventory.length) {
            this.inventory[var5] = ItemStack.fromNbt(var4);
         }
      }

      this.fuelTime = nbt.getShort("BurnTime");
      this.cookTime = nbt.getShort("CookTime");
      this.totalCookTime = nbt.getShort("CookTimeTotal");
      this.totalFuelTime = getFuelTime(this.inventory[1]);
      if (nbt.isType("CustomName", 8)) {
         this.customName = nbt.getString("CustomName");
      }
   }

   @Override
   public void writeNbt(NbtCompound nbt) {
      super.writeNbt(nbt);
      nbt.putShort("BurnTime", (short)this.fuelTime);
      nbt.putShort("CookTime", (short)this.cookTime);
      nbt.putShort("CookTimeTotal", (short)this.totalCookTime);
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
      if (this.hasCustomName()) {
         nbt.putString("CustomName", this.customName);
      }
   }

   @Override
   public int getMaxStackSize() {
      return 64;
   }

   public boolean hasFuel() {
      return this.fuelTime > 0;
   }

   @Environment(EnvType.CLIENT)
   public static boolean isLit(Inventory furnace) {
      return furnace.getData(0) > 0;
   }

   @Override
   public void tick() {
      boolean var1 = this.hasFuel();
      boolean var2 = false;
      if (this.hasFuel()) {
         --this.fuelTime;
      }

      if (!this.world.isClient) {
         if (this.hasFuel() || this.inventory[1] != null && this.inventory[0] != null) {
            if (!this.hasFuel() && this.canCook()) {
               this.totalFuelTime = this.fuelTime = getFuelTime(this.inventory[1]);
               if (this.hasFuel()) {
                  var2 = true;
                  if (this.inventory[1] != null) {
                     --this.inventory[1].size;
                     if (this.inventory[1].size == 0) {
                        Item var3 = this.inventory[1].getItem().getRecipeRemainder();
                        this.inventory[1] = var3 != null ? new ItemStack(var3) : null;
                     }
                  }
               }
            }

            if (this.hasFuel() && this.canCook()) {
               ++this.cookTime;
               if (this.cookTime == this.totalCookTime) {
                  this.cookTime = 0;
                  this.totalCookTime = this.getTotalCookTime(this.inventory[0]);
                  this.finishCooking();
                  var2 = true;
               }
            } else {
               this.cookTime = 0;
            }
         } else if (!this.hasFuel() && this.cookTime > 0) {
            this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.totalCookTime);
         }

         if (var1 != this.hasFuel()) {
            var2 = true;
            FurnaceBlock.updateLitState(this.hasFuel(), this.world, this.pos);
         }
      }

      if (var2) {
         this.markDirty();
      }
   }

   public int getTotalCookTime(ItemStack stack) {
      return 200;
   }

   private boolean canCook() {
      if (this.inventory[0] == null) {
         return false;
      } else {
         ItemStack var1 = SmeltingManager.getInstance().getResult(this.inventory[0]);
         if (var1 == null) {
            return false;
         } else if (this.inventory[2] == null) {
            return true;
         } else if (!this.inventory[2].matchesItem(var1)) {
            return false;
         } else if (this.inventory[2].size < this.getMaxStackSize() && this.inventory[2].size < this.inventory[2].getMaxSize()) {
            return true;
         } else {
            return this.inventory[2].size < var1.getMaxSize();
         }
      }
   }

   public void finishCooking() {
      if (this.canCook()) {
         ItemStack var1 = SmeltingManager.getInstance().getResult(this.inventory[0]);
         if (this.inventory[2] == null) {
            this.inventory[2] = var1.copy();
         } else if (this.inventory[2].getItem() == var1.getItem()) {
            ++this.inventory[2].size;
         }

         if (this.inventory[0].getItem() == Item.byBlock(Blocks.SPONGE)
            && this.inventory[0].getMetadata() == 1
            && this.inventory[1] != null
            && this.inventory[1].getItem() == Items.BUCKET) {
            this.inventory[1] = new ItemStack(Items.WATER_BUCKET);
         }

         --this.inventory[0].size;
         if (this.inventory[0].size <= 0) {
            this.inventory[0] = null;
         }
      }
   }

   public static int getFuelTime(ItemStack stack) {
      if (stack == null) {
         return 0;
      } else {
         Item var1 = stack.getItem();
         if (var1 instanceof BlockItem && Block.byItem(var1) != Blocks.AIR) {
            Block var2 = Block.byItem(var1);
            if (var2 == Blocks.WOODEN_SLAB) {
               return 150;
            }

            if (var2.getMaterial() == Material.WOOD) {
               return 300;
            }

            if (var2 == Blocks.COAL_BLOCK) {
               return 16000;
            }
         }

         if (var1 instanceof ToolItem && ((ToolItem)var1).getMaterialAsString().equals("WOOD")) {
            return 200;
         } else if (var1 instanceof SwordItem && ((SwordItem)var1).getToolMaterial().equals("WOOD")) {
            return 200;
         } else if (var1 instanceof HoeItem && ((HoeItem)var1).getAsString().equals("WOOD")) {
            return 200;
         } else if (var1 == Items.STICK) {
            return 100;
         } else if (var1 == Items.COAL) {
            return 1600;
         } else if (var1 == Items.LAVA_BUCKET) {
            return 20000;
         } else if (var1 == Item.byBlock(Blocks.SAPLING)) {
            return 100;
         } else {
            return var1 == Items.BLAZE_ROD ? 2400 : 0;
         }
      }
   }

   public static boolean isFuel(ItemStack stack) {
      return getFuelTime(stack) > 0;
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
      if (slot == 2) {
         return false;
      } else if (slot != 1) {
         return true;
      } else {
         return isFuel(stack) || FurnaceFuelSlot.isBucket(stack);
      }
   }

   @Override
   public int[] getSlots(Direction side) {
      if (side == Direction.DOWN) {
         return INVENTORY_SLOTS_BOTTOM;
      } else {
         return side == Direction.UP ? INVENTORY_SLOTS_TOP : INVENTORY_SLOTS_SIDES;
      }
   }

   @Override
   public boolean canHopperAddStack(int slot, ItemStack stack, Direction side) {
      return this.canSetStack(slot, stack);
   }

   @Override
   public boolean canHopperRemoveStack(int slot, ItemStack stack, Direction side) {
      if (side == Direction.DOWN && slot == 1) {
         Item var4 = stack.getItem();
         if (var4 != Items.WATER_BUCKET || var4 != Items.BUCKET) {
            return false;
         }
      }

      return true;
   }

   @Override
   public String getMenuType() {
      return "minecraft:furnace";
   }

   @Override
   public InventoryMenu createMenu(PlayerInventory playerInventory, PlayerEntity player) {
      return new FurnaceMenu(playerInventory, this);
   }

   @Override
   public int getData(int id) {
      switch(id) {
         case 0:
            return this.fuelTime;
         case 1:
            return this.totalFuelTime;
         case 2:
            return this.cookTime;
         case 3:
            return this.totalCookTime;
         default:
            return 0;
      }
   }

   @Override
   public void setData(int id, int value) {
      switch(id) {
         case 0:
            this.fuelTime = value;
            break;
         case 1:
            this.totalFuelTime = value;
            break;
         case 2:
            this.cookTime = value;
            break;
         case 3:
            this.totalCookTime = value;
      }
   }

   @Override
   public int getDataCount() {
      return 4;
   }

   @Override
   public void clear() {
      for(int var1 = 0; var1 < this.inventory.length; ++var1) {
         this.inventory[var1] = null;
      }
   }
}
