package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.LivingEntityType;
import net.minecraft.entity.living.attribute.AttributeModifier;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.locale.I18n;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.resource.Identifier;
import net.minecraft.stat.Stats;
import net.minecraft.text.Formatting;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public final class ItemStack {
   public static final DecimalFormat MODIFIER_FORMAT = new DecimalFormat("#.###");
   public int size;
   public int popAnimationTime;
   private Item item;
   private NbtCompound nbt;
   private int metadata;
   private ItemFrameEntity itemFrame;
   private Block cachedMineBlockOverride = null;
   private boolean cachedMineBlockOverrideResult = false;
   private Block cachedPlaceOnBlockOverride = null;
   private boolean cachedPlaceOnBlockOverrideResult = false;

   public ItemStack(Block block) {
      this(block, 1);
   }

   public ItemStack(Block block, int size) {
      this(block, size, 0);
   }

   public ItemStack(Block block, int size, int metadata) {
      this(Item.byBlock(block), size, metadata);
   }

   public ItemStack(Item item) {
      this(item, 1);
   }

   public ItemStack(Item item, int size) {
      this(item, size, 0);
   }

   public ItemStack(Item item, int size, int metadata) {
      this.item = item;
      this.size = size;
      this.metadata = metadata;
      if (this.metadata < 0) {
         this.metadata = 0;
      }
   }

   public static ItemStack fromNbt(NbtCompound nbt) {
      ItemStack var1 = new ItemStack();
      var1.readNbt(nbt);
      return var1.getItem() != null ? var1 : null;
   }

   private ItemStack() {
   }

   public ItemStack split(int amount) {
      ItemStack var2 = new ItemStack(this.item, amount, this.metadata);
      if (this.nbt != null) {
         var2.nbt = (NbtCompound)this.nbt.copy();
      }

      this.size -= amount;
      return var2;
   }

   public Item getItem() {
      return this.item;
   }

   public boolean use(PlayerEntity player, World world, BlockPos pos, Direction face, float z, float dy, float dz) {
      boolean var8 = this.getItem().use(this, player, world, pos, face, z, dy, dz);
      if (var8) {
         player.incrementStat(Stats.ITEMS_USED[Item.getRawId(this.item)]);
      }

      return var8;
   }

   public float getMiningSpeed(Block block) {
      return this.getItem().getMiningSpeed(this, block);
   }

   public ItemStack startUsing(World world, PlayerEntity player) {
      return this.getItem().startUsing(this, world, player);
   }

   public ItemStack finishUsing(World world, PlayerEntity player) {
      return this.getItem().finishUsing(this, world, player);
   }

   public NbtCompound writeNbt(NbtCompound nbt) {
      Identifier var2 = (Identifier)Item.REGISTRY.getKey(this.item);
      nbt.putString("id", var2 == null ? "minecraft:air" : var2.toString());
      nbt.putByte("Count", (byte)this.size);
      nbt.putShort("Damage", (short)this.metadata);
      if (this.nbt != null) {
         nbt.put("tag", this.nbt);
      }

      return nbt;
   }

   public void readNbt(NbtCompound nbt) {
      if (nbt.isType("id", 8)) {
         this.item = Item.byId(nbt.getString("id"));
      } else {
         this.item = Item.byRawId(nbt.getShort("id"));
      }

      this.size = nbt.getByte("Count");
      this.metadata = nbt.getShort("Damage");
      if (this.metadata < 0) {
         this.metadata = 0;
      }

      if (nbt.isType("tag", 10)) {
         this.nbt = nbt.getCompound("tag");
         if (this.item != null) {
            this.item.validateNbt(this.nbt);
         }
      }
   }

   public int getMaxSize() {
      return this.getItem().getMaxStackSize();
   }

   public boolean isStackable() {
      return this.getMaxSize() > 1 && (!this.isDamageable() || !this.isDamaged());
   }

   public boolean isDamageable() {
      if (this.item.getMaxDamage() <= 0) {
         return false;
      } else {
         return !this.hasNbt() || !this.getNbt().getBoolean("Unbreakable");
      }
   }

   public boolean isItemStackable() {
      return this.item.isStackable();
   }

   public boolean isDamaged() {
      return this.isDamageable() && this.metadata > 0;
   }

   public int getDamage() {
      return this.metadata;
   }

   public int getMetadata() {
      return this.metadata;
   }

   public void setDamage(int damage) {
      this.metadata = damage;
      if (this.metadata < 0) {
         this.metadata = 0;
      }
   }

   public int getMaxDamage() {
      return this.item.getMaxDamage();
   }

   public boolean damage(int amount, Random random) {
      if (!this.isDamageable()) {
         return false;
      } else {
         if (amount > 0) {
            int var3 = EnchantmentHelper.getLevel(Enchantment.UNBREAKING.id, this);
            int var4 = 0;

            for(int var5 = 0; var3 > 0 && var5 < amount; ++var5) {
               if (UnbreakingEnchantment.shouldReduceDamage(this, var3, random)) {
                  ++var4;
               }
            }

            amount -= var4;
            if (amount <= 0) {
               return false;
            }
         }

         this.metadata += amount;
         return this.metadata > this.getMaxDamage();
      }
   }

   public void damageAndBreak(int amount, LivingEntity entity) {
      if (!(entity instanceof PlayerEntity) || !((PlayerEntity)entity).abilities.creativeMode) {
         if (this.isDamageable()) {
            if (this.damage(amount, entity.getRandom())) {
               entity.renderBrokenItem(this);
               --this.size;
               if (entity instanceof PlayerEntity) {
                  PlayerEntity var3 = (PlayerEntity)entity;
                  var3.incrementStat(Stats.ITEMS_BROKEN[Item.getRawId(this.item)]);
                  if (this.size == 0 && this.getItem() instanceof BowItem) {
                     var3.clearSelectedSlot();
                  }
               }

               if (this.size < 0) {
                  this.size = 0;
               }

               this.metadata = 0;
            }
         }
      }
   }

   public void attackEntity(LivingEntity target, PlayerEntity attacker) {
      boolean var3 = this.item.attackEntity(this, target, attacker);
      if (var3) {
         attacker.incrementStat(Stats.ITEMS_USED[Item.getRawId(this.item)]);
      }
   }

   public void mineBlock(World world, Block block, BlockPos pos, PlayerEntity player) {
      boolean var5 = this.item.mineBlock(this, world, block, pos, player);
      if (var5) {
         player.incrementStat(Stats.ITEMS_USED[Item.getRawId(this.item)]);
      }
   }

   public boolean canEffectivelyMine(Block block) {
      return this.item.canEffectivelyMine(block);
   }

   public boolean canInteract(PlayerEntity player, LivingEntity entity) {
      return this.item.canInteract(this, player, entity);
   }

   public ItemStack copy() {
      ItemStack var1 = new ItemStack(this.item, this.size, this.metadata);
      if (this.nbt != null) {
         var1.nbt = (NbtCompound)this.nbt.copy();
      }

      return var1;
   }

   public static boolean matchesNbt(ItemStack stack1, ItemStack stack2) {
      if (stack1 == null && stack2 == null) {
         return true;
      } else if (stack1 == null || stack2 == null) {
         return false;
      } else if (stack1.nbt == null && stack2.nbt != null) {
         return false;
      } else {
         return stack1.nbt == null || stack1.nbt.equals(stack2.nbt);
      }
   }

   public static boolean matches(ItemStack stack1, ItemStack stack2) {
      if (stack1 == null && stack2 == null) {
         return true;
      } else {
         return stack1 != null && stack2 != null ? stack1.matches(stack2) : false;
      }
   }

   private boolean matches(ItemStack other) {
      if (this.size != other.size) {
         return false;
      } else if (this.item != other.item) {
         return false;
      } else if (this.metadata != other.metadata) {
         return false;
      } else if (this.nbt == null && other.nbt != null) {
         return false;
      } else {
         return this.nbt == null || this.nbt.equals(other.nbt);
      }
   }

   public static boolean matchesItem(ItemStack stack1, ItemStack stack2) {
      if (stack1 == null && stack2 == null) {
         return true;
      } else {
         return stack1 != null && stack2 != null ? stack1.matchesItem(stack2) : false;
      }
   }

   public boolean matchesItem(ItemStack other) {
      return this.item == other.item && this.metadata == other.metadata;
   }

   public String getTranslationKey() {
      return this.item.getTranslationKey(this);
   }

   public static ItemStack copyOf(ItemStack stack) {
      return stack == null ? null : stack.copy();
   }

   @Override
   public String toString() {
      return this.size + "x" + this.item.getTranslationKey() + "@" + this.metadata;
   }

   public void tick(World world, Entity entity, int slot, boolean selected) {
      if (this.popAnimationTime > 0) {
         --this.popAnimationTime;
      }

      this.item.tick(this, world, entity, slot, selected);
   }

   public void onResult(World world, PlayerEntity player, int amount) {
      player.incrementStat(Stats.ITEMS_CRAFTED[Item.getRawId(this.item)], amount);
      this.item.onResult(this, world, player);
   }

   @Environment(EnvType.CLIENT)
   public boolean isEqualForHoldAnimation(ItemStack stack) {
      return this.matches(stack);
   }

   public int getUseDuration() {
      return this.getItem().getUseDuration(this);
   }

   public UseAction getUseAction() {
      return this.getItem().getUseAction(this);
   }

   public void stopUsing(World world, PlayerEntity player, int remainingUseTime) {
      this.getItem().stopUsing(this, world, player, remainingUseTime);
   }

   public boolean hasNbt() {
      return this.nbt != null;
   }

   public NbtCompound getNbt() {
      return this.nbt;
   }

   public NbtCompound getNbt(String key, boolean orCreate) {
      if (this.nbt != null && this.nbt.isType(key, 10)) {
         return this.nbt.getCompound(key);
      } else if (orCreate) {
         NbtCompound var3 = new NbtCompound();
         this.addToNbt(key, var3);
         return var3;
      } else {
         return null;
      }
   }

   public NbtList getEnchantments() {
      return this.nbt == null ? null : this.nbt.getList("ench", 10);
   }

   public void setNbt(NbtCompound nbt) {
      this.nbt = nbt;
   }

   public String getHoverName() {
      String var1 = this.getItem().getName(this);
      if (this.nbt != null && this.nbt.isType("display", 10)) {
         NbtCompound var2 = this.nbt.getCompound("display");
         if (var2.isType("Name", 8)) {
            var1 = var2.getString("Name");
         }
      }

      return var1;
   }

   public ItemStack setHoverName(String name) {
      if (this.nbt == null) {
         this.nbt = new NbtCompound();
      }

      if (!this.nbt.isType("display", 10)) {
         this.nbt.put("display", new NbtCompound());
      }

      this.nbt.getCompound("display").putString("Name", name);
      return this;
   }

   public void resetHoverName() {
      if (this.nbt != null) {
         if (this.nbt.isType("display", 10)) {
            NbtCompound var1 = this.nbt.getCompound("display");
            var1.remove("Name");
            if (var1.isEmpty()) {
               this.nbt.remove("display");
               if (this.nbt.isEmpty()) {
                  this.setNbt(null);
               }
            }
         }
      }
   }

   public boolean hasCustomHoverName() {
      if (this.nbt == null) {
         return false;
      } else {
         return !this.nbt.isType("display", 10) ? false : this.nbt.getCompound("display").isType("Name", 8);
      }
   }

   @Environment(EnvType.CLIENT)
   public List getTooltip(PlayerEntity player, boolean advanced) {
      ArrayList var3 = Lists.newArrayList();
      String var4 = this.getHoverName();
      if (this.hasCustomHoverName()) {
         var4 = Formatting.ITALIC + var4 + Formatting.RESET;
      }

      if (advanced) {
         String var5 = "";
         if (var4.length() > 0) {
            var4 = var4 + " (";
            var5 = ")";
         }

         int var6 = Item.getRawId(this.item);
         if (this.isItemStackable()) {
            var4 = var4 + String.format("#%04d/%d%s", var6, this.metadata, var5);
         } else {
            var4 = var4 + String.format("#%04d%s", var6, var5);
         }
      } else if (!this.hasCustomHoverName() && this.item == Items.FILLED_MAP) {
         var4 = var4 + " #" + this.metadata;
      }

      var3.add(var4);
      int var14 = 0;
      if (this.hasNbt() && this.nbt.isType("HideFlags", 99)) {
         var14 = this.nbt.getInt("HideFlags");
      }

      if ((var14 & 32) == 0) {
         this.item.addHoverText(this, player, var3, advanced);
      }

      if (this.hasNbt()) {
         if ((var14 & 1) == 0) {
            NbtList var15 = this.getEnchantments();
            if (var15 != null) {
               for(int var7 = 0; var7 < var15.size(); ++var7) {
                  short var8 = var15.getCompound(var7).getShort("id");
                  short var9 = var15.getCompound(var7).getShort("lvl");
                  if (Enchantment.byRawId(var8) != null) {
                     var3.add(Enchantment.byRawId(var8).getDisplayName(var9));
                  }
               }
            }
         }

         if (this.nbt.isType("display", 10)) {
            NbtCompound var16 = this.nbt.getCompound("display");
            if (var16.isType("color", 3)) {
               if (advanced) {
                  var3.add("Color: #" + Integer.toHexString(var16.getInt("color")).toUpperCase());
               } else {
                  var3.add(Formatting.ITALIC + I18n.translate("item.dyed"));
               }
            }

            if (var16.getType("Lore") == 9) {
               NbtList var18 = var16.getList("Lore", 8);
               if (var18.size() > 0) {
                  for(int var22 = 0; var22 < var18.size(); ++var22) {
                     var3.add(Formatting.DARK_PURPLE + "" + Formatting.ITALIC + var18.getString(var22));
                  }
               }
            }
         }
      }

      Multimap var17 = this.getAttributeModifiers();
      if (!var17.isEmpty() && (var14 & 2) == 0) {
         var3.add("");

         for(Entry var23 : var17.entries()) {
            AttributeModifier var26 = (AttributeModifier)var23.getValue();
            double var10 = var26.get();
            if (var26.getId() == Item.ATTACK_DAMAGE_MODIFIER_UUID) {
               var10 += (double)EnchantmentHelper.modifyDamage(this, LivingEntityType.UNDEFINED);
            }

            double var12;
            if (var26.getOperation() != 1 && var26.getOperation() != 2) {
               var12 = var10;
            } else {
               var12 = var10 * 100.0;
            }

            if (var10 > 0.0) {
               var3.add(
                  Formatting.BLUE
                     + I18n.translate(
                        "attribute.modifier.plus." + var26.getOperation(),
                        MODIFIER_FORMAT.format(var12),
                        I18n.translate("attribute.name." + (String)var23.getKey())
                     )
               );
            } else if (var10 < 0.0) {
               var12 *= -1.0;
               var3.add(
                  Formatting.RED
                     + I18n.translate(
                        "attribute.modifier.take." + var26.getOperation(),
                        MODIFIER_FORMAT.format(var12),
                        I18n.translate("attribute.name." + (String)var23.getKey())
                     )
               );
            }
         }
      }

      if (this.hasNbt() && this.getNbt().getBoolean("Unbreakable") && (var14 & 4) == 0) {
         var3.add(Formatting.BLUE + I18n.translate("item.unbreakable"));
      }

      if (this.hasNbt() && this.nbt.isType("CanDestroy", 9) && (var14 & 8) == 0) {
         NbtList var20 = this.nbt.getList("CanDestroy", 8);
         if (var20.size() > 0) {
            var3.add("");
            var3.add(Formatting.GRAY + I18n.translate("item.canBreak"));

            for(int var24 = 0; var24 < var20.size(); ++var24) {
               Block var27 = Block.byId(var20.getString(var24));
               if (var27 != null) {
                  var3.add(Formatting.DARK_GRAY + var27.getName());
               } else {
                  var3.add(Formatting.DARK_GRAY + "missingno");
               }
            }
         }
      }

      if (this.hasNbt() && this.nbt.isType("CanPlaceOn", 9) && (var14 & 16) == 0) {
         NbtList var21 = this.nbt.getList("CanPlaceOn", 8);
         if (var21.size() > 0) {
            var3.add("");
            var3.add(Formatting.GRAY + I18n.translate("item.canPlace"));

            for(int var25 = 0; var25 < var21.size(); ++var25) {
               Block var28 = Block.byId(var21.getString(var25));
               if (var28 != null) {
                  var3.add(Formatting.DARK_GRAY + var28.getName());
               } else {
                  var3.add(Formatting.DARK_GRAY + "missingno");
               }
            }
         }
      }

      if (advanced) {
         if (this.isDamaged()) {
            var3.add("Durability: " + (this.getMaxDamage() - this.getDamage()) + " / " + this.getMaxDamage());
         }

         var3.add(Formatting.DARK_GRAY + ((Identifier)Item.REGISTRY.getKey(this.item)).toString());
         if (this.hasNbt()) {
            var3.add(Formatting.DARK_GRAY + "NBT: " + this.getNbt().getKeys().size() + " tag(s)");
         }
      }

      return var3;
   }

   @Environment(EnvType.CLIENT)
   public boolean hasEnchantmentGlint() {
      return this.getItem().hasEnchantmentGlint(this);
   }

   public Rarity getRarity() {
      return this.getItem().getRarity(this);
   }

   public boolean isEnchantable() {
      if (!this.getItem().isEnchantable(this)) {
         return false;
      } else {
         return !this.hasEnchantments();
      }
   }

   public void addEnchantment(Enchantment enchantment, int level) {
      if (this.nbt == null) {
         this.setNbt(new NbtCompound());
      }

      if (!this.nbt.isType("ench", 9)) {
         this.nbt.put("ench", new NbtList());
      }

      NbtList var3 = this.nbt.getList("ench", 10);
      NbtCompound var4 = new NbtCompound();
      var4.putShort("id", (short)enchantment.id);
      var4.putShort("lvl", (short)((byte)level));
      var3.add(var4);
   }

   public boolean hasEnchantments() {
      return this.nbt != null && this.nbt.isType("ench", 9);
   }

   public void addToNbt(String key, NbtElement nbt) {
      if (this.nbt == null) {
         this.setNbt(new NbtCompound());
      }

      this.nbt.put(key, nbt);
   }

   public boolean canAlwaysUse() {
      return this.getItem().canAlwaysUse();
   }

   public boolean isInItemFrame() {
      return this.itemFrame != null;
   }

   public void setItemFrame(ItemFrameEntity itemFrame) {
      this.itemFrame = itemFrame;
   }

   public ItemFrameEntity getItemFrame() {
      return this.itemFrame;
   }

   public int getRepairCost() {
      return this.hasNbt() && this.nbt.isType("RepairCost", 3) ? this.nbt.getInt("RepairCost") : 0;
   }

   public void setRepairCost(int cost) {
      if (!this.hasNbt()) {
         this.nbt = new NbtCompound();
      }

      this.nbt.putInt("RepairCost", cost);
   }

   public Multimap getAttributeModifiers() {
      Object var1;
      if (this.hasNbt() && this.nbt.isType("AttributeModifiers", 9)) {
         var1 = HashMultimap.create();
         NbtList var2 = this.nbt.getList("AttributeModifiers", 10);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            NbtCompound var4 = var2.getCompound(var3);
            AttributeModifier var5 = EntityAttributes.fromNbt(var4);
            if (var5 != null && var5.getId().getLeastSignificantBits() != 0L && var5.getId().getMostSignificantBits() != 0L) {
               var1.put(var4.getString("AttributeName"), var5);
            }
         }
      } else {
         var1 = this.getItem().getDefaultAttributeModifiers();
      }

      return (Multimap)var1;
   }

   public void setItem(Item item) {
      this.item = item;
   }

   public Text getDisplayName() {
      Text var1 = new LiteralText("[").append(this.getHoverName()).append("]");
      if (this.item != null) {
         NbtCompound var2 = new NbtCompound();
         this.writeNbt(var2);
         var1.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new LiteralText(var2.toString())));
         var1.getStyle().setColor(this.getRarity().formatting);
      }

      return var1;
   }

   public boolean hasMineBlockOverride(Block block) {
      if (block == this.cachedMineBlockOverride) {
         return this.cachedMineBlockOverrideResult;
      } else {
         this.cachedMineBlockOverride = block;
         if (this.hasNbt() && this.nbt.isType("CanDestroy", 9)) {
            NbtList var2 = this.nbt.getList("CanDestroy", 8);

            for(int var3 = 0; var3 < var2.size(); ++var3) {
               Block var4 = Block.byId(var2.getString(var3));
               if (var4 == block) {
                  this.cachedMineBlockOverrideResult = true;
                  return true;
               }
            }
         }

         this.cachedMineBlockOverrideResult = false;
         return false;
      }
   }

   public boolean hasPlaceOnBlockOverride(Block block) {
      if (block == this.cachedPlaceOnBlockOverride) {
         return this.cachedPlaceOnBlockOverrideResult;
      } else {
         this.cachedPlaceOnBlockOverride = block;
         if (this.hasNbt() && this.nbt.isType("CanPlaceOn", 9)) {
            NbtList var2 = this.nbt.getList("CanPlaceOn", 8);

            for(int var3 = 0; var3 < var2.size(); ++var3) {
               Block var4 = Block.byId(var2.getString(var3));
               if (var4 == block) {
                  this.cachedPlaceOnBlockOverrideResult = true;
                  return true;
               }
            }
         }

         this.cachedPlaceOnBlockOverrideResult = false;
         return false;
      }
   }
}
