package net.minecraft.client;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientBrandRetriever {
   public static String getClientModName() {
      return "vanilla";
   }
}
