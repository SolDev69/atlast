package net.minecraft.block.state;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;

public class BlockPredicate implements Predicate {
   private final Block block;

   private BlockPredicate(Block block) {
      this.block = block;
   }

   public static BlockPredicate of(Block block) {
      return new BlockPredicate(block);
   }

   public boolean apply(BlockState c_17agfiprw) {
      return c_17agfiprw != null && c_17agfiprw.getBlock() == this.block;
   }
}
