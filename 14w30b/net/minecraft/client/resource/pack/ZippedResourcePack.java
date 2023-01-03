package net.minecraft.client.resource.pack;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.client.resource.ResourceNotFoundException;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ZippedResourcePack extends ResourcePack implements Closeable {
   public static final Splitter TYPE_NAMESPACE_SPLITTER = Splitter.on('/').omitEmptyStrings().limit(3);
   private ZipFile file;

   public ZippedResourcePack(File file) {
      super(file);
   }

   private ZipFile getZipFile() {
      if (this.file == null) {
         this.file = new ZipFile(this.file);
      }

      return this.file;
   }

   @Override
   protected InputStream openResource(String path) {
      ZipFile var2 = this.getZipFile();
      ZipEntry var3 = var2.getEntry(path);
      if (var3 == null) {
         throw new ResourceNotFoundException(this.file, path);
      } else {
         return var2.getInputStream(var3);
      }
   }

   @Override
   public boolean hasResource(String path) {
      try {
         return this.getZipFile().getEntry(path) != null;
      } catch (IOException var3) {
         return false;
      }
   }

   @Override
   public Set getNamespaces() {
      ZipFile var1;
      try {
         var1 = this.getZipFile();
      } catch (IOException var8) {
         return Collections.emptySet();
      }

      Enumeration var2 = var1.entries();
      HashSet var3 = Sets.newHashSet();

      while(var2.hasMoreElements()) {
         ZipEntry var4 = (ZipEntry)var2.nextElement();
         String var5 = var4.getName();
         if (var5.startsWith("assets/")) {
            ArrayList var6 = Lists.newArrayList(TYPE_NAMESPACE_SPLITTER.split(var5));
            if (var6.size() > 1) {
               String var7 = (String)var6.get(1);
               if (!var7.equals(var7.toLowerCase())) {
                  this.warnNonLowercaseNamespace(var7);
               } else {
                  var3.add(var7);
               }
            }
         }
      }

      return var3;
   }

   @Override
   protected void finalize() {
      this.close();
      super.finalize();
   }

   @Override
   public void close() {
      if (this.file != null) {
         this.file.close();
         this.file = null;
      }
   }
}
