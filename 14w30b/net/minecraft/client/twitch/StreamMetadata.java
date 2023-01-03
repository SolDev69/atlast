package net.minecraft.client.twitch;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import java.util.Map;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class StreamMetadata {
   private static final Gson GSON = new Gson();
   private final String id;
   private String message;
   private Map payload;

   public StreamMetadata(String id, String message) {
      this.id = id;
      this.message = message;
   }

   public StreamMetadata(String id) {
      this(id, null);
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public String getMessage() {
      return this.message == null ? this.id : this.message;
   }

   public void put(String key, String value) {
      if (this.payload == null) {
         this.payload = Maps.newHashMap();
      }

      if (this.payload.size() > 50) {
         throw new IllegalArgumentException("Metadata payload is full, cannot add more to it!");
      } else if (key == null) {
         throw new IllegalArgumentException("Metadata payload key cannot be null!");
      } else if (key.length() > 255) {
         throw new IllegalArgumentException("Metadata payload key is too long!");
      } else if (value == null) {
         throw new IllegalArgumentException("Metadata payload value cannot be null!");
      } else if (value.length() > 255) {
         throw new IllegalArgumentException("Metadata payload value is too long!");
      } else {
         this.payload.put(key, value);
      }
   }

   public String serialize() {
      return this.payload != null && !this.payload.isEmpty() ? GSON.toJson(this.payload) : null;
   }

   public String getId() {
      return this.id;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("name", this.id).add("description", this.message).add("data", this.serialize()).toString();
   }
}
