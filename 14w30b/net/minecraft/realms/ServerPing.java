package net.minecraft.realms;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ServerPing {
   public volatile String nrOfPlayers = "0";
   public volatile long lastPingSnapshot = 0L;
}
