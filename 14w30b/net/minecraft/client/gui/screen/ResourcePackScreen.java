package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.screen.resourcepack.AbstractResourcePackEntryWidget;
import net.minecraft.client.gui.screen.resourcepack.AvailableResourcePackListWidget;
import net.minecraft.client.gui.screen.resourcepack.DefaultResourcePackEntryWidget;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackEntryWidget;
import net.minecraft.client.gui.screen.resourcepack.SelectedResourcePackListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.pack.ResourcePackLoader;
import net.minecraft.util.Utils;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Sys;

@Environment(EnvType.CLIENT)
public class ResourcePackScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private Screen parent;
   private List unnamedResourcePackEntryList;
   private List namedResourcePackEntryList;
   private AvailableResourcePackListWidget availableList;
   private SelectedResourcePackListWidget selectedList;
   private boolean f_00zudsrll = false;

   public ResourcePackScreen(Screen parent) {
      this.parent = parent;
   }

   @Override
   public void init() {
      this.buttons.add(new OptionButtonWidget(2, this.titleWidth / 2 - 154, this.height - 48, I18n.translate("resourcePack.openFolder")));
      this.buttons.add(new OptionButtonWidget(1, this.titleWidth / 2 + 4, this.height - 48, I18n.translate("gui.done")));
      this.unnamedResourcePackEntryList = Lists.newArrayList();
      this.namedResourcePackEntryList = Lists.newArrayList();
      ResourcePackLoader var1 = this.client.getResourcePackLoader();
      var1.loadResourcePacks();
      ArrayList var2 = Lists.newArrayList(var1.getAvailableResourcePacks());
      var2.removeAll(var1.getAppliedResourcePacks());

      for(ResourcePackLoader.Entry var4 : var2) {
         this.unnamedResourcePackEntryList.add(new ResourcePackEntryWidget(this, var4));
      }

      for(ResourcePackLoader.Entry var6 : Lists.reverse(var1.getAppliedResourcePacks())) {
         this.namedResourcePackEntryList.add(new ResourcePackEntryWidget(this, var6));
      }

      this.namedResourcePackEntryList.add(new DefaultResourcePackEntryWidget(this));
      this.availableList = new AvailableResourcePackListWidget(this.client, 200, this.height, this.unnamedResourcePackEntryList);
      this.availableList.setXPos(this.titleWidth / 2 - 4 - 200);
      this.availableList.setScrollButtonIds(7, 8);
      this.selectedList = new SelectedResourcePackListWidget(this.client, 200, this.height, this.namedResourcePackEntryList);
      this.selectedList.setXPos(this.titleWidth / 2 + 4);
      this.selectedList.setScrollButtonIds(7, 8);
   }

   @Override
   public void handleMouse() {
      super.handleMouse();
      this.selectedList.m_94jnhyuiz();
      this.availableList.m_94jnhyuiz();
   }

   public boolean isResourcePackEntryNamed(AbstractResourcePackEntryWidget abstractResourcePackEntryWidget) {
      return this.namedResourcePackEntryList.contains(abstractResourcePackEntryWidget);
   }

   public List getListContainingResourcePackEntry(AbstractResourcePackEntryWidget abstractResourcePackEntryList) {
      return this.isResourcePackEntryNamed(abstractResourcePackEntryList) ? this.namedResourcePackEntryList : this.unnamedResourcePackEntryList;
   }

   public List getUnnamedResourcePackEntryList() {
      return this.unnamedResourcePackEntryList;
   }

   public List getNamedResourcePackEntryList() {
      return this.namedResourcePackEntryList;
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         if (buttonWidget.id == 2) {
            File var2 = this.client.getResourcePackLoader().getDirectory();
            String var3 = var2.getAbsolutePath();
            if (Utils.getOS() == Utils.OS.MACOS) {
               try {
                  LOGGER.info(var3);
                  Runtime.getRuntime().exec(new String[]{"/usr/bin/open", var3});
                  return;
               } catch (IOException var9) {
                  LOGGER.error("Couldn't open file", var9);
               }
            } else if (Utils.getOS() == Utils.OS.WINDOWS) {
               String var4 = String.format("cmd.exe /C start \"Open file\" \"%s\"", var3);

               try {
                  Runtime.getRuntime().exec(var4);
                  return;
               } catch (IOException var8) {
                  LOGGER.error("Couldn't open file", var8);
               }
            }

            boolean var13 = false;

            try {
               Class var5 = Class.forName("java.awt.Desktop");
               Object var6 = var5.getMethod("getDesktop").invoke(null);
               var5.getMethod("browse", URI.class).invoke(var6, var2.toURI());
            } catch (Throwable var7) {
               LOGGER.error("Couldn't open link", var7);
               var13 = true;
            }

            if (var13) {
               LOGGER.info("Opening via system class!");
               Sys.openURL("file://" + var3);
            }
         } else if (buttonWidget.id == 1) {
            if (this.f_00zudsrll) {
               ArrayList var10 = Lists.newArrayList();

               for(AbstractResourcePackEntryWidget var14 : this.namedResourcePackEntryList) {
                  if (var14 instanceof ResourcePackEntryWidget) {
                     var10.add(((ResourcePackEntryWidget)var14).getEntry());
                  }
               }

               Collections.reverse(var10);
               this.client.getResourcePackLoader().applyResourcePacks(var10);
               this.client.options.resourcePacks.clear();

               for(ResourcePackLoader.Entry var15 : var10) {
                  this.client.options.resourcePacks.add(var15.getName());
               }

               this.client.options.save();
               this.client.reloadResources();
            }

            this.client.openScreen(this.parent);
         }
      }
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.availableList.mouseClicked(mouseX, mouseY, mouseButton);
      this.selectedList.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
      super.mouseReleased(mouseX, mouseY, mouseButton);
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      this.drawBackgroundTexture(0);
      this.availableList.render(mouseX, mouseY, tickDelta);
      this.selectedList.render(mouseX, mouseY, tickDelta);
      this.drawCenteredString(this.textRenderer, I18n.translate("resourcePack.title"), this.titleWidth / 2, 16, 16777215);
      this.drawCenteredString(this.textRenderer, I18n.translate("resourcePack.folderInfo"), this.titleWidth / 2 - 77, this.height - 26, 8421504);
      super.render(mouseX, mouseY, tickDelta);
   }

   public void m_60oatrcpx() {
      this.f_00zudsrll = true;
   }
}
