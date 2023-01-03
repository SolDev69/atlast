package net.minecraft.entity.vehicle;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.menu.ChestMenu;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.item.Item;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ChestMinecartEntity extends InventoryMinecartEntity {
   public ChestMinecartEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   public ChestMinecartEntity(World c_54ruxjwzt, double d, double e, double f) {
      super(c_54ruxjwzt, d, e, f);
   }

   @Override
   public void dropItems(DamageSource damageSource) {
      super.dropItems(damageSource);
      this.dropItem(Item.byBlock(Blocks.CHEST), 1, 0.0F);
   }

   @Override
   public int getSize() {
      return 27;
   }

   @Override
   public MinecartEntity.Type getMinecartType() {
      return MinecartEntity.Type.CHEST;
   }

   @Override
   public Block getDefaultDisplayBlock() {
      return Blocks.CHEST;
   }

   @Override
   public int getDefaultDisplayBlockMetadata() {
      return Direction.NORTH.getId();
   }

   @Override
   public int getDefaultDisplayBlockOffset() {
      return 8;
   }

   @Override
   public String getMenuType() {
      return "minecraft:chest";
   }

   @Override
   public InventoryMenu createMenu(PlayerInventory playerInventory, PlayerEntity player) {
      return new ChestMenu(playerInventory, this, player);
   }
}
