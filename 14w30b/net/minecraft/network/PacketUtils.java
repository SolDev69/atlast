package net.minecraft.network;

import net.minecraft.network.handler.PacketHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.BlockableEventLoop;

public class PacketUtils {
   public static void ensureOnSameThread(Packet packet, PacketHandler listener, BlockableEventLoop eventLoop) {
      if (!eventLoop.isOnSameThread()) {
         eventLoop.submit(new Runnable() {
            @Override
            public void run() {
               packet.handle(listener);
            }
         });
         throw DifferentThreadException.INSTANCE;
      }
   }
}
