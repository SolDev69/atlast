package net.minecraft.block.state.property;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;

public class BooleanProperty extends AbstractProperty {
   private final ImmutableSet values = ImmutableSet.of(true, false);

   protected BooleanProperty(String name) {
      super(name, Boolean.class);
   }

   @Override
   public Collection values() {
      return this.values;
   }

   public static BooleanProperty of(String name) {
      return new BooleanProperty(name);
   }

   public String getName(Boolean boolean_) {
      return boolean_.toString();
   }
}
