package net.minecraft.server.dedicated;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.SERVER)
public interface IDedicatedServer {
   int getPropertyOrDefault(String key, int defaultValue);

   String getPropertyOrDefault(String key, String defaultValue);

   void setProperty(String key, Object value);

   void saveProperties();

   String getPropertiesFilePath();

   String getIp();

   int getPort();

   String getMotd();

   String getGameVersion();

   int getPlayerCount();

   int getMaxPlayerCount();

   String[] getPlayerNames();

   String getWorldName();

   String getPlugins();

   String executeRconCommand(String command);

   boolean isDebuggingEnabled();

   void info(String message);

   void warn(String message);

   void error(String message);

   void log(String message);
}
