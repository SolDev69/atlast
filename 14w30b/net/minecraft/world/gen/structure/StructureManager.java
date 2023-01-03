package net.minecraft.world.gen.structure;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private static Map ID_TO_START = Maps.newHashMap();
   private static Map START_TO_ID = Maps.newHashMap();
   private static Map ID_TO_PIECE = Maps.newHashMap();
   private static Map PIECE_TO_ID = Maps.newHashMap();

   private static void registerStart(Class startType, String id) {
      ID_TO_START.put(id, startType);
      START_TO_ID.put(startType, id);
   }

   static void registerPiece(Class pieceType, String id) {
      ID_TO_PIECE.put(id, pieceType);
      PIECE_TO_ID.put(pieceType, id);
   }

   public static String getId(StructureStart start) {
      return (String)START_TO_ID.get(start.getClass());
   }

   public static String getId(StructurePiece piece) {
      return (String)PIECE_TO_ID.get(piece.getClass());
   }

   public static StructureStart getStartFromNbt(NbtCompound nbt, World world) {
      StructureStart var2 = null;

      try {
         Class var3 = (Class)ID_TO_START.get(nbt.getString("id"));
         if (var3 != null) {
            var2 = (StructureStart)var3.newInstance();
         }
      } catch (Exception var4) {
         LOGGER.warn("Failed Start with id " + nbt.getString("id"));
         var4.printStackTrace();
      }

      if (var2 != null) {
         var2.readNbt(world, nbt);
      } else {
         LOGGER.warn("Skipping Structure with id " + nbt.getString("id"));
      }

      return var2;
   }

   public static StructurePiece getPieceFromNbt(NbtCompound nbt, World world) {
      StructurePiece var2 = null;

      try {
         Class var3 = (Class)ID_TO_PIECE.get(nbt.getString("id"));
         if (var3 != null) {
            var2 = (StructurePiece)var3.newInstance();
         }
      } catch (Exception var4) {
         LOGGER.warn("Failed Piece with id " + nbt.getString("id"));
         var4.printStackTrace();
      }

      if (var2 != null) {
         var2.readNbt(world, nbt);
      } else {
         LOGGER.warn("Skipping Piece with id " + nbt.getString("id"));
      }

      return var2;
   }

   static {
      registerStart(MineshaftStart.class, "Mineshaft");
      registerStart(VillageStructure.Start.class, "Village");
      registerStart(FortressStructure.Start.class, "Fortress");
      registerStart(StrongholdStructure.Start.class, "Stronghold");
      registerStart(TempleStructure.Start.class, "Temple");
      registerStart(OceanMonumentStructure.Start.class, "Monument");
      MineshaftPieces.register();
      VillagePieces.register();
      FortressPieces.register();
      StrongholdPieces.register();
      TemplePieces.register();
      OceanMonumentPieces.register();
   }
}
