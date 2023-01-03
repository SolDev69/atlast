package net.minecraft.entity.vehicle;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.EntityFilter;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Hopper;
import net.minecraft.inventory.menu.HopperMenu;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HopperMinecartEntity extends InventoryMinecartEntity implements Hopper {
   private boolean unpowered = true;
   private int transferCooldown = -1;
   private BlockPos f_25wmefmmf = BlockPos.ORIGIN;

   public HopperMinecartEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   public HopperMinecartEntity(World c_54ruxjwzt, double d, double e, double f) {
      super(c_54ruxjwzt, d, e, f);
   }

   @Override
   public MinecartEntity.Type getMinecartType() {
      return MinecartEntity.Type.HOPPER;
   }

   @Override
   public Block getDefaultDisplayBlock() {
      return Blocks.HOPPER;
   }

   @Override
   public int getDefaultDisplayBlockOffset() {
      return 1;
   }

   @Override
   public int getSize() {
      return 5;
   }

   @Override
   public boolean interact(PlayerEntity player) {
      if (!this.world.isClient) {
         player.openInventoryMenu(this);
      }

      return true;
   }

   @Override
   public void onActivatorRail(int x, int y, int z, boolean powered) {
      boolean var5 = !powered;
      if (var5 != this.isUnpowered()) {
         this.setPowerState(var5);
      }
   }

   public boolean isUnpowered() {
      return this.unpowered;
   }

   public void setPowerState(boolean unpowered) {
      this.unpowered = unpowered;
   }

   @Override
   public World getWorld() {
      return this.world;
   }

   @Override
   public double getX() {
      return this.x;
   }

   @Override
   public double getY() {
      return this.y;
   }

   @Override
   public double getZ() {
      return this.z;
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.world.isClient && this.isAlive() && this.isUnpowered()) {
         BlockPos var1 = new BlockPos(this);
         if (var1.equals(this.f_25wmefmmf)) {
            --this.transferCooldown;
         } else {
            this.setTransferCooldown(0);
         }

         if (!this.hasTransferCooldown()) {
            this.setTransferCooldown(0);
            if (this.tickInventory()) {
               this.setTransferCooldown(4);
               this.markDirty();
            }
         }
      }
   }

   public boolean tickInventory() {
      if (HopperBlockEntity.pullItems(this)) {
         return true;
      } else {
         List var1 = this.world.getEntities(ItemEntity.class, this.getBoundingBox().expand(0.25, 0.0, 0.25), EntityFilter.ALIVE);
         if (var1.size() > 0) {
            HopperBlockEntity.pickUpItems(this, (ItemEntity)var1.get(0));
         }

         return false;
      }
   }

   @Override
   public void dropItems(DamageSource damageSource) {
      super.dropItems(damageSource);
      this.dropItem(Item.byBlock(Blocks.HOPPER), 1, 0.0F);
   }

   @Override
   protected void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putInt("TransferCooldown", this.transferCooldown);
   }

   @Override
   protected void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.transferCooldown = nbt.getInt("TransferCooldown");
   }

   public void setTransferCooldown(int transferCooldown) {
      this.transferCooldown = transferCooldown;
   }

   public boolean hasTransferCooldown() {
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
}
