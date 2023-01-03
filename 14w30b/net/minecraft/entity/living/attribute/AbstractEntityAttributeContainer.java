package net.minecraft.entity.living.attribute;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.LowercaseMap;

public abstract class AbstractEntityAttributeContainer {
   protected final Map byType = Maps.newHashMap();
   protected final Map byName = new LowercaseMap();
   protected final Multimap byParent = HashMultimap.create();

   public IEntityAttributeInstance get(IEntityAttribute attribute) {
      return (IEntityAttributeInstance)this.byType.get(attribute);
   }

   public IEntityAttributeInstance get(String name) {
      return (IEntityAttributeInstance)this.byName.get(name);
   }

   public IEntityAttributeInstance registerAttribute(IEntityAttribute attribute) {
      if (this.byName.containsKey(attribute.getName())) {
         throw new IllegalArgumentException("Attribute is already registered!");
      } else {
         IEntityAttributeInstance var2 = this.register(attribute);
         this.byName.put(attribute.getName(), var2);
         this.byType.put(attribute, var2);

         for(IEntityAttribute var3 = attribute.getParent(); var3 != null; var3 = var3.getParent()) {
            this.byParent.put(var3, attribute);
         }

         return var2;
      }
   }

   protected abstract IEntityAttributeInstance register(IEntityAttribute attribute);

   public Collection getAll() {
      return this.byName.values();
   }

   public void track(IEntityAttributeInstance instance) {
   }

   public void removeModifiers(Multimap modifiers) {
      for(Entry var3 : modifiers.entries()) {
         IEntityAttributeInstance var4 = this.get((String)var3.getKey());
         if (var4 != null) {
            var4.removeModifier((AttributeModifier)var3.getValue());
         }
      }
   }

   public void addModifiers(Multimap modifiers) {
      for(Entry var3 : modifiers.entries()) {
         IEntityAttributeInstance var4 = this.get((String)var3.getKey());
         if (var4 != null) {
            var4.removeModifier((AttributeModifier)var3.getValue());
            var4.addModifier((AttributeModifier)var3.getValue());
         }
      }
   }
}
