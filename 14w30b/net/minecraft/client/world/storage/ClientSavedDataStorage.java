package net.minecraft.client.world.storage;

import net.minecraft.world.saved.SavedData;
import net.minecraft.world.storage.SavedDataStorage;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientSavedDataStorage extends SavedDataStorage {
   public ClientSavedDataStorage() {
      super(null);
   }

   @Override
   public SavedData loadData(Class type, String id) {
      return (SavedData)this.loadedDataById.get(id);
   }

   @Override
   public void setData(String id, SavedData data) {
      this.loadedDataById.put(id, data);
   }

   @Override
   public void save() {
   }

   @Override
   public int getNextCount(String id) {
      return 0;
   }
}
