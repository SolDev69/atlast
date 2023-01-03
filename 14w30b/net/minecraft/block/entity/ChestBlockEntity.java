package net.minecraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.menu.ChestMenu;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class ChestBlockEntity extends InventoryBlockEntity implements Tickable, Inventory {
   private ItemStack[] inventory = new ItemStack[27];
   public boolean isDoubleChest;
   public ChestBlockEntity northNeighbor;
   public ChestBlockEntity eastNeighbor;
   public ChestBlockEntity westNeighbor;
   public ChestBlockEntity southNeighbor;
   public float animationProgress;
   public float lastAnimationProgress;
   public int viewerCount;
   private int ticks;
   private int chestType;
   private String customName;

   public ChestBlockEntity() {
      this.chestType = -1;
   }

   @Environment(EnvType.CLIENT)
   public ChestBlockEntity(int type) {
      this.chestType = type;
   }

   @Override
   public int getSize() {
      return 27;
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
            this.markDirty();
            return var4;
         } else {
            ItemStack var3 = this.inventory[slot].split(amount);
            if (this.inventory[slot].size == 0) {
               this.inventory[slot] = null;
            }

            this.markDirty();
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

      this.markDirty();
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.chest";
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
      if (nbt.isType("CustomName", 8)) {
         this.customName = nbt.getString("CustomName");
      }

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         NbtCompound var4 = var2.getCompound(var3);
         int var5 = var4.getByte("Slot") & 255;
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
      if (this.hasCustomName()) {
         nbt.putString("CustomName", this.customName);
      }
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
   public void clearBlockCache() {
      super.clearBlockCache();
      this.isDoubleChest = false;
   }

   private void combineWithNeighbor(ChestBlockEntity neighborChest, Direction dir) {
      if (neighborChest.isRemoved()) {
         this.isDoubleChest = false;
      } else if (this.isDoubleChest) {
         switch(dir) {
            case NORTH:
               if (this.northNeighbor != neighborChest) {
                  this.isDoubleChest = false;
               }
               break;
            case SOUTH:
               if (this.southNeighbor != neighborChest) {
                  this.isDoubleChest = false;
               }
               break;
            case EAST:
               if (this.eastNeighbor != neighborChest) {
                  this.isDoubleChest = false;
               }
               break;
            case WEST:
               if (this.westNeighbor != neighborChest) {
                  this.isDoubleChest = false;
               }
         }
      }
   }

   public void updateShape() {
      if (!this.isDoubleChest) {
         this.isDoubleChest = true;
         this.westNeighbor = this.findNeighborChest(Direction.WEST);
         this.eastNeighbor = this.findNeighborChest(Direction.EAST);
         this.northNeighbor = this.findNeighborChest(Direction.NORTH);
         this.southNeighbor = this.findNeighborChest(Direction.SOUTH);
      }
   }

   protected ChestBlockEntity findNeighborChest(Direction dir) {
      BlockPos var2 = this.pos.offset(dir);
      if (this.isChestOfSameType(var2)) {
         BlockEntity var3 = this.world.getBlockEntity(var2);
         if (var3 instanceof ChestBlockEntity) {
            ChestBlockEntity var4 = (ChestBlockEntity)var3;
            var4.combineWithNeighbor(this, dir.getOpposite());
            return var4;
         }
      }

      return null;
   }

   private boolean isChestOfSameType(BlockPos pos) {
      if (this.world == null) {
         return false;
      } else {
         Block var2 = this.world.getBlockState(pos).getBlock();
         return var2 instanceof ChestBlock && ((ChestBlock)var2).type == this.getChestType();
      }
   }

   @Override
   public void tick() {
      this.updateShape();
      int var1 = this.pos.getX();
      int var2 = this.pos.getY();
      int var3 = this.pos.getZ();
      ++this.ticks;
      if (!this.world.isClient && this.viewerCount != 0 && (this.ticks + var1 + var2 + var3) % 200 == 0) {
         this.viewerCount = 0;
         float var4 = 5.0F;

         for(PlayerEntity var7 : this.world
            .getEntities(
               PlayerEntity.class,
               new Box(
                  (double)((float)var1 - var4),
                  (double)((float)var2 - var4),
                  (double)((float)var3 - var4),
                  (double)((float)(var1 + 1) + var4),
                  (double)((float)(var2 + 1) + var4),
                  (double)((float)(var3 + 1) + var4)
               )
            )) {
            if (var7.menu instanceof ChestMenu) {
               Inventory var8 = ((ChestMenu)var7.menu).getChest();
               if (var8 == this || var8 instanceof DoubleInventory && ((DoubleInventory)var8).contains(this)) {
                  ++this.viewerCount;
               }
            }
         }
      }

      this.lastAnimationProgress = this.animationProgress;
      float var11 = 0.1F;
      if (this.viewerCount > 0 && this.animationProgress == 0.0F && this.northNeighbor == null && this.westNeighbor == null) {
         double var12 = (double)var1 + 0.5;
         double var15 = (double)var3 + 0.5;
         if (this.southNeighbor != null) {
            var15 += 0.5;
         }

         if (this.eastNeighbor != null) {
            var12 += 0.5;
         }

         this.world.playSound(var12, (double)var2 + 0.5, var15, "random.chestopen", 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
      }

      if (this.viewerCount == 0 && this.animationProgress > 0.0F || this.viewerCount > 0 && this.animationProgress < 1.0F) {
         float var13 = this.animationProgress;
         if (this.viewerCount > 0) {
            this.animationProgress += var11;
         } else {
            this.animationProgress -= var11;
         }

         if (this.animationProgress > 1.0F) {
            this.animationProgress = 1.0F;
         }

         float var14 = 0.5F;
         if (this.animationProgress < var14 && var13 >= var14 && this.northNeighbor == null && this.westNeighbor == null) {
            double var16 = (double)var1 + 0.5;
            double var9 = (double)var3 + 0.5;
            if (this.southNeighbor != null) {
               var9 += 0.5;
            }

            if (this.eastNeighbor != null) {
               var16 += 0.5;
            }

            this.world.playSound(var16, (double)var2 + 0.5, var9, "random.chestclosed", 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
         }

         if (this.animationProgress < 0.0F) {
            this.animationProgress = 0.0F;
         }
      }
   }

   @Override
   public boolean doEvent(int type, int data) {
      if (type == 1) {
         this.viewerCount = data;
         return true;
      } else {
         return super.doEvent(type, data);
      }
   }

   @Override
   public void onOpen(PlayerEntity player) {
      if (!player.isSpectator()) {
         if (this.viewerCount < 0) {
            this.viewerCount = 0;
         }

         ++this.viewerCount;
         this.world.addBlockEvent(this.pos, this.getCachedBlock(), 1, this.viewerCount);
         this.world.updateNeighbors(this.pos, this.getCachedBlock());
         this.world.updateNeighbors(this.pos.down(), this.getCachedBlock());
      }
   }

   @Override
   public void onClose(PlayerEntity player) {
      if (!player.isSpectator() && this.getCachedBlock() instanceof ChestBlock) {
         --this.viewerCount;
         this.world.addBlockEvent(this.pos, this.getCachedBlock(), 1, this.viewerCount);
         this.world.updateNeighbors(this.pos, this.getCachedBlock());
         this.world.updateNeighbors(this.pos.down(), this.getCachedBlock());
      }
   }

   @Override
   public boolean canSetStack(int slot, ItemStack stack) {
      return true;
   }

   @Override
   public void markRemoved() {
      super.markRemoved();
      this.clearBlockCache();
      this.updateShape();
   }

   public int getChestType() {
      if (this.chestType == -1) {
         if (this.world == null || !(this.getCachedBlock() instanceof ChestBlock)) {
            return 0;
         }

         this.chestType = ((ChestBlock)this.getCachedBlock()).type;
      }

      return this.chestType;
   }

   @Override
   public String getMenuType() {
      return "minecraft:chest";
   }

   @Override
   public InventoryMenu createMenu(PlayerInventory playerInventory, PlayerEntity player) {
      return new ChestMenu(playerInventory, this, player);
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
