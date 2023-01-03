package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.menu.StatsListener;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.event.SimpleSoundEvent;
import net.minecraft.entity.Entities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.resource.Identifier;
import net.minecraft.stat.ItemStat;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.Stats;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.input.Mouse;

@Environment(EnvType.CLIENT)
public class StatsScreen extends Screen implements StatsListener {
   protected Screen parent;
   protected String title = "Select world";
   private StatsScreen.GeneralStatsListWidget generalStats;
   private StatsScreen.ItemStatsListWidget itemStats;
   private StatsScreen.StatsListWidget blockStats;
   private StatsScreen.EntityStatsListWidget mobStats;
   private StatHandler statHandler;
   private ListWidget selectedStatsList;
   private boolean downloadingStats = true;

   public StatsScreen(Screen parent, StatHandler statHandler) {
      this.parent = parent;
      this.statHandler = statHandler;
   }

   @Override
   public void init() {
      this.title = I18n.translate("gui.stats");
      this.downloadingStats = true;
      this.client.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Status.REQUEST_STATS));
   }

   @Override
   public void handleMouse() {
      super.handleMouse();
      if (this.selectedStatsList != null) {
         this.selectedStatsList.m_94jnhyuiz();
      }
   }

   public void m_90qvqxcia() {
      this.generalStats = new StatsScreen.GeneralStatsListWidget(this.client);
      this.generalStats.setScrollButtonIds(1, 1);
      this.itemStats = new StatsScreen.ItemStatsListWidget(this.client);
      this.itemStats.setScrollButtonIds(1, 1);
      this.blockStats = new StatsScreen.StatsListWidget(this.client);
      this.blockStats.setScrollButtonIds(1, 1);
      this.mobStats = new StatsScreen.EntityStatsListWidget(this.client);
      this.mobStats.setScrollButtonIds(1, 1);
   }

   public void createButtons() {
      this.buttons.add(new ButtonWidget(0, this.titleWidth / 2 + 4, this.height - 28, 150, 20, I18n.translate("gui.done")));
      this.buttons.add(new ButtonWidget(1, this.titleWidth / 2 - 160, this.height - 52, 80, 20, I18n.translate("stat.generalButton")));
      ButtonWidget var1;
      this.buttons.add(var1 = new ButtonWidget(2, this.titleWidth / 2 - 80, this.height - 52, 80, 20, I18n.translate("stat.blocksButton")));
      ButtonWidget var2;
      this.buttons.add(var2 = new ButtonWidget(3, this.titleWidth / 2, this.height - 52, 80, 20, I18n.translate("stat.itemsButton")));
      ButtonWidget var3;
      this.buttons.add(var3 = new ButtonWidget(4, this.titleWidth / 2 + 80, this.height - 52, 80, 20, I18n.translate("stat.mobsButton")));
      if (this.blockStats.getEntriesSize() == 0) {
         var1.active = false;
      }

      if (this.itemStats.getEntriesSize() == 0) {
         var2.active = false;
      }

      if (this.mobStats.getEntriesSize() == 0) {
         var3.active = false;
      }
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         if (buttonWidget.id == 0) {
            this.client.openScreen(this.parent);
         } else if (buttonWidget.id == 1) {
            this.selectedStatsList = this.generalStats;
         } else if (buttonWidget.id == 3) {
            this.selectedStatsList = this.itemStats;
         } else if (buttonWidget.id == 2) {
            this.selectedStatsList = this.blockStats;
         } else if (buttonWidget.id == 4) {
            this.selectedStatsList = this.mobStats;
         } else {
            this.selectedStatsList.buttonClicked(buttonWidget);
         }
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      if (this.downloadingStats) {
         this.renderBackground();
         this.drawCenteredString(this.textRenderer, I18n.translate("multiplayer.downloadingStats"), this.titleWidth / 2, this.height / 2, 16777215);
         this.drawCenteredString(
            this.textRenderer,
            PROGRESS_BAR_STAGES[(int)(MinecraftClient.getTime() / 150L % (long)PROGRESS_BAR_STAGES.length)],
            this.titleWidth / 2,
            this.height / 2 + this.textRenderer.fontHeight * 2,
            16777215
         );
      } else {
         this.selectedStatsList.render(mouseX, mouseY, tickDelta);
         this.drawCenteredString(this.textRenderer, this.title, this.titleWidth / 2, 20, 16777215);
         super.render(mouseX, mouseY, tickDelta);
      }
   }

   @Override
   public void onStatsReady() {
      if (this.downloadingStats) {
         this.m_90qvqxcia();
         this.createButtons();
         this.selectedStatsList = this.generalStats;
         this.downloadingStats = false;
      }
   }

   @Override
   public boolean shouldPauseGame() {
      return !this.downloadingStats;
   }

   private void renderStatItem(int x, int y, Item item) {
      this.renderIcon(x + 1, y + 1);
      GlStateManager.enableRescaleNormal();
      Lighting.turnOnGui();
      this.itemRenderer.renderGuiItemModel(new ItemStack(item, 1, 0), x + 2, y + 2);
      Lighting.turnOff();
      GlStateManager.disableRescaleNormal();
   }

   private void renderIcon(int x, int y) {
      this.renderIcon(x, y, 0, 0);
   }

   private void renderIcon(int x, int y, int u, int v) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.client.getTextureManager().bind(STATS_ICONS);
      float var5 = 0.0078125F;
      float var6 = 0.0078125F;
      boolean var7 = true;
      boolean var8 = true;
      Tessellator var9 = Tessellator.getInstance();
      BufferBuilder var10 = var9.getBufferBuilder();
      var10.start();
      var10.vertex((double)(x + 0), (double)(y + 18), (double)this.drawOffset, (double)((float)(u + 0) * 0.0078125F), (double)((float)(v + 18) * 0.0078125F));
      var10.vertex((double)(x + 18), (double)(y + 18), (double)this.drawOffset, (double)((float)(u + 18) * 0.0078125F), (double)((float)(v + 18) * 0.0078125F));
      var10.vertex((double)(x + 18), (double)(y + 0), (double)this.drawOffset, (double)((float)(u + 18) * 0.0078125F), (double)((float)(v + 0) * 0.0078125F));
      var10.vertex((double)(x + 0), (double)(y + 0), (double)this.drawOffset, (double)((float)(u + 0) * 0.0078125F), (double)((float)(v + 0) * 0.0078125F));
      var9.end();
   }

   @Environment(EnvType.CLIENT)
   abstract class AbstractStatsListWidget extends ListWidget {
      protected int clickedIconId = -1;
      protected List entries;
      protected Comparator f_18ohullgh;
      protected int f_46cjxrwfg = -1;
      protected int f_16qlblnsc;

      protected AbstractStatsListWidget(MinecraftClient c_13piauvdk) {
         super(c_13piauvdk, StatsScreen.this.titleWidth, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);
         this.setRenderSelection(false);
         this.setHeader(true, 20);
      }

      @Override
      protected void selectEntry(int y, boolean isValid, int lastMouseX, int lastMouseY) {
      }

      @Override
      protected boolean isEntrySelected(int index) {
         return false;
      }

      @Override
      protected void renderBackground() {
         StatsScreen.this.renderBackground();
      }

      @Override
      protected void renderHeader(int x, int y, Tessellator bufferBuilder) {
         if (!Mouse.isButtonDown(0)) {
            this.clickedIconId = -1;
         }

         if (this.clickedIconId == 0) {
            StatsScreen.this.renderIcon(x + 115 - 18, y + 1, 0, 0);
         } else {
            StatsScreen.this.renderIcon(x + 115 - 18, y + 1, 0, 18);
         }

         if (this.clickedIconId == 1) {
            StatsScreen.this.renderIcon(x + 165 - 18, y + 1, 0, 0);
         } else {
            StatsScreen.this.renderIcon(x + 165 - 18, y + 1, 0, 18);
         }

         if (this.clickedIconId == 2) {
            StatsScreen.this.renderIcon(x + 215 - 18, y + 1, 0, 0);
         } else {
            StatsScreen.this.renderIcon(x + 215 - 18, y + 1, 0, 18);
         }

         if (this.f_46cjxrwfg != -1) {
            short var4 = 79;
            byte var5 = 18;
            if (this.f_46cjxrwfg == 1) {
               var4 = 129;
            } else if (this.f_46cjxrwfg == 2) {
               var4 = 179;
            }

            if (this.f_16qlblnsc == 1) {
               var5 = 36;
            }

            StatsScreen.this.renderIcon(x + var4, y + 1, var5, 0);
         }
      }

      @Override
      protected void render(int x, int y) {
         this.clickedIconId = -1;
         if (x >= 79 && x < 115) {
            this.clickedIconId = 0;
         } else if (x >= 129 && x < 165) {
            this.clickedIconId = 1;
         } else if (x >= 179 && x < 215) {
            this.clickedIconId = 2;
         }

         if (this.clickedIconId >= 0) {
            this.click(this.clickedIconId);
            this.client.getSoundManager().play(SimpleSoundEvent.of(new Identifier("gui.button.press"), 1.0F));
         }
      }

      @Override
      protected final int getEntriesSize() {
         return this.entries.size();
      }

      protected final ItemStat m_33gvvmooh(int i) {
         return (ItemStat)this.entries.get(i);
      }

      protected abstract String m_14jhbdeke(int i);

      protected void m_02zulydnm(Stat c_36bxmjvmi, int i, int j, boolean bl) {
         if (c_36bxmjvmi != null) {
            String var5 = c_36bxmjvmi.format(StatsScreen.this.statHandler.getValue(c_36bxmjvmi));
            StatsScreen.this.drawString(
               StatsScreen.this.textRenderer, var5, i - StatsScreen.this.textRenderer.getStringWidth(var5), j + 5, bl ? 16777215 : 9474192
            );
         } else {
            String var6 = "-";
            StatsScreen.this.drawString(
               StatsScreen.this.textRenderer, var6, i - StatsScreen.this.textRenderer.getStringWidth(var6), j + 5, bl ? 16777215 : 9474192
            );
         }
      }

      @Override
      protected void renderDecorations(int mouseX, int mouseY) {
         if (mouseY >= this.yStart && mouseY <= this.yEnd) {
            int var3 = this.getEntryAt(mouseX, mouseY);
            int var4 = this.width / 2 - 92 - 16;
            if (var3 >= 0) {
               if (mouseX < var4 + 40 || mouseX > var4 + 40 + 20) {
                  return;
               }

               ItemStat var11 = this.m_33gvvmooh(var3);
               this.renderStat(var11, mouseX, mouseY);
            } else {
               String var5 = "";
               if (mouseX >= var4 + 115 - 18 && mouseX <= var4 + 115) {
                  var5 = this.m_14jhbdeke(0);
               } else if (mouseX >= var4 + 165 - 18 && mouseX <= var4 + 165) {
                  var5 = this.m_14jhbdeke(1);
               } else {
                  if (mouseX < var4 + 215 - 18 || mouseX > var4 + 215) {
                     return;
                  }

                  var5 = this.m_14jhbdeke(2);
               }

               var5 = ("" + I18n.translate(var5)).trim();
               if (var5.length() > 0) {
                  int var6 = mouseX + 12;
                  int var7 = mouseY - 12;
                  int var8 = StatsScreen.this.textRenderer.getStringWidth(var5);
                  StatsScreen.this.fillGradient(var6 - 3, var7 - 3, var6 + var8 + 3, var7 + 8 + 3, -1073741824, -1073741824);
                  StatsScreen.this.textRenderer.drawWithShadow(var5, (float)var6, (float)var7, -1);
               }
            }
         }
      }

      protected void renderStat(ItemStat stat, int x, int z) {
         if (stat != null) {
            Item var4 = stat.getItem();
            String var5 = ("" + I18n.translate(var4.getTranslationKey() + ".name")).trim();
            if (var5.length() > 0) {
               int var6 = x + 12;
               int var7 = z - 12;
               int var8 = StatsScreen.this.textRenderer.getStringWidth(var5);
               StatsScreen.this.fillGradient(var6 - 3, var7 - 3, var6 + var8 + 3, var7 + 8 + 3, -1073741824, -1073741824);
               StatsScreen.this.textRenderer.drawWithShadow(var5, (float)var6, (float)var7, -1);
            }
         }
      }

      protected void click(int buttonDownTime) {
         if (buttonDownTime != this.f_46cjxrwfg) {
            this.f_46cjxrwfg = buttonDownTime;
            this.f_16qlblnsc = -1;
         } else if (this.f_16qlblnsc == -1) {
            this.f_16qlblnsc = 1;
         } else {
            this.f_46cjxrwfg = -1;
            this.f_16qlblnsc = 0;
         }

         Collections.sort(this.entries, this.f_18ohullgh);
      }
   }

   @Environment(EnvType.CLIENT)
   class EntityStatsListWidget extends ListWidget {
      private final List entries = Lists.newArrayList();

      public EntityStatsListWidget(MinecraftClient c_13piauvdk) {
         super(
            c_13piauvdk, StatsScreen.this.titleWidth, StatsScreen.this.height, 32, StatsScreen.this.height - 64, StatsScreen.this.textRenderer.fontHeight * 4
         );
         this.setRenderSelection(false);

         for(Entities.SpawnEggData var4 : Entities.RAW_ID_TO_SPAWN_EGG_DATA.values()) {
            if (StatsScreen.this.statHandler.getValue(var4.killEntityStat) > 0 || StatsScreen.this.statHandler.getValue(var4.entityKilledByStat) > 0) {
               this.entries.add(var4);
            }
         }
      }

      @Override
      protected int getEntriesSize() {
         return this.entries.size();
      }

      @Override
      protected void selectEntry(int y, boolean isValid, int lastMouseX, int lastMouseY) {
      }

      @Override
      protected boolean isEntrySelected(int index) {
         return false;
      }

      @Override
      protected int getListSize() {
         return this.getEntriesSize() * StatsScreen.this.textRenderer.fontHeight * 4;
      }

      @Override
      protected void renderBackground() {
         StatsScreen.this.renderBackground();
      }

      @Override
      protected void renderEntry(int index, int x, int y, int rowHeight, int bufferBuilder, int mouseX) {
         Entities.SpawnEggData var7 = (Entities.SpawnEggData)this.entries.get(index);
         String var8 = I18n.translate("entity." + Entities.getId(var7.id) + ".name");
         int var9 = StatsScreen.this.statHandler.getValue(var7.killEntityStat);
         int var10 = StatsScreen.this.statHandler.getValue(var7.entityKilledByStat);
         String var11 = I18n.translate("stat.entityKills", var9, var8);
         String var12 = I18n.translate("stat.entityKilledBy", var8, var10);
         if (var9 == 0) {
            var11 = I18n.translate("stat.entityKills.none", var8);
         }

         if (var10 == 0) {
            var12 = I18n.translate("stat.entityKilledBy.none", var8);
         }

         StatsScreen.this.drawString(StatsScreen.this.textRenderer, var8, x + 2 - 10, y + 1, 16777215);
         StatsScreen.this.drawString(
            StatsScreen.this.textRenderer, var11, x + 2, y + 1 + StatsScreen.this.textRenderer.fontHeight, var9 == 0 ? 6316128 : 9474192
         );
         StatsScreen.this.drawString(
            StatsScreen.this.textRenderer, var12, x + 2, y + 1 + StatsScreen.this.textRenderer.fontHeight * 2, var10 == 0 ? 6316128 : 9474192
         );
      }
   }

   @Environment(EnvType.CLIENT)
   class GeneralStatsListWidget extends ListWidget {
      public GeneralStatsListWidget(MinecraftClient c_13piauvdk) {
         super(c_13piauvdk, StatsScreen.this.titleWidth, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);
         this.setRenderSelection(false);
      }

      @Override
      protected int getEntriesSize() {
         return Stats.GENERAL.size();
      }

      @Override
      protected void selectEntry(int y, boolean isValid, int lastMouseX, int lastMouseY) {
      }

      @Override
      protected boolean isEntrySelected(int index) {
         return false;
      }

      @Override
      protected int getListSize() {
         return this.getEntriesSize() * 10;
      }

      @Override
      protected void renderBackground() {
         StatsScreen.this.renderBackground();
      }

      @Override
      protected void renderEntry(int index, int x, int y, int rowHeight, int bufferBuilder, int mouseX) {
         Stat var7 = (Stat)Stats.GENERAL.get(index);
         StatsScreen.this.drawString(StatsScreen.this.textRenderer, var7.getDecoratedName().buildString(), x + 2, y + 1, index % 2 == 0 ? 16777215 : 9474192);
         String var8 = var7.format(StatsScreen.this.statHandler.getValue(var7));
         StatsScreen.this.drawString(
            StatsScreen.this.textRenderer, var8, x + 2 + 213 - StatsScreen.this.textRenderer.getStringWidth(var8), y + 1, index % 2 == 0 ? 16777215 : 9474192
         );
      }
   }

   @Environment(EnvType.CLIENT)
   class ItemStatsListWidget extends StatsScreen.AbstractStatsListWidget {
      public ItemStatsListWidget(MinecraftClient c_13piauvdk) {
         super(c_13piauvdk);
         this.entries = Lists.newArrayList();

         for(ItemStat var4 : Stats.USED) {
            boolean var5 = false;
            int var6 = Item.getRawId(var4.getItem());
            if (StatsScreen.this.statHandler.getValue(var4) > 0) {
               var5 = true;
            } else if (Stats.ITEMS_BROKEN[var6] != null && StatsScreen.this.statHandler.getValue(Stats.ITEMS_BROKEN[var6]) > 0) {
               var5 = true;
            } else if (Stats.ITEMS_CRAFTED[var6] != null && StatsScreen.this.statHandler.getValue(Stats.ITEMS_CRAFTED[var6]) > 0) {
               var5 = true;
            }

            if (var5) {
               this.entries.add(var4);
            }
         }

         this.f_18ohullgh = new Comparator() {
            public int compare(ItemStat c_19xapmrza, ItemStat c_19xapmrza2) {
               int var3 = Item.getRawId(c_19xapmrza.getItem());
               int var4 = Item.getRawId(c_19xapmrza2.getItem());
               Stat var5 = null;
               Stat var6 = null;
               if (ItemStatsListWidget.this.f_46cjxrwfg == 0) {
                  var5 = Stats.ITEMS_BROKEN[var3];
                  var6 = Stats.ITEMS_BROKEN[var4];
               } else if (ItemStatsListWidget.this.f_46cjxrwfg == 1) {
                  var5 = Stats.ITEMS_CRAFTED[var3];
                  var6 = Stats.ITEMS_CRAFTED[var4];
               } else if (ItemStatsListWidget.this.f_46cjxrwfg == 2) {
                  var5 = Stats.ITEMS_USED[var3];
                  var6 = Stats.ITEMS_USED[var4];
               }

               if (var5 != null || var6 != null) {
                  if (var5 == null) {
                     return 1;
                  }

                  if (var6 == null) {
                     return -1;
                  }

                  int var7 = StatsScreen.this.statHandler.getValue(var5);
                  int var8 = StatsScreen.this.statHandler.getValue(var6);
                  if (var7 != var8) {
                     return (var7 - var8) * ItemStatsListWidget.this.f_16qlblnsc;
                  }
               }

               return var3 - var4;
            }
         };
      }

      @Override
      protected void renderHeader(int x, int y, Tessellator bufferBuilder) {
         super.renderHeader(x, y, bufferBuilder);
         if (this.clickedIconId == 0) {
            StatsScreen.this.renderIcon(x + 115 - 18 + 1, y + 1 + 1, 72, 18);
         } else {
            StatsScreen.this.renderIcon(x + 115 - 18, y + 1, 72, 18);
         }

         if (this.clickedIconId == 1) {
            StatsScreen.this.renderIcon(x + 165 - 18 + 1, y + 1 + 1, 18, 18);
         } else {
            StatsScreen.this.renderIcon(x + 165 - 18, y + 1, 18, 18);
         }

         if (this.clickedIconId == 2) {
            StatsScreen.this.renderIcon(x + 215 - 18 + 1, y + 1 + 1, 36, 18);
         } else {
            StatsScreen.this.renderIcon(x + 215 - 18, y + 1, 36, 18);
         }
      }

      @Override
      protected void renderEntry(int index, int x, int y, int rowHeight, int bufferBuilder, int mouseX) {
         ItemStat var7 = this.m_33gvvmooh(index);
         Item var8 = var7.getItem();
         StatsScreen.this.renderStatItem(x + 40, y, var8);
         int var9 = Item.getRawId(var8);
         this.m_02zulydnm(Stats.ITEMS_BROKEN[var9], x + 115, y, index % 2 == 0);
         this.m_02zulydnm(Stats.ITEMS_CRAFTED[var9], x + 165, y, index % 2 == 0);
         this.m_02zulydnm(var7, x + 215, y, index % 2 == 0);
      }

      @Override
      protected String m_14jhbdeke(int i) {
         if (i == 1) {
            return "stat.crafted";
         } else {
            return i == 2 ? "stat.used" : "stat.depleted";
         }
      }
   }

   @Environment(EnvType.CLIENT)
   class StatsListWidget extends StatsScreen.AbstractStatsListWidget {
      public StatsListWidget(MinecraftClient c_13piauvdk) {
         super(c_13piauvdk);
         this.entries = Lists.newArrayList();

         for(ItemStat var4 : Stats.MINED) {
            boolean var5 = false;
            int var6 = Item.getRawId(var4.getItem());
            if (StatsScreen.this.statHandler.getValue(var4) > 0) {
               var5 = true;
            } else if (Stats.ITEMS_USED[var6] != null && StatsScreen.this.statHandler.getValue(Stats.ITEMS_USED[var6]) > 0) {
               var5 = true;
            } else if (Stats.ITEMS_CRAFTED[var6] != null && StatsScreen.this.statHandler.getValue(Stats.ITEMS_CRAFTED[var6]) > 0) {
               var5 = true;
            }

            if (var5) {
               this.entries.add(var4);
            }
         }

         this.f_18ohullgh = new Comparator() {
            public int compare(ItemStat c_19xapmrza, ItemStat c_19xapmrza2) {
               int var3 = Item.getRawId(c_19xapmrza.getItem());
               int var4 = Item.getRawId(c_19xapmrza2.getItem());
               Stat var5 = null;
               Stat var6 = null;
               if (StatsListWidget.this.f_46cjxrwfg == 2) {
                  var5 = Stats.BLOCKS_MINED[var3];
                  var6 = Stats.BLOCKS_MINED[var4];
               } else if (StatsListWidget.this.f_46cjxrwfg == 0) {
                  var5 = Stats.ITEMS_CRAFTED[var3];
                  var6 = Stats.ITEMS_CRAFTED[var4];
               } else if (StatsListWidget.this.f_46cjxrwfg == 1) {
                  var5 = Stats.ITEMS_USED[var3];
                  var6 = Stats.ITEMS_USED[var4];
               }

               if (var5 != null || var6 != null) {
                  if (var5 == null) {
                     return 1;
                  }

                  if (var6 == null) {
                     return -1;
                  }

                  int var7 = StatsScreen.this.statHandler.getValue(var5);
                  int var8 = StatsScreen.this.statHandler.getValue(var6);
                  if (var7 != var8) {
                     return (var7 - var8) * StatsListWidget.this.f_16qlblnsc;
                  }
               }

               return var3 - var4;
            }
         };
      }

      @Override
      protected void renderHeader(int x, int y, Tessellator bufferBuilder) {
         super.renderHeader(x, y, bufferBuilder);
         if (this.clickedIconId == 0) {
            StatsScreen.this.renderIcon(x + 115 - 18 + 1, y + 1 + 1, 18, 18);
         } else {
            StatsScreen.this.renderIcon(x + 115 - 18, y + 1, 18, 18);
         }

         if (this.clickedIconId == 1) {
            StatsScreen.this.renderIcon(x + 165 - 18 + 1, y + 1 + 1, 36, 18);
         } else {
            StatsScreen.this.renderIcon(x + 165 - 18, y + 1, 36, 18);
         }

         if (this.clickedIconId == 2) {
            StatsScreen.this.renderIcon(x + 215 - 18 + 1, y + 1 + 1, 54, 18);
         } else {
            StatsScreen.this.renderIcon(x + 215 - 18, y + 1, 54, 18);
         }
      }

      @Override
      protected void renderEntry(int index, int x, int y, int rowHeight, int bufferBuilder, int mouseX) {
         ItemStat var7 = this.m_33gvvmooh(index);
         Item var8 = var7.getItem();
         StatsScreen.this.renderStatItem(x + 40, y, var8);
         int var9 = Item.getRawId(var8);
         this.m_02zulydnm(Stats.ITEMS_CRAFTED[var9], x + 115, y, index % 2 == 0);
         this.m_02zulydnm(Stats.ITEMS_USED[var9], x + 165, y, index % 2 == 0);
         this.m_02zulydnm(var7, x + 215, y, index % 2 == 0);
      }

      @Override
      protected String m_14jhbdeke(int i) {
         if (i == 0) {
            return "stat.crafted";
         } else {
            return i == 1 ? "stat.used" : "stat.mined";
         }
      }
   }
}
