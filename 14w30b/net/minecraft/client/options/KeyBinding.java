package net.minecraft.client.options;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Int2ObjectHashMap;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class KeyBinding implements Comparable {
   private static final List ALL = Lists.newArrayList();
   private static final Int2ObjectHashMap BY_KEY_CODE = new Int2ObjectHashMap();
   private static final Set CATEGORIES = Sets.newHashSet();
   private final String name;
   private final int defaultKeyCode;
   private final String category;
   private int keyCode;
   private boolean pressed;
   private int timesPressed;

   public static void onKeyPressed(int keyCode) {
      if (keyCode != 0) {
         KeyBinding var1 = (KeyBinding)BY_KEY_CODE.get(keyCode);
         if (var1 != null) {
            ++var1.timesPressed;
         }
      }
   }

   public static void setKeyPressed(int keyCode, boolean pressed) {
      if (keyCode != 0) {
         KeyBinding var2 = (KeyBinding)BY_KEY_CODE.get(keyCode);
         if (var2 != null) {
            var2.pressed = pressed;
         }
      }
   }

   public static void resetAll() {
      for(KeyBinding var1 : ALL) {
         var1.reset();
      }
   }

   public static void updateKeyCodeMap() {
      BY_KEY_CODE.clear();

      for(KeyBinding var1 : ALL) {
         BY_KEY_CODE.put(var1.keyCode, var1);
      }
   }

   public static Set getCategories() {
      return CATEGORIES;
   }

   public KeyBinding(String name, int keyCode, String category) {
      this.name = name;
      this.keyCode = keyCode;
      this.defaultKeyCode = keyCode;
      this.category = category;
      ALL.add(this);
      BY_KEY_CODE.put(keyCode, this);
      CATEGORIES.add(category);
   }

   public boolean isPressed() {
      return this.pressed;
   }

   public String getCategory() {
      return this.category;
   }

   public boolean wasPressed() {
      if (this.timesPressed == 0) {
         return false;
      } else {
         --this.timesPressed;
         return true;
      }
   }

   private void reset() {
      this.timesPressed = 0;
      this.pressed = false;
   }

   public String getName() {
      return this.name;
   }

   public int getDefaultKeyCode() {
      return this.defaultKeyCode;
   }

   public int getKeyCode() {
      return this.keyCode;
   }

   public void setKeyCode(int keyCode) {
      this.keyCode = keyCode;
   }

   public int compareTo(KeyBinding c_59aqzdfif) {
      int var2 = I18n.translate(this.category).compareTo(I18n.translate(c_59aqzdfif.category));
      if (var2 == 0) {
         var2 = I18n.translate(this.name).compareTo(I18n.translate(c_59aqzdfif.name));
      }

      return var2;
   }
}
