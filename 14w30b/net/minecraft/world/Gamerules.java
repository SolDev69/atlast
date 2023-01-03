package net.minecraft.world;

import java.util.TreeMap;
import net.minecraft.nbt.NbtCompound;

public class Gamerules {
   private TreeMap values = new TreeMap();

   public Gamerules() {
      this.add("doFireTick", "true");
      this.add("mobGriefing", "true");
      this.add("keepInventory", "false");
      this.add("doMobSpawning", "true");
      this.add("doMobLoot", "true");
      this.add("doTileDrops", "true");
      this.add("commandBlockOutput", "true");
      this.add("naturalRegeneration", "true");
      this.add("doDaylightCycle", "true");
      this.add("logAdminCommands", "true");
      this.add("showDeathMessages", "true");
      this.add("randomTickSpeed", "3");
      this.add("sendCommandFeedback", "true");
      this.add("reducedDebugInfo", "false");
   }

   public void add(String name, String defaultValue) {
      this.values.put(name, new Gamerules.Value(defaultValue));
   }

   public void set(String name, String value) {
      Gamerules.Value var3 = (Gamerules.Value)this.values.get(name);
      if (var3 != null) {
         var3.set(value);
      } else {
         this.add(name, value);
      }
   }

   public String get(String name) {
      Gamerules.Value var2 = (Gamerules.Value)this.values.get(name);
      return var2 != null ? var2.get() : "";
   }

   public boolean getBoolean(String name) {
      Gamerules.Value var2 = (Gamerules.Value)this.values.get(name);
      return var2 != null ? var2.getBoolean() : false;
   }

   public int getInt(String name) {
      Gamerules.Value var2 = (Gamerules.Value)this.values.get(name);
      return var2 != null ? var2.getInt() : 0;
   }

   public NbtCompound toNbt() {
      NbtCompound var1 = new NbtCompound();

      for(String var3 : this.values.keySet()) {
         Gamerules.Value var4 = (Gamerules.Value)this.values.get(var3);
         var1.putString(var3, var4.get());
      }

      return var1;
   }

   public void readNbt(NbtCompound nbt) {
      for(String var4 : nbt.getKeys()) {
         String var6 = nbt.getString(var4);
         this.set(var4, var6);
      }
   }

   public String[] getAll() {
      return this.values.keySet().toArray(new String[0]);
   }

   public boolean contains(String name) {
      return this.values.containsKey(name);
   }

   static class Value {
      private String value;
      private boolean booleanValue;
      private int intValue;
      private double doubleValue;

      public Value(String value) {
         this.set(value);
      }

      public void set(String value) {
         this.value = value;
         this.booleanValue = Boolean.parseBoolean(value);
         this.intValue = this.booleanValue ? 1 : 0;

         try {
            this.intValue = Integer.parseInt(value);
         } catch (NumberFormatException var4) {
         }

         try {
            this.doubleValue = Double.parseDouble(value);
         } catch (NumberFormatException var3) {
         }
      }

      public String get() {
         return this.value;
      }

      public boolean getBoolean() {
         return this.booleanValue;
      }

      public int getInt() {
         return this.intValue;
      }
   }
}
