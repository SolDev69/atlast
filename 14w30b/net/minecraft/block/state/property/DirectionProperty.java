package net.minecraft.block.state.property;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.Collection;
import net.minecraft.util.math.Direction;

public class DirectionProperty extends EnumProperty {
   protected DirectionProperty(String name, Collection values) {
      super(name, Direction.class, values);
   }

   public static DirectionProperty of(String name) {
      return of(name, Predicates.alwaysTrue());
   }

   public static DirectionProperty of(String name, Predicate filter) {
      return of(name, Collections2.filter(Lists.newArrayList(Direction.values()), filter));
   }

   public static DirectionProperty of(String name, Collection values) {
      return new DirectionProperty(name, values);
   }
}
