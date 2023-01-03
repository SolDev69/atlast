package net.minecraft.inventory.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class InventoryMenu {
   public List stacks = Lists.newArrayList();
   public List slots = Lists.newArrayList();
   public int networkId;
   @Environment(EnvType.CLIENT)
   private short nextActionNetworkId;
   private int clickDragMode = -1;
   private int clickDragStage;
   private final Set clickDragSlots = Sets.newHashSet();
   protected List listeners = Lists.newArrayList();
   private Set restrictedPlayers = Sets.newHashSet();

   protected InventorySlot addSlot(InventorySlot invSlot) {
      invSlot.id = this.slots.size();
      this.slots.add(invSlot);
      this.stacks.add(null);
      return invSlot;
   }

   public void addListener(InventoryMenuListener listener) {
      if (this.listeners.contains(listener)) {
         throw new IllegalArgumentException("Listener already listening");
      } else {
         this.listeners.add(listener);
         listener.updateMenu(this, this.getStacks());
         this.updateListeners();
      }
   }

   @Environment(EnvType.CLIENT)
   public void removeListener(InventoryMenuListener listener) {
      this.listeners.remove(listener);
   }

   public List getStacks() {
      ArrayList var1 = Lists.newArrayList();

      for(int var2 = 0; var2 < this.slots.size(); ++var2) {
         var1.add(((InventorySlot)this.slots.get(var2)).getStack());
      }

      return var1;
   }

   public void updateListeners() {
      for(int var1 = 0; var1 < this.slots.size(); ++var1) {
         ItemStack var2 = ((InventorySlot)this.slots.get(var1)).getStack();
         ItemStack var3 = (ItemStack)this.stacks.get(var1);
         if (!ItemStack.matches(var3, var2)) {
            var3 = var2 == null ? null : var2.copy();
            this.stacks.set(var1, var3);

            for(int var4 = 0; var4 < this.listeners.size(); ++var4) {
               ((InventoryMenuListener)this.listeners.get(var4)).onSlotChanged(this, var1, var3);
            }
         }
      }
   }

   public boolean onButtonClick(PlayerEntity player, int id) {
      return false;
   }

   public InventorySlot getSlot(Inventory inventory, int slot) {
      for(int var3 = 0; var3 < this.slots.size(); ++var3) {
         InventorySlot var4 = (InventorySlot)this.slots.get(var3);
         if (var4.equals(inventory, slot)) {
            return var4;
         }
      }

      return null;
   }

   public InventorySlot getSlot(int id) {
      return (InventorySlot)this.slots.get(id);
   }

   public ItemStack quickMoveStack(PlayerEntity player, int id) {
      InventorySlot var3 = (InventorySlot)this.slots.get(id);
      return var3 != null ? var3.getStack() : null;
   }

   public ItemStack onClickSlot(int id, int clickData, int action, PlayerEntity player) {
      ItemStack var5 = null;
      PlayerInventory var6 = player.inventory;
      if (action == 5) {
         int var7 = this.clickDragStage;
         this.clickDragStage = unpackClickDragStage(clickData);
         if ((var7 != 1 || this.clickDragStage != 2) && var7 != this.clickDragStage) {
            this.dropClickDragging();
         } else if (var6.getCursorStack() == null) {
            this.dropClickDragging();
         } else if (this.clickDragStage == 0) {
            this.clickDragMode = unpackClickDragMode(clickData);
            if (isValidClickDragMode(this.clickDragMode, player)) {
               this.clickDragStage = 1;
               this.clickDragSlots.clear();
            } else {
               this.dropClickDragging();
            }
         } else if (this.clickDragStage == 1) {
            InventorySlot var8 = (InventorySlot)this.slots.get(id);
            if (var8 != null
               && canClickDragInto(var8, var6.getCursorStack(), true)
               && var8.canSetStack(var6.getCursorStack())
               && var6.getCursorStack().size > this.clickDragSlots.size()
               && this.canClickDragInto(var8)) {
               this.clickDragSlots.add(var8);
            }
         } else if (this.clickDragStage == 2) {
            if (!this.clickDragSlots.isEmpty()) {
               ItemStack var22 = var6.getCursorStack().copy();
               int var9 = var6.getCursorStack().size;

               for(InventorySlot var11 : this.clickDragSlots) {
                  if (var11 != null
                     && canClickDragInto(var11, var6.getCursorStack(), true)
                     && var11.canSetStack(var6.getCursorStack())
                     && var6.getCursorStack().size >= this.clickDragSlots.size()
                     && this.canClickDragInto(var11)) {
                     ItemStack var12 = var22.copy();
                     int var13 = var11.hasStack() ? var11.getStack().size : 0;
                     updateClickDragStackSize(this.clickDragSlots, this.clickDragMode, var12, var13);
                     if (var12.size > var12.getMaxSize()) {
                        var12.size = var12.getMaxSize();
                     }

                     if (var12.size > var11.getMaxStackSize(var12)) {
                        var12.size = var11.getMaxStackSize(var12);
                     }

                     var9 -= var12.size - var13;
                     var11.setStack(var12);
                  }
               }

               var22.size = var9;
               if (var22.size <= 0) {
                  var22 = null;
               }

               var6.setCursorStack(var22);
            }

            this.dropClickDragging();
         } else {
            this.dropClickDragging();
         }
      } else if (this.clickDragStage != 0) {
         this.dropClickDragging();
      } else if ((action == 0 || action == 1) && (clickData == 0 || clickData == 1)) {
         if (id == -999) {
            if (var6.getCursorStack() != null) {
               if (clickData == 0) {
                  player.dropItem(var6.getCursorStack(), true);
                  var6.setCursorStack(null);
               }

               if (clickData == 1) {
                  player.dropItem(var6.getCursorStack().split(1), true);
                  if (var6.getCursorStack().size == 0) {
                     var6.setCursorStack(null);
                  }
               }
            }
         } else if (action == 1) {
            if (id < 0) {
               return null;
            }

            InventorySlot var20 = (InventorySlot)this.slots.get(id);
            if (var20 != null && var20.canPickUp(player)) {
               ItemStack var27 = this.quickMoveStack(player, id);
               if (var27 != null) {
                  Item var32 = var27.getItem();
                  var5 = var27.copy();
                  if (var20.getStack() != null && var20.getStack().getItem() == var32) {
                     this.quickMoveStack(id, clickData, true, player);
                  }
               }
            }
         } else {
            if (id < 0) {
               return null;
            }

            InventorySlot var21 = (InventorySlot)this.slots.get(id);
            if (var21 != null) {
               ItemStack var28 = var21.getStack();
               ItemStack var33 = var6.getCursorStack();
               if (var28 != null) {
                  var5 = var28.copy();
               }

               if (var28 == null) {
                  if (var33 != null && var21.canSetStack(var33)) {
                     int var39 = clickData == 0 ? var33.size : 1;
                     if (var39 > var21.getMaxStackSize(var33)) {
                        var39 = var21.getMaxStackSize(var33);
                     }

                     if (var33.size >= var39) {
                        var21.setStack(var33.split(var39));
                     }

                     if (var33.size == 0) {
                        var6.setCursorStack(null);
                     }
                  }
               } else if (var21.canPickUp(player)) {
                  if (var33 == null) {
                     int var38 = clickData == 0 ? var28.size : (var28.size + 1) / 2;
                     ItemStack var42 = var21.removeStack(var38);
                     var6.setCursorStack(var42);
                     if (var28.size == 0) {
                        var21.setStack(null);
                     }

                     var21.onStackRemovedByPlayer(player, var6.getCursorStack());
                  } else if (var21.canSetStack(var33)) {
                     if (var28.getItem() == var33.getItem() && var28.getMetadata() == var33.getMetadata() && ItemStack.matchesNbt(var28, var33)) {
                        int var37 = clickData == 0 ? var33.size : 1;
                        if (var37 > var21.getMaxStackSize(var33) - var28.size) {
                           var37 = var21.getMaxStackSize(var33) - var28.size;
                        }

                        if (var37 > var33.getMaxSize() - var28.size) {
                           var37 = var33.getMaxSize() - var28.size;
                        }

                        var33.split(var37);
                        if (var33.size == 0) {
                           var6.setCursorStack(null);
                        }

                        var28.size += var37;
                     } else if (var33.size <= var21.getMaxStackSize(var33)) {
                        var21.setStack(var33);
                        var6.setCursorStack(var28);
                     }
                  } else if (var28.getItem() == var33.getItem()
                     && var33.getMaxSize() > 1
                     && (!var28.isItemStackable() || var28.getMetadata() == var33.getMetadata())
                     && ItemStack.matchesNbt(var28, var33)) {
                     int var36 = var28.size;
                     if (var36 > 0 && var36 + var33.size <= var33.getMaxSize()) {
                        var33.size += var36;
                        var28 = var21.removeStack(var36);
                        if (var28.size == 0) {
                           var21.setStack(null);
                        }

                        var21.onStackRemovedByPlayer(player, var6.getCursorStack());
                     }
                  }
               }

               var21.markDirty();
            }
         }
      } else if (action == 2 && clickData >= 0 && clickData < 9) {
         InventorySlot var19 = (InventorySlot)this.slots.get(id);
         if (var19.canPickUp(player)) {
            ItemStack var26 = var6.getStack(clickData);
            boolean var31 = var26 == null || var19.inventory == var6 && var19.canSetStack(var26);
            int var35 = -1;
            if (!var31) {
               var35 = var6.getEmptySlot();
               var31 |= var35 > -1;
            }

            if (var19.hasStack() && var31) {
               ItemStack var41 = var19.getStack();
               var6.setStack(clickData, var41.copy());
               if ((var19.inventory != var6 || !var19.canSetStack(var26)) && var26 != null) {
                  if (var35 > -1) {
                     var6.insertStack(var26);
                     var19.removeStack(var41.size);
                     var19.setStack(null);
                     var19.onStackRemovedByPlayer(player, var41);
                  }
               } else {
                  var19.removeStack(var41.size);
                  var19.setStack(var26);
                  var19.onStackRemovedByPlayer(player, var41);
               }
            } else if (!var19.hasStack() && var26 != null && var19.canSetStack(var26)) {
               var6.setStack(clickData, null);
               var19.setStack(var26);
            }
         }
      } else if (action == 3 && player.abilities.creativeMode && var6.getCursorStack() == null && id >= 0) {
         InventorySlot var18 = (InventorySlot)this.slots.get(id);
         if (var18 != null && var18.hasStack()) {
            ItemStack var25 = var18.getStack().copy();
            var25.size = var25.getMaxSize();
            var6.setCursorStack(var25);
         }
      } else if (action == 4 && var6.getCursorStack() == null && id >= 0) {
         InventorySlot var17 = (InventorySlot)this.slots.get(id);
         if (var17 != null && var17.hasStack() && var17.canPickUp(player)) {
            ItemStack var24 = var17.removeStack(clickData == 0 ? 1 : var17.getStack().size);
            var17.onStackRemovedByPlayer(player, var24);
            player.dropItem(var24, true);
         }
      } else if (action == 6 && id >= 0) {
         InventorySlot var16 = (InventorySlot)this.slots.get(id);
         ItemStack var23 = var6.getCursorStack();
         if (var23 != null && (var16 == null || !var16.hasStack() || !var16.canPickUp(player))) {
            int var30 = clickData == 0 ? 0 : this.slots.size() - 1;
            int var34 = clickData == 0 ? 1 : -1;

            for(int var40 = 0; var40 < 2; ++var40) {
               for(int var43 = var30; var43 >= 0 && var43 < this.slots.size() && var23.size < var23.getMaxSize(); var43 += var34) {
                  InventorySlot var44 = (InventorySlot)this.slots.get(var43);
                  if (var44.hasStack()
                     && canClickDragInto(var44, var23, true)
                     && var44.canPickUp(player)
                     && this.canRemoveForPickupAll(var23, var44)
                     && (var40 != 0 || var44.getStack().size != var44.getStack().getMaxSize())) {
                     int var14 = Math.min(var23.getMaxSize() - var23.size, var44.getStack().size);
                     ItemStack var15 = var44.removeStack(var14);
                     var23.size += var14;
                     if (var15.size <= 0) {
                        var44.setStack(null);
                     }

                     var44.onStackRemovedByPlayer(player, var15);
                  }
               }
            }
         }

         this.updateListeners();
      }

      return var5;
   }

   public boolean canRemoveForPickupAll(ItemStack stack, InventorySlot invSlot) {
      return true;
   }

   protected void quickMoveStack(int id, int clickData, boolean bl, PlayerEntity player) {
      this.onClickSlot(id, clickData, 1, player);
   }

   public void close(PlayerEntity player) {
      PlayerInventory var2 = player.inventory;
      if (var2.getCursorStack() != null) {
         player.dropItem(var2.getCursorStack(), false);
         var2.setCursorStack(null);
      }
   }

   public void onContentChanged(Inventory inventory) {
      this.updateListeners();
   }

   public void setStack(int id, ItemStack stack) {
      this.getSlot(id).setStack(stack);
   }

   @Environment(EnvType.CLIENT)
   public void setStacks(ItemStack[] stacks) {
      for(int var2 = 0; var2 < stacks.length; ++var2) {
         this.getSlot(var2).setStack(stacks[var2]);
      }
   }

   @Environment(EnvType.CLIENT)
   public void setData(int id, int value) {
   }

   @Environment(EnvType.CLIENT)
   public short getNextActionNetworkId(PlayerInventory inventory) {
      ++this.nextActionNetworkId;
      return this.nextActionNetworkId;
   }

   public boolean isSynced(PlayerEntity player) {
      return !this.restrictedPlayers.contains(player);
   }

   public void setSynced(PlayerEntity player, boolean synced) {
      if (synced) {
         this.restrictedPlayers.remove(player);
      } else {
         this.restrictedPlayers.add(player);
      }
   }

   public abstract boolean isValid(PlayerEntity player);

   protected boolean moveStack(ItemStack stack, int startId, int endId, boolean reverse) {
      boolean var5 = false;
      int var6 = startId;
      if (reverse) {
         var6 = endId - 1;
      }

      if (stack.isStackable()) {
         while(stack.size > 0 && (!reverse && var6 < endId || reverse && var6 >= startId)) {
            InventorySlot var7 = (InventorySlot)this.slots.get(var6);
            ItemStack var8 = var7.getStack();
            if (var8 != null
               && var8.getItem() == stack.getItem()
               && (!stack.isItemStackable() || stack.getMetadata() == var8.getMetadata())
               && ItemStack.matchesNbt(stack, var8)) {
               int var9 = var8.size + stack.size;
               if (var9 <= stack.getMaxSize()) {
                  stack.size = 0;
                  var8.size = var9;
                  var7.markDirty();
                  var5 = true;
               } else if (var8.size < stack.getMaxSize()) {
                  stack.size -= stack.getMaxSize() - var8.size;
                  var8.size = stack.getMaxSize();
                  var7.markDirty();
                  var5 = true;
               }
            }

            if (reverse) {
               --var6;
            } else {
               ++var6;
            }
         }
      }

      if (stack.size > 0) {
         if (reverse) {
            var6 = endId - 1;
         } else {
            var6 = startId;
         }

         while(!reverse && var6 < endId || reverse && var6 >= startId) {
            InventorySlot var11 = (InventorySlot)this.slots.get(var6);
            ItemStack var12 = var11.getStack();
            if (var12 == null) {
               var11.setStack(stack.copy());
               var11.markDirty();
               stack.size = 0;
               var5 = true;
               break;
            }

            if (reverse) {
               --var6;
            } else {
               ++var6;
            }
         }
      }

      return var5;
   }

   public static int unpackClickDragMode(int clickData) {
      return clickData >> 2 & 3;
   }

   public static int unpackClickDragStage(int clickData) {
      return clickData & 3;
   }

   @Environment(EnvType.CLIENT)
   public static int packClickData(int clickDragStage, int clickDragMode) {
      return clickDragStage & 3 | (clickDragMode & 3) << 2;
   }

   public static boolean isValidClickDragMode(int clickDragMode, PlayerEntity player) {
      if (clickDragMode == 0) {
         return true;
      } else if (clickDragMode == 1) {
         return true;
      } else {
         return clickDragMode == 2 && player.abilities.creativeMode;
      }
   }

   protected void dropClickDragging() {
      this.clickDragStage = 0;
      this.clickDragSlots.clear();
   }

   public static boolean canClickDragInto(InventorySlot invSlot, ItemStack stack, boolean stackable) {
      boolean var3 = invSlot == null || !invSlot.hasStack();
      if (invSlot != null && invSlot.hasStack() && stack != null && stack.matchesItem(invSlot.getStack()) && ItemStack.matchesNbt(invSlot.getStack(), stack)) {
         var3 |= invSlot.getStack().size + (stackable ? 0 : stack.size) <= stack.getMaxSize();
      }

      return var3;
   }

   public static void updateClickDragStackSize(Set slots, int clickDragMode, ItemStack stack, int stackSize) {
      switch(clickDragMode) {
         case 0:
            stack.size = MathHelper.floor((float)stack.size / (float)slots.size());
            break;
         case 1:
            stack.size = 1;
            break;
         case 2:
            stack.size = stack.getItem().getMaxStackSize();
      }

      stack.size += stackSize;
   }

   public boolean canClickDragInto(InventorySlot invSlot) {
      return true;
   }

   public static int getAnalogOutput(BlockEntity blockEntity) {
      return blockEntity instanceof Inventory ? getAnalogOutput((Inventory)blockEntity) : 0;
   }

   public static int getAnalogOutput(Inventory inventory) {
      if (inventory == null) {
         return 0;
      } else {
         int var1 = 0;
         float var2 = 0.0F;

         for(int var3 = 0; var3 < inventory.getSize(); ++var3) {
            ItemStack var4 = inventory.getStack(var3);
            if (var4 != null) {
               var2 += (float)var4.size / (float)Math.min(inventory.getMaxStackSize(), var4.getMaxSize());
               ++var1;
            }
         }

         var2 /= (float)inventory.getSize();
         return MathHelper.floor(var2 * 14.0F) + (var1 > 0 ? 1 : 0);
      }
   }
}
