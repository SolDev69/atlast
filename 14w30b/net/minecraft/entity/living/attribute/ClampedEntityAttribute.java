package net.minecraft.entity.living.attribute;

import net.minecraft.util.math.MathHelper;

public class ClampedEntityAttribute extends EntityAttribute {
   private final double min;
   private final double max;
   private String displayName;

   public ClampedEntityAttribute(IEntityAttribute parent, String name, double defaultValue, double min, double max) {
      super(parent, name, defaultValue);
      this.min = min;
      this.max = max;
      if (min > max) {
         throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
      } else if (defaultValue < min) {
         throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
      } else if (defaultValue > max) {
         throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
      }
   }

   public ClampedEntityAttribute setDisplayName(String displayName) {
      this.displayName = displayName;
      return this;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   @Override
   public double clamp(double value) {
      return MathHelper.clamp(value, this.min, this.max);
   }
}
