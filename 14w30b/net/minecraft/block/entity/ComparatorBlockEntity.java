package net.minecraft.block.entity;

import net.minecraft.nbt.NbtCompound;

public class ComparatorBlockEntity extends BlockEntity {
   private int powerLevel;

   @Override
   public void writeNbt(NbtCompound nbt) {
      super.writeNbt(nbt);
      nbt.putInt("OutputSignal", this.powerLevel);
   }

   @Override
   public void readNbt(NbtCompound nbt) {
      super.readNbt(nbt);
      this.powerLevel = nbt.getInt("OutputSignal");
   }

   public int getPowerLevel() {
      return this.powerLevel;
   }

   public void setPowerLevel(int powerLevel) {
      this.powerLevel = powerLevel;
   }
}
