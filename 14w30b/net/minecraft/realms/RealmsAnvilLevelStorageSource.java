package net.minecraft.realms;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.storage.WorldSaveInfo;
import net.minecraft.world.storage.WorldStorageSource;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class RealmsAnvilLevelStorageSource {
   private WorldStorageSource levelStorageSource;

   public RealmsAnvilLevelStorageSource(WorldStorageSource c_63ozacouy) {
      this.levelStorageSource = c_63ozacouy;
   }

   public String getName() {
      return this.levelStorageSource.getName();
   }

   public boolean levelExists(String string) {
      return this.levelStorageSource.exists(string);
   }

   public boolean convertLevel(String string, ProgressListener c_89znffsun) {
      return this.levelStorageSource.convert(string, c_89znffsun);
   }

   public boolean requiresConversion(String string) {
      return this.levelStorageSource.needsConversion(string);
   }

   public boolean isNewLevelIdAcceptable(String string) {
      return this.levelStorageSource.canCreate(string);
   }

   public boolean deleteLevel(String string) {
      return this.levelStorageSource.delete(string);
   }

   public boolean isConvertible(String string) {
      return this.levelStorageSource.isConvertible(string);
   }

   public void renameLevel(String string, String string2) {
      this.levelStorageSource.rename(string, string2);
   }

   public void clearAll() {
      this.levelStorageSource.clearRegionIo();
   }

   public List getLevelList() {
      ArrayList var1 = Lists.newArrayList();

      for(WorldSaveInfo var3 : this.levelStorageSource.getAll()) {
         var1.add(new RealmsLevelSummary(var3));
      }

      return var1;
   }
}
