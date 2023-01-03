package net.minecraft.block.state.property;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import net.minecraft.util.StringRepresentable;

public class EnumProperty extends AbstractProperty {
   private final ImmutableSet values;
   private final Map valuesByName = Maps.newHashMap();

   protected EnumProperty(String name, Class type, Collection values) {
      super(name, type);
      this.values = ImmutableSet.copyOf(values);

      for(Enum var5 : values) {
         String var6 = ((StringRepresentable)var5).getStringRepresentation();
         if (this.valuesByName.containsKey(var6)) {
            throw new IllegalArgumentException("Multiple values have the same name '" + var6 + "'");
         }

         this.valuesByName.put(var6, var5);
      }
   }

   @Override
   public Collection values() {
      return this.values;
   }

   public String getName(Enum enum_) {
      return ((StringRepresentable)enum_).getStringRepresentation();
   }

   public static EnumProperty of(String name, Class type) {
      return of(name, type, Predicates.alwaysTrue());
   }

   public static EnumProperty of(String name, Class type, Predicate filter) {
      return of(name, type, Collections2.filter(Lists.newArrayList(type.getEnumConstants()), filter));
   }

   public static EnumProperty of(String name, Class type, Enum... values) {
      return of(name, type, Lists.newArrayList(values));
   }

   public static EnumProperty of(String name, Class type, Collection values) {
      return new EnumProperty(name, type, values);
   }
}
