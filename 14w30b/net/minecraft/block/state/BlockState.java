package net.minecraft.block.state;

import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import net.minecraft.block.Block;
import net.minecraft.block.state.property.Property;

public interface BlockState {
   Collection properties();

   Comparable get(Property property);

   BlockState set(Property property, Comparable value);

   BlockState next(Property property);

   ImmutableMap values();

   Block getBlock();
}
