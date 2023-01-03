package net.minecraft.block.entity;

import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class NoteBlockBlockEntity extends BlockEntity {
   public byte note;
   public boolean powered;

   @Override
   public void writeNbt(NbtCompound nbt) {
      super.writeNbt(nbt);
      nbt.putByte("note", this.note);
   }

   @Override
   public void readNbt(NbtCompound nbt) {
      super.readNbt(nbt);
      this.note = nbt.getByte("note");
      this.note = (byte)MathHelper.clamp(this.note, 0, 24);
   }

   public void tunePitch() {
      this.note = (byte)((this.note + 1) % 25);
      this.markDirty();
   }

   public void playNote(World world, BlockPos pos) {
      if (world.getBlockState(pos.up()).getBlock().getMaterial() == Material.AIR) {
         Material var3 = world.getBlockState(pos.down()).getBlock().getMaterial();
         byte var4 = 0;
         if (var3 == Material.STONE) {
            var4 = 1;
         }

         if (var3 == Material.SAND) {
            var4 = 2;
         }

         if (var3 == Material.GLASS) {
            var4 = 3;
         }

         if (var3 == Material.WOOD) {
            var4 = 4;
         }

         world.addBlockEvent(pos, Blocks.NOTEBLOCK, var4, this.note);
      }
   }
}
