package net.minecraft.inventory;

import net.minecraft.text.Text;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class AnimalInventory extends SimpleInventory {
   public AnimalInventory(String name, int size) {
      super(name, false, size);
   }

   @Environment(EnvType.CLIENT)
   public AnimalInventory(Text c_21uoltggz, int i) {
      super(c_21uoltggz, i);
   }
}
