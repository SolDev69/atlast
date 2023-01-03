package net.minecraft.network.packet.s2c.play;

import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class EntityEquipmentS2CPacket implements Packet {
   private int id;
   private int equipmentSlot;
   private ItemStack stack;

   public EntityEquipmentS2CPacket() {
   }

   public EntityEquipmentS2CPacket(int id, int equipmentSlot, ItemStack stack) {
      this.id = id;
      this.equipmentSlot = equipmentSlot;
      this.stack = stack == null ? null : stack.copy();
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readVarInt();
      this.equipmentSlot = buffer.readShort();
      this.stack = buffer.readItemStack();
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.id);
      buffer.writeShort(this.equipmentSlot);
      buffer.writeItemStack(this.stack);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleEntityEquipment(this);
   }

   @Environment(EnvType.CLIENT)
   public ItemStack getStack() {
      return this.stack;
   }

   @Environment(EnvType.CLIENT)
   public int getId() {
      return this.id;
   }

   @Environment(EnvType.CLIENT)
   public int getEquipmentSlot() {
      return this.equipmentSlot;
   }
}
