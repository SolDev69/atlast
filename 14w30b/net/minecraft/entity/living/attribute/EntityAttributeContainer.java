package net.minecraft.entity.living.attribute;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.LowercaseMap;

public class EntityAttributeContainer extends AbstractEntityAttributeContainer {
   private final Set tracked = Sets.newHashSet();
   protected final Map byName = new LowercaseMap();

   public EntityAttributeInstance get(IEntityAttribute c_10trhoqvf) {
      return (EntityAttributeInstance)super.get(c_10trhoqvf);
   }

   public EntityAttributeInstance get(String string) {
      IEntityAttributeInstance var2 = super.get(string);
      if (var2 == null) {
         var2 = (IEntityAttributeInstance)this.byName.get(string);
      }

      return (EntityAttributeInstance)var2;
   }

   @Override
   public IEntityAttributeInstance registerAttribute(IEntityAttribute attribute) {
      IEntityAttributeInstance var2 = super.registerAttribute(attribute);
      if (attribute instanceof ClampedEntityAttribute && ((ClampedEntityAttribute)attribute).getDisplayName() != null) {
         this.byName.put(((ClampedEntityAttribute)attribute).getDisplayName(), var2);
      }

      return var2;
   }

   @Override
   protected IEntityAttributeInstance register(IEntityAttribute attribute) {
      return new EntityAttributeInstance(this, attribute);
   }

   @Override
   public void track(IEntityAttributeInstance instance) {
      if (instance.getAttribute().isTrackable()) {
         this.tracked.add(instance);
      }

      for(IEntityAttribute var3 : this.byParent.get(instance.getAttribute())) {
         EntityAttributeInstance var4 = this.get(var3);
         if (var4 != null) {
            var4.markDirty();
         }
      }
   }

   public Set getTracked() {
      return this.tracked;
   }

   public Collection getTrackable() {
      HashSet var1 = Sets.newHashSet();

      for(IEntityAttributeInstance var3 : this.getAll()) {
         if (var3.getAttribute().isTrackable()) {
            var1.add(var3);
         }
      }

      return var1;
   }
}
