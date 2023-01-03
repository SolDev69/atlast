package net.minecraft.resource;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class Identifier {
   protected final String namespace;
   protected final String path;

   protected Identifier(int i, String... identifier) {
      this.namespace = StringUtils.isEmpty(identifier[0]) ? "minecraft" : identifier[0].toLowerCase();
      this.path = identifier[1];
      Validate.notNull(this.path);
   }

   public Identifier(String identifier) {
      this(0, splitIdentifier(identifier));
   }

   @Environment(EnvType.CLIENT)
   public Identifier(String namespace, String path) {
      this(0, namespace, path);
   }

   protected static String[] splitIdentifier(String identifier) {
      String[] var1 = new String[]{null, identifier};
      int var2 = identifier.indexOf(58);
      if (var2 >= 0) {
         var1[1] = identifier.substring(var2 + 1, identifier.length());
         if (var2 > 1) {
            var1[0] = identifier.substring(0, var2);
         }
      }

      return var1;
   }

   @Environment(EnvType.CLIENT)
   public String getPath() {
      return this.path;
   }

   @Environment(EnvType.CLIENT)
   public String getNamespace() {
      return this.namespace;
   }

   @Override
   public String toString() {
      return this.namespace + ':' + this.path;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof Identifier)) {
         return false;
      } else {
         Identifier var2 = (Identifier)object;
         return this.namespace.equals(var2.namespace) && this.path.equals(var2.path);
      }
   }

   @Override
   public int hashCode() {
      return 31 * this.namespace.hashCode() + this.path.hashCode();
   }
}
