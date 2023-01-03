package net.minecraft.server.world;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class BlockEvent {
   private BlockPos pos;
   private Block block;
   private int type;
   private int data;

   public BlockEvent(BlockPos pos, Block block, int type, int data) {
      this.pos = pos;
      this.type = type;
      this.data = data;
      this.block = block;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int getType() {
      return this.type;
   }

   public int getData() {
      return this.data;
   }

   public Block getBlock() {
      return this.block;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof BlockEvent)) {
         return false;
      } else {
         BlockEvent var2 = (BlockEvent)obj;
         return this.pos.equals(var2.pos) && this.type == var2.type && this.data == var2.data && this.block == var2.block;
      }
   }

   @Override
   public String toString() {
      return "TE(" + this.pos + ")," + this.type + "," + this.data + "," + this.block;
   }
}
