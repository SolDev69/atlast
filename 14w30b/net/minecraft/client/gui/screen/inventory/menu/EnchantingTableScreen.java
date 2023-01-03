package net.minecraft.client.gui.screen.inventory.menu;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.Window;
import net.minecraft.client.render.model.block.entity.EnchantingTableBookModel;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.menu.EnchantingTableMenu;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Identifier;
import net.minecraft.text.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.util.glu.Project;

@Environment(EnvType.CLIENT)
public class EnchantingTableScreen extends InventoryMenuScreen {
   private static final Identifier SCREEN_TEXTURE = new Identifier("textures/gui/container/enchanting_table.png");
   private static final Identifier BOOK_TEXTURE = new Identifier("textures/entity/enchanting_table_book.png");
   private static final EnchantingTableBookModel BOOK_MODEL = new EnchantingTableBookModel();
   private final PlayerInventory playerInventory;
   private Random random = new Random();
   private EnchantingTableMenu enchantingScreenHandler;
   public int ticksOpen;
   public float nextPageAngle;
   public float pageAngle;
   public float approximatePageAngle;
   public float pageRotationSpeed;
   public float nextTurningSpeed;
   public float turningSpeed;
   ItemStack itemStackToEnchant;
   private final Nameable inventory;

   public EnchantingTableScreen(PlayerInventory playerInventory, World world, Nameable inventory) {
      super(new EnchantingTableMenu(playerInventory, world));
      this.playerInventory = playerInventory;
      this.enchantingScreenHandler = (EnchantingTableMenu)this.menu;
      this.inventory = inventory;
   }

   @Override
   protected void drawForeground(int mouseX, int mouseY) {
      this.textRenderer.drawWithoutShadow(this.inventory.getDisplayName().buildString(), 12, 5, 4210752);
      this.textRenderer.drawWithoutShadow(this.playerInventory.getDisplayName().buildString(), 8, this.backgroundHeight - 96 + 2, 4210752);
   }

   @Override
   public void tick() {
      super.tick();
      this.tickEnchantingScreen();
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      int var4 = (this.titleWidth - this.backgroundWidth) / 2;
      int var5 = (this.height - this.backgroundHeight) / 2;

      for(int var6 = 0; var6 < 3; ++var6) {
         int var7 = mouseX - (var4 + 60);
         int var8 = mouseY - (var5 + 14 + 19 * var6);
         if (var7 >= 0 && var8 >= 0 && var7 < 108 && var8 < 19 && this.enchantingScreenHandler.onButtonClick(this.client.player, var6)) {
            this.client.interactionManager.clickMenuButton(this.enchantingScreenHandler.networkId, var6);
         }
      }
   }

   @Override
   protected void drawBackground(float tickDelta, int mouseX, int mouseY) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.client.getTextureManager().bind(SCREEN_TEXTURE);
      int var4 = (this.titleWidth - this.backgroundWidth) / 2;
      int var5 = (this.height - this.backgroundHeight) / 2;
      this.drawTexture(var4, var5, 0, 0, this.backgroundWidth, this.backgroundHeight);
      GlStateManager.pushMatrix();
      GlStateManager.matrixMode(5889);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      Window var6 = new Window(this.client, this.client.width, this.client.height);
      GlStateManager.viewport(
         (var6.getWidth() - 320) / 2 * var6.getScale(), (var6.getHeight() - 240) / 2 * var6.getScale(), 320 * var6.getScale(), 240 * var6.getScale()
      );
      GlStateManager.translatef(-0.34F, 0.23F, 0.0F);
      Project.gluPerspective(90.0F, 1.3333334F, 9.0F, 80.0F);
      float var7 = 1.0F;
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      Lighting.turnOn();
      GlStateManager.translatef(0.0F, 3.3F, -16.0F);
      GlStateManager.scalef(var7, var7, var7);
      float var8 = 5.0F;
      GlStateManager.scalef(var8, var8, var8);
      GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
      this.client.getTextureManager().bind(BOOK_TEXTURE);
      GlStateManager.rotatef(20.0F, 1.0F, 0.0F, 0.0F);
      float var9 = this.turningSpeed + (this.nextTurningSpeed - this.turningSpeed) * tickDelta;
      GlStateManager.translatef((1.0F - var9) * 0.2F, (1.0F - var9) * 0.1F, (1.0F - var9) * 0.25F);
      GlStateManager.rotatef(-(1.0F - var9) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
      float var10 = this.pageAngle + (this.nextPageAngle - this.pageAngle) * tickDelta + 0.25F;
      float var11 = this.pageAngle + (this.nextPageAngle - this.pageAngle) * tickDelta + 0.75F;
      var10 = (var10 - (float)MathHelper.fastFloor((double)var10)) * 1.6F - 0.3F;
      var11 = (var11 - (float)MathHelper.fastFloor((double)var11)) * 1.6F - 0.3F;
      if (var10 < 0.0F) {
         var10 = 0.0F;
      }

      if (var11 < 0.0F) {
         var11 = 0.0F;
      }

      if (var10 > 1.0F) {
         var10 = 1.0F;
      }

      if (var11 > 1.0F) {
         var11 = 1.0F;
      }

      GlStateManager.enableRescaleNormal();
      BOOK_MODEL.render(null, 0.0F, var10, var11, var9, 0.0F, 0.0625F);
      GlStateManager.disableRescaleNormal();
      Lighting.turnOff();
      GlStateManager.matrixMode(5889);
      GlStateManager.viewport(0, 0, this.client.width, this.client.height);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      Lighting.turnOff();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      EnchantingPhrases.getInstance().setSeed((long)this.enchantingScreenHandler.seed);
      int var12 = this.enchantingScreenHandler.getLapisCount();

      for(int var13 = 0; var13 < 3; ++var13) {
         int var14 = var4 + 60;
         int var15 = var14 + 20;
         byte var16 = 86;
         String var17 = EnchantingPhrases.getInstance().getRandomPhrase();
         this.drawOffset = 0.0F;
         this.client.getTextureManager().bind(SCREEN_TEXTURE);
         int var18 = this.enchantingScreenHandler.enchantingCosts[var13];
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         if (var18 == 0) {
            this.drawTexture(var14, var5 + 14 + 19 * var13, 0, 185, 108, 19);
         } else {
            String var19 = "" + var18;
            TextRenderer var20 = this.client.shadowTextRenderer;
            int var21 = 6839882;
            if ((var12 < var13 + 1 || this.client.player.xpLevel < var18) && !this.client.player.abilities.creativeMode) {
               this.drawTexture(var14, var5 + 14 + 19 * var13, 0, 185, 108, 19);
               this.drawTexture(var14 + 1, var5 + 15 + 19 * var13, 16 * var13, 239, 16, 16);
               var20.drawTrimmed(var17, var15, var5 + 16 + 19 * var13, var16, (var21 & 16711422) >> 1);
               var21 = 4226832;
            } else {
               int var22 = mouseX - (var4 + 60);
               int var23 = mouseY - (var5 + 14 + 19 * var13);
               if (var22 >= 0 && var23 >= 0 && var22 < 108 && var23 < 19) {
                  this.drawTexture(var14, var5 + 14 + 19 * var13, 0, 204, 108, 19);
                  var21 = 16777088;
               } else {
                  this.drawTexture(var14, var5 + 14 + 19 * var13, 0, 166, 108, 19);
               }

               this.drawTexture(var14 + 1, var5 + 15 + 19 * var13, 16 * var13, 223, 16, 16);
               var20.drawTrimmed(var17, var15, var5 + 16 + 19 * var13, var16, var21);
               var21 = 8453920;
            }

            var20 = this.client.textRenderer;
            var20.drawWithShadow(var19, (float)(var15 + 86 - var20.getStringWidth(var19)), (float)(var5 + 16 + 19 * var13 + 7), var21);
         }
      }
   }

