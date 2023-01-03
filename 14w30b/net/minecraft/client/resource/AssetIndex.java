package net.minecraft.client.resource;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.JsonUtils;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class AssetIndex {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map index = Maps.newHashMap();

   public AssetIndex(File dir, String index) {
      if (index != null) {
         File var3 = new File(dir, "objects");
         File var4 = new File(dir, "indexes/" + index + ".json");
         BufferedReader var5 = null;

         try {
            var5 = Files.newReader(var4, Charsets.UTF_8);
            JsonObject var6 = new JsonParser().parse(var5).getAsJsonObject();
            JsonObject var7 = JsonUtils.getJsonObjectOrDefault(var6, "objects", null);
            if (var7 != null) {
               for(Entry var9 : var7.entrySet()) {
                  JsonObject var10 = (JsonObject)var9.getValue();
                  String var11 = (String)var9.getKey();
                  String[] var12 = var11.split("/", 2);
                  String var13 = var12.length == 1 ? var12[0] : var12[0] + ":" + var12[1];
                  String var14 = JsonUtils.getString(var10, "hash");
                  File var15 = new File(var3, var14.substring(0, 2) + "/" + var14);
                  this.index.put(var13, var15);
               }
            }
         } catch (JsonParseException var20) {
            LOGGER.error("Unable to parse resource index file: " + var4);
         } catch (FileNotFoundException var21) {
            LOGGER.error("Can't find the resource index file: " + var4);
         } finally {
            IOUtils.closeQuietly(var5);
         }
      }
   }

   public Map m_06tewhjwt() {
      return this.index;
   }
}
