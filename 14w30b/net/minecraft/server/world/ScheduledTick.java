package net.minecraft.server.world;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class ScheduledTick implements Comparable {
   private static long idCounter;
   private final Block block;
   public final BlockPos pos;
   public long time;
   public int priority;
   private long id;

   public ScheduledTick(BlockPos pos, Block block) {
      this.id = (long)(idCounter++);
      this.pos = pos;
      this.block = block;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof ScheduledTick)) {
         return false;
      } else {
         ScheduledTick var2 = (ScheduledTick)obj;
         return this.pos.equals(var2.pos) && Block.areEqual(this.block, var2.block);
      }
   }

   @Override
   public int hashCode() {
      return this.pos.hashCode();
   }

   public ScheduledTick setTime(long time) {
      this.time = time;
      return this;
   }

   public void setPriority(int priority) {
      this.priority = priority;
   }

   public int compareTo(ScheduledTick c_84btcnzvg) {
      if (this.time < c_84btcnzvg.time) {
         return -1;
      } else if (this.time > c_84btcnzvg.time) {
         return 1;
      } else if (this.priority != c_84btcnzvg.priority) {
         return this.priority - c_84btcnzvg.priority;
      } else if (this.id < c_84btcnzvg.id) {
         return -1;
      } else {
         return this.id > c_84btcnzvg.id ? 1 : 0;
      }
   }

   @Override
   public String toString() {
      return Block.getRawId(this.block) + ": " + this.pos + ", " + this.time + ", " + this.priority + ", " + this.id;
   }

   public Block getBlock() {
      return this.block;
   }
}
