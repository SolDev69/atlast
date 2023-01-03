package net.minecraft.inventory.menu;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentEntry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EnchantingTableMenu extends InventoryMenu {
   public Inventory enchantingTable = new SimpleInventory("Enchant", true, 2) {
      @Override
      public int getMaxStackSize() {
         return 64;
      }

      @Override
      public void markDirty() {
         super.markDirty();
         EnchantingTableMenu.this.onContentChanged(this);
      }
   };
   private World world;
   private BlockPos pos;
   private Random random = new Random();
   public int seed;
   public int[] enchantingCosts = new int[3];
   public int[] enchantmentClues = new int[]{-1, -1, -1};

   @Environment(EnvType.CLIENT)
   public EnchantingTableMenu(PlayerInventory playerInventory, World world) {
      this(playerInventory, world, BlockPos.ORIGIN);
   }

   public EnchantingTableMenu(PlayerInventory playerInventory, World world, BlockPos pos) {
      this.world = world;
      this.pos = pos;
      this.seed = playerInventory.player.getEnchantingSeed();
      this.addSlot(new InventorySlot(this.enchantingTable, 0, 15, 47) {
         @Override
         public boolean canSetStack(ItemStack stack) {
            return true;
         }

         @Override
         public int getMaxStackSize() {
            return 1;
         }
      });
      this.addSlot(new InventorySlot(this.enchantingTable, 1, 35, 47) {
         @Override
         public boolean canSetStack(ItemStack stack) {
            return stack.getItem() == Items.DYE && DyeColor.byMetadata(stack.getMetadata()) == DyeColor.BLUE;
         }
      });

      for(int var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 9; ++var5) {
            this.addSlot(new InventorySlot(playerInventory, var5 + var4 * 9 + 9, 8 + var5 * 18, 84 + var4 * 18));
         }
      }

      for(int var6 = 0; var6 < 9; ++var6) {
         this.addSlot(new InventorySlot(playerInventory, var6, 8 + var6 * 18, 142));
      }
   }

   @Override
   public void addListener(InventoryMenuListener listener) {
      super.addListener(listener);
      listener.onDataChanged(this, 0, this.enchantingCosts[0]);
      listener.onDataChanged(this, 1, this.enchantingCosts[1]);
      listener.onDataChanged(this, 2, this.enchantingCosts[2]);
      listener.onDataChanged(this, 3, this.seed & -16);
      listener.onDataChanged(this, 4, this.enchantmentClues[0]);
      listener.onDataChanged(this, 5, this.enchantmentClues[1]);
      listener.onDataChanged(this, 6, this.enchantmentClues[2]);
   }

   @Override
   public void updateListeners() {
      super.updateListeners();

      for(int var1 = 0; var1 < this.listeners.size(); ++var1) {
         InventoryMenuListener var2 = (InventoryMenuListener)this.listeners.get(var1);
         var2.onDataChanged(this, 0, this.enchantingCosts[0]);
         var2.onDataChanged(this, 1, this.enchantingCosts[1]);
         var2.onDataChanged(this, 2, this.enchantingCosts[2]);
         var2.onDataChanged(this, 3, this.seed & -16);
         var2.onDataChanged(this, 4, this.enchantmentClues[0]);
         var2.onDataChanged(this, 5, this.enchantmentClues[1]);
         var2.onDataChanged(this, 6, this.enchantmentClues[2]);
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void setData(int id, int value) {
      if (id >= 0 && id <= 2) {
         this.enchantingCosts[id] = value;
      } else if (id == 3) {
         this.seed = value;
      } else if (id >= 4 && id <= 6) {
         this.enchantmentClues[id - 4] = value;
      } else {
         super.setData(id, value);
      }
   }

   @Override
   public void onContentChanged(Inventory inventory) {
      if (inventory == this.enchantingTable) {
         ItemStack var2 = inventory.getStack(0);
         if (var2 != null && var2.isEnchantable()) {
            if (!this.world.isClient) {
               int var7 = 0;

               for(int var4 = -1; var4 <= 1; ++var4) {
                  for(int var5 = -1; var5 <= 1; ++var5) {
                     if ((var4 != 0 || var5 != 0) && this.world.isAir(this.pos.add(var5, 0, var4)) && this.world.isAir(this.pos.add(var5, 1, var4))) {
                        if (this.world.getBlockState(this.pos.add(var5 * 2, 0, var4 * 2)).getBlock() == Blocks.BOOKSHELF) {
                           ++var7;
                        }

                        if (this.world.getBlockState(this.pos.add(var5 * 2, 1, var4 * 2)).getBlock() == Blocks.BOOKSHELF) {
                           ++var7;
                        }

                        if (var5 != 0 && var4 != 0) {
                           if (this.world.getBlockState(this.pos.add(var5 * 2, 0, var4)).getBlock() == Blocks.BOOKSHELF) {
                              ++var7;
                           }

                           if (this.world.getBlockState(this.pos.add(var5 * 2, 1, var4)).getBlock() == Blocks.BOOKSHELF) {
                              ++var7;
                           }

                           if (this.world.getBlockState(this.pos.add(var5, 0, var4 * 2)).getBlock() == Blocks.BOOKSHELF) {
                              ++var7;
                           }

                           if (this.world.getBlockState(this.pos.add(var5, 1, var4 * 2)).getBlock() == Blocks.BOOKSHELF) {
                              ++var7;
                           }
                        }
                     }
                  }
               }

               this.random.setSeed((long)this.seed);

               for(int var8 = 0; var8 < 3; ++var8) {
                  this.enchantingCosts[var8] = EnchantmentHelper.getRequiredXpLevel(this.random, var8, var7, var2);
                  this.enchantmentClues[var8] = -1;
                  if (this.enchantingCosts[var8] < var8 + 1) {
                     this.enchantingCosts[var8] = 0;
                  }
               }

               for(int var9 = 0; var9 < 3; ++var9) {
                  if (this.enchantingCosts[var9] > 0) {
                     List var10 = this.getEnchantments(var2, var9, this.enchantingCosts[var9]);
                     if (var10 != null && !var10.isEmpty()) {
                        EnchantmentEntry var6 = (EnchantmentEntry)var10.get(this.random.nextInt(var10.size()));
                        this.enchantmentClues[var9] = var6.enchantment.id | var6.level << 8;
                     }
                  }
               }

               this.updateListeners();
            }
         } else {
            for(int var3 = 0; var3 < 3; ++var3) {
               this.enchantingCosts[var3] = 0;
               this.enchantmentClues[var3] = -1;
            }
         }
      }
   }

   @Override
   public boolean onButtonClick(PlayerEntity player, int id) {
      ItemStack var3 = this.enchantingTable.getStack(0);
      ItemStack var4 = this.enchantingTable.getStack(1);
      int var5 = id + 1;
      if ((var4 == null || var4.size < var5) && !player.abilities.creativeMode) {
         return false;
      } else if (this.enchantingCosts[id] > 0
         && var3 != null
         && (player.xpLevel >= var5 && player.xpLevel >= this.enchantingCosts[id] || player.abilities.creativeMode)) {
         if (!this.world.isClient) {
            List var6 = this.getEnchantments(var3, id, this.enchantingCosts[id]);
            boolean var7 = var3.getItem() == Items.BOOK;
            if (var6 != null) {
               player.m_92coykull(var5);
               if (var7) {
                  var3.setItem(Items.ENCHANTED_BOOK);
               }

               for(int var8 = 0; var8 < var6.size(); ++var8) {
                  EnchantmentEntry var9 = (EnchantmentEntry)var6.get(var8);
                  if (var7) {
                     Items.ENCHANTED_BOOK.addEnchantment(var3, var9);
                  } else {
                     var3.addEnchantment(var9.enchantment, var9.level);
                  }
               }

               if (!player.abilities.creativeMode) {
                  var4.size -= var5;
                  if (var4.size <= 0) {
                     this.enchantingTable.setStack(1, null);
                  }
               }

               this.enchantingTable.markDirty();
               this.seed = player.getEnchantingSeed();
               this.onContentChanged(this.enchantingTable);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private List getEnchantments(ItemStack stack, int id, int level) {
      this.random.setSeed((long)(this.seed + id));
      List var4 = EnchantmentHelper.getEnchantmentEntries(this.random, stack, level);
      if (stack.getItem() == Items.BOOK && var4 != null && var4.size() > 1) {
         var4.remove(this.random.nextInt(var4.size()));
      }

      return var4;
   }

   @Environment(EnvType.CLIENT)
   public int getLapisCount() {
      ItemStack var1 = this.enchantingTable.getStack(1);
      return var1 == null ? 0 : var1.size;
   }

   @Override
   public void close(PlayerEntity player) {
      super.close(player);
      if (!this.world.isClient) {
         for(int var2 = 0; var2 < this.enchantingTable.getSize(); ++var2) {
            ItemStack var3 = this.enchantingTable.removeStackQuietly(var2);
            if (var3 != null) {
               player.dropItem(var3, false);
            }
         }
      }
   }

   @Override
   public boolean isValid(PlayerEntity player) {
      if (this.world.getBlockState(this.pos).getBlock() != Blocks.ENCHANTING_TABLE) {
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
         if (id == 0) {
            if (!this.moveStack(var5, 2, 38, true)) {
               return null;
            }
         } else if (id == 1) {
            if (!this.moveStack(var5, 2, 38, true)) {
               return null;
            }
         } else if (var5.getItem() == Items.DYE && DyeColor.byMetadata(var5.getMetadata()) == DyeColor.BLUE) {
            if (!this.moveStack(var5, 1, 2, true)) {
               return null;
            }
         } else {
            if (((InventorySlot)this.slots.get(0)).hasStack() || !((InventorySlot)this.slots.get(0)).canSetStack(var5)) {
               return null;
            }

            if (var5.hasNbt() && var5.size == 1) {
               ((InventorySlot)this.slots.get(0)).setStack(var5.copy());
               var5.size = 0;
            } else if (var5.size >= 1) {
               ((InventorySlot)this.slots.get(0)).setStack(new ItemStack(var5.getItem(), 1, var5.getMetadata()));
               --var5.size;
            }
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
}
