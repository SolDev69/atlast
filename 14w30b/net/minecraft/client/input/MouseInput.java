package net.minecraft.client.input;

import net.minecraft.client.MinecraftClient;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

@Environment(EnvType.CLIENT)
public class MouseInput {
   public int dx;
   public int dy;

   public void lock() {
      if (MinecraftClient.IS_MAC && !Mouse.isInsideWindow()) {
         Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
      }

      Mouse.setGrabbed(true);
      this.dx = 0;
      this.dy = 0;
   }

   public void unlock() {
      Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
      Mouse.setGrabbed(false);
   }

   public void tick() {
      this.dx = Mouse.getDX();
      this.dy = Mouse.getDY();
   }
}
