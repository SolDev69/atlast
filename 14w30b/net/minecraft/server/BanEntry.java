package net.minecraft.server;

import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BanEntry extends StoredUserEntry {
   public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   protected final Date startDate;
   protected final String source;
   protected final Date expirationDate;
   protected final String reason;

   public BanEntry(Object user, Date startDate, String source, Date expirationDate, String reason) {
      super(user);
      this.startDate = startDate == null ? new Date() : startDate;
      this.source = source == null ? "(Unknown)" : source;
      this.expirationDate = expirationDate;
      this.reason = reason == null ? "Banned by an operator." : reason;
   }

   protected BanEntry(Object object, JsonObject jsonObject) {
      super(object, jsonObject);

      Date var3;
      try {
         var3 = jsonObject.has("created") ? DATE_FORMAT.parse(jsonObject.get("created").getAsString()) : new Date();
      } catch (ParseException var7) {
         var3 = new Date();
      }

      this.startDate = var3;
      this.source = jsonObject.has("source") ? jsonObject.get("source").getAsString() : "(Unknown)";

      Date var4;
      try {
         var4 = jsonObject.has("expires") ? DATE_FORMAT.parse(jsonObject.get("expires").getAsString()) : null;
      } catch (ParseException var6) {
         var4 = null;
      }

      this.expirationDate = var4;
      this.reason = jsonObject.has("reason") ? jsonObject.get("reason").getAsString() : "Banned by an operator.";
   }

   public Date getExpirationDate() {
      return this.expirationDate;
   }

   public String getReason() {
      return this.reason;
   }

   @Override
   boolean hasExpired() {
      return this.expirationDate == null ? false : this.expirationDate.before(new Date());
   }

   @Override
   protected void serialize(JsonObject json) {
      json.addProperty("created", DATE_FORMAT.format(this.startDate));
      json.addProperty("source", this.source);
      json.addProperty("expires", this.expirationDate == null ? "forever" : DATE_FORMAT.format(this.expirationDate));
      json.addProperty("reason", this.reason);
   }
}
