package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class IntegratedPlayerManager extends PlayerManager {
   private NbtCompound playerData;

   public IntegratedPlayerManager(IntegratedServer server) {
      super(server);
      this.updateViewDistance(10);
   }

   @Override
   protected void saveData(ServerPlayerEntity player) {
      if (player.getName().equals(this.getServer().getUserName())) {
         this.playerData = new NbtCompound();
         player.writeEntityNbt(this.playerData);
      }

      super.saveData(player);
   }

   @Override
   public String canLogin(SocketAddress address, GameProfile profile) {
      return profile.getName().equalsIgnoreCase(this.getServer().getUserName()) && this.get(profile.getName()) != null
         ? "That name is already taken."
         : super.canLogin(address, profile);
   }

   public IntegratedServer getServer() {
      return (IntegratedServer)super.getServer();
   }

   @Override
   public NbtCompound getSinglePlayerData() {
      return this.playerData;
   }
}
