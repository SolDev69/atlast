package net.minecraft.entity.living.attribute;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EntityAttributeInstance implements IEntityAttributeInstance {
   private final AbstractEntityAttributeContainer container;
   private final IEntityAttribute attribute;
   private final Map modifiersByOperation = Maps.newHashMap();
   private final Map modifiersByName = Maps.newHashMap();
   private final Map modifiersById = Maps.newHashMap();
   private double base;
   private boolean dirty = true;
   private double value;

   public EntityAttributeInstance(AbstractEntityAttributeContainer container, IEntityAttribute attribute) {
      this.container = container;
      this.attribute = attribute;
      this.base = attribute.getDefault();

      for(int var3 = 0; var3 < 3; ++var3) {
         this.modifiersByOperation.put(var3, Sets.newHashSet());
      }
   }

   @Override
   public IEntityAttribute getAttribute() {
      return this.attribute;
   }

   @Override
   public double getBase() {
      return this.base;
   }

   @Override
   public void setBase(double base) {
      if (base != this.getBase()) {
         this.base = base;
         this.markDirty();
      }
   }

   @Override
   public Collection getModifiers(int operation) {
      return (Collection)this.modifiersByOperation.get(operation);
   }

   @Override
   public Collection getModifiers() {
      HashSet var1 = Sets.newHashSet();

      for(int var2 = 0; var2 < 3; ++var2) {
         var1.addAll(this.getModifiers(var2));
      }

      return var1;
   }

   @Override
   public AttributeModifier getModifier(UUID id) {
      return (AttributeModifier)this.modifiersById.get(id);
   }

   @Override
   public boolean hasModifier(AttributeModifier modifier) {
      return this.modifiersById.get(modifier.getId()) != null;
   }

   @Override
   public void addModifier(AttributeModifier modifier) {
      if (this.getModifier(modifier.getId()) != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      } else {
         Object var2 = (Set)this.modifiersByName.get(modifier.getName());
         if (var2 == null) {
            var2 = Sets.newHashSet();
            this.modifiersByName.put(modifier.getName(), var2);
         }

         ((Set)this.modifiersByOperation.get(modifier.getOperation())).add(modifier);
         var2.add(modifier);
         this.modifiersById.put(modifier.getId(), modifier);
         this.markDirty();
      }
   }

   protected void markDirty() {
      this.dirty = true;
      this.container.track(this);
   }

   @Override
   public void removeModifier(AttributeModifier modifier) {
      for(int var2 = 0; var2 < 3; ++var2) {
         Set var3 = (Set)this.modifiersByOperation.get(var2);
         var3.remove(modifier);
      }

      Set var4 = (Set)this.modifiersByName.get(modifier.getName());
      if (var4 != null) {
         var4.remove(modifier);
         if (var4.isEmpty()) {
            this.modifiersByName.remove(modifier.getName());
         }
      }

      this.modifiersById.remove(modifier.getId());
      this.markDirty();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void clearModifiers() {
      Collection var1 = this.getModifiers();
      if (var1 != null) {
         for(AttributeModifier var3 : Lists.newArrayList(var1)) {
            this.removeModifier(var3);
         }
      }
   }

   @Override
   public double get() {
      if (this.dirty) {
         this.value = this.computeValue();
         this.dirty = false;
      }

      return this.value;
   }

   private double computeValue() {
      double var1 = this.getBase();

      for(AttributeModifier var4 : this.getAppliedModifiers(0)) {
         var1 += var4.get();
      }

      double var7 = var1;

      for(AttributeModifier var6 : this.getAppliedModifiers(1)) {
         var7 += var1 * var6.get();
      }

      for(AttributeModifier var9 : this.getAppliedModifiers(2)) {
         var7 *= 1.0 + var9.get();
      }

      return this.attribute.clamp(var7);
   }

   private Collection getAppliedModifiers(int operation) {
      HashSet var2 = Sets.newHashSet(this.getModifiers(operation));

      for(IEntityAttribute var3 = this.attribute.getParent(); var3 != null; var3 = var3.getParent()) {
         IEntityAttributeInstance var4 = this.container.get(var3);
         if (var4 != null) {
            var2.addAll(var4.getModifiers(operation));
         }
      }

      return var2;
   }
}
