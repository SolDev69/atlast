package net.minecraft.block.state;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.state.property.Property;
import net.minecraft.util.IterableBuilder;
import net.minecraft.util.MapBuilder;

public class StateDefinition {
   private static final Joiner PROPERTY_JOINER = Joiner.on(", ");
   private static final Function PROPERTY_TO_STRING = new Function() {
      public String apply(Property c_52qwlgkuh) {
         return c_52qwlgkuh == null ? "<NULL>" : c_52qwlgkuh.getName();
      }
   };
   private final Block block;
   private final ImmutableList properties;
   private final ImmutableList states;

   public StateDefinition(Block block, Property... properties) {
      this.block = block;
      Arrays.sort(properties, new Comparator() {
         public int compare(Property c_52qwlgkuh, Property c_52qwlgkuh2) {
            return c_52qwlgkuh.getName().compareTo(c_52qwlgkuh2.getName());
         }
      });
      this.properties = ImmutableList.copyOf(properties);
      LinkedHashMap var3 = Maps.newLinkedHashMap();
      ArrayList var4 = Lists.newArrayList();

      for(List var7 : IterableBuilder.iterableIterableToListIterable(this.collectValues())) {
         Map var8 = MapBuilder.linkedHashMap(this.properties, var7);
         StateDefinition.BlockStateImpl var9 = new StateDefinition.BlockStateImpl(block, ImmutableMap.copyOf(var8));
         var3.put(var8, var9);
         var4.add(var9);
      }

      for(StateDefinition.BlockStateImpl var11 : var4) {
         var11.findNeighbors(var3);
      }

      this.states = ImmutableList.copyOf(var4);
   }

   public ImmutableList all() {
      return this.states;
   }

   private List collectValues() {
      ArrayList var1 = Lists.newArrayList();

      for(int var2 = 0; var2 < this.properties.size(); ++var2) {
         var1.add(((Property)this.properties.get(var2)).values());
      }

      return var1;
   }

   public BlockState any() {
      return (BlockState)this.states.get(0);
   }

   public Block getBlock() {
      return this.block;
   }

   public Collection properties() {
      return this.properties;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
         .add("block", Block.REGISTRY.getKey(this.block))
         .add("properties", Iterables.transform(this.properties, PROPERTY_TO_STRING))
         .toString();
   }

   static class BlockStateImpl extends AbstractBlockState {
      private final Block block;
      private final ImmutableMap values;
      private ImmutableTable neighbors;

      private BlockStateImpl(Block block, ImmutableMap values) {
         this.block = block;
         this.values = values;
      }

      @Override
      public Collection properties() {
         return Collections.unmodifiableCollection(this.values.keySet());
      }

      @Override
      public Comparable get(Property property) {
         if (!this.values.containsKey(property)) {
            throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + this.block.stateDefinition());
         } else {
            return (Comparable)property.getType().cast(this.values.get(property));
         }
      }

      @Override
      public BlockState set(Property property, Comparable value) {
         if (!this.values.containsKey(property)) {
            throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.block.stateDefinition());
         } else if (!property.values().contains(value)) {
            throw new IllegalArgumentException(
               "Cannot set property " + property + " to " + value + " on block " + Block.REGISTRY.getKey(this.block) + ", it is not an allowed value"
            );
         } else {
            return (BlockState)(this.values.get(property) == value ? this : (BlockState)this.neighbors.get(property, value));
         }
      }

      @Override
      public ImmutableMap values() {
         return this.values;
      }

      @Override
      public Block getBlock() {
         return this.block;
      }

      @Override
      public boolean equals(Object obj) {
         return this == obj;
      }

      @Override
      public int hashCode() {
         return this.values.hashCode();
      }

      public void findNeighbors(Map neighbors) {
         if (this.neighbors != null) {
            throw new IllegalStateException();
         } else {
            HashBasedTable var2 = HashBasedTable.create();

            for(Property var4 : this.values.keySet()) {
               for(Comparable var6 : var4.values()) {
                  if (var6 != this.values.get(var4)) {
                     var2.put(var4, var6, neighbors.get(this.addNeighbors(var4, var6)));
                  }
               }
            }

            this.neighbors = ImmutableTable.copyOf(var2);
         }
      }

      private Map addNeighbors(Property property, Comparable value) {
         HashMap var3 = Maps.newHashMap(this.values);
         var3.put(property, value);
         return var3;
      }
   }
}
