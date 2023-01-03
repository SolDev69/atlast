package net.minecraft.client.resource;

import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class ModelIdentifier extends Identifier {
   private final String variant;

   protected ModelIdentifier(int i, String... strings) {
      super(0, strings[0], strings[1]);
      this.variant = StringUtils.isEmpty(strings[2]) ? "normal" : strings[2].toLowerCase();
   }

   public ModelIdentifier(String string) {
      this(0, splitModelIdentifier(string));
   }

   public ModelIdentifier(Identifier identifier, String variant) {
      this(identifier.toString(), variant);
   }

   public ModelIdentifier(String string, String string2) {
      this(0, splitModelIdentifier(string + '#' + (string2 == null ? "normal" : string2)));
   }

   protected static String[] splitModelIdentifier(String modelIdentifier) {
      String[] var1 = new String[]{null, modelIdentifier, null};
      int var2 = modelIdentifier.indexOf(35);
      String var3 = modelIdentifier;
      if (var2 >= 0) {
         var1[2] = modelIdentifier.substring(var2 + 1, modelIdentifier.length());
         if (var2 > 1) {
            var3 = modelIdentifier.substring(0, var2);
         }
      }

      System.arraycopy(Identifier.splitIdentifier(var3), 0, var1, 0, 2);
      return var1;
   }

   public String getVariant() {
      return this.variant;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (object instanceof ModelIdentifier && super.equals(object)) {
         ModelIdentifier var2 = (ModelIdentifier)object;
         return this.variant.equals(var2.variant);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return 31 * super.hashCode() + this.variant.hashCode();
   }

   @Override
   public String toString() {
      return super.toString() + '#' + this.variant;
   }
}
