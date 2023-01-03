package net.minecraft.block.state.property;

import java.util.Collection;

public interface Property {
   String getName();

   Collection values();

   Class getType();

   String getName(Comparable value);
}
