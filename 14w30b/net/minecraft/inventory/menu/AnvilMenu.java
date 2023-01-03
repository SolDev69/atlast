package net.minecraft.inventory.menu;

import java.util.Map;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ResultInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilMenu extends InventoryMenu {
   private static final Logger LOGGER = LogManager.getLogger();
   private Inventory resultInventory = new ResultInventory();
   private Inventory inputInventory = new SimpleInventory("Repair", true, 2) {
      @Override
      public void markDirty() {
         super.markDirty();
         AnvilMenu.this.onContentChanged(this);
      }
   };
   private World world;
   private BlockPos pos;
   public int repairCost;
   private int minRepairItemCount;
   private String itemName;
   private final PlayerEntity player;

   @Environment(EnvType.CLIENT)
   public AnvilMenu(PlayerInventory playerInventory, World world, PlayerEntity player) {
      this(playerInventory, world, BlockPos.ORIGIN, player);
   }

   public AnvilMenu(PlayerInventory playerInventory, World world, BlockPos pos, PlayerEntity player) {
      this.pos = pos;
      this.world = world;
      this.player = player;
      this.addSlot(new InventorySlot(this.inputInventory, 0, 27, 47));
      this.addSlot(new InventorySlot(this.inputInventory, 1, 76, 47));
      this.addSlot(
         new InventorySlot(this.resultInventory, 2, 134, 47) {
            @Override
            public boolean canSetStack(ItemStack stack) {
               return false;
            }
   
            @Override
            public boolean canPickUp(PlayerEntity playerEntity) {
               return (playerEntity.abilities.creativeMode || playerEntity.xpLevel >= AnvilMenu.this.repairCost)
                  && AnvilMenu.this.repairCost > 0
                  && this.hasStack();
            }
   
            @Override
            public void onStackRemovedByPlayer(PlayerEntity player, ItemStack stack) {
               if (!player.abilities.creativeMode) {
                  player.addXp(-AnvilMenu.this.repairCost);
               }
   
               AnvilMenu.this.inputInventory.setStack(0, null);
               if (AnvilMenu.this.minRepairItemCount > 0) {
                  ItemStack var3 = AnvilMenu.this.inputInventory.getStack(1);
                  if (var3 != null && var3.size > AnvilMenu.this.minRepairItemCount) {
                     var3.size -= AnvilMenu.this.minRepairItemCount;
                     AnvilMenu.this.inputInventory.setStack(1, var3);
                  } else {
                     AnvilMenu.this.inputInventory.setStack(1, null);
                  }
               } else {
                  AnvilMenu.this.inputInventory.setStack(1, null);
               }
   
               AnvilMenu.this.repairCost = 0;
               BlockState var5 = world.getBlockState(pos);
               if (!player.abilities.creativeMode && !world.isClient && var5.getBlock() == Blocks.ANVIL && player.getRandom().nextFloat() < 0.12F) {
                  int var4 = var5.get(AnvilBlock.DAMAGE);
                  if (++var4 > 2) {
                     world.removeBlock(pos);
                     world.doEvent(1020, pos, 0);
                  } else {
                     world.setBlockState(pos, var5.set(AnvilBlock.DAMAGE, var4), 2);
                     world.doEvent(1021, pos, 0);
                  }
               } else if (!world.isClient) {
                  world.doEvent(1021, pos, 0);
               }
            }
         }
      );

      for(int var5 = 0; var5 < 3; ++var5) {
         for(int var6 = 0; var6 < 9; ++var6) {
            this.addSlot(new InventorySlot(playerInventory, var6 + var5 * 9 + 9, 8 + var6 * 18, 84 + var5 * 18));
         }
      }

      for(int var7 = 0; var7 < 9; ++var7) {
         this.addSlot(new InventorySlot(playerInventory, var7, 8 + var7 * 18, 142));
      }
   }

   @Override
   public void onContentChanged(Inventory inventory) {
      super.onContentChanged(inventory);
      if (inventory == this.inputInventory) {
         this.updateResult();
      }
   }

   public void updateResult() {
      boolean var1 = false;
      boolean var2 = true;
      boolean var3 = true;
      boolean var4 = true;
      boolean var5 = true;
      boolean var6 = true;
      boolean var7 = true;
      ItemStack var8 = this.inputInventory.getStack(0);
      this.repairCost = 1;
      int var9 = 0;
      int var10 = 0;
      byte var11 = 0;
      if (var8 == null) {
         this.resultInventory.setStack(0, null);
         this.repairCost = 0;
      } else {
         ItemStack var12 = var8.copy();
         ItemStack var13 = this.inputInventory.getStack(1);
         Map var14 = EnchantmentHelper.getEnchantments(var12);
         boolean var15 = false;
         var10 += var8.getRepairCost() + (var13 == null ? 0 : var13.getRepairCost());
         this.minRepairItemCount = 0;
         if (var13 != null) {
            var15 = var13.getItem() == Items.ENCHANTED_BOOK && Items.ENCHANTED_BOOK.getStoredEnchantments(var13).size() > 0;
            if (var12.isDamageable() && var12.getItem().isReparable(var8, var13)) {
               int var28 = Math.min(var12.getDamage(), var12.getMaxDamage() / 4);
               if (var28 <= 0) {
                  this.resultInventory.setStack(0, null);
                  this.repairCost = 0;
                  return;
               }

               int var32;
               for(var32 = 0; var28 > 0 && var32 < var13.size; ++var32) {
                  int var34 = var12.getDamage() - var28;
                  var12.setDamage(var34);
                  ++var9;
                  var28 = Math.min(var12.getDamage(), var12.getMaxDamage() / 4);
               }

               this.minRepairItemCount = var32;
            } else {
               if (!var15 && (var12.getItem() != var13.getItem() || !var12.isDamageable())) {
                  this.resultInventory.setStack(0, null);
                  this.repairCost = 0;
                  return;
               }

               if (var12.isDamageable() && !var15) {
                  int var16 = var8.getMaxDamage() - var8.getDamage();
                  int var17 = var13.getMaxDamage() - var13.getDamage();
                  int var18 = var17 + var12.getMaxDamage() * 12 / 100;
                  int var19 = var16 + var18;
                  int var20 = var12.getMaxDamage() - var19;
                  if (var20 < 0) {
                     var20 = 0;
                  }

                  if (var20 < var12.getMetadata()) {
                     var12.setDamage(var20);
                     var9 += 2;
                  }
               }

               Map var27 = EnchantmentHelper.getEnchantments(var13);

               for(int var33 : var27.keySet()) {
                  Enchantment var35 = Enchantment.byRawId(var33);
                  if (var35 != null) {
                     int var36 = var14.containsKey(var33) ? var14.get(var33) : 0;
                     int var21 = var27.get(var33);
                     var21 = var36 == var21 ? ++var21 : Math.max(var21, var36);
                     boolean var22 = var35.isValidTarget(var8);
                     if (this.player.abilities.creativeMode || var8.getItem() == Items.ENCHANTED_BOOK) {
                        var22 = true;
                     }

                     for(int var24 : var14.keySet()) {
                        if (var24 != var33 && !var35.checkCompatibility(Enchantment.byRawId(var24))) {
                           var22 = false;
                           ++var9;
                        }
                     }

                     if (var22) {
                        if (var21 > var35.getMaxLevel()) {
                           var21 = var35.getMaxLevel();
                        }

                        var14.put(var33, var21);
                        int var39 = 0;
                        switch(var35.getType()) {
                           case 1:
                              var39 = 8;
                              break;
                           case 2:
                              var39 = 4;
                           case 3:
                           case 4:
                           case 6:
                           case 7:
                           case 8:
                           case 9:
                           default:
                              break;
                           case 5:
                              var39 = 2;
                              break;
                           case 10:
                              var39 = 1;
                        }

                        if (var15) {
                           var39 = Math.max(1, var39 / 2);
                        }

                        var9 += var39 * var21;
                     }
                  }
               }
            }
         }

         if (StringUtils.isBlank(this.itemName)) {
            if (var8.hasCustomHoverName()) {
               var11 = 1;
               var9 += var11;
               var12.resetHoverName();
            }
         } else if (!this.itemName.equals(var8.getHoverName())) {
            var11 = 1;
            var9 += var11;
            var12.setHoverName(this.itemName);
         }

         this.repairCost = var10 + var9;
         if (var9 <= 0) {
            var12 = null;
         }

         if (var11 == var9 && var11 > 0 && this.repairCost >= 40) {
            this.repairCost = 39;
         }

         if (this.repairCost >= 40 && !this.player.abilities.creativeMode) {
            var12 = null;
         }

         if (var12 != null) {
            int var29 = var12.getRepairCost();
            if (var13 != null && var29 < var13.getRepairCost()) {
               var29 = var13.getRepairCost();
            }

            var29 = var29 * 2 + 1;
            var12.setRepairCost(var29);
            EnchantmentHelper.setEnchantments(var14, var12);
         }

         this.resultInventory.setStack(0, var12);
         this.updateListeners();
      }
   }

   @Override
   public void addListener(InventoryMenuListener listener) {
      super.addListener(listener);
      listener.onDataChanged(this, 0, this.repairCost);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void setData(int id, int value) {
      if (id == 0) {
         this.repairCost = value;
      }
   }

   @Override
   public void close(PlayerEntity player) {
      super.close(player);
      if (!this.world.isClient) {
         for(int var2 = 0; var2 < this.inputInventory.getSize(); ++var2) {
            ItemStack var3 = this.inputInventory.removeStackQuietly(var2);
            if (var3 != null) {
               player.dropItem(var3, false);
            }
         }
      }
   }

   @Override
   public boolean isValid(PlayerEntity player) {
      if (this.world.getBlockState(this.pos).getBlock() != Blocks.ANVIL) {
         return false;
      } else {
         return !(player.getSquaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
      }
   }

   @Override
   public ItemStack quickMoveStack(PlayerEntity player, int id) {
      ItemStack var3 = null;
      InventorySlot var4 = (InventorySlot)this.slots.get(id);
      if (var4 != null && var4.hasStack()) {
         ItemStack var5 = var4.getStack();
         var3 = var5.copy();
         if (id == 2) {
            if (!this.moveStack(var5, 3, 39, true)) {
               return null;
            }

            var4.onQuickMoved(var5, var3);
         } else if (id != 0 && id != 1) {
            if (id >= 3 && id < 39 && !this.moveStack(var5, 0, 2, false)) {
               return null;
            }
         } else if (!this.moveStack(var5, 3, 39, false)) {
            return null;
         }

         if (var5.size == 0) {
            var4.setStack(null);
         } else {
            var4.markDirty();
         }

         if (var5.size == var3.size) {
            return null;
         }

         var4.onStackRemovedByPlayer(player, var5);
      }

      return var3;
   }

   public void setItemName(String name) {
      this.itemName = name;
      if (this.getSlot(2).hasStack()) {
         ItemStack var2 = this.getSlot(2).getStack();
         if (StringUtils.isBlank(name)) {
            var2.resetHoverName();
         } else {
            var2.setHoverName(this.itemName);
         }
      }

      this.updateResult();
   }
}
