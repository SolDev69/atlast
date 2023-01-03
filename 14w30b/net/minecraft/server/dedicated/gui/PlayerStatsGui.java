package net.minecraft.server.dedicated.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.JComponent;
import javax.swing.Timer;
import net.minecraft.server.MinecraftServer;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.SERVER)
public class PlayerStatsGui extends JComponent {
   private static final DecimalFormat AVG_TICK_FORMAT = new DecimalFormat("########0.000");
   private int[] memoryUsePercentage = new int[256];
   private int memoryUsage;
   private String[] lines = new String[11];
   private final MinecraftServer server;

   public PlayerStatsGui(MinecraftServer server) {
      this.server = server;
      this.setPreferredSize(new Dimension(456, 246));
      this.setMinimumSize(new Dimension(456, 246));
      this.setMaximumSize(new Dimension(456, 246));
      new Timer(500, new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent actionEvent) {
            PlayerStatsGui.this.update();
         }
      }).start();
      this.setBackground(Color.BLACK);
   }

   private void update() {
      long var1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
      System.gc();
      this.lines[0] = "Memory use: " + var1 / 1024L / 1024L + " mb (" + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory() + "% free)";
      this.lines[1] = "Avg tick: " + AVG_TICK_FORMAT.format(this.average(this.server.averageTickTimes) * 1.0E-6) + " ms";
      this.repaint();
   }

   private double average(long[] longs) {
      long var2 = 0L;

      for(int var4 = 0; var4 < longs.length; ++var4) {
         var2 += longs[var4];
      }

      return (double)var2 / (double)longs.length;
   }

   @Override
   public void paint(Graphics graphics) {
      graphics.setColor(new Color(16777215));
      graphics.fillRect(0, 0, 456, 246);

      for(int var2 = 0; var2 < 256; ++var2) {
         int var3 = this.memoryUsePercentage[var2 + this.memoryUsage & 0xFF];
         graphics.setColor(new Color(var3 + 28 << 16));
         graphics.fillRect(var2, 100 - var3, 1, var3);
      }

      graphics.setColor(Color.BLACK);

      for(int var4 = 0; var4 < this.lines.length; ++var4) {
         String var5 = this.lines[var4];
         if (var5 != null) {
            graphics.drawString(var5, 32, 116 + var4 * 16);
         }
      }
   }
}
