package net.minecraft.server.dedicated.gui;

import java.util.Vector;
import javax.swing.JList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.Tickable;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.SERVER)
public class PlayerListGui extends JList implements Tickable {
   private MinecraftServer server;
   private int tick;

   public PlayerListGui(MinecraftServer server) {
      this.server = server;
      server.addTickable(this);
   }

   @Override
   public void tick() {
      if (this.tick++ % 20 == 0) {
         Vector var1 = new Vector();

         for(int var2 = 0; var2 < this.server.getPlayerManager().players.size(); ++var2) {
            var1.add(((ServerPlayerEntity)this.server.getPlayerManager().players.get(var2)).getName());
         }

         this.setListData(var1);
      }
   }
}
