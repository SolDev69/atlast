package net.minecraft.client.resource.language;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class I18n {
   private static TranslationStorage translations;

   static void setTranslations(TranslationStorage translations) {
      I18n.translations = translations;
   }

   public static String translate(String key, Object... args) {
      return translations.translate(key, args);
   }
}
