package net.minecraft.client.gui.screen.inventory.menu;

import java.util.Random;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class EnchantingPhrases {
   private static final EnchantingPhrases INSTANCE = new EnchantingPhrases();
   private Random random = new Random();
   private String[] words = "the elder scrolls klaatu berata niktu xyzzy bless curse light darkness fire air earth water hot dry cold wet ignite snuff embiggen twist shorten stretch fiddle destroy imbue galvanize enchant free limited range of towards inside sphere cube self other ball mental physical grow shrink demon elemental spirit animal creature beast humanoid undead fresh stale "
      .split(" ");

   private EnchantingPhrases() {
   }

   public static EnchantingPhrases getInstance() {
      return INSTANCE;
   }

   public String getRandomPhrase() {
      int var1 = this.random.nextInt(2) + 3;
      String var2 = "";

      for(int var3 = 0; var3 < var1; ++var3) {
         if (var3 > 0) {
            var2 = var2 + " ";
         }

         var2 = var2 + this.words[this.random.nextInt(this.words.length)];
      }

      return var2;
   }

   public void setSeed(long seed) {
      this.random.setSeed(seed);
   }
}
