package net.minecraft.world.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;

public class BlockStateStorage {
   private final short[] states = new short[65536];
   private final BlockState defaultState = Blocks.AIR.defaultState();

   public BlockState get(int x, int y, int z) {
      int var4 = x << 12 | z << 8 | y;
      return this.get(var4);
   }

   public BlockState get(int index) {
      if (index >= 0 && index < this.states.length) {
         BlockState var2 = (BlockState)Block.STATE_REGISTRY.get(this.states[index]);
         return var2 != null ? var2 : this.defaultState;
      } else {
         throw new IndexOutOfBoundsException("The coordinate is out of range");
      }
   }

   public void set(int x, int y, int z, BlockState state) {
      int var5 = x << 12 | z << 8 | y;
      this.set(var5, state);
   }

   public void set(int index, BlockState state) {
      if (index >= 0 && index < this.states.length) {
         this.states[index] = (short)Block.STATE_REGISTRY.getId(state);
      } else {
         throw new IndexOutOfBoundsException("The coordinate is out of range");
      }
   }
}
