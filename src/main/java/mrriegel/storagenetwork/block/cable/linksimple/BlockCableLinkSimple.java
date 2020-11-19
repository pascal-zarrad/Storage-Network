package mrriegel.storagenetwork.block.cable.linksimple;

import javax.annotation.Nullable;
import mrriegel.storagenetwork.block.cable.BlockCableWithFacing;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCableLinkSimple extends BlockCableWithFacing {

  public BlockCableLinkSimple(String registryName) {
    super(registryName);
  }

  @Nullable
  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    return new TileCableLinkSimple();
  }
}
