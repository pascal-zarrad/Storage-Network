package com.lothrazar.storagenetwork.block.inventory;

import com.lothrazar.storagenetwork.block.BaseBlock;
import com.lothrazar.storagenetwork.network.SortClientMessage;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class BlockInventory extends BaseBlock {

  public BlockInventory(String registryName) {
    super(Material.METAL, registryName);
  }
  @Override
  public RenderShape getRenderShape(BlockState bs) {
    return RenderShape.MODEL;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TileInventory(pos, state);
  }

  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
    if (!world.isClientSide) {
      TileInventory tile = (TileInventory) world.getBlockEntity(pos);
      if (tile.getMain() == null || tile.getMain().getBlockPos() == null) {
        return InteractionResult.PASS;
      }
      //sync
      ServerPlayer sp = (ServerPlayer) player;
      PacketRegistry.INSTANCE.sendTo(new SortClientMessage(pos, tile.isDownwards(), tile.getSort()), sp.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
      //end sync
      if (tile instanceof MenuProvider) {
        NetworkHooks.openGui((ServerPlayer) player, (MenuProvider) tile, tile.getBlockPos());
      }
      else {
        throw new IllegalStateException("Our named container provider is missing!");
      }
    }
    return InteractionResult.SUCCESS;
  }

}
