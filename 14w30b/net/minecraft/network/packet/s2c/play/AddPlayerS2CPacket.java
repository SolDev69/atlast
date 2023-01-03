package net.minecraft.network.packet.s2c.play;

import java.util.List;
import java.util.UUID;
import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.entity.DataTracker;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class AddPlayerS2CPacket implements Packet {
   private int id;
   private UUID uuid;
   private int x;
   private int y;
   private int z;
   private byte yaw;
   private byte pitch;
   private int heldItemId;
   private DataTracker tracker;
   private List dataEntries;

   public AddPlayerS2CPacket() {
   }

   public AddPlayerS2CPacket(PlayerEntity player) {
      this.id = player.getNetworkId();
      this.uuid = player.getGameProfile().getId();
      this.x = MathHelper.floor(player.x * 32.0);
      this.y = MathHelper.floor(player.y * 32.0);
      this.z = MathHelper.floor(player.z * 32.0);
      this.yaw = (byte)((int)(player.yaw * 256.0F / 360.0F));
      this.pitch = (byte)((int)(player.pitch * 256.0F / 360.0F));
      ItemStack var2 = player.inventory.getMainHandStack();
      this.heldItemId = var2 == null ? 0 : Item.getRawId(var2.getItem());
      this.tracker = player.getDataTracker();
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.id = buffer.readVarInt();
      this.uuid = buffer.readUuid();
      this.x = buffer.readInt();
      this.y = buffer.readInt();
      this.z = buffer.readInt();
      this.yaw = buffer.readByte();
      this.pitch = buffer.readByte();
      this.heldItemId = buffer.readShort();
      this.dataEntries = DataTracker.read(buffer);
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeVarInt(this.id);
      buffer.writeUuid(this.uuid);
      buffer.writeInt(this.x);
      buffer.writeInt(this.y);
      buffer.writeInt(this.z);
      buffer.writeByte(this.yaw);
      buffer.writeByte(this.pitch);
      buffer.writeShort(this.heldItemId);
      this.tracker.write(buffer);
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleAddPlayer(this);
   }

   @Environment(EnvType.CLIENT)
   public List getDataTrackerEntries() {
      if (this.dataEntries == null) {
         this.dataEntries = this.tracker.collectEntries();
      }

      return this.dataEntries;
   }

   @Environment(EnvType.CLIENT)
   public int getId() {
      return this.id;
   }

   @Environment(EnvType.CLIENT)
   public UUID getUuid() {
      return this.uuid;
   }

   @Environment(EnvType.CLIENT)
   public int getX() {
      return this.x;
   }

   @Environment(EnvType.CLIENT)
   public int getY() {
      return this.y;
   }

   @Environment(EnvType.CLIENT)
   public int getZ() {
      return this.z;
   }

   @Environment(EnvType.CLIENT)
   public byte getYaw() {
      return this.yaw;
   }

   @Environment(EnvType.CLIENT)
   public byte getPitch() {
      return this.pitch;
   }

   @Environment(EnvType.CLIENT)
   public int getHeldItemId() {
      return this.heldItemId;
   }
}
