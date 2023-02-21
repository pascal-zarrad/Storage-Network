package com.lothrazar.storagenetwork.util;

import java.util.ArrayList;
import java.util.List;

import com.lothrazar.storagenetwork.api.IConnectableLink;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RequestBatch extends Batch<Request> {
    public void extractStacks(IConnectableLink providerStorage, Integer slot, Item item) {
        List<Request> requests = get(item);
        List<Request> remainingRequests = new ArrayList<Request>();
        for (Request request : requests) {
            if (!request.insertStack(providerStorage, slot)) {
                remainingRequests.add(request);
            }
            ItemStack stack = providerStorage.extractFromSlot(slot, 1, true);
            if (stack.isEmpty()) {
                return;
            }
        }
        put(item, remainingRequests);
    }
}
