package com.lothrazar.storagenetwork.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import com.lothrazar.storagenetwork.StorageNetwork;
import com.lothrazar.storagenetwork.api.data.ItemStackMatcher;
import com.lothrazar.storagenetwork.api.util.UtilInventory;
import com.lothrazar.storagenetwork.block.master.TileMaster;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import com.lothrazar.storagenetwork.registry.PacketRegistry;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

public class RecipeMessage {

  /** @formatter:off
   * Sample data structure can have list of items for each slot (example: ore dictionary)
   * {
   *  s0:[{id:"ic2:ingot",Count:1b,Damage:2s},{id:"immersiveengineering:metal",Count:1b,Damage:0s}],
   *  s1:[{id:"ic2:ingot",Count:1b,Damage:2s},{id:"immersiveengineering:metal",Count:1b,Damage:0s}],
   *  s2:[{id:"ic2:ingot",Count:1b,Damage:2s},{id:"immersiveengineering:metal",Count:1b,Damage:0s}],
   *  s3:[{id:"ic2:ingot",Count:1b,Damage:2s},{id:"immersiveengineering:metal",Count:1b,Damage:0s}],
   *  s4:[{id:"ic2:ingot",Count:1b,Damage:2s},{id:"immersiveengineering:metal",Count:1b,Damage:0s}],
   *  s5:[{id:"ic2:ingot",Count:1b,Damage:2s},{id:"immersiveengineering:metal",Count:1b,Damage:0s}],
   *  s6:[{id:"ic2:ingot",Count:1b,Damage:2s},{id:"immersiveengineering:metal",Count:1b,Damage:0s}],
   *  s7:[{id:"ic2:ingot",Count:1b,Damage:2s},{id:"immersiveengineering:metal",Count:1b,Damage:0s}],
   *  s8:[{id:"ic2:ingot",Count:1b,Damage:2s},{id:"immersiveengineering:metal",Count:1b,Damage:0s}]
   *  }
   * @formatter:on
   */
  private CompoundNBT nbt;
  private int index = 0;

  private RecipeMessage() {}

  public RecipeMessage(CompoundNBT nbt) {
    this.nbt = nbt;
  }

  public static RecipeMessage decode(PacketBuffer buf) {
    RecipeMessage message = new RecipeMessage();
    message.index = buf.readInt();
    message.nbt = buf.readCompoundTag();
    return message;
  }

  public static void encode(RecipeMessage msg, PacketBuffer buf) {
    buf.writeInt(msg.index);
    buf.writeCompoundTag(msg.nbt);
  }

  public static void handle(RecipeMessage message, Supplier<NetworkEvent.Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity player = ctx.get().getSender();
      if (player.openContainer instanceof ContainerNetwork == false) {
        return;
      }
      ContainerNetwork ctr = (ContainerNetwork) player.openContainer;
      TileMaster master = ctr.getTileMaster();
      if (master == null) {
        StorageNetwork.log("Recipe message cancelled, null tile");
        return;
      }
      ClearRecipeMessage.clearContainerRecipe(player, false);
      CraftingInventory craftMatrix = ctr.getCraftMatrix();
      //        String[] oreDictKeys;// = oreDictKey.split(",");
      for (int slot = 0; slot < 9; slot++) {
        Map<Integer, ItemStack> map = new HashMap<>();
        //if its a string, then ore dict is allowed
        /*********
         * parse nbt of the slot, whether its ore dict, itemstack, ore empty
         **********/
        boolean isOreDict;
        isOreDict = false;
        ListNBT invList = message.nbt.getList("s" + slot, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < invList.size(); i++) {
          CompoundNBT stackTag = invList.getCompound(i);
          ItemStack s = ItemStack.read(stackTag);
          map.put(i, s);
        }
        /********* end parse nbt of this current slot ******/
        /********** now start trying to fill in recipe **/
        for (int i = 0; i < map.size(); i++) {
          ItemStack stackCurrent = map.get(i);
          if (stackCurrent == null || stackCurrent.isEmpty()) {
            continue;
          }
          ItemStackMatcher itemStackMatcher = new ItemStackMatcher(stackCurrent);
          itemStackMatcher.setNbt(true);
          itemStackMatcher.setOre(isOreDict);//important: set this for correct matching
          if (stackCurrent.getMaxDamage() > 0) {
            //its a tool or something with a durability cap so IGNORE metadata 
            //              itemStackMatcher.setMeta(false);
          }
          ItemStack ex = UtilInventory.extractItem(new PlayerMainInvWrapper(player.inventory), itemStackMatcher, 1, true);
          /*********** First try and use the players inventory **/
          if (ex != null && !ex.isEmpty() && craftMatrix.getStackInSlot(slot).isEmpty()) {
            UtilInventory.extractItem(new PlayerMainInvWrapper(player.inventory), itemStackMatcher, 1, false);
            //make sure to add the real item after the nonsimulated withdrawl is complete https://github.com/PrinceOfAmber/Storage-Network/issues/16
            craftMatrix.setInventorySlotContents(slot, ex);
            break;
          }
          /********* now find it from the network ***/
          stackCurrent = master.request(!stackCurrent.isEmpty() ? itemStackMatcher : null, 1, false);
          if (!stackCurrent.isEmpty() && craftMatrix.getStackInSlot(slot).isEmpty()) {
            craftMatrix.setInventorySlotContents(slot, stackCurrent);
            break;
          }
        }
        /************** finished recipe population **/
        //        }
        //now make sure client sync happens.
        ctr.slotChanged();
        List<ItemStack> list = master.getStacks();
        PacketRegistry.INSTANCE.sendTo(new StackRefreshClientMessage(list, new ArrayList<>()),
            player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
      } //end run
    });
  }
}
