package net.minecraft.entity.living.attribute;

public abstract class EntityAttribute implements IEntityAttribute {
   private final IEntityAttribute parent;
   private final String name;
   private final double defaultValue;
   private boolean trackable;

   protected EntityAttribute(IEntityAttribute parent, String name, double defaultValue) {
      this.parent = parent;
      this.name = name;
      this.defaultValue = defaultValue;
      if (name == null) {
         throw new IllegalArgumentException("Name cannot be null!");
      }
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public double getDefault() {
      return this.defaultValue;
   }

   @Override
   public boolean isTrackable() {
      return this.trackable;
   }

   public EntityAttribute setTrackable(boolean trackable) {
      this.trackable = trackable;
      return this;
   }

   @Override
   public IEntityAttribute getParent() {
      return this.parent;
   }

   @Override
   public int hashCode() {
      return this.name.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof IEntityAttribute && this.name.equals(((IEntityAttribute)obj).getName());
   }
}
