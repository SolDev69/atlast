package net.minecraft.block.state;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.state.property.Property;

public class BlockStatePredicate implements Predicate {
   private final StateDefinition stateDefinition;
   private final Map properties = Maps.newHashMap();

   private BlockStatePredicate(StateDefinition stateDefinition) {
      this.stateDefinition = stateDefinition;
   }

   public static BlockStatePredicate of(Block block) {
      return new BlockStatePredicate(block.stateDefinition());
   }

   public boolean apply(BlockState c_17agfiprw) {
      if (c_17agfiprw != null && c_17agfiprw.getBlock().equals(this.stateDefinition.getBlock())) {
         for(Entry var3 : this.properties.entrySet()) {
            Comparable var4 = c_17agfiprw.get((Property)var3.getKey());
            if (!((Predicate)var3.getValue()).apply(var4)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public BlockStatePredicate with(Property property, Predicate predicate) {
      if (!this.stateDefinition.properties().contains(property)) {
         throw new IllegalArgumentException(this.stateDefinition + " cannot support property " + property);
      } else {
         this.properties.put(property, predicate);
         return this;
      }
   }
}
