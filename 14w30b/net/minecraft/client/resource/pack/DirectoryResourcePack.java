package net.minecraft.client.resource.pack;

import com.google.common.collect.Sets;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

@Environment(EnvType.CLIENT)
public class DirectoryResourcePack extends ResourcePack {
   public DirectoryResourcePack(File file) {
      super(file);
   }

   @Override
   protected InputStream openResource(String path) {
      return new BufferedInputStream(new FileInputStream(new File(this.file, path)));
   }

   @Override
   protected boolean hasResource(String path) {
      return new File(this.file, path).isFile();
   }

   @Override
   public Set getNamespaces() {
      HashSet var1 = Sets.newHashSet();
      File var2 = new File(this.file, "assets/");
      if (var2.isDirectory()) {
         for(File var6 : var2.listFiles(DirectoryFileFilter.DIRECTORY)) {
            String var7 = relativize(var2, var6);
            if (!var7.equals(var7.toLowerCase())) {
               this.warnNonLowercaseNamespace(var7);
            } else {
               var1.add(var7.substring(0, var7.length() - 1));
            }
         }
      }

      return var1;
   }
}
