package net.minecraft.util.snooper;

import com.google.common.collect.Maps;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.util.NetworkUtils;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class Snooper {
   private final Map f_02irwwrjw = Maps.newHashMap();
   private final Map snoopedDataMap = Maps.newHashMap();
   private final String snooperToken = UUID.randomUUID().toString();
   private final URL snoopUrl;
   private final Snoopable snooped;
   private final Timer snooperTimer = new Timer("Snooper Timer", true);
   private final Object syncObject = new Object();
   private final long snooperInitTime;
   private boolean active;
   private int snooperCount;

   public Snooper(String side, Snoopable snooped, long time) {
      try {
         this.snoopUrl = new URL("http://snoop.minecraft.net/" + side + "?version=" + 2);
      } catch (MalformedURLException var6) {
         throw new IllegalArgumentException();
      }

      this.snooped = snooped;
      this.snooperInitTime = time;
   }

   public void startSnooping() {
      if (!this.active) {
         this.active = true;
         this.addJavaInfo();
         this.snooperTimer.schedule(new TimerTask() {
            @Override
            public void run() {
               if (Snooper.this.snooped.isSnooperEnabled()) {
                  HashMap var1;
                  synchronized(Snooper.this.syncObject) {
                     var1 = Maps.newHashMap(Snooper.this.snoopedDataMap);
                     if (Snooper.this.snooperCount == 0) {
                        var1.putAll(Snooper.this.f_02irwwrjw);
                     }

                     var1.put("snooper_count", Snooper.this.snooperCount++);
                     var1.put("snooper_token", Snooper.this.snooperToken);
                  }

                  NetworkUtils.getLangFile(Snooper.this.snoopUrl, var1, true);
               }
            }
         }, 0L, 900000L);
      }
   }

   private void addJavaInfo() {
      this.addJvmArgs();
      this.addToSnoopedData("snooper_token", this.snooperToken);
      this.put("snooper_token", this.snooperToken);
      this.put("os_name", System.getProperty("os.name"));
      this.put("os_version", System.getProperty("os.version"));
      this.put("os_architecture", System.getProperty("os.arch"));
      this.put("java_version", System.getProperty("java.version"));
      this.put("version", "14w30c");
      this.snooped.addSnooper(this);
   }

   private void addJvmArgs() {
      RuntimeMXBean var1 = ManagementFactory.getRuntimeMXBean();
      List var2 = var1.getInputArguments();
      int var3 = 0;

      for(String var5 : var2) {
         if (var5.startsWith("-X")) {
            this.addToSnoopedData("jvm_arg[" + var3++ + "]", var5);
         }
      }

      this.addToSnoopedData("jvm_args", var3);
   }

   public void addCpuInfo() {
      this.put("memory_total", Runtime.getRuntime().totalMemory());
      this.put("memory_max", Runtime.getRuntime().maxMemory());
      this.put("memory_free", Runtime.getRuntime().freeMemory());
      this.put("cpu_cores", Runtime.getRuntime().availableProcessors());
      this.snooped.addSnooperInfo(this);
   }

   public void addToSnoopedData(String name, Object data) {
      synchronized(this.syncObject) {
         this.snoopedDataMap.put(name, data);
      }
   }

   public void put(String string, Object object) {
      synchronized(this.syncObject) {
         this.f_02irwwrjw.put(string, object);
      }
   }

   @Environment(EnvType.CLIENT)
   public Map getSnooperDataAsLinkedHashmap() {
      LinkedHashMap var1 = Maps.newLinkedHashMap();
      synchronized(this.syncObject) {
         this.addCpuInfo();

         for(Entry var4 : this.f_02irwwrjw.entrySet()) {
            var1.put(var4.getKey(), var4.getValue().toString());
         }

         for(Entry var8 : this.snoopedDataMap.entrySet()) {
            var1.put(var8.getKey(), var8.getValue().toString());
         }

         return var1;
      }
   }

   public boolean isActive() {
      return this.active;
   }

   public void stopSnooping() {
      this.snooperTimer.cancel();
   }

   @Environment(EnvType.CLIENT)
   public String getSnooperToken() {
      return this.snooperToken;
   }

   public long getSnooperInitTime() {
      return this.snooperInitTime;
   }
}
