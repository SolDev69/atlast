package net.minecraft.client.resource.language;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.client.resource.IResource;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

@Environment(EnvType.CLIENT)
public class TranslationStorage {
   private static final Splitter SPLITTER = Splitter.on('=').limit(2);
   private static final Pattern PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   Map translations = Maps.newHashMap();
   private boolean unicode;

   public synchronized void load(IResourceManager resourceManager, List languageCodes) {
      this.translations.clear();

      for(String var4 : languageCodes) {
         String var5 = String.format("lang/%s.lang", var4);

         for(String var7 : resourceManager.getNamespaces()) {
            try {
               this.load(resourceManager.getResources(new Identifier(var7, var5)));
            } catch (IOException var9) {
            }
         }
      }

      this.checkUnicodeUsage();
   }

   public boolean isUnicode() {
      return this.unicode;
   }

   private void checkUnicodeUsage() {
      this.unicode = false;
      int var1 = 0;
      int var2 = 0;

      for(String var4 : this.translations.values()) {
         int var5 = var4.length();
         var2 += var5;

         for(int var6 = 0; var6 < var5; ++var6) {
            if (var4.charAt(var6) >= 256) {
               ++var1;
            }
         }
      }

      float var7 = (float)var1 / (float)var2;
      this.unicode = (double)var7 > 0.1;
   }

   private void load(List resources) {
      for(IResource var3 : resources) {
         InputStream var4 = var3.asStream();

         try {
            this.load(var4);
         } finally {
            IOUtils.closeQuietly(var4);
         }
      }
   }

   private void load(InputStream is) {
      for(String var3 : IOUtils.readLines(is, Charsets.UTF_8)) {
         if (!var3.isEmpty() && var3.charAt(0) != '#') {
            String[] var4 = (String[])Iterables.toArray(SPLITTER.split(var3), String.class);
            if (var4 != null && var4.length == 2) {
               String var5 = var4[0];
               String var6 = PATTERN.matcher(var4[1]).replaceAll("%$1s");
               this.translations.put(var5, var6);
            }
         }
      }
   }

   private String translateKey(String key) {
      String var2 = (String)this.translations.get(key);
      return var2 == null ? key : var2;
   }

   public String translate(String key, Object[] args) {
      String var3 = this.translateKey(key);

      try {
         return String.format(var3, args);
      } catch (IllegalFormatException var5) {
         return "Format error: " + var3;
      }
   }
}
