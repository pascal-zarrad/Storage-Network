package mrriegel.storagenetwork.block.cable;

import mrriegel.storagenetwork.block.AbstractBlockConnectable;
import mrriegel.storagenetwork.block.BaseBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class BlockCable extends Block {

 public BlockCable(String registryName) {


   super(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.2F));
   this.setRegistryName(registryName  );
  }

}
