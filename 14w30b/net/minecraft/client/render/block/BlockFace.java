package net.minecraft.client.render.block;

import net.minecraft.SharedConstants;
import net.minecraft.util.math.Direction;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public enum BlockFace {
   DOWN(
      new BlockFace.Vertex(SharedConstants.f_35cytruuz, SharedConstants.f_65hjhkibg, SharedConstants.f_11horuacr),
      new BlockFace.Vertex(SharedConstants.f_35cytruuz, SharedConstants.f_65hjhkibg, SharedConstants.f_80ionvmpr),
      new BlockFace.Vertex(SharedConstants.f_12bhivnfu, SharedConstants.f_65hjhkibg, SharedConstants.f_80ionvmpr),
      new BlockFace.Vertex(SharedConstants.f_12bhivnfu, SharedConstants.f_65hjhkibg, SharedConstants.f_11horuacr)
   ),
   UP(
      new BlockFace.Vertex(SharedConstants.f_35cytruuz, SharedConstants.f_45rwuiagn, SharedConstants.f_80ionvmpr),
      new BlockFace.Vertex(SharedConstants.f_35cytruuz, SharedConstants.f_45rwuiagn, SharedConstants.f_11horuacr),
      new BlockFace.Vertex(SharedConstants.f_12bhivnfu, SharedConstants.f_45rwuiagn, SharedConstants.f_11horuacr),
      new BlockFace.Vertex(SharedConstants.f_12bhivnfu, SharedConstants.f_45rwuiagn, SharedConstants.f_80ionvmpr)
   ),
   NORTH(
      new BlockFace.Vertex(SharedConstants.f_12bhivnfu, SharedConstants.f_45rwuiagn, SharedConstants.f_80ionvmpr),
      new BlockFace.Vertex(SharedConstants.f_12bhivnfu, SharedConstants.f_65hjhkibg, SharedConstants.f_80ionvmpr),
      new BlockFace.Vertex(SharedConstants.f_35cytruuz, SharedConstants.f_65hjhkibg, SharedConstants.f_80ionvmpr),
      new BlockFace.Vertex(SharedConstants.f_35cytruuz, SharedConstants.f_45rwuiagn, SharedConstants.f_80ionvmpr)
   ),
   SOUTH(
      new BlockFace.Vertex(SharedConstants.f_35cytruuz, SharedConstants.f_45rwuiagn, SharedConstants.f_11horuacr),
      new BlockFace.Vertex(SharedConstants.f_35cytruuz, SharedConstants.f_65hjhkibg, SharedConstants.f_11horuacr),
      new BlockFace.Vertex(SharedConstants.f_12bhivnfu, SharedConstants.f_65hjhkibg, SharedConstants.f_11horuacr),
      new BlockFace.Vertex(SharedConstants.f_12bhivnfu, SharedConstants.f_45rwuiagn, SharedConstants.f_11horuacr)
   ),
   WEST(
      new BlockFace.Vertex(SharedConstants.f_35cytruuz, SharedConstants.f_45rwuiagn, SharedConstants.f_80ionvmpr),
      new BlockFace.Vertex(SharedConstants.f_35cytruuz, SharedConstants.f_65hjhkibg, SharedConstants.f_80ionvmpr),
      new BlockFace.Vertex(SharedConstants.f_35cytruuz, SharedConstants.f_65hjhkibg, SharedConstants.f_11horuacr),
      new BlockFace.Vertex(SharedConstants.f_35cytruuz, SharedConstants.f_45rwuiagn, SharedConstants.f_11horuacr)
   ),
   EAST(
      new BlockFace.Vertex(SharedConstants.f_12bhivnfu, SharedConstants.f_45rwuiagn, SharedConstants.f_11horuacr),
      new BlockFace.Vertex(SharedConstants.f_12bhivnfu, SharedConstants.f_65hjhkibg, SharedConstants.f_11horuacr),
      new BlockFace.Vertex(SharedConstants.f_12bhivnfu, SharedConstants.f_65hjhkibg, SharedConstants.f_80ionvmpr),
      new BlockFace.Vertex(SharedConstants.f_12bhivnfu, SharedConstants.f_45rwuiagn, SharedConstants.f_80ionvmpr)
   );

   private static final BlockFace[] ALL = new BlockFace[6];
   private final BlockFace.Vertex[] vertices;

   public static BlockFace byDirection(Direction dir) {
      return ALL[dir.getId()];
   }

   private BlockFace(BlockFace.Vertex... vertices) {
      this.vertices = vertices;
   }

   public BlockFace.Vertex getVertex(int index) {
      return this.vertices[index];
   }

   static {
      ALL[SharedConstants.f_65hjhkibg] = DOWN;
      ALL[SharedConstants.f_45rwuiagn] = UP;
      ALL[SharedConstants.f_80ionvmpr] = NORTH;
      ALL[SharedConstants.f_11horuacr] = SOUTH;
      ALL[SharedConstants.f_35cytruuz] = WEST;
      ALL[SharedConstants.f_12bhivnfu] = EAST;
   }

   @Environment(EnvType.CLIENT)
   public static class Vertex {
      public final int x;
      public final int y;
      public final int z;

      private Vertex(int x, int y, int z) {
         this.x = x;
         this.y = y;
         this.z = z;
      }
   }
}
