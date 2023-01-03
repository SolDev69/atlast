package net.minecraft.client.resource.metadata;

import java.util.Collection;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class LanguageMetadata implements ResourceMetadataSection {
   private final Collection languageDefinitions;

   public LanguageMetadata(Collection languageDefinitions) {
      this.languageDefinitions = languageDefinitions;
   }

   public Collection getLanguageDefinitions() {
      return this.languageDefinitions;
   }
}
