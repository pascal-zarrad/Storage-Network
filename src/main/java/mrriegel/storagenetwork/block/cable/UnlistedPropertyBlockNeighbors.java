package mrriegel.storagenetwork.block.cable;
import net.minecraft.util.Direction;

import java.util.HashMap;
import java.util.Map;

public class UnlistedPropertyBlockNeighbors {// implements IUnlistedProperty<UnlistedPropertyBlockNeighbors.BlockNeighbors> {

  public static String getName() {
    return "hillNeighbors";
  }

  public static boolean isValid(BlockNeighbors value) {
    return true;
  }

  public static Class<BlockNeighbors> getType() {
    return BlockNeighbors.class;
  }

  public static String valueToString(BlockNeighbors value) {
    return value.toString();
  }

  public enum EnumNeighborType {
    NONE, CABLE, SPECIAL
  }

  public static class BlockNeighbors {

    Map<Direction, EnumNeighborType> neighborTypes = new HashMap<>();

    void setNeighborType(Direction facing, EnumNeighborType type) {
      neighborTypes.put(facing, type);
    }

    private static String getFacingShortName(Direction facing) {
      return facing.getName().substring(0, 1).toLowerCase();
    }

    @Override
    public int hashCode() {
      return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      return toString().equals(obj.toString());
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder("NeighborTypes[");
      for (Map.Entry<Direction, EnumNeighborType> entry : neighborTypes.entrySet()) {
        if (entry.getValue() == EnumNeighborType.NONE) {
          continue;
        }
        if (entry.getValue() == EnumNeighborType.CABLE) {
          builder.append(getFacingShortName(entry.getKey()).toLowerCase());
        }
        else {
          builder.append(getFacingShortName(entry.getKey()).toUpperCase());
        }
      }
      builder.append(']');
      return builder.toString();
    }

    boolean requiresCube() {
      // Only Y-Axis -> no cube
      boolean hasNorth = north() != EnumNeighborType.NONE;
      boolean hasSouth = south() != EnumNeighborType.NONE;
      boolean hasWest = west() != EnumNeighborType.NONE;
      boolean hasEast = east() != EnumNeighborType.NONE;
      boolean hasUp = up() != EnumNeighborType.NONE;
      boolean hasDown = down() != EnumNeighborType.NONE;
      boolean a = hasNorth && hasSouth && !hasWest && !hasEast && !hasUp && !hasDown;
      boolean b = !hasNorth && !hasSouth && hasWest && hasEast && !hasUp && !hasDown;
      boolean c = !hasNorth && !hasSouth && !hasWest && !hasEast && hasUp && hasDown;
      return !(a ^ b ^ c);
    }

    public EnumNeighborType north() {
      return neighborTypes.getOrDefault(Direction.NORTH, EnumNeighborType.NONE);
    }

    public EnumNeighborType east() {
      return neighborTypes.getOrDefault(Direction.EAST, EnumNeighborType.NONE);
    }

    public EnumNeighborType south() {
      return neighborTypes.getOrDefault(Direction.SOUTH, EnumNeighborType.NONE);
    }

    public EnumNeighborType west() {
      return neighborTypes.getOrDefault(Direction.WEST, EnumNeighborType.NONE);
    }

    public EnumNeighborType up() {
      return neighborTypes.getOrDefault(Direction.UP, EnumNeighborType.NONE);
    }

    public EnumNeighborType down() {
      return neighborTypes.getOrDefault(Direction.DOWN, EnumNeighborType.NONE);
    }
  }
}
