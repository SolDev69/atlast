package net.minecraft.server.command;

import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.resource.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.EntityNotFoundException;
import net.minecraft.server.command.exception.InvalidNumberException;
import net.minecraft.server.command.exception.PlayerNotFoundException;
import net.minecraft.server.command.handler.CommandListener;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.border.WorldBorder;

public abstract class Command implements ICommand {
   private static CommandListener listener;

   public int getRequiredPermissionLevel() {
      return 4;
   }

   @Override
   public List getAliases() {
      return null;
   }

   @Override
   public boolean canUse(CommandSource source) {
      return source.canUseCommand(this.getRequiredPermissionLevel(), this.getName());
   }

   @Override
   public List getSuggestions(CommandSource source, String[] args) {
      return null;
   }

   public static int parseInt(String s) {
      try {
         return Integer.parseInt(s);
      } catch (NumberFormatException var2) {
         throw new InvalidNumberException("commands.generic.num.invalid", s);
      }
   }

   public static int parseInt(String s, int min) {
      return parseInt(s, min, Integer.MAX_VALUE);
   }

   public static int parseInt(String s, int min, int max) {
      int var3 = parseInt(s);
      if (var3 < min) {
         throw new InvalidNumberException("commands.generic.num.tooSmall", var3, min);
      } else if (var3 > max) {
         throw new InvalidNumberException("commands.generic.num.tooBig", var3, max);
      } else {
         return var3;
      }
   }

   public static BlockPos parseBlockPos(CommandSource source, String[] args, int startIndex, boolean center) {
      BlockPos var4 = source.getSourceBlockPos();
      WorldBorder var5 = source.getSourceWorld().getWorldBorder();
      return new BlockPos(
         parseRawCoordinate((double)var4.getX(), args[startIndex], MathHelper.floor(var5.getMinX()), MathHelper.floor(var5.getMaxX()), center),
         parseRawCoordinate((double)var4.getY(), args[startIndex + 1], 0, 256, false),
         parseRawCoordinate((double)var4.getZ(), args[startIndex + 2], MathHelper.floor(var5.getMinZ()), MathHelper.floor(var5.getMaxZ()), center)
      );
   }

   public static double parseDouble(String s) {
      try {
         double var1 = Double.parseDouble(s);
         if (!Doubles.isFinite(var1)) {
            throw new InvalidNumberException("commands.generic.num.invalid", s);
         } else {
            return var1;
         }
      } catch (NumberFormatException var3) {
         throw new InvalidNumberException("commands.generic.num.invalid", s);
      }
   }

   public static double parseDouble(String s, double min) {
      return parseDouble(s, min, Double.MAX_VALUE);
   }

   public static double parseDouble(String s, double min, double max) {
      double var5 = parseDouble(s);
      if (var5 < min) {
         throw new InvalidNumberException("commands.generic.double.tooSmall", var5, min);
      } else if (var5 > max) {
         throw new InvalidNumberException("commands.generic.double.tooBig", var5, max);
      } else {
         return var5;
      }
   }

   public static boolean parseBoolean(String s) {
      if (s.equals("true") || s.equals("1")) {
         return true;
      } else if (!s.equals("false") && !s.equals("0")) {
         throw new CommandException("commands.generic.boolean.invalid", s);
      } else {
         return false;
      }
   }

   public static ServerPlayerEntity asPlayer(CommandSource source) {
      if (source instanceof ServerPlayerEntity) {
         return (ServerPlayerEntity)source;
      } else {
         throw new PlayerNotFoundException("You must specify which player you wish to perform this action on.");
      }
   }

   public static ServerPlayerEntity parsePlayer(CommandSource source, String s) {
      ServerPlayerEntity var2 = TargetSelector.selectFirstPlayer(source, s);
      if (var2 == null) {
         try {
            var2 = MinecraftServer.getInstance().getPlayerManager().getMatching(UUID.fromString(s));
         } catch (IllegalArgumentException var4) {
         }
      }

      if (var2 == null) {
         var2 = MinecraftServer.getInstance().getPlayerManager().get(s);
      }

      if (var2 == null) {
         throw new PlayerNotFoundException();
      } else {
         return var2;
      }
   }

   public static Entity parseEntity(CommandSource source, String s) {
      return parseEntity(source, s, Entity.class);
   }

