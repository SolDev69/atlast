package net.minecraft.entity.living.attribute;

public interface IEntityAttribute {
   String getName();

   double clamp(double value);

   double getDefault();

   boolean isTrackable();

   IEntityAttribute getParent();
}
