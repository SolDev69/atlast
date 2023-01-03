package net.minecraft.client.gui.screen.inventory;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.GlStateManager;
import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.TextRenderUtils;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.resource.Identifier;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Formatting;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

@Environment(EnvType.CLIENT)
public class BookEditScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Identifier BOOK_TEXTURE = new Identifier("textures/gui/book.png");
   private final PlayerEntity reader;
   private final ItemStack book;
   private final boolean unsigned;
   private boolean dirty;
   private boolean signing;
   private int tickCounter;
   private int widthOffset = 192;
   private int horizontalMargin = 192;
   private int verticalMargin = 1;
   private int currentPage;
   private NbtList pageList;
   private String title = "";
   private List f_65pvddjqr;
   private int f_28bmmrxik = -1;
   private BookEditScreen.BookButton nextPageButton;
   private BookEditScreen.BookButton previousPageButton;
   private ButtonWidget doneButton;
   private ButtonWidget signButton;
   private ButtonWidget finalizeButton;
   private ButtonWidget cancelButton;

   public BookEditScreen(PlayerEntity reader, ItemStack book, boolean unsigned) {
      this.reader = reader;
      this.book = book;
      this.unsigned = unsigned;
      if (book.hasNbt()) {
         NbtCompound var4 = book.getNbt();
         this.pageList = var4.getList("pages", 8);
         if (this.pageList != null) {
            this.pageList = (NbtList)this.pageList.copy();
            this.verticalMargin = this.pageList.size();
            if (this.verticalMargin < 1) {
               this.verticalMargin = 1;
            }
         }
      }

      if (this.pageList == null && unsigned) {
         this.pageList = new NbtList();
         this.pageList.add(new NbtString(""));
         this.verticalMargin = 1;
      }
   }

   @Override
   public void tick() {
      super.tick();
      ++this.tickCounter;
   }

   @Override
   public void init() {
      this.buttons.clear();
      Keyboard.enableRepeatEvents(true);
      if (this.unsigned) {
         this.buttons
            .add(this.signButton = new ButtonWidget(3, this.titleWidth / 2 - 100, 4 + this.horizontalMargin, 98, 20, I18n.translate("book.signButton")));
         this.buttons.add(this.doneButton = new ButtonWidget(0, this.titleWidth / 2 + 2, 4 + this.horizontalMargin, 98, 20, I18n.translate("gui.done")));
         this.buttons
            .add(this.finalizeButton = new ButtonWidget(5, this.titleWidth / 2 - 100, 4 + this.horizontalMargin, 98, 20, I18n.translate("book.finalizeButton")));
         this.buttons.add(this.cancelButton = new ButtonWidget(4, this.titleWidth / 2 + 2, 4 + this.horizontalMargin, 98, 20, I18n.translate("gui.cancel")));
      } else {
         this.buttons.add(this.doneButton = new ButtonWidget(0, this.titleWidth / 2 - 100, 4 + this.horizontalMargin, 200, 20, I18n.translate("gui.done")));
      }

      int var1 = (this.titleWidth - this.widthOffset) / 2;
      byte var2 = 2;
      this.buttons.add(this.nextPageButton = new BookEditScreen.BookButton(1, var1 + 120, var2 + 154, true));
      this.buttons.add(this.previousPageButton = new BookEditScreen.BookButton(2, var1 + 38, var2 + 154, false));
      this.updateButtons();
   }

   @Override
   public void removed() {
      Keyboard.enableRepeatEvents(false);
   }

   private void updateButtons() {
      this.nextPageButton.visible = !this.signing && (this.currentPage < this.verticalMargin - 1 || this.unsigned);
      this.previousPageButton.visible = !this.signing && this.currentPage > 0;
      this.doneButton.visible = !this.unsigned || !this.signing;
      if (this.unsigned) {
         this.signButton.visible = !this.signing;
         this.cancelButton.visible = this.signing;
         this.finalizeButton.visible = this.signing;
         this.finalizeButton.active = this.title.trim().length() > 0;
      }
   }

   private void finalizeBook(boolean signBook) {
      if (this.unsigned && this.dirty) {
         if (this.pageList != null) {
            while(this.pageList.size() > 1) {
               String var2 = this.pageList.getString(this.pageList.size() - 1);
               if (var2.length() != 0) {
                  break;
               }

               this.pageList.remove(this.pageList.size() - 1);
            }

            if (this.book.hasNbt()) {
               NbtCompound var11 = this.book.getNbt();
               var11.put("pages", this.pageList);
            } else {
               this.book.addToNbt("pages", this.pageList);
            }

            String var12 = "MC|BEdit";
            if (signBook) {
               var12 = "MC|BSign";
               this.book.addToNbt("author", new NbtString(this.reader.getName()));
               this.book.addToNbt("title", new NbtString(this.title.trim()));

               for(int var3 = 0; var3 < this.pageList.size(); ++var3) {
                  String var4 = this.pageList.getString(var3);
                  LiteralText var5 = new LiteralText(var4);
                  var4 = Text.Serializer.toJson(var5);
                  this.pageList.set(var3, new NbtString(var4));
               }

               this.book.setItem(Items.WRITTEN_BOOK);
            }

            PacketByteBuf var13 = new PacketByteBuf(Unpooled.buffer());

            try {
               new PacketByteBuf(var13).writeItemStack(this.book);
               this.client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(var12, var13));
            } catch (Exception var9) {
               LOGGER.error("Couldn't send book info", var9);
            } finally {
               var13.release();
            }
         }
      }
   }

   @Override
   protected void buttonClicked(ButtonWidget buttonWidget) {
      if (buttonWidget.active) {
         if (buttonWidget.id == 0) {
            this.client.openScreen(null);
            this.finalizeBook(false);
         } else if (buttonWidget.id == 3 && this.unsigned) {
            this.signing = true;
         } else if (buttonWidget.id == 1) {
            if (this.currentPage < this.verticalMargin - 1) {
               ++this.currentPage;
            } else if (this.unsigned) {
               this.appendNewPage();
               if (this.currentPage < this.verticalMargin - 1) {
                  ++this.currentPage;
               }
            }
         } else if (buttonWidget.id == 2) {
            if (this.currentPage > 0) {
               --this.currentPage;
            }
         } else if (buttonWidget.id == 5 && this.signing) {
            this.finalizeBook(true);
            this.client.openScreen(null);
         } else if (buttonWidget.id == 4 && this.signing) {
            this.signing = false;
         }

         this.updateButtons();
      }
   }

   private void appendNewPage() {
      if (this.pageList != null && this.pageList.size() < 50) {
         this.pageList.add(new NbtString(""));
         ++this.verticalMargin;
         this.dirty = true;
      }
   }

   @Override
   protected void keyPressed(char chr, int key) {
      super.keyPressed(chr, key);
      if (this.unsigned) {
         if (this.signing) {
            this.handleTitleKeyPresses(chr, key);
         } else {
            this.handleBookKeyPresses(chr, key);
         }
      }
   }

   private void handleBookKeyPresses(char chr, int key) {
      switch(chr) {
         case '\u0016':
            this.handleClipboardPaste(Screen.getClipboard());
            return;
         default:
            switch(key) {
               case 14:
                  String var3 = this.getCurrentPageContent();
                  if (var3.length() > 0) {
                     this.setPageContent(var3.substring(0, var3.length() - 1));
                  }

                  return;
               case 28:
               case 156:
                  this.handleClipboardPaste("\n");
                  return;
               default:
                  if (SharedConstants.isValidChatChar(chr)) {
                     this.handleClipboardPaste(Character.toString(chr));
                  }
            }
      }
   }

   private void handleTitleKeyPresses(char chr, int key) {
      switch(key) {
         case 14:
            if (!this.title.isEmpty()) {
               this.title = this.title.substring(0, this.title.length() - 1);
               this.updateButtons();
            }

            return;
         case 28:
         case 156:
            if (!this.title.isEmpty()) {
               this.finalizeBook(true);
               this.client.openScreen(null);
            }

            return;
         default:
            if (this.title.length() < 16 && SharedConstants.isValidChatChar(chr)) {
               this.title = this.title + Character.toString(chr);
               this.updateButtons();
               this.dirty = true;
            }
      }
   }

   private String getCurrentPageContent() {
      return this.pageList != null && this.currentPage >= 0 && this.currentPage < this.pageList.size() ? this.pageList.getString(this.currentPage) : "";
   }

   private void setPageContent(String pageContent) {
      if (this.pageList != null && this.currentPage >= 0 && this.currentPage < this.pageList.size()) {
         this.pageList.set(this.currentPage, new NbtString(pageContent));
         this.dirty = true;
      }
   }

   private void handleClipboardPaste(String content) {
      String var2 = this.getCurrentPageContent();
      String var3 = var2 + content;
      int var4 = this.textRenderer.getTextBoxHeight(var3 + "" + Formatting.BLACK + "_", 118);
      if (var4 <= 128 && var3.length() < 256) {
         this.setPageContent(var3);
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.client.getTextureManager().bind(BOOK_TEXTURE);
      int var4 = (this.titleWidth - this.widthOffset) / 2;
      byte var5 = 2;
      this.drawTexture(var4, var5, 0, 0, this.widthOffset, this.horizontalMargin);
      if (this.signing) {
         String var6 = this.title;
         if (this.unsigned) {
            if (this.tickCounter / 6 % 2 == 0) {
               var6 = var6 + "" + Formatting.BLACK + "_";
            } else {
               var6 = var6 + "" + Formatting.GRAY + "_";
            }
         }

         String var7 = I18n.translate("book.editTitle");
         int var8 = this.textRenderer.getStringWidth(var7);
         this.textRenderer.drawWithoutShadow(var7, var4 + 36 + (116 - var8) / 2, var5 + 16 + 16, 0);
         int var9 = this.textRenderer.getStringWidth(var6);
         this.textRenderer.drawWithoutShadow(var6, var4 + 36 + (116 - var9) / 2, var5 + 48, 0);
         String var10 = I18n.translate("book.byAuthor", this.reader.getName());
         int var11 = this.textRenderer.getStringWidth(var10);
         this.textRenderer.drawWithoutShadow(Formatting.DARK_GRAY + var10, var4 + 36 + (116 - var11) / 2, var5 + 48 + 10, 0);
         String var12 = I18n.translate("book.finalizeWarning");
         this.textRenderer.drawTrimmed(var12, var4 + 36, var5 + 80, 116, 0);
      } else {
         String var14 = I18n.translate("book.pageIndicator", this.currentPage + 1, this.verticalMargin);
         String var15 = "";
         if (this.pageList != null && this.currentPage >= 0 && this.currentPage < this.pageList.size()) {
            var15 = this.pageList.getString(this.currentPage);
         }

         if (this.unsigned) {
            if (this.textRenderer.isRightToLeft()) {
               var15 = var15 + "_";
            } else if (this.tickCounter / 6 % 2 == 0) {
               var15 = var15 + "" + Formatting.BLACK + "_";
            } else {
               var15 = var15 + "" + Formatting.GRAY + "_";
            }
         } else if (this.f_28bmmrxik != this.currentPage) {
            if (WrittenBookItem.isValid(this.book.getNbt())) {
               try {
                  Text var16 = Text.Serializer.fromJson(var15);
                  this.f_65pvddjqr = var16 != null ? TextRenderUtils.wrapText(var16, 116, this.textRenderer, true, true) : null;
               } catch (JsonParseException var13) {
                  this.f_65pvddjqr = null;
               }
            } else {
               LiteralText var17 = new LiteralText(Formatting.DARK_RED.toString() + "* Invalid book tag *");
               this.f_65pvddjqr = Lists.newArrayList(var17);
            }

            this.f_28bmmrxik = this.currentPage;
         }

         int var18 = this.textRenderer.getStringWidth(var14);
         this.textRenderer.drawWithoutShadow(var14, var4 - var18 + this.widthOffset - 44, var5 + 16, 0);
         if (this.f_65pvddjqr == null) {
            this.textRenderer.drawTrimmed(var15, var4 + 36, var5 + 16 + 16, 116, 0);
         } else {
            int var19 = Math.min(128 / this.textRenderer.fontHeight, this.f_65pvddjqr.size());

            for(int var20 = 0; var20 < var19; ++var20) {
               Text var22 = (Text)this.f_65pvddjqr.get(var20);
               this.textRenderer.drawWithoutShadow(var22.buildString(), var4 + 36, var5 + 16 + 16 + var20 * this.textRenderer.fontHeight, 0);
            }

            Text var21 = this.m_77zwelvin(mouseX, mouseY);
            if (var21 != null) {
               this.m_44svopzkj(var21, mouseX, mouseY);
            }
         }
      }

      super.render(mouseX, mouseY, tickDelta);
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 0) {
         Text var4 = this.m_77zwelvin(mouseX, mouseY);
         if (this.m_35nuugpna(var4)) {
            return;
         }
      }

      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   protected boolean m_35nuugpna(Text c_21uoltggz) {
      ClickEvent var2 = c_21uoltggz == null ? null : c_21uoltggz.getStyle().getClickEvent();
      if (var2 == null) {
         return false;
      } else {
         boolean var3 = super.m_35nuugpna(c_21uoltggz);
         if (var3 && var2.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.client.openScreen(null);
         }

         return var3;
      }
   }

   public Text m_77zwelvin(int i, int j) {
      if (this.f_65pvddjqr == null) {
         return null;
      } else {
         int var3 = i - (this.titleWidth - this.widthOffset) / 2 - 36;
         int var4 = j - 2 - 16 - 16;
         if (var3 >= 0 && var4 >= 0) {
            int var5 = Math.min(128 / this.textRenderer.fontHeight, this.f_65pvddjqr.size());
            if (var3 <= 116 && var4 < this.client.textRenderer.fontHeight * var5 + var5) {
               int var6 = var4 / this.client.textRenderer.fontHeight;
               if (var6 >= 0 && var6 < this.f_65pvddjqr.size()) {
                  Text var7 = (Text)this.f_65pvddjqr.get(var6);
                  int var8 = 0;

                  for(Text var10 : var7) {
                     if (var10 instanceof LiteralText) {
                        var8 += this.client.textRenderer.getStringWidth(((LiteralText)var10).getRawString());
                        if (var8 > var3) {
                           return var10;
                        }
                     }
                  }
               }

               return null;
            } else {
               return null;
            }
         } else {
            return null;
         }
      }
   }

   @Environment(EnvType.CLIENT)
   static class BookButton extends ButtonWidget {
      private final boolean clickable;

      public BookButton(int i, int j, int k, boolean bl) {
         super(i, j, k, 23, 13, "");
         this.clickable = bl;
      }

      @Override
      public void render(MinecraftClient client, int mouseX, int mouseY) {
         if (this.visible) {
            boolean var4 = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            client.getTextureManager().bind(BookEditScreen.BOOK_TEXTURE);
            int var5 = 0;
            int var6 = 192;
            if (var4) {
               var5 += 23;
            }

            if (!this.clickable) {
               var6 += 13;
            }

            this.drawTexture(this.x, this.y, var5, var6, 23, 13);
         }
      }
   }
}
