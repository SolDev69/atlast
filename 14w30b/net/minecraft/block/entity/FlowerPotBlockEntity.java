package net.minecraft.block.entity;

import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.resource.Identifier;

public class FlowerPotBlockEntity extends BlockEntity {
   private Item plant;
   private int metadata;

   public FlowerPotBlockEntity() {
   }

   public FlowerPotBlockEntity(Item plant, int metadata) {
      this.plant = plant;
      this.metadata = metadata;
   }

   @Override
   public void writeNbt(NbtCompound nbt) {
      super.writeNbt(nbt);
      Identifier var2 = (Identifier)Item.REGISTRY.getKey(this.plant);
      nbt.putString("Item", var2 == null ? "" : var2.toString());
      nbt.putInt("Data", this.metadata);
   }

   @Override
   public void readNbt(NbtCompound nbt) {
      super.readNbt(nbt);
      if (nbt.isType("Item", 8)) {
         this.plant = Item.byId(nbt.getString("Item"));
      } else {
         this.plant = Item.byRawId(nbt.getInt("Item"));
      }

      this.metadata = nbt.getInt("Data");
   }

   @Override
   public Packet createUpdatePacket() {
      NbtCompound var1 = new NbtCompound();
      this.writeNbt(var1);
      var1.remove("Item");
      var1.putInt("Item", Item.getRawId(this.plant));
      return new BlockEntityUpdateS2CPacket(this.pos, 5, var1);
   }

   public void setPlant(Item plant, int metadata) {
      this.plant = plant;
      this.metadata = metadata;
   }

   public Item getPlant() {
      return this.plant;
   }

   public int getMetadata() {
      return this.metadata;
   }
}
