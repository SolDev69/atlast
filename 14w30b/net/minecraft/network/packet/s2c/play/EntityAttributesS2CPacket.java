package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.entity.living.attribute.AttributeModifier;
import net.minecraft.entity.living.attribute.IEntityAttributeInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EntityAttributesS2CPacket implements Packet {
   private int id;
   private final List entries = Lists.newArrayList();

   public EntityAttributesS2CPacket() {
   }

   public EntityAttributesS2CPacket(int id, Collection attributes) {
      this.id = id;

      for(IEntityAttributeInstance var4 : attributes) {
         this.entries.add(new EntityAttributesS2CPacket.Entry(var4.getAttribute().getName(), var4.getBase(), var4.getModifiers()));
      }
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readVarInt();
      int var2 = buffer.readInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = buffer.readString(64);
         double var5 = buffer.readDouble();
         ArrayList var7 = Lists.newArrayList();
         int var8 = buffer.readVarInt();

         for(int var9 = 0; var9 < var8; ++var9) {
            UUID var10 = buffer.readUuid();
            var7.add(new AttributeModifier(var10, "Unknown synced attribute modifier", buffer.readDouble(), buffer.readByte()));
         }

         this.entries.add(new EntityAttributesS2CPacket.Entry(var4, var5, var7));
      }
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.id);
      buffer.writeInt(this.entries.size());

      for(EntityAttributesS2CPacket.Entry var3 : this.entries) {
         buffer.writeString(var3.getId());
         buffer.writeDouble(var3.getBaseValue());
         buffer.writeVarInt(var3.getModifiers().size());

         for(AttributeModifier var5 : var3.getModifiers()) {
            buffer.writeUuid(var5.getId());
            buffer.writeDouble(var5.get());
            buffer.writeByte(var5.getOperation());
         }
      }
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleEntityAttributes(this);
   }

   @Environment(EnvType.CLIENT)
   public int getId() {
      return this.id;
   }

   @Environment(EnvType.CLIENT)
   public List getEntries() {
      return this.entries;
   }

   public class Entry {
      private final String id;
      private final double baseValue;
      private final Collection modifiers;

      public Entry(String id, double baseValue, Collection modifiers) {
         this.id = id;
         this.baseValue = baseValue;
         this.modifiers = modifiers;
      }

      public String getId() {
         return this.id;
      }

      public double getBaseValue() {
         return this.baseValue;
      }

      public Collection getModifiers() {
         return this.modifiers;
      }
   }
}
