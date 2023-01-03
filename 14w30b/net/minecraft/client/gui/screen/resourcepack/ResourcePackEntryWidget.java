package net.minecraft.client.gui.screen.resourcepack;

import net.minecraft.client.gui.screen.ResourcePackScreen;
import net.minecraft.client.resource.pack.ResourcePackLoader;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ResourcePackEntryWidget extends AbstractResourcePackEntryWidget {
   private final ResourcePackLoader.Entry entry;

   public ResourcePackEntryWidget(ResourcePackScreen parent, ResourcePackLoader.Entry entry) {
      super(parent);
      this.entry = entry;
   }

   @Override
   protected void bindIcon() {
      this.entry.bindIconTexture(this.client.getTextureManager());
   }

   @Override
   protected String getDescription() {
      return this.entry.getDescription();
   }

   @Override
   protected String getName() {
      return this.entry.getName();
   }

   public ResourcePackLoader.Entry getEntry() {
      return this.entry;
   }
}
