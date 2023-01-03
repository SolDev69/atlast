package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class TitlesS2CPacket implements Packet {
   private TitlesS2CPacket.Type type;
   private Text text;
   private int fadeIn;
   private int duration;
   private int fadeOut;

   public TitlesS2CPacket() {
   }

   public TitlesS2CPacket(TitlesS2CPacket.Type type, Text text) {
      this(type, text, -1, -1, -1);
   }

   public TitlesS2CPacket(int fadeIn, int duration, int fadeOut) {
      this(TitlesS2CPacket.Type.TIMES, null, fadeIn, duration, fadeOut);
   }

   public TitlesS2CPacket(TitlesS2CPacket.Type type, Text text, int fadeIn, int duration, int fadeOut) {
      this.type = type;
      this.text = text;
      this.fadeIn = fadeIn;
      this.duration = duration;
      this.fadeOut = fadeOut;
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.type = (TitlesS2CPacket.Type)buffer.readEnum(TitlesS2CPacket.Type.class);
      if (this.type == TitlesS2CPacket.Type.TITLE || this.type == TitlesS2CPacket.Type.SUBTITLE) {
         this.text = buffer.readText();
      }

      if (this.type == TitlesS2CPacket.Type.TIMES) {
         this.fadeIn = buffer.readInt();
         this.duration = buffer.readInt();
         this.fadeOut = buffer.readInt();
      }
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeEnum(this.type);
      if (this.type == TitlesS2CPacket.Type.TITLE || this.type == TitlesS2CPacket.Type.SUBTITLE) {
         buffer.writeText(this.text);
      }

      if (this.type == TitlesS2CPacket.Type.TIMES) {
         buffer.writeInt(this.fadeIn);
         buffer.writeInt(this.duration);
         buffer.writeInt(this.fadeOut);
      }
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleTitles(this);
   }

   @Environment(EnvType.CLIENT)
   public TitlesS2CPacket.Type getType() {
      return this.type;
   }

   @Environment(EnvType.CLIENT)
   public Text getText() {
      return this.text;
   }

   @Environment(EnvType.CLIENT)
   public int getFadeIn() {
      return this.fadeIn;
   }

   @Environment(EnvType.CLIENT)
   public int getDuration() {
      return this.duration;
   }

   @Environment(EnvType.CLIENT)
   public int getFadeOut() {
      return this.fadeOut;
   }

   public static enum Type {
      TITLE,
      SUBTITLE,
      TIMES,
      CLEAR,
      RESET;

      public static TitlesS2CPacket.Type byName(String name) {
         for(TitlesS2CPacket.Type var4 : values()) {
            if (var4.name().equalsIgnoreCase(name)) {
               return var4;
            }
         }

         return TITLE;
      }

      public static String[] getNames() {
         String[] var0 = new String[values().length];
         int var1 = 0;

         for(TitlesS2CPacket.Type var5 : values()) {
            var0[var1++] = var5.name().toLowerCase();
         }

         return var0;
      }
   }
}
