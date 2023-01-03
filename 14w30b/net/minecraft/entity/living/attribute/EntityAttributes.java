package net.minecraft.entity.living.attribute;

import java.util.Collection;
import java.util.UUID;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAttributes {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final IEntityAttribute MAX_HEALTH = new ClampedEntityAttribute(null, "generic.maxHealth", 20.0, 0.0, Double.MAX_VALUE)
      .setDisplayName("Max Health")
      .setTrackable(true);
   public static final IEntityAttribute FOLLOW_RANGE = new ClampedEntityAttribute(null, "generic.followRange", 32.0, 0.0, 2048.0)
      .setDisplayName("Follow Range");
   public static final IEntityAttribute KNOCKBACK_RESISTANCE = new ClampedEntityAttribute(null, "generic.knockbackResistance", 0.0, 0.0, 1.0)
      .setDisplayName("Knockback Resistance");
   public static final IEntityAttribute MOVEMENT_SPEED = new ClampedEntityAttribute(null, "generic.movementSpeed", 0.7F, 0.0, Double.MAX_VALUE)
      .setDisplayName("Movement Speed")
      .setTrackable(true);
   public static final IEntityAttribute ATTACK_DAMAGE = new ClampedEntityAttribute(null, "generic.attackDamage", 2.0, 0.0, Double.MAX_VALUE);

   public static NbtList toNbt(AbstractEntityAttributeContainer container) {
      NbtList var1 = new NbtList();

      for(IEntityAttributeInstance var3 : container.getAll()) {
         var1.add(toNbt(var3));
      }

      return var1;
   }

   private static NbtCompound toNbt(IEntityAttributeInstance instance) {
      NbtCompound var1 = new NbtCompound();
      IEntityAttribute var2 = instance.getAttribute();
      var1.putString("Name", var2.getName());
      var1.putDouble("Base", instance.getBase());
      Collection var3 = instance.getModifiers();
      if (var3 != null && !var3.isEmpty()) {
         NbtList var4 = new NbtList();

         for(AttributeModifier var6 : var3) {
            if (var6.isSerialized()) {
               var4.add(toNbt(var6));
            }
         }

         var1.put("Modifiers", var4);
      }

      return var1;
   }

   private static NbtCompound toNbt(AttributeModifier modifier) {
      NbtCompound var1 = new NbtCompound();
      var1.putString("Name", modifier.getName());
      var1.putDouble("Amount", modifier.get());
      var1.putInt("Operation", modifier.getOperation());
      var1.putLong("UUIDMost", modifier.getId().getMostSignificantBits());
      var1.putLong("UUIDLeast", modifier.getId().getLeastSignificantBits());
      return var1;
   }

   public static void readNbt(AbstractEntityAttributeContainer container, NbtList nbt) {
      for(int var2 = 0; var2 < nbt.size(); ++var2) {
         NbtCompound var3 = nbt.getCompound(var2);
         IEntityAttributeInstance var4 = container.get(var3.getString("Name"));
         if (var4 != null) {
            readNbt(var4, var3);
         } else {
            LOGGER.warn("Ignoring unknown attribute '" + var3.getString("Name") + "'");
         }
      }
   }

   private static void readNbt(IEntityAttributeInstance instance, NbtCompound nbt) {
      instance.setBase(nbt.getDouble("Base"));
      if (nbt.isType("Modifiers", 9)) {
         NbtList var2 = nbt.getList("Modifiers", 10);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            AttributeModifier var4 = fromNbt(var2.getCompound(var3));
            if (var4 != null) {
               AttributeModifier var5 = instance.getModifier(var4.getId());
               if (var5 != null) {
                  instance.removeModifier(var5);
               }

               instance.addModifier(var4);
            }
         }
      }
   }

   public static AttributeModifier fromNbt(NbtCompound nbt) {
      UUID var1 = new UUID(nbt.getLong("UUIDMost"), nbt.getLong("UUIDLeast"));

      try {
         return new AttributeModifier(var1, nbt.getString("Name"), nbt.getDouble("Amount"), nbt.getInt("Operation"));
      } catch (Exception var3) {
         LOGGER.warn("Unable to create attribute: " + var3.getMessage());
         return null;
      }
   }
}
