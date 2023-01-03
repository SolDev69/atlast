package net.minecraft.entity.living.mob.passive.animal;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.crafting.CraftingManager;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.AnimalBreedGoal;
import net.minecraft.entity.ai.goal.EatGrassGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.MobEntityNavigation;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.mob.passive.PassiveEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;

public class SheepEntity extends AnimalEntity {
   private final CraftingInventory inventory = new CraftingInventory(new InventoryMenu() {
      @Override
      public boolean isValid(PlayerEntity player) {
         return false;
      }
   }, 2, 1);
   private static final Map DYE_COLOR_TO_RGB = Maps.newEnumMap(DyeColor.class);
   private int eatGrassTimer;
   private EatGrassGoal eatGrassGoal = new EatGrassGoal(this);

   public static float[] getColorRgb(DyeColor color) {
      return (float[])DYE_COLOR_TO_RGB.get(color);
   }

   public SheepEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.9F, 1.3F);
      ((MobEntityNavigation)this.getNavigation()).m_61diarbat(true);
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new EscapeDangerGoal(this, 1.25));
      this.goalSelector.addGoal(2, new AnimalBreedGoal(this, 1.0));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.1, Items.WHEAT, false));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(5, this.eatGrassGoal);
      this.goalSelector.addGoal(6, new WanderAroundGoal(this, 1.0));
      this.goalSelector.addGoal(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(8, new LookAroundGoal(this));
      this.inventory.setStack(0, new ItemStack(Items.DYE, 1, 0));
      this.inventory.setStack(1, new ItemStack(Items.DYE, 1, 0));
   }

   @Override
   protected void m_45jbqtvrb() {
      this.eatGrassTimer = this.eatGrassGoal.getTimer();
      super.m_45jbqtvrb();
   }

   @Override
   public void tickAI() {
      if (this.world.isClient) {
         this.eatGrassTimer = Math.max(0, this.eatGrassTimer - 1);
      }

      super.tickAI();
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(8.0);
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.23F);
   }

   @Override
   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.put(16, new Byte((byte)0));
   }

   @Override
   protected void dropLoot(boolean allowDrops, int lootingMultiplier) {
      if (!this.isSheared()) {
         this.dropItem(new ItemStack(Item.byBlock(Blocks.WOOL), 1, this.getColorId().getIndex()), 0.0F);
      }

      int var3 = this.random.nextInt(2) + 1 + this.random.nextInt(1 + lootingMultiplier);

      for(int var4 = 0; var4 < var3; ++var4) {
         if (this.isOnFire()) {
            this.dropItem(Items.COOKED_MUTTON, 1);
         } else {
            this.dropItem(Items.MUTTON, 1);
         }
      }
   }

   @Override
   protected Item getDefaultDropLoot() {
      return Item.byBlock(Blocks.WOOL);
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   @Override
   public void doEvent(byte event) {
      if (event == 10) {
         this.eatGrassTimer = 40;
      } else {
         super.doEvent(event);
      }
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public float getNeckAngle(float delta) {
      if (this.eatGrassTimer <= 0) {
         return 0.0F;
      } else if (this.eatGrassTimer >= 4 && this.eatGrassTimer <= 36) {
         return 1.0F;
      } else {
         return this.eatGrassTimer < 4 ? ((float)this.eatGrassTimer - delta) / 4.0F : -((float)(this.eatGrassTimer - 40) - delta) / 4.0F;
      }
   }

   @net.ornithemc.api.Environment(EnvType.CLIENT)
   public float getHeadAngle(float delta) {
      if (this.eatGrassTimer > 4 && this.eatGrassTimer <= 36) {
         float var2 = ((float)(this.eatGrassTimer - 4) - delta) / 32.0F;
         return (float) (Math.PI / 5) + 0.21991149F * MathHelper.sin(var2 * 28.7F);
      } else {
         return this.eatGrassTimer > 0 ? (float) (Math.PI / 5) : this.pitch / (180.0F / (float)Math.PI);
      }
   }

   @Override
   public boolean canInteract(PlayerEntity player) {
      ItemStack var2 = player.inventory.getMainHandStack();
      if (var2 != null && var2.getItem() == Items.SHEARS && !this.isSheared() && !this.isBaby()) {
         if (!this.world.isClient) {
            this.setSheared(true);
            int var3 = 1 + this.random.nextInt(3);

            for(int var4 = 0; var4 < var3; ++var4) {
               ItemEntity var5 = this.dropItem(new ItemStack(Item.byBlock(Blocks.WOOL), 1, this.getColorId().getIndex()), 1.0F);
               var5.velocityY += (double)(this.random.nextFloat() * 0.05F);
               var5.velocityX += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
               var5.velocityZ += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
            }
         }

         var2.damageAndBreak(1, player);
         this.playSound("mob.sheep.shear", 1.0F, 1.0F);
      }

      return super.canInteract(player);
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      super.writeCustomNbt(nbt);
      nbt.putBoolean("Sheared", this.isSheared());
      nbt.putByte("Color", (byte)this.getColorId().getIndex());
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      super.readCustomNbt(nbt);
      this.setSheared(nbt.getBoolean("Sheared"));
      this.setColor(DyeColor.byIndex(nbt.getByte("Color")));
   }

   @Override
   protected String getAmbientSound() {
      return "mob.sheep.say";
   }

   @Override
   protected String getHurtSound() {
      return "mob.sheep.say";
   }

   @Override
   protected String getDeathSound() {
      return "mob.sheep.say";
   }

   @Override
   protected void playStepSound(BlockPos x, Block y) {
      this.playSound("mob.sheep.step", 0.15F, 1.0F);
   }

   public DyeColor getColorId() {
      return DyeColor.byIndex(this.dataTracker.getByte(16) & 15);
   }

   public void setColor(DyeColor id) {
      byte var2 = this.dataTracker.getByte(16);
      this.dataTracker.update(16, (byte)(var2 & 240 | id.getIndex() & 15));
   }

   public boolean isSheared() {
      return (this.dataTracker.getByte(16) & 16) != 0;
   }

   public void setSheared(boolean scheared) {
      byte var2 = this.dataTracker.getByte(16);
      if (scheared) {
         this.dataTracker.update(16, (byte)(var2 | 16));
      } else {
         this.dataTracker.update(16, (byte)(var2 & -17));
      }
   }

   public static DyeColor generateBasicRandomColor(Random random) {
      int var1 = random.nextInt(100);
      if (var1 < 5) {
         return DyeColor.BLACK;
      } else if (var1 < 10) {
         return DyeColor.GRAY;
      } else if (var1 < 15) {
         return DyeColor.SILVER;
      } else if (var1 < 18) {
         return DyeColor.BROWN;
      } else {
         return random.nextInt(500) == 0 ? DyeColor.PINK : DyeColor.WHITE;
      }
   }

   public SheepEntity makeChild(PassiveEntity c_19nmglwmx) {
      SheepEntity var2 = (SheepEntity)c_19nmglwmx;
      SheepEntity var3 = new SheepEntity(this.world);
      var3.setColor(this.getChildColor(this, var2));
      return var3;
   }

   @Override
   public void onEatingGrass() {
      this.setSheared(false);
      if (this.isBaby()) {
         this.growUp(60);
      }
   }

   @Override
   public EntityData initialize(LocalDifficulty localDifficulty, EntityData entityData) {
      entityData = super.initialize(localDifficulty, entityData);
      this.setColor(generateBasicRandomColor(this.world.random));
      return entityData;
   }

   private DyeColor getChildColor(AnimalEntity firstParent, AnimalEntity secondParent) {
      int var3 = ((SheepEntity)firstParent).getColorId().getMetadata();
      int var4 = ((SheepEntity)secondParent).getColorId().getMetadata();
      this.inventory.getStack(0).setDamage(var3);
      this.inventory.getStack(1).setDamage(var4);
      ItemStack var5 = CraftingManager.getInstance().getResult(this.inventory, ((SheepEntity)firstParent).world);
      int var6;
      if (var5 != null && var5.getItem() == Items.DYE) {
         var6 = var5.getMetadata();
      } else {
         var6 = this.world.random.nextBoolean() ? var3 : var4;
      }

      return DyeColor.byMetadata(var6);
   }

   @Override
   public float getEyeHeight() {
      return 0.95F * this.height;
   }

   static {
      DYE_COLOR_TO_RGB.put(DyeColor.WHITE, new float[]{1.0F, 1.0F, 1.0F});
      DYE_COLOR_TO_RGB.put(DyeColor.ORANGE, new float[]{0.85F, 0.5F, 0.2F});
      DYE_COLOR_TO_RGB.put(DyeColor.MAGENTA, new float[]{0.7F, 0.3F, 0.85F});
      DYE_COLOR_TO_RGB.put(DyeColor.LIGHT_BLUE, new float[]{0.4F, 0.6F, 0.85F});
      DYE_COLOR_TO_RGB.put(DyeColor.YELLOW, new float[]{0.9F, 0.9F, 0.2F});
      DYE_COLOR_TO_RGB.put(DyeColor.LIME, new float[]{0.5F, 0.8F, 0.1F});
      DYE_COLOR_TO_RGB.put(DyeColor.PINK, new float[]{0.95F, 0.5F, 0.65F});
      DYE_COLOR_TO_RGB.put(DyeColor.GRAY, new float[]{0.3F, 0.3F, 0.3F});
      DYE_COLOR_TO_RGB.put(DyeColor.SILVER, new float[]{0.6F, 0.6F, 0.6F});
      DYE_COLOR_TO_RGB.put(DyeColor.CYAN, new float[]{0.3F, 0.5F, 0.6F});
      DYE_COLOR_TO_RGB.put(DyeColor.PURPLE, new float[]{0.5F, 0.25F, 0.7F});
      DYE_COLOR_TO_RGB.put(DyeColor.BLUE, new float[]{0.2F, 0.3F, 0.7F});
      DYE_COLOR_TO_RGB.put(DyeColor.BROWN, new float[]{0.4F, 0.3F, 0.2F});
      DYE_COLOR_TO_RGB.put(DyeColor.GREEN, new float[]{0.4F, 0.5F, 0.2F});
      DYE_COLOR_TO_RGB.put(DyeColor.RED, new float[]{0.6F, 0.2F, 0.2F});
      DYE_COLOR_TO_RGB.put(DyeColor.BLACK, new float[]{0.1F, 0.1F, 0.1F});
   }
}