   public static Entity parseEntity(CommandSource source, String s, Class type) {
      Object var3 = TargetSelector.selectFirst(source, s, type);
      MinecraftServer var4 = MinecraftServer.getInstance();
      if (var3 == null) {
         var3 = var4.getPlayerManager().get(s);
      }

      if (var3 == null) {
         try {
            UUID var5 = UUID.fromString(s);
            var3 = var4.getEntity(var5);
            if (var3 == null) {
               var3 = var4.getPlayerManager().getMatching(var5);
            }
         } catch (IllegalArgumentException var6) {
            throw new EntityNotFoundException("commands.generic.entity.invalidUuid");
         }
      }

      if (var3 != null && type.isAssignableFrom(var3.getClass())) {
         return (Entity)var3;
      } else {
         throw new EntityNotFoundException();
      }
   }

   public static List parseEntities(CommandSource source, String s) {
      Object var2;
      if (TargetSelector.isValid(s)) {
         var2 = TargetSelector.select(source, s, Entity.class);
      } else {
         var2 = Lists.newArrayList(new Entity[]{parseEntity(source, s)});
      }

      return (List)var2;
   }

   public static String parsePlayerName(CommandSource source, String s) {
      try {
         return parsePlayer(source, s).getName();
      } catch (PlayerNotFoundException var3) {
         if (TargetSelector.isValid(s)) {
            throw var3;
         } else {
            return s;
         }
      }
   }

   public static String parseEntityName(CommandSource source, String s) {
      try {
         return parsePlayer(source, s).getName();
      } catch (PlayerNotFoundException var5) {
         try {
            return parseEntity(source, s).getUuid().toString();
         } catch (EntityNotFoundException var4) {
            if (TargetSelector.isValid(s)) {
               throw var4;
            } else {
               return s;
            }
         }
      }
   }

   public static Text parseText(CommandSource source, String[] args, int startIndex) {
      return parseText(source, args, startIndex, false);
   }

   public static Text parseText(CommandSource source, String[] args, int startIndex, boolean parseEntityNames) {
      LiteralText var4 = new LiteralText("");

      for(int var5 = startIndex; var5 < args.length; ++var5) {
         if (var5 > startIndex) {
            var4.append(" ");
         }

         Object var6 = new LiteralText(args[var5]);
         if (parseEntityNames) {
            Text var7 = TargetSelector.getSelectionAsText(source, args[var5]);
            if (var7 != null) {
               var6 = var7;
            } else if (TargetSelector.isValid(args[var5])) {
               throw new PlayerNotFoundException();
            }
         }

         var4.append((Text)var6);
      }

      return var4;
   }

   public static String parseString(String[] args, int startIndex) {
      StringBuilder var2 = new StringBuilder();

      for(int var3 = startIndex; var3 < args.length; ++var3) {
         if (var3 > startIndex) {
            var2.append(" ");
         }

         String var4 = args[var3];
         var2.append(var4);
      }

      return var2.toString();
   }

   public static Command.Coordinate parseCoordinate(double c, String s, boolean center) {
      return parseCoordinate(c, s, -30000000, 30000000, center);
   }

   public static Command.Coordinate parseCoordinate(double c, String s, int min, int max, boolean center) {
      boolean var6 = s.startsWith("~");
      if (var6 && Double.isNaN(c)) {
         throw new InvalidNumberException("commands.generic.num.invalid", c);
      } else {
         double var7 = 0.0;
         if (!var6 || s.length() > 1) {
            boolean var9 = s.contains(".");
            if (var6) {
               s = s.substring(1);
            }

            var7 += parseDouble(s);
            if (!var9 && !var6 && center) {
               var7 += 0.5;
            }
         }

         if (min != 0 || max != 0) {
            if (var7 < (double)min) {
               throw new InvalidNumberException("commands.generic.double.tooSmall", var7, min);
            }

            if (var7 > (double)max) {
               throw new InvalidNumberException("commands.generic.double.tooBig", var7, max);
            }
         }

         return new Command.Coordinate(var7 + (var6 ? c : 0.0), var7, var6);
      }
   }

   public static double parseRawCoordinate(double c, String s, boolean center) {
      return parseRawCoordinate(c, s, -30000000, 30000000, center);
   }

