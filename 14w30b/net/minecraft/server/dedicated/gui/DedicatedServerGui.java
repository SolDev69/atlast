package net.minecraft.server.dedicated.gui;

import com.mojang.util.QueueLogAppender;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.SERVER)
public class DedicatedServerGui extends JComponent {
   private static final Font FONT_MONOSPACE = new Font("Monospaced", 0, 12);
   private static final Logger LOGGER = LogManager.getLogger();
   private DedicatedServer server;

   public static void create(DedicatedServer server) {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception var3) {
      }

      DedicatedServerGui var1 = new DedicatedServerGui(server);
      JFrame var2 = new JFrame("Minecraft server");
      var2.add(var1);
      var2.pack();
      var2.setLocationRelativeTo(null);
      var2.setVisible(true);
      var2.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent event) {
            server.stopRunning();

            while(!server.hasStopped()) {
               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var3) {
                  var3.printStackTrace();
               }
            }

            System.exit(0);
         }
      });
   }

   public DedicatedServerGui(DedicatedServer server) {
      this.server = server;
      this.setPreferredSize(new Dimension(854, 480));
      this.setLayout(new BorderLayout());

      try {
         this.add(this.createLogPanel(), "Center");
         this.add(this.createStatsPanel(), "West");
      } catch (Exception var3) {
         LOGGER.error("Couldn't build server GUI", var3);
      }
   }

   private JComponent createStatsPanel() {
      JPanel var1 = new JPanel(new BorderLayout());
      var1.add(new PlayerStatsGui(this.server), "North");
      var1.add(this.createPlaysPanel(), "Center");
      var1.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
      return var1;
   }

   private JComponent createPlaysPanel() {
      PlayerListGui var1 = new PlayerListGui(this.server);
      JScrollPane var2 = new JScrollPane(var1, 22, 30);
      var2.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
      return var2;
   }

   private JComponent createLogPanel() {
      JPanel var1 = new JPanel(new BorderLayout());
      final JTextArea var2 = new JTextArea();
      final JScrollPane var3 = new JScrollPane(var2, 22, 30);
      var2.setEditable(false);
      var2.setFont(FONT_MONOSPACE);
      final JTextField var4 = new JTextField();
      var4.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent event) {
            String var2 = var4.getText().trim();
            if (var2.length() > 0) {
               DedicatedServerGui.this.server.queueCommand(var2, MinecraftServer.getInstance());
            }

            var4.setText("");
         }
      });
      var2.addFocusListener(new FocusAdapter() {
         @Override
         public void focusGained(FocusEvent event) {
         }
      });
      var1.add(var3, "Center");
      var1.add(var4, "South");
      var1.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
      Thread var5 = new Thread(new Runnable() {
         @Override
         public void run() {
            String var1;
            while((var1 = QueueLogAppender.getNextLogEvent("ServerGuiConsole")) != null) {
               DedicatedServerGui.this.appendToConsole(var2, var3, var1);
            }
         }
      });
      var5.setDaemon(true);
      var5.start();
      return var1;
   }

   public void appendToConsole(JTextArea textArea, JScrollPane scrollPane, String logString) {
      if (!SwingUtilities.isEventDispatchThread()) {
         SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
               DedicatedServerGui.this.appendToConsole(textArea, scrollPane, logString);
            }
         });
      } else {
         Document var4 = textArea.getDocument();
         JScrollBar var5 = scrollPane.getVerticalScrollBar();
         boolean var6 = false;
         if (scrollPane.getViewport().getView() == textArea) {
            var6 = (double)var5.getValue() + var5.getSize().getHeight() + (double)(FONT_MONOSPACE.getSize() * 4) > (double)var5.getMaximum();
         }

         try {
            var4.insertString(var4.getLength(), logString, null);
         } catch (BadLocationException var8) {
         }

         if (var6) {
            var5.setValue(Integer.MAX_VALUE);
         }
      }
   }
}
