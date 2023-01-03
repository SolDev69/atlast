package net.minecraft.client.gui.screen.world;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ConfirmationListener;
import net.minecraft.client.gui.screen.FatalErrorScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldData;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.WorldSaveInfo;
import net.minecraft.world.storage.WorldStorage;
import net.minecraft.world.storage.WorldStorageException;
import net.minecraft.world.storage.WorldStorageSource;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class SelectWorldScreen extends Screen implements ConfirmationListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final DateFormat dateFormat = new SimpleDateFormat();
   protected Screen parent;
   protected String title = "Select world";
   private boolean worldLoaded;
   private int selectedWorldId;
   private List saves;
   private SelectWorldScreen.WorldListWidget worldList;
   private String worldText;
   private String conversionText;
   private String[] gameModeTexts = new String[4];
   private boolean isInChildScreen;
   private ButtonWidget deleteWorldButton;
   private ButtonWidget playSelectedWorldButton;
   private ButtonWidget renameWorldButton;
   private ButtonWidget recreateWorldButton;

   public SelectWorldScreen(Screen parent) {
      this.parent = parent;
   }

   @Override
   public void init() {
      this.title = I18n.translate("selectWorld.title");

      try {
         this.getSaves();
      } catch (WorldStorageException var2) {
         LOGGER.error("Couldn't load level list", var2);
         this.client.openScreen(new FatalErrorScreen("Unable to load worlds", var2.getMessage()));
         return;
      }

      this.worldText = I18n.translate("selectWorld.world");
      this.conversionText = I18n.translate("selectWorld.conversion");
      this.gameModeTexts[WorldSettings.GameMode.SURVIVAL.getIndex()] = I18n.translate("gameMode.survival");
      this.gameModeTexts[WorldSettings.GameMode.CREATIVE.getIndex()] = I18n.translate("gameMode.creative");
      this.gameModeTexts[WorldSettings.GameMode.ADVENTURE.getIndex()] = I18n.translate("gameMode.adventure");
      this.gameModeTexts[WorldSettings.GameMode.SPECTATOR.getIndex()] = I18n.translate("gameMode.spectator");
      this.worldList = new SelectWorldScreen.WorldListWidget(this.client);
      this.worldList.setScrollButtonIds(4, 5);
      this.addButtons();
   }

   @Override
   public void handleMouse() {
      super.handleMouse();
      this.worldList.m_94jnhyuiz();
   }

   private void getSaves() {
      WorldStorageSource var1 = this.client.getWorldStorageSource();
      this.saves = var1.getAll();
      Collections.sort(this.saves);
      this.selectedWorldId = -1;
   }

   protected String getSaveFileName(int index) {
      return ((WorldSaveInfo)this.saves.get(index)).getFileName();
   }

   protected String getWorldName(int index) {
      String var2 = ((WorldSaveInfo)this.saves.get(index)).getWorldName();
      if (var2 == null || MathHelper.isEmpty(var2)) {
         var2 = I18n.translate("selectWorld.world") + " " + (index + 1);
      }

      return var2;
   }

   public void addButtons() {
      this.buttons
         .add(this.playSelectedWorldButton = new ButtonWidget(1, this.titleWidth / 2 - 154, this.height - 52, 150, 20, I18n.translate("selectWorld.select")));
      this.buttons.add(new ButtonWidget(3, this.titleWidth / 2 + 4, this.height - 52, 150, 20, I18n.translate("selectWorld.create")));
      this.buttons.add(this.renameWorldButton = new ButtonWidget(6, this.titleWidth / 2 - 154, this.height - 28, 72, 20, I18n.translate("selectWorld.rename")));
      this.buttons.add(this.deleteWorldButton = new ButtonWidget(2, this.titleWidth / 2 - 76, this.height - 28, 72, 20, I18n.translate("selectWorld.delete")));
      this.buttons
         .add(this.recreateWorldButton = new ButtonWidget(7, this.titleWidth / 2 + 4, this.height - 28, 72, 20, I18n.translate("selectWorld.recreate")));
      this.buttons.add(new ButtonWidget(0, this.titleWidth / 2 + 82, this.height - 28, 72, 20, I18n.translate("gui.cancel")));
      this.playSelectedWorldButton.active = false;
      this.deleteWorldButton.active = false;
      this.renameWorldButton.active = false;
      this.recreateWorldButton.active = false;
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         if (buttonWidget.id == 2) {
            String var2 = this.getWorldName(this.selectedWorldId);
            if (var2 != null) {
               this.isInChildScreen = true;
               ConfirmScreen var3 = getDeleteWarningPrompt(this, var2, this.selectedWorldId);
               this.client.openScreen(var3);
            }
         } else if (buttonWidget.id == 1) {
            this.loadWorld(this.selectedWorldId);
         } else if (buttonWidget.id == 3) {
            this.client.openScreen(new CreateWorldScreen(this));
         } else if (buttonWidget.id == 6) {
            this.client.openScreen(new EditWorldScreen(this, this.getSaveFileName(this.selectedWorldId)));
         } else if (buttonWidget.id == 0) {
            this.client.openScreen(this.parent);
         } else if (buttonWidget.id == 7) {
            CreateWorldScreen var5 = new CreateWorldScreen(this);
            WorldStorage var6 = this.client.getWorldStorageSource().get(this.getSaveFileName(this.selectedWorldId), false);
            WorldData var4 = var6.loadData();
            var6.waitIfSaving();
            var5.copyWorld(var4);
            this.client.openScreen(var5);
         } else {
            this.worldList.buttonClicked(buttonWidget);
         }
      }
   }

   public void loadWorld(int id) {
      this.client.openScreen(null);
      if (!this.worldLoaded) {
         this.worldLoaded = true;
         String var2 = this.getSaveFileName(id);
         if (var2 == null) {
            var2 = "World" + id;
         }

         String var3 = this.getWorldName(id);
         if (var3 == null) {
            var3 = "World" + id;
         }

         if (this.client.getWorldStorageSource().exists(var2)) {
            this.client.startGame(var2, var3, null);
         }
      }
   }

   @Override
   public void confirmResult(boolean result, int id) {
      if (this.isInChildScreen) {
         this.isInChildScreen = false;
         if (result) {
            WorldStorageSource var3 = this.client.getWorldStorageSource();
            var3.clearRegionIo();
            var3.delete(this.getSaveFileName(id));

            try {
               this.getSaves();
            } catch (WorldStorageException var5) {
               LOGGER.error("Couldn't load level list", var5);
            }
         }

         this.client.openScreen(this);
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.worldList.render(mouseX, mouseY, tickDelta);
      this.drawCenteredString(this.textRenderer, this.title, this.titleWidth / 2, 20, 16777215);
      super.render(mouseX, mouseY, tickDelta);
   }

   public static ConfirmScreen getDeleteWarningPrompt(ConfirmationListener screen, String worldName, int id) {
      String var3 = I18n.translate("selectWorld.deleteQuestion");
      String var4 = "'" + worldName + "' " + I18n.translate("selectWorld.deleteWarning");
      String var5 = I18n.translate("selectWorld.deleteButton");
      String var6 = I18n.translate("gui.cancel");
      return new ConfirmScreen(screen, var3, var4, var5, var6, id);
   }

   @Environment(EnvType.CLIENT)
   class WorldListWidget extends ListWidget {
      public WorldListWidget(MinecraftClient c_13piauvdk) {
         super(c_13piauvdk, SelectWorldScreen.this.titleWidth, SelectWorldScreen.this.height, 32, SelectWorldScreen.this.height - 64, 36);
      }

      @Override
      protected int getEntriesSize() {
         return SelectWorldScreen.this.saves.size();
      }

      @Override
      protected void selectEntry(int y, boolean isValid, int lastMouseX, int lastMouseY) {
         SelectWorldScreen.this.selectedWorldId = y;
         boolean var5 = SelectWorldScreen.this.selectedWorldId >= 0 && SelectWorldScreen.this.selectedWorldId < this.getEntriesSize();
         SelectWorldScreen.this.playSelectedWorldButton.active = var5;
         SelectWorldScreen.this.deleteWorldButton.active = var5;
         SelectWorldScreen.this.renameWorldButton.active = var5;
         SelectWorldScreen.this.recreateWorldButton.active = var5;
         if (isValid && var5) {
            SelectWorldScreen.this.loadWorld(y);
         }
      }

      @Override
      protected boolean isEntrySelected(int index) {
         return index == SelectWorldScreen.this.selectedWorldId;
      }

      @Override
      protected int getListSize() {
         return SelectWorldScreen.this.saves.size() * 36;
      }

      @Override
      protected void renderBackground() {
         SelectWorldScreen.this.renderBackground();
      }

      @Override
      protected void renderEntry(int index, int x, int y, int rowHeight, int bufferBuilder, int mouseX) {
         WorldSaveInfo var7 = (WorldSaveInfo)SelectWorldScreen.this.saves.get(index);
         String var8 = var7.getWorldName();
         if (var8 == null || MathHelper.isEmpty(var8)) {
            var8 = SelectWorldScreen.this.worldText + " " + (index + 1);
         }

         String var9 = var7.getFileName();
         var9 = var9 + " (" + SelectWorldScreen.this.dateFormat.format(new Date(var7.getLastPlayed()));
         var9 = var9 + ")";
         String var10 = "";
         if (var7.isSameVersion()) {
            var10 = SelectWorldScreen.this.conversionText + " " + var10;
         } else {
            var10 = SelectWorldScreen.this.gameModeTexts[var7.getGameMode().getIndex()];
            if (var7.isHardcore()) {
               var10 = Formatting.DARK_RED + I18n.translate("gameMode.hardcore") + Formatting.RESET;
            }

            if (var7.areCheatsEnabled()) {
               var10 = var10 + ", " + I18n.translate("selectWorld.cheats");
            }
         }

         SelectWorldScreen.this.drawString(SelectWorldScreen.this.textRenderer, var8, x + 2, y + 1, 16777215);
         SelectWorldScreen.this.drawString(SelectWorldScreen.this.textRenderer, var9, x + 2, y + 12, 8421504);
         SelectWorldScreen.this.drawString(SelectWorldScreen.this.textRenderer, var10, x + 2, y + 12 + 10, 8421504);
      }
   }
}