   public static double parseRawCoordinate(double c, String s, int min, int max, boolean center) {
      boolean var6 = s.startsWith("~");
      if (var6 && Double.isNaN(c)) {
         throw new InvalidNumberException("commands.generic.num.invalid", c);
      } else {
         double var7 = var6 ? c : 0.0;
         if (!var6 || s.length() > 1) {
            boolean var9 = s.contains(".");
            if (var6) {
               s = s.substring(1);
            }

            var7 += parseDouble(s);
            if (!var9 && !var6 && center) {
               var7 += 0.5;
            }
         }

         if (min != 0 || max != 0) {
            if (var7 < (double)min) {
               throw new InvalidNumberException("commands.generic.double.tooSmall", var7, min);
            }

            if (var7 > (double)max) {
               throw new InvalidNumberException("commands.generic.double.tooBig", var7, max);
            }
         }

         return var7;
      }
   }

   public static Item parseItem(CommandSource source, String s) {
      Identifier var2 = new Identifier(s);
      Item var3 = (Item)Item.REGISTRY.get(var2);
      if (var3 == null) {
         throw new InvalidNumberException("commands.give.notFound", var2);
      } else {
         return var3;
      }
   }

   public static Block parseBlock(CommandSource source, String s) {
      Identifier var2 = new Identifier(s);
      if (!Block.REGISTRY.containsKey(var2)) {
         throw new InvalidNumberException("commands.give.notFound", var2);
      } else {
         Block var3 = (Block)Block.REGISTRY.get(var2);
         if (var3 == null) {
            throw new InvalidNumberException("commands.give.notFound", var2);
         } else {
            return var3;
         }
      }
   }

   public static String listArgs(Object[] args) {
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < args.length; ++var2) {
         String var3 = args[var2].toString();
         if (var2 > 0) {
            if (var2 == args.length - 1) {
               var1.append(" and ");
            } else {
               var1.append(", ");
            }
         }

         var1.append(var3);
      }

      return var1.toString();
   }

   public static Text listText(List text) {
      LiteralText var1 = new LiteralText("");

      for(int var2 = 0; var2 < text.size(); ++var2) {
         if (var2 > 0) {
            if (var2 == text.size() - 1) {
               var1.append(" and ");
            } else if (var2 > 0) {
               var1.append(", ");
            }
         }

         var1.append((Text)text.get(var2));
      }

      return var1;
   }

   public static String listArgs(Collection args) {
      return listArgs(args.toArray(new String[args.size()]));
   }

   public static boolean doesStringStartWith(String region, String s) {
      return s.regionMatches(true, 0, region, 0, region.length());
   }

   public static List suggestMatching(String[] args, String... suggestions) {
      return suggestMatching(args, Arrays.asList(suggestions));
   }

   public static List suggestMatching(String[] args, Iterable suggestions) {
      String var2 = args[args.length - 1];
      ArrayList var3 = Lists.newArrayList();

      for(String var5 : Iterables.transform(suggestions, Functions.toStringFunction())) {
         if (doesStringStartWith(var2, var5)) {
            var3.add(var5);
         }
      }

      return var3;
   }

   @Override
   public boolean hasTargetSelectorAt(String[] args, int index) {
      return false;
   }

   public static void sendSuccess(CommandSource source, ICommand command, String message, Object... args) {
      sendSuccess(source, command, 0, message, args);
   }

   public static void sendSuccess(CommandSource source, ICommand command, int flags, String message, Object... args) {
      if (listener != null) {
         listener.sendSuccess(source, command, flags, message, args);
      }
   }

   public static void setListener(CommandListener listener) {
      Command.listener = listener;
   }

   public int compareTo(ICommand c_05gpxwito) {
      return this.getName().compareTo(c_05gpxwito.getName());
   }

   public static class Coordinate {
      private final double absolute;
      private final double relative;
      private final boolean isRelative;

      protected Coordinate(double absolute, double relative, boolean isRelative) {
         this.absolute = absolute;
         this.relative = relative;
         this.isRelative = isRelative;
      }

      public double getAbsolute() {
         return this.absolute;
      }

      public double getRelative() {
         return this.relative;
      }

      public boolean isRelative() {
         return this.isRelative;
      }
   }
}
