package net.minecraft.client.gui.screen.resourcepack;

import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import net.minecraft.client.gui.screen.ResourcePackScreen;
import net.minecraft.client.resource.metadata.ResourcePackMetadata;
import net.minecraft.client.resource.pack.IResourcePack;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.Identifier;
import net.minecraft.text.Formatting;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class DefaultResourcePackEntryWidget extends AbstractResourcePackEntryWidget {
   private static final Logger LOGGER = LogManager.getLogger();
   private final IResourcePack pack = this.client.getResourcePackLoader().defaultResourcePack;
   private final Identifier identifier;

   public DefaultResourcePackEntryWidget(ResourcePackScreen c_60grxgbfe) {
      super(c_60grxgbfe);

      NativeImageBackedTexture var2;
      try {
         var2 = new NativeImageBackedTexture(this.pack.getIcon());
      } catch (IOException var4) {
         var2 = TextureUtil.MISSING_TEXTURE;
      }

      this.identifier = this.client.getTextureManager().register("texturepackicon", var2);
   }

   @Override
   protected String getDescription() {
      try {
         ResourcePackMetadata var1 = (ResourcePackMetadata)this.pack.getMetadataSection(this.client.getResourcePackLoader().metadataSerializerRegistry, "pack");
         if (var1 != null) {
            return var1.getDescription().buildFormattedString();
         }
      } catch (JsonParseException var2) {
         LOGGER.error("Couldn't load metadata info", var2);
      } catch (IOException var3) {
         LOGGER.error("Couldn't load metadata info", var3);
      }

      return Formatting.RED + "Missing " + "pack.mcmeta" + " :(";
   }

   @Override
   protected boolean isNotNamed() {
      return false;
   }

   @Override
   protected boolean isNamed() {
      return false;
   }

   @Override
   protected boolean moveDown() {
      return false;
   }

   @Override
   protected boolean moveUp() {
      return false;
   }

   @Override
   protected String getName() {
      return "Default";
   }

   @Override
   protected void bindIcon() {
      this.client.getTextureManager().bind(this.identifier);
   }

   @Override
   protected boolean hasResourcePack() {
      return false;
   }
}
