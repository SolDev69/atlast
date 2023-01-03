package net.minecraft.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.resource.pack.ServerResourcePack;
import net.minecraft.server.MinecraftServer;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkUtils {
   private static final AtomicInteger downloadThreadCounter = new AtomicInteger(0);
   private static final Logger LOGGER = LogManager.getLogger();

   public static String getString(Map hashMap) {
      StringBuilder var1 = new StringBuilder();

      for(Entry var3 : hashMap.entrySet()) {
         if (var1.length() > 0) {
            var1.append('&');
         }

         try {
            var1.append(URLEncoder.encode((String)var3.getKey(), "UTF-8"));
         } catch (UnsupportedEncodingException var6) {
            var6.printStackTrace();
         }

         if (var3.getValue() != null) {
            var1.append('=');

            try {
               var1.append(URLEncoder.encode(var3.getValue().toString(), "UTF-8"));
            } catch (UnsupportedEncodingException var5) {
               var5.printStackTrace();
            }
         }
      }

      return var1.toString();
   }

   public static String getLangFile(URL url, Map hasmap, boolean quietly) {
      return getLangFile(url, getString(hasmap), quietly);
   }

   private static String getLangFile(URL url, String contentLength, boolean quietly) {
      try {
         Proxy var3 = MinecraftServer.getInstance() == null ? null : MinecraftServer.getInstance().getProxy();
         if (var3 == null) {
            var3 = Proxy.NO_PROXY;
         }

         HttpURLConnection var4 = (HttpURLConnection)url.openConnection(var3);
         var4.setRequestMethod("POST");
         var4.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         var4.setRequestProperty("Content-Length", "" + contentLength.getBytes().length);
         var4.setRequestProperty("Content-Language", "en-US");
         var4.setUseCaches(false);
         var4.setDoInput(true);
         var4.setDoOutput(true);
         DataOutputStream var5 = new DataOutputStream(var4.getOutputStream());
         var5.writeBytes(contentLength);
         var5.flush();
         var5.close();
         BufferedReader var6 = new BufferedReader(new InputStreamReader(var4.getInputStream()));
         StringBuffer var8 = new StringBuffer();

         String var7;
         while((var7 = var6.readLine()) != null) {
            var8.append(var7);
            var8.append('\r');
         }

         var6.close();
         return var8.toString();
      } catch (Exception var9) {
         if (!quietly) {
            LOGGER.error("Could not post to " + url, var9);
         }

         return "";
      }
   }

   @Environment(EnvType.CLIENT)
   public static void downloadServerPack(
      File file, String url, ServerResourcePack resourcePack, Map hashMap, int maxFileSize, ProgressListener progressListener, Proxy proxy
   ) {
      Thread var7 = new Thread(new Runnable() {
         @Override
         public void run() {
            InputStream var2 = null;
            DataOutputStream var3 = null;
            if (progressListener != null) {
               progressListener.updateTitle("Downloading Texture Pack");
               progressListener.setTask("Making Request...");
            }

            try {
               try {
                  byte[] var4 = new byte[4096];
                  URL var5 = new URL(url);
                  URLConnection var1 = var5.openConnection(proxy);
                  float var6 = 0.0F;
                  float var7 = (float)hashMap.entrySet().size();

                  for(Entry var9 : hashMap.entrySet()) {
                     var1.setRequestProperty((String)var9.getKey(), (String)var9.getValue());
                     if (progressListener != null) {
                        progressListener.progressStagePercentage((int)(++var6 / var7 * 100.0F));
                     }
                  }

                  var2 = var1.getInputStream();
                  var7 = (float)var1.getContentLength();
                  int var17 = var1.getContentLength();
                  if (progressListener != null) {
                     progressListener.setTask(String.format("Downloading file (%.2f MB)...", var7 / 1000.0F / 1000.0F));
                  }

                  if (file.exists()) {
                     long var18 = file.length();
                     if (var18 == (long)var17) {
                        resourcePack.apply(file);
                        if (progressListener != null) {
                           progressListener.setDone();
                        }

                        return;
                     }

                     NetworkUtils.LOGGER.warn("Deleting " + file + " as it does not match what we currently have (" + var17 + " vs our " + var18 + ").");
                     FileUtils.deleteQuietly(file);
                  } else if (file.getParentFile() != null) {
                     file.getParentFile().mkdirs();
                  }

                  var3 = new DataOutputStream(new FileOutputStream(file));
                  if (maxFileSize > 0 && var7 > (float)maxFileSize) {
                     if (progressListener != null) {
                        progressListener.setDone();
                     }

                     throw new IOException("Filesize is bigger than maximum allowed (file is " + var6 + ", limit is " + maxFileSize + ")");
                  }

                  int var19 = 0;

                  while((var19 = var2.read(var4)) >= 0) {
                     var6 += (float)var19;
                     if (progressListener != null) {
                        progressListener.progressStagePercentage((int)(var6 / var7 * 100.0F));
                     }

                     if (maxFileSize > 0 && var6 > (float)maxFileSize) {
                        if (progressListener != null) {
                           progressListener.setDone();
                        }

                        throw new IOException("Filesize was bigger than maximum allowed (got >= " + var6 + ", limit was " + maxFileSize + ")");
                     }

                     var3.write(var4, 0, var19);
                  }

                  resourcePack.apply(file);
                  if (progressListener != null) {
                     progressListener.setDone();
                     return;
                  }
               } catch (Throwable var14) {
                  var14.printStackTrace();
               }
            } finally {
               IOUtils.closeQuietly(var2);
               IOUtils.closeQuietly(var3);
            }
         }
      }, "File Downloader #" + downloadThreadCounter.incrementAndGet());
      var7.setDaemon(true);
      var7.start();
   }

   @Environment(EnvType.CLIENT)
   public static int getLocalPort() {
      ServerSocket var0 = null;
      int var1 = -1;

      try {
         var0 = new ServerSocket(0);
         var1 = var0.getLocalPort();
      } finally {
         try {
            if (var0 != null) {
               var0.close();
            }
         } catch (IOException var8) {
         }
      }

      return var1;
   }

   @Environment(EnvType.CLIENT)
   public static String getUrlContents(URL url) {
      HttpURLConnection var1 = (HttpURLConnection)url.openConnection();
      var1.setRequestMethod("GET");
      BufferedReader var2 = new BufferedReader(new InputStreamReader(var1.getInputStream()));
      StringBuilder var4 = new StringBuilder();

      String var3;
      while((var3 = var2.readLine()) != null) {
         var4.append(var3);
         var4.append('\r');
      }

      var2.close();
      return var4.toString();
   }
}
