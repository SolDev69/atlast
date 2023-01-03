package net.minecraft.client.resource.language;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.client.resource.manager.ResourceReloadListener;
import net.minecraft.client.resource.metadata.LanguageMetadata;
import net.minecraft.client.resource.metadata.ResourceMetadataSerializerRegistry;
import net.minecraft.client.resource.pack.IResourcePack;
import net.minecraft.locale.Language;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class LanguageManager implements ResourceReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ResourceMetadataSerializerRegistry metadataSerializers;
   private String currentLanguageCode;
   protected static final TranslationStorage TRANSLATIONS = new TranslationStorage();
   private Map languages = Maps.newHashMap();

   public LanguageManager(ResourceMetadataSerializerRegistry metadataSerializers, String currentLanguageCode) {
      this.metadataSerializers = metadataSerializers;
      this.currentLanguageCode = currentLanguageCode;
      I18n.setTranslations(TRANSLATIONS);
   }

   public void reload(List resourcePacks) {
      this.languages.clear();

      for(IResourcePack var3 : resourcePacks) {
         try {
            LanguageMetadata var4 = (LanguageMetadata)var3.getMetadataSection(this.metadataSerializers, "language");
            if (var4 != null) {
               for(LanguageDefinition var6 : var4.getLanguageDefinitions()) {
                  if (!this.languages.containsKey(var6.getCode())) {
                     this.languages.put(var6.getCode(), var6);
                  }
               }
            }
         } catch (RuntimeException var7) {
            LOGGER.warn("Unable to parse metadata section of resourcepack: " + var3.getName(), var7);
         } catch (IOException var8) {
            LOGGER.warn("Unable to parse metadata section of resourcepack: " + var3.getName(), var8);
         }
      }
   }

   @Override
   public void reload(IResourceManager resourceManager) {
      ArrayList var2 = Lists.newArrayList(new String[]{"en_US"});
      if (!"en_US".equals(this.currentLanguageCode)) {
         var2.add(this.currentLanguageCode);
      }

      TRANSLATIONS.load(resourceManager, var2);
      Language.load(TRANSLATIONS.translations);
   }

   public boolean isUnicode() {
      return TRANSLATIONS.isUnicode();
   }

   public boolean isRightToLeft() {
      return this.getLanguage() != null && this.getLanguage().isRightToLeft();
   }

   public void setLanguage(LanguageDefinition language) {
      this.currentLanguageCode = language.getCode();
   }

   public LanguageDefinition getLanguage() {
      return this.languages.containsKey(this.currentLanguageCode)
         ? (LanguageDefinition)this.languages.get(this.currentLanguageCode)
         : (LanguageDefinition)this.languages.get("en_US");
   }

   public SortedSet getLanguages() {
      return Sets.newTreeSet(this.languages.values());
   }
}
