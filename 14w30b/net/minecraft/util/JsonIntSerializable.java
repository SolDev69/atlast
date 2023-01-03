package net.minecraft.util;

public class JsonIntSerializable {
   private int value;
   private JsonSet jsonSet;

   public int getValue() {
      return this.value;
   }

   public void setValue(int value) {
      this.value = value;
   }

   public JsonSet getJsonSet() {
      return this.jsonSet;
   }

   public void setJsonSet(JsonSet jsonSet) {
      this.jsonSet = jsonSet;
   }
}
