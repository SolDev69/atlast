package net.minecraft;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.LabelWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.Int2ObjectHashMap;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class C_37rnsjynt extends EntryListWidget {
   private final List f_98xpxbdif = Lists.newArrayList();
   private final Int2ObjectHashMap f_25wpdvmyd = new Int2ObjectHashMap();
   private final List f_94rzrwtxm = Lists.newArrayList();
   private final C_37rnsjynt.C_30sltyycm[][] f_83wdcpmes;
   private int f_64sfzdawd;
   private C_37rnsjynt.C_59lpdyoky f_27phqbomr;
   private GuiElement f_33tiptpim;

   public C_37rnsjynt(
      MinecraftClient c_13piauvdk, int i, int j, int k, int l, int m, C_37rnsjynt.C_59lpdyoky c_59lpdyoky, C_37rnsjynt.C_30sltyycm[]... c_30sltyycms
   ) {
      super(c_13piauvdk, i, j, k, l, m);
      this.f_27phqbomr = c_59lpdyoky;
      this.f_83wdcpmes = c_30sltyycms;
      this.centerAlongY = false;
      this.m_73bazyiun();
      this.m_57xxezhwp();
   }

   private void m_73bazyiun() {
      for(C_37rnsjynt.C_30sltyycm[] var4 : this.f_83wdcpmes) {
         for(int var5 = 0; var5 < var4.length; var5 += 2) {
            C_37rnsjynt.C_30sltyycm var6 = var4[var5];
            C_37rnsjynt.C_30sltyycm var7 = var5 < var4.length - 1 ? var4[var5 + 1] : null;
            GuiElement var8 = this.m_60tegvtse(var6, 0, var7 == null);
            GuiElement var9 = this.m_60tegvtse(var7, 160, var6 == null);
            C_37rnsjynt.C_22axpichd var10 = new C_37rnsjynt.C_22axpichd(var8, var9);
            this.f_98xpxbdif.add(var10);
            if (var6 != null && var8 != null) {
               this.f_25wpdvmyd.put(var6.m_92ocxyipu(), var8);
               if (var8 instanceof TextFieldWidget) {
                  this.f_94rzrwtxm.add((TextFieldWidget)var8);
               }
            }

            if (var7 != null && var9 != null) {
               this.f_25wpdvmyd.put(var7.m_92ocxyipu(), var9);
               if (var9 instanceof TextFieldWidget) {
                  this.f_94rzrwtxm.add((TextFieldWidget)var9);
               }
            }
         }
      }
   }

   private void m_57xxezhwp() {
      this.f_98xpxbdif.clear();

      for(int var1 = 0; var1 < this.f_83wdcpmes[this.f_64sfzdawd].length; var1 += 2) {
         C_37rnsjynt.C_30sltyycm var2 = this.f_83wdcpmes[this.f_64sfzdawd][var1];
         C_37rnsjynt.C_30sltyycm var3 = var1 < this.f_83wdcpmes[this.f_64sfzdawd].length - 1 ? this.f_83wdcpmes[this.f_64sfzdawd][var1 + 1] : null;
         GuiElement var4 = (GuiElement)this.f_25wpdvmyd.get(var2.m_92ocxyipu());
         GuiElement var5 = var3 != null ? (GuiElement)this.f_25wpdvmyd.get(var3.m_92ocxyipu()) : null;
         C_37rnsjynt.C_22axpichd var6 = new C_37rnsjynt.C_22axpichd(var4, var5);
         this.f_98xpxbdif.add(var6);
      }
   }

   public int m_43mhodhmv() {
      return this.f_64sfzdawd;
   }

   public int m_53kgxgxjx() {
      return this.f_83wdcpmes.length;
   }

   public GuiElement m_58djpuafj() {
      return this.f_33tiptpim;
   }

   public void m_82ubxazmc() {
      if (this.f_64sfzdawd > 0) {
         int var1 = this.f_64sfzdawd--;
         this.m_57xxezhwp();
         this.m_58dwqquvm(var1, this.f_64sfzdawd);
         this.scrollAmount = 0.0F;
      }
   }

   public void m_95jabtuaf() {
      if (this.f_64sfzdawd < this.f_83wdcpmes.length - 1) {
         int var1 = this.f_64sfzdawd++;
         this.m_57xxezhwp();
         this.m_58dwqquvm(var1, this.f_64sfzdawd);
         this.scrollAmount = 0.0F;
      }
   }

   public GuiElement m_24dbyhzbl(int i) {
      return (GuiElement)this.f_25wpdvmyd.get(i);
   }

   private void m_58dwqquvm(int i, int j) {
      for(C_37rnsjynt.C_30sltyycm var6 : this.f_83wdcpmes[i]) {
         if (var6 != null) {
            this.m_20wditjio((GuiElement)this.f_25wpdvmyd.get(var6.m_92ocxyipu()), false);
         }
      }

      for(C_37rnsjynt.C_30sltyycm var10 : this.f_83wdcpmes[j]) {
         if (var10 != null) {
            this.m_20wditjio((GuiElement)this.f_25wpdvmyd.get(var10.m_92ocxyipu()), true);
         }
      }
   }

   private void m_20wditjio(GuiElement c_77jecrlbg, boolean bl) {
      if (c_77jecrlbg instanceof ButtonWidget) {
         ((ButtonWidget)c_77jecrlbg).visible = bl;
      } else if (c_77jecrlbg instanceof TextFieldWidget) {
         ((TextFieldWidget)c_77jecrlbg).setVisible(bl);
      } else if (c_77jecrlbg instanceof LabelWidget) {
         ((LabelWidget)c_77jecrlbg).skip = bl;
      }
   }

   private GuiElement m_60tegvtse(C_37rnsjynt.C_30sltyycm c_30sltyycm, int i, boolean bl) {
      if (c_30sltyycm instanceof C_37rnsjynt.C_95ddunyyj) {
         return this.m_99nonmlvl(this.width / 2 - 155 + i, 0, (C_37rnsjynt.C_95ddunyyj)c_30sltyycm);
      } else if (c_30sltyycm instanceof C_37rnsjynt.C_60wnrvuxr) {
         return this.m_83fngvujl(this.width / 2 - 155 + i, 0, (C_37rnsjynt.C_60wnrvuxr)c_30sltyycm);
      } else if (c_30sltyycm instanceof C_37rnsjynt.C_04tnjsgih) {
         return this.m_28veqzvil(this.width / 2 - 155 + i, 0, (C_37rnsjynt.C_04tnjsgih)c_30sltyycm);
      } else {
         return c_30sltyycm instanceof C_37rnsjynt.C_37zmgzpzj ? this.m_09izkgbgm(this.width / 2 - 155 + i, 0, (C_37rnsjynt.C_37zmgzpzj)c_30sltyycm, bl) : null;
      }
   }

   @Override
   public boolean mouseClicked(int mouseX, int mouseY, int button) {
      boolean var4 = super.mouseClicked(mouseX, mouseY, button);
      int var5 = this.getEntryAt(mouseX, mouseY);
      if (var5 >= 0) {
         C_37rnsjynt.C_22axpichd var6 = this.getEntry(var5);
         if (this.f_33tiptpim != var6.f_49pvqdmtt && this.f_33tiptpim != null && this.f_33tiptpim instanceof TextFieldWidget) {
            ((TextFieldWidget)this.f_33tiptpim).setFocused(false);
         }

         this.f_33tiptpim = var6.f_49pvqdmtt;
      }

      return var4;
   }

   private C_97enwlcph m_99nonmlvl(int i, int j, C_37rnsjynt.C_95ddunyyj c_95ddunyyj) {
      C_97enwlcph var4 = new C_97enwlcph(
         this.f_27phqbomr,
         c_95ddunyyj.m_92ocxyipu(),
         i,
         j,
         c_95ddunyyj.m_17crajuqt(),
         c_95ddunyyj.m_51rmyyiey(),
         c_95ddunyyj.m_58cdexcvh(),
         c_95ddunyyj.m_61rctwavz(),
         c_95ddunyyj.m_54uzgnwfa()
      );
      var4.visible = c_95ddunyyj.m_19efnofwl();
      return var4;
   }

   private C_13fsheoyt m_83fngvujl(int i, int j, C_37rnsjynt.C_60wnrvuxr c_60wnrvuxr) {
      C_13fsheoyt var4 = new C_13fsheoyt(this.f_27phqbomr, c_60wnrvuxr.m_92ocxyipu(), i, j, c_60wnrvuxr.m_17crajuqt(), c_60wnrvuxr.m_35xrnhbmd());
      var4.visible = c_60wnrvuxr.m_19efnofwl();
      return var4;
   }

   private TextFieldWidget m_28veqzvil(int i, int j, C_37rnsjynt.C_04tnjsgih c_04tnjsgih) {
      TextFieldWidget var4 = new TextFieldWidget(c_04tnjsgih.m_92ocxyipu(), this.client.textRenderer, i, j, 150, 20);
      var4.setText(c_04tnjsgih.m_17crajuqt());
      var4.m_30nnadsqo(this.f_27phqbomr);
      var4.setVisible(c_04tnjsgih.m_19efnofwl());
      var4.m_75zfafuxs(c_04tnjsgih.m_49uqaxjgz());
      return var4;
   }

   private LabelWidget m_09izkgbgm(int i, int j, C_37rnsjynt.C_37zmgzpzj c_37zmgzpzj, boolean bl) {
      LabelWidget var5;
      if (bl) {
         var5 = new LabelWidget(this.client.textRenderer, c_37zmgzpzj.m_92ocxyipu(), i, j, this.width - i * 2, 20, -1);
      } else {
         var5 = new LabelWidget(this.client.textRenderer, c_37zmgzpzj.m_92ocxyipu(), i, j, 150, 20, -1);
      }

      var5.skip = c_37zmgzpzj.m_19efnofwl();
      var5.m_22radzyzf(c_37zmgzpzj.m_17crajuqt());
      var5.m_34zfrlvbr();
      return var5;
   }

   public void m_85eqxnfua(char c, int i) {
      if (this.f_33tiptpim instanceof TextFieldWidget) {
         TextFieldWidget var3 = (TextFieldWidget)this.f_33tiptpim;
         if (c != 22) {
            if (i == 15) {
               var3.setFocused(false);
               int var13 = this.f_94rzrwtxm.indexOf(this.f_33tiptpim);
               if (Screen.isShiftDown()) {
                  if (var13 == 0) {
                     var13 = this.f_94rzrwtxm.size() - 1;
                  } else {
                     --var13;
                  }
               } else if (var13 == this.f_94rzrwtxm.size() - 1) {
                  var13 = 0;
               } else {
                  ++var13;
               }

               this.f_33tiptpim = (GuiElement)this.f_94rzrwtxm.get(var13);
               var3 = (TextFieldWidget)this.f_33tiptpim;
               var3.setFocused(true);
               int var15 = var3.y + this.entryHeight;
               int var16 = var3.y;
               if (var15 > this.yEnd) {
                  this.scrollAmount += (float)(var15 - this.yEnd);
               } else if (var16 < this.yStart) {
                  this.scrollAmount = (float)var16;
               }
            } else {
               var3.keyPressed(c, i);
            }
         } else {
            String var4 = Screen.getClipboard();
            String[] var5 = var4.split(";");
            int var6 = this.f_94rzrwtxm.indexOf(this.f_33tiptpim);
            int var7 = var6;

            for(String var11 : var5) {
               ((TextFieldWidget)this.f_94rzrwtxm.get(var7)).setText(var11);
               if (var7 == this.f_94rzrwtxm.size() - 1) {
                  var7 = 0;
               } else {
                  ++var7;
               }

               if (var7 == var6) {
                  break;
               }
            }
         }
      }
   }

   public C_37rnsjynt.C_22axpichd getEntry(int i) {
      return (C_37rnsjynt.C_22axpichd)this.f_98xpxbdif.get(i);
   }

   @Override
   public int getEntriesSize() {
      return this.f_98xpxbdif.size();
   }

   @Override
   public int getRowWidth() {
      return 400;
   }

   @Override
   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 32;
   }

   @Environment(EnvType.CLIENT)
   public static class C_04tnjsgih extends C_37rnsjynt.C_30sltyycm {
      private final Predicate f_30sbhtkkd;

      public C_04tnjsgih(int i, String string, boolean bl, Predicate predicate) {
         super(i, string, bl);
         this.f_30sbhtkkd = (Predicate)Objects.firstNonNull(predicate, Predicates.alwaysTrue());
      }

      public Predicate m_49uqaxjgz() {
         return this.f_30sbhtkkd;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class C_22axpichd implements EntryListWidget.Entry {
      private final MinecraftClient f_38uvxsabr = MinecraftClient.getInstance();
      private final GuiElement f_08lozyvms;
      private final GuiElement f_20ecaxira;
      private GuiElement f_49pvqdmtt;

      public C_22axpichd(GuiElement c_77jecrlbg, GuiElement c_77jecrlbg2) {
         this.f_08lozyvms = c_77jecrlbg;
         this.f_20ecaxira = c_77jecrlbg2;
      }

      public GuiElement m_50ucuzcll() {
         return this.f_08lozyvms;
      }

      public GuiElement m_72ddynkhz() {
         return this.f_20ecaxira;
      }

      @Override
      public void render(int id, int x, int y, int width, int height, int bufferBuilder, int mouseX, boolean mouseY) {
         this.m_07wwkzkln(this.f_08lozyvms, y, bufferBuilder, mouseX, false);
         this.m_07wwkzkln(this.f_20ecaxira, y, bufferBuilder, mouseX, false);
      }

      private void m_07wwkzkln(GuiElement c_77jecrlbg, int i, int j, int k, boolean bl) {
         if (c_77jecrlbg != null) {
            if (c_77jecrlbg instanceof ButtonWidget) {
               this.m_78nclosrd((ButtonWidget)c_77jecrlbg, i, j, k, bl);
            } else if (c_77jecrlbg instanceof TextFieldWidget) {
               this.m_11cqhrtad((TextFieldWidget)c_77jecrlbg, i, bl);
            } else if (c_77jecrlbg instanceof LabelWidget) {
               this.m_02jrzrvsz((LabelWidget)c_77jecrlbg, i, j, k, bl);
            }
         }
      }

      private void m_78nclosrd(ButtonWidget c_62wzojhks, int i, int j, int k, boolean bl) {
         c_62wzojhks.y = i;
         if (!bl) {
            c_62wzojhks.render(this.f_38uvxsabr, j, k);
         }
      }

      private void m_11cqhrtad(TextFieldWidget c_53lkvtkbk, int i, boolean bl) {
         c_53lkvtkbk.y = i;
         if (!bl) {
            c_53lkvtkbk.render();
         }
      }

      private void m_02jrzrvsz(LabelWidget c_02runjfxa, int i, int j, int k, boolean bl) {
         c_02runjfxa.y = i;
         if (!bl) {
            c_02runjfxa.render(this.f_38uvxsabr, j, k);
         }
      }

      @Override
      public void m_82anuocxe(int i, int j, int k) {
         this.m_07wwkzkln(this.f_08lozyvms, k, 0, 0, true);
         this.m_07wwkzkln(this.f_20ecaxira, k, 0, 0, true);
      }

      @Override
      public boolean mouseClicked(int id, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
         boolean var7 = this.m_75srmivld(this.f_08lozyvms, mouseX, mouseY, button);
         boolean var8 = this.m_75srmivld(this.f_20ecaxira, mouseX, mouseY, button);
         return var7 || var8;
      }

      private boolean m_75srmivld(GuiElement c_77jecrlbg, int i, int j, int k) {
         if (c_77jecrlbg == null) {
            return false;
         } else if (c_77jecrlbg instanceof ButtonWidget) {
            return this.m_84pkzpirk((ButtonWidget)c_77jecrlbg, i, j, k);
         } else {
            if (c_77jecrlbg instanceof TextFieldWidget) {
               this.m_14lrvflqw((TextFieldWidget)c_77jecrlbg, i, j, k);
            }

            return false;
         }
      }

      private boolean m_84pkzpirk(ButtonWidget c_62wzojhks, int i, int j, int k) {
         boolean var5 = c_62wzojhks.isMouseOver(this.f_38uvxsabr, i, j);
         if (var5) {
            this.f_49pvqdmtt = c_62wzojhks;
         }

         return var5;
      }

      private void m_14lrvflqw(TextFieldWidget c_53lkvtkbk, int i, int j, int k) {
         c_53lkvtkbk.mouseClicked(i, j, k);
         if (c_53lkvtkbk.isFocused()) {
            this.f_49pvqdmtt = c_53lkvtkbk;
         }
      }

      @Override
      public void mouseReleased(int id, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
         this.m_34vmhtist(this.f_08lozyvms, mouseX, mouseY, button);
         this.m_34vmhtist(this.f_20ecaxira, mouseX, mouseY, button);
      }

      private void m_34vmhtist(GuiElement c_77jecrlbg, int i, int j, int k) {
         if (c_77jecrlbg != null) {
            if (c_77jecrlbg instanceof ButtonWidget) {
               this.m_18xznbgan((ButtonWidget)c_77jecrlbg, i, j, k);
            }
         }
      }

      private void m_18xznbgan(ButtonWidget c_62wzojhks, int i, int j, int k) {
         c_62wzojhks.mouseReleased(i, j);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class C_30sltyycm {
      private final int f_64szlnhjl;
      private final String f_43xjjkffi;
      private final boolean f_89nblzzla;

      public C_30sltyycm(int i, String string, boolean bl) {
         this.f_64szlnhjl = i;
         this.f_43xjjkffi = string;
         this.f_89nblzzla = bl;
      }

      public int m_92ocxyipu() {
         return this.f_64szlnhjl;
      }

      public String m_17crajuqt() {
         return this.f_43xjjkffi;
      }

      public boolean m_19efnofwl() {
         return this.f_89nblzzla;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class C_37zmgzpzj extends C_37rnsjynt.C_30sltyycm {
      public C_37zmgzpzj(int i, String string, boolean bl) {
         super(i, string, bl);
      }
   }

   @Environment(EnvType.CLIENT)
   public interface C_59lpdyoky {
      void m_10bjktzqq(int i, boolean bl);

      void m_03bvoeuzb(int i, float f);

      void m_40mldmjxh(int i, String string);
   }

   @Environment(EnvType.CLIENT)
   public static class C_60wnrvuxr extends C_37rnsjynt.C_30sltyycm {
      private final boolean f_12kvmnxxi;

      public C_60wnrvuxr(int i, String string, boolean bl, boolean bl2) {
         super(i, string, bl);
         this.f_12kvmnxxi = bl2;
      }

      public boolean m_35xrnhbmd() {
         return this.f_12kvmnxxi;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class C_95ddunyyj extends C_37rnsjynt.C_30sltyycm {
      private final C_97enwlcph.C_56tbhdubs f_19uuwztcg;
      private final float f_76yxzmahk;
      private final float f_18tndcrcz;
      private final float f_81epjkqqo;

      public C_95ddunyyj(int i, String string, boolean bl, C_97enwlcph.C_56tbhdubs c_56tbhdubs, float f, float g, float h) {
         super(i, string, bl);
         this.f_19uuwztcg = c_56tbhdubs;
         this.f_76yxzmahk = f;
         this.f_18tndcrcz = g;
         this.f_81epjkqqo = h;
      }

      public C_97enwlcph.C_56tbhdubs m_54uzgnwfa() {
         return this.f_19uuwztcg;
      }

      public float m_51rmyyiey() {
         return this.f_76yxzmahk;
      }

      public float m_58cdexcvh() {
         return this.f_18tndcrcz;
      }

      public float m_61rctwavz() {
         return this.f_81epjkqqo;
      }
   }
}
