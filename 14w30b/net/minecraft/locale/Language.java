package net.minecraft.locale;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.regex.Pattern;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Language {
   private static final Pattern TRANSLATION_ARG_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   private static final Splitter TRANSLATION_SPLITTER = Splitter.on('=').limit(2);
   private static Language INSTANCE = new Language();
   private final Map translations = Maps.newHashMap();
   private long loadTimestamp;

   public Language() {
      try {
         InputStream var1 = Language.class.getResourceAsStream("/assets/minecraft/lang/en_US.lang");

         for(String var3 : IOUtils.readLines(var1, Charsets.UTF_8)) {
            if (!var3.isEmpty() && var3.charAt(0) != '#') {
               String[] var4 = (String[])Iterables.toArray(TRANSLATION_SPLITTER.split(var3), String.class);
               if (var4 != null && var4.length == 2) {
                  String var5 = var4[0];
                  String var6 = TRANSLATION_ARG_PATTERN.matcher(var4[1]).replaceAll("%$1s");
                  this.translations.put(var5, var6);
               }
            }
         }

         this.loadTimestamp = System.currentTimeMillis();
      } catch (IOException var7) {
      }
   }

   static Language getInstance() {
      return INSTANCE;
   }

   @Environment(EnvType.CLIENT)
   public static synchronized void load(Map translations) {
      INSTANCE.translations.clear();
      INSTANCE.translations.putAll(translations);
      INSTANCE.loadTimestamp = System.currentTimeMillis();
   }

   public synchronized String translate(String key) {
      return this.translateKey(key);
   }

   public synchronized String translate(String key, Object... args) {
      String var3 = this.translateKey(key);

      try {
         return String.format(var3, args);
      } catch (IllegalFormatException var5) {
         return "Format error: " + var3;
      }
   }

   private String translateKey(String key) {
      String var2 = (String)this.translations.get(key);
      return var2 == null ? key : var2;
   }

   public synchronized boolean hasTranslation(String key) {
      return this.translations.containsKey(key);
   }

   public long getLoadTimestamp() {
      return this.loadTimestamp;
   }
}
