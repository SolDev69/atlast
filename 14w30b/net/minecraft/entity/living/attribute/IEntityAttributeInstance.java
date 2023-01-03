package net.minecraft.entity.living.attribute;

import java.util.Collection;
import java.util.UUID;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public interface IEntityAttributeInstance {
   IEntityAttribute getAttribute();

   double getBase();

   void setBase(double base);

   Collection getModifiers(int operation);

   Collection getModifiers();

   boolean hasModifier(AttributeModifier modifier);

   AttributeModifier getModifier(UUID id);

   void addModifier(AttributeModifier modifier);

   void removeModifier(AttributeModifier modifier);

   @Environment(EnvType.CLIENT)
   void clearModifiers();

   double get();
}
