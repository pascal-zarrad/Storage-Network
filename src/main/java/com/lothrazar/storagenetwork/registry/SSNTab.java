package com.lothrazar.storagenetwork.registry;

import com.lothrazar.storagenetwork.StorageNetworkMod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SSNTab {
    public static CreativeModeTab TAB;

    @SubscribeEvent
    public static void registerCreativeTabs(final CreativeModeTabEvent.Register event) {
        TAB = event.registerCreativeModeTab(new ResourceLocation(StorageNetworkMod.MODID, "tab"), builder ->
                builder.icon(() -> new ItemStack(SsnRegistry.Items.REQUEST.get()))
                        .title(Component.translatable("itemGroup.storagenetwork"))
                        .displayItems((features, output, hasPermissions) -> {
                            List<ItemStack> stacks = SsnRegistry.ITEMS.getEntries().stream().map(reg -> new ItemStack(reg.get())).toList();
                            output.acceptAll(stacks);
                        }));
    }
}
