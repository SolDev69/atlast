package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.util.Collection;
import net.minecraft.client.network.handler.ClientPlayPacketHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.scoreboard.team.AbstractTeam;
import net.minecraft.scoreboard.team.Team;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class TeamS2CPacket implements Packet {
   private String name = "";
   private String displayName = "";
   private String prefix = "";
   private String suffix = "";
   private String nameTagVisibility = AbstractTeam.Visibility.ALWAYS.name;
   private int color = -1;
   private Collection members = Lists.newArrayList();
   private int action;
   private int flags;

   public TeamS2CPacket() {
   }

   public TeamS2CPacket(Team team, int action) {
      this.name = team.getName();
      this.action = action;
      if (action == 0 || action == 2) {
         this.displayName = team.getDisplayName();
         this.prefix = team.getPrefix();
         this.suffix = team.getSuffix();
         this.flags = team.packFriendlyFlags();
         this.nameTagVisibility = team.getNameTagVisibility().name;
         this.color = team.getColor().getIndex();
      }

      if (action == 0) {
         this.members.addAll(team.getMembers());
      }
   }

   public TeamS2CPacket(Team team, Collection members, int action) {
      if (action != 3 && action != 4) {
         throw new IllegalArgumentException("Method must be join or leave for player constructor");
      } else if (members != null && !members.isEmpty()) {
         this.action = action;
         this.name = team.getName();
         this.members.addAll(members);
      } else {
         throw new IllegalArgumentException("Players cannot be null/empty");
      }
   }

   @Override
   public void write(PacketByteBuf buffer) {
      this.name = buffer.readString(16);
      this.action = buffer.readByte();
      if (this.action == 0 || this.action == 2) {
         this.displayName = buffer.readString(32);
         this.prefix = buffer.readString(16);
         this.suffix = buffer.readString(16);
         this.flags = buffer.readByte();
         this.nameTagVisibility = buffer.readString(32);
         this.color = buffer.readByte();
      }

      if (this.action == 0 || this.action == 3 || this.action == 4) {
         int var2 = buffer.readVarInt();

         for(int var3 = 0; var3 < var2; ++var3) {
            this.members.add(buffer.readString(40));
         }
      }
   }

   @Override
   public void read(PacketByteBuf buffer) {
      buffer.writeString(this.name);
      buffer.writeByte(this.action);
      if (this.action == 0 || this.action == 2) {
         buffer.writeString(this.displayName);
         buffer.writeString(this.prefix);
         buffer.writeString(this.suffix);
         buffer.writeByte(this.flags);
         buffer.writeString(this.nameTagVisibility);
         buffer.writeByte(this.color);
      }

      if (this.action == 0 || this.action == 3 || this.action == 4) {
         buffer.writeVarInt(this.members.size());

         for(String var3 : this.members) {
            buffer.writeString(var3);
         }
      }
   }

   public void handle(ClientPlayPacketHandler c_68ydbefqv) {
      c_68ydbefqv.handleTeam(this);
   }

   @Environment(EnvType.CLIENT)
   public String getName() {
      return this.name;
   }

   @Environment(EnvType.CLIENT)
   public String getDisplayName() {
      return this.displayName;
   }

   @Environment(EnvType.CLIENT)
   public String getPrefix() {
      return this.prefix;
   }

   @Environment(EnvType.CLIENT)
   public String getSuffix() {
      return this.suffix;
   }

   @Environment(EnvType.CLIENT)
   public Collection getMembers() {
      return this.members;
   }

   @Environment(EnvType.CLIENT)
   public int getAction() {
      return this.action;
   }

   @Environment(EnvType.CLIENT)
   public int getFlags() {
      return this.flags;
   }

   @Environment(EnvType.CLIENT)
   public int getColor() {
      return this.color;
   }

   @Environment(EnvType.CLIENT)
   public String getNameTagVisibility() {
      return this.nameTagVisibility;
   }
}
