package net.minecraft.client.resource.language;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class LanguageDefinition implements Comparable {
   private final String code;
   private final String name;
   private final String region;
   private final boolean rightToLeft;

   public LanguageDefinition(String code, String name, String region, boolean rightToLeft) {
      this.code = code;
      this.name = name;
      this.region = region;
      this.rightToLeft = rightToLeft;
   }

   public String getCode() {
      return this.code;
   }

   public boolean isRightToLeft() {
      return this.rightToLeft;
   }

   @Override
   public String toString() {
      return String.format("%s (%s)", this.region, this.name);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return !(object instanceof LanguageDefinition) ? false : this.code.equals(((LanguageDefinition)object).code);
      }
   }

   @Override
   public int hashCode() {
      return this.code.hashCode();
   }

   public int compareTo(LanguageDefinition c_59lasurrx) {
      return this.code.compareTo(c_59lasurrx.code);
   }
}