   @Override
   public void render(int mouseX, int mouseY, float tickDelta) {
      super.render(mouseX, mouseY, tickDelta);
      boolean var4 = this.client.player.abilities.creativeMode;
      int var5 = this.enchantingScreenHandler.getLapisCount();

      for(int var6 = 0; var6 < 3; ++var6) {
         int var7 = this.enchantingScreenHandler.enchantingCosts[var6];
         int var8 = this.enchantingScreenHandler.enchantmentClues[var6];
         int var9 = var6 + 1;
         if (this.isMouseInRegion(60, 14 + 19 * var6, 108, 17, mouseX, mouseY) && var7 > 0 && var8 >= 0) {
            ArrayList var10 = Lists.newArrayList();
            if (var8 >= 0 && Enchantment.byRawId(var8 & 0xFF) != null) {
               String var11 = Enchantment.byRawId(var8 & 0xFF).getDisplayName((var8 & 0xFF00) >> 8);
               var10.add(Formatting.WHITE.toString() + Formatting.ITALIC.toString() + I18n.translate("container.enchant.clue", var11));
            }

            if (!var4) {
               if (var8 >= 0) {
                  var10.add("");
               }

               if (this.client.player.xpLevel < var7) {
                  var10.add(Formatting.RED.toString() + "Level Requirement: " + this.enchantingScreenHandler.enchantingCosts[var6]);
               } else {
                  String var12 = "";
                  if (var9 == 1) {
                     var12 = I18n.translate("container.enchant.lapis.one");
                  } else {
                     var12 = I18n.translate("container.enchant.lapis.many", var9);
                  }

                  if (var5 >= var9) {
                     var10.add(Formatting.GRAY.toString() + "" + var12);
                  } else {
                     var10.add(Formatting.RED.toString() + "" + var12);
                  }

                  if (var9 == 1) {
                     var12 = I18n.translate("container.enchant.level.one");
                  } else {
                     var12 = I18n.translate("container.enchant.level.many", var9);
                  }

                  var10.add(Formatting.GRAY.toString() + "" + var12);
               }
            }

            this.renderTooltip(var10, mouseX, mouseY);
            break;
         }
      }
   }

   public void tickEnchantingScreen() {
      ItemStack var1 = this.menu.getSlot(0).getStack();
      if (!ItemStack.matches(var1, this.itemStackToEnchant)) {
         this.itemStackToEnchant = var1;

         do {
            this.approximatePageAngle += (float)(this.random.nextInt(4) - this.random.nextInt(4));
         } while(this.nextPageAngle <= this.approximatePageAngle + 1.0F && this.nextPageAngle >= this.approximatePageAngle - 1.0F);
      }

      ++this.ticksOpen;
      this.pageAngle = this.nextPageAngle;
      this.turningSpeed = this.nextTurningSpeed;
      boolean var2 = false;

      for(int var3 = 0; var3 < 3; ++var3) {
         if (this.enchantingScreenHandler.enchantingCosts[var3] != 0) {
            var2 = true;
         }
      }

      if (var2) {
         this.nextTurningSpeed += 0.2F;
      } else {
         this.nextTurningSpeed -= 0.2F;
      }

      this.nextTurningSpeed = MathHelper.clamp(this.nextTurningSpeed, 0.0F, 1.0F);
      float var5 = (this.approximatePageAngle - this.nextPageAngle) * 0.4F;
      float var4 = 0.2F;
      var5 = MathHelper.clamp(var5, -var4, var4);
      this.pageRotationSpeed += (var5 - this.pageRotationSpeed) * 0.9F;
      this.nextPageAngle += this.pageRotationSpeed;
   }
}
