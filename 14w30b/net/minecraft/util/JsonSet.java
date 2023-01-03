package net.minecraft.util;

import com.google.gson.JsonElement;

public interface JsonSet {
   void add(JsonElement element);

   JsonElement toJson();
}
