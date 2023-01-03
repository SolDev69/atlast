package com.mojang.blaze3d.shaders;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.JsonUtils;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.opengl.GL14;

@Environment(EnvType.CLIENT)
public class BlendMode {
   private static BlendMode lastApplied = null;
   private final int srcColorFactor;
   private final int srcAlphaFactor;
   private final int dstColorFactor;
   private final int dstAlphaFactor;
   private final int blendFunc;
   private final boolean separateBlend;
   private final boolean opaque;

   private BlendMode(boolean separateBlend, boolean opaque, int srcColorFactor, int dstColorFactor, int srcAlphaFactor, int dstAlphaFactor, int blendFunc) {
      this.separateBlend = separateBlend;
      this.srcColorFactor = srcColorFactor;
      this.dstColorFactor = dstColorFactor;
      this.srcAlphaFactor = srcAlphaFactor;
      this.dstAlphaFactor = dstAlphaFactor;
      this.opaque = opaque;
      this.blendFunc = blendFunc;
   }

   public BlendMode() {
      this(false, true, 1, 0, 1, 0, 32774);
   }

   public BlendMode(int srcColorFactor, int dstColorFactor, int blendFunc) {
      this(false, false, srcColorFactor, dstColorFactor, srcColorFactor, dstColorFactor, blendFunc);
   }

   public BlendMode(int srcColorFactor, int dstColorFactor, int srcAlphaFactor, int dstAlphaFactor, int blendFunc) {
      this(true, false, srcColorFactor, dstColorFactor, srcAlphaFactor, dstAlphaFactor, blendFunc);
   }

   public void apply() {
      if (!this.equals(lastApplied)) {
         if (lastApplied == null || this.opaque != lastApplied.isOpaque()) {
            lastApplied = this;
            if (this.opaque) {
               GlStateManager.enableBlend();
               return;
            }

            GlStateManager.disableBlend();
         }

         GL14.glBlendEquation(this.blendFunc);
         if (this.separateBlend) {
            GlStateManager.blendFuncSeparate(this.srcColorFactor, this.dstColorFactor, this.srcAlphaFactor, this.dstAlphaFactor);
         } else {
            GlStateManager.blendFunc(this.srcColorFactor, this.dstColorFactor);
         }
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof BlendMode)) {
         return false;
      } else {
         BlendMode var2 = (BlendMode)obj;
         if (this.blendFunc != var2.blendFunc) {
            return false;
         } else if (this.dstAlphaFactor != var2.dstAlphaFactor) {
            return false;
         } else if (this.dstColorFactor != var2.dstColorFactor) {
            return false;
         } else if (this.opaque != var2.opaque) {
            return false;
         } else if (this.separateBlend != var2.separateBlend) {
            return false;
         } else if (this.srcAlphaFactor != var2.srcAlphaFactor) {
            return false;
         } else {
            return this.srcColorFactor == var2.srcColorFactor;
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.srcColorFactor;
      var1 = 31 * var1 + this.srcAlphaFactor;
      var1 = 31 * var1 + this.dstColorFactor;
      var1 = 31 * var1 + this.dstAlphaFactor;
      var1 = 31 * var1 + this.blendFunc;
      var1 = 31 * var1 + (this.separateBlend ? 1 : 0);
      return 31 * var1 + (this.opaque ? 1 : 0);
   }

   public boolean isOpaque() {
      return this.opaque;
   }

   public static BlendMode fromJson(JsonObject json) {
      if (json == null) {
         return new BlendMode();
      } else {
         int var1 = 32774;
         int var2 = 1;
         int var3 = 0;
         int var4 = 1;
         int var5 = 0;
         boolean var6 = true;
         boolean var7 = false;
         if (JsonUtils.hasString(json, "func")) {
            var1 = stringToBlendFunc(json.get("func").getAsString());
            if (var1 != 32774) {
               var6 = false;
            }
         }

         if (JsonUtils.hasString(json, "srcrgb")) {
            var2 = stringToBlendFactor(json.get("srcrgb").getAsString());
            if (var2 != 1) {
               var6 = false;
            }
         }

         if (JsonUtils.hasString(json, "dstrgb")) {
            var3 = stringToBlendFactor(json.get("dstrgb").getAsString());
            if (var3 != 0) {
               var6 = false;
            }
         }

         if (JsonUtils.hasString(json, "srcalpha")) {
            var4 = stringToBlendFactor(json.get("srcalpha").getAsString());
            if (var4 != 1) {
               var6 = false;
            }

            var7 = true;
         }

         if (JsonUtils.hasString(json, "dstalpha")) {
            var5 = stringToBlendFactor(json.get("dstalpha").getAsString());
            if (var5 != 0) {
               var6 = false;
            }

            var7 = true;
         }

         if (var6) {
            return new BlendMode();
         } else {
            return var7 ? new BlendMode(var2, var3, var4, var5, var1) : new BlendMode(var2, var3, var1);
         }
      }
   }

   private static int stringToBlendFunc(String s) {
      String var1 = s.trim().toLowerCase();
      if (var1.equals("add")) {
         return 32774;
      } else if (var1.equals("subtract")) {
         return 32778;
      } else if (var1.equals("reversesubtract")) {
         return 32779;
      } else if (var1.equals("reverse_subtract")) {
         return 32779;
      } else if (var1.equals("min")) {
         return 32775;
      } else {
         return var1.equals("max") ? 32776 : 32774;
      }
   }

   private static int stringToBlendFactor(String s) {
      String var1 = s.trim().toLowerCase();
      var1 = var1.replaceAll("_", "");
      var1 = var1.replaceAll("one", "1");
      var1 = var1.replaceAll("zero", "0");
      var1 = var1.replaceAll("minus", "-");
      if (var1.equals("0")) {
         return 0;
      } else if (var1.equals("1")) {
         return 1;
      } else if (var1.equals("srccolor")) {
         return 768;
      } else if (var1.equals("1-srccolor")) {
         return 769;
      } else if (var1.equals("dstcolor")) {
         return 774;
      } else if (var1.equals("1-dstcolor")) {
         return 775;
      } else if (var1.equals("srcalpha")) {
         return 770;
      } else if (var1.equals("1-srcalpha")) {
         return 771;
      } else if (var1.equals("dstalpha")) {
         return 772;
      } else {
         return var1.equals("1-dstalpha") ? 773 : -1;
      }
   }
}
